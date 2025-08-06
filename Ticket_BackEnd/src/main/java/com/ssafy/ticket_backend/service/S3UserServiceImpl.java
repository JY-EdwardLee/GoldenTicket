package com.ssafy.ticket_backend.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.ssafy.ticket_backend.dto.request.S3DownloadRequest;
import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3DownloadResponse;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;
import com.ssafy.ticket_backend.exception.DatabaseException;
import com.ssafy.ticket_backend.exception.ImageDownloadUrlGenerationException;
import com.ssafy.ticket_backend.exception.PresignedUrlGenerationException;
import com.ssafy.ticket_backend.exception.SaveUploadKeyException;
import com.ssafy.ticket_backend.exception.UserProfileKeyQueryException;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.S3Type;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class S3UserServiceImpl implements S3UserService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final UserMapper userMapper;

    /**
     * 이미지 업로드, 수정을 위한 임시 Presigned URL요청
     *
     * @param refId    userId or postId
     * @param fileName 원본파일 이름
     * @return S3UploadUrlResponse
     */
    @Transactional
    @Override
    public S3UploadUrlResponse generatePresignedUploadUrl(Long refId, String fileName) {
        try {
            // 버킷 설정 검증
            validateBucketConfiguration();

            // 파라미터 검증
            if (refId == null || refId <= 0) {
                throw new IllegalArgumentException("유효하지 않은 참조 ID입니다.");
            }

            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("파일명이 비어있습니다.");
            }

            String key;
            String existingKey = null;
            // 업데이트 여부
            boolean isUpdate = false;

            // 기존 key가 있는지 확인 (Update + Insert 구현방식)
            try {
                existingKey = userMapper.getUserProfileImageKey(refId);
                if (existingKey != null && !existingKey.isEmpty()) {
                    // 기존 key가 있으면 업데이트 모드
                    isUpdate = true;
                } else {
                    // 새로운 이미지 업로드 모드
                }
            } catch (Exception e) {
                throw new PresignedUrlGenerationException("사용자 정보 조회에 실패하였습니다.");
            }

            // 항상 새로운 key 생성 (캐싱 문제 해결) -> 키 삭제는 이후 front에서 업로드 성공시 요청에서 처리한다.
            key = generateKey(S3Type.UserProfile.name(), refId, fileName);

            String url = generatePresignedPutUrl(key);

            S3UploadUrlResponse response = new S3UploadUrlResponse();
            response.setKey(key);
            response.setPresignedUrl(url);

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new PresignedUrlGenerationException("Presigned URL 생성에 실패하였습니다.");
        }
    }

    private String generateKey(String type, Long refId, String originalFileName) {
        // 파일명이 null이거나 빈 문자열인 경우
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 비어있습니다.");
        }

        // 확장자가 없는 경우
        int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == originalFileName.length() - 1) {
            throw new IllegalArgumentException("유효한 파일 확장자가 없습니다.");
        }

        // 확장자 추출 png, jpg..
        String ext = originalFileName.substring(lastDotIndex + 1);

        return type + "/" + refId + "/" + UUID.randomUUID() + "." + ext;
    }

    private String generatePresignedPutUrl(String key) {
        try {
            // UTC 시간대를 명시적으로 사용
            java.util.Calendar cal = java.util.Calendar.getInstance(
                java.util.TimeZone.getTimeZone("UTC"));
            cal.add(java.util.Calendar.HOUR, 10); // 10시간 후로 설정
            Date expiration = cal.getTime();

            // 해당 bucket에 key에 대해서 PUT(업로드) 요청 10시간간 허용 URL 생성
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket,
                key).withMethod(HttpMethod.PUT).withExpiration(expiration);

            // 임시 URL 클라이언트에 반환
            return amazonS3.generatePresignedUrl(request).toString();
        } catch (Exception e) {
            throw new PresignedUrlGenerationException("Presigned URL 생성에 실패하였습니다.");
        }
    }

    /**
     * 업로드 키 저장 (S3 업로드 성공 이후) - UserProfile 전용
     *
     * @param s3SaverRequest
     * @return S3SaveResponse
     */
    @Transactional
    @Override
    public S3SaveResponse saveUploadKey(S3SaverRequest s3SaverRequest) {
        try {
            // 파라미터 검증
            if (s3SaverRequest == null) {
                throw new IllegalArgumentException("요청 객체가 null입니다.");
            }

            if (s3SaverRequest.getRefId() == null || s3SaverRequest.getRefId() <= 0) {
                throw new IllegalArgumentException("유효하지 않은 참조 ID입니다.");
            }

            if (s3SaverRequest.getKey() == null || s3SaverRequest.getKey().trim().isEmpty()) {
                throw new IllegalArgumentException("S3 키가 비어있습니다.");
            }

            // 기존 key가 있는지 확인하여 업데이트 여부 판단
            boolean isUpdate = false;
            String existingKey = null;

            // 기존 키 조회
            try {
                existingKey = userMapper.getUserProfileImageKey(s3SaverRequest.getRefId());
                isUpdate = (existingKey != null && !existingKey.isEmpty());
            } catch (Exception e) {
                throw new UserProfileKeyQueryException("사용자 정보 조회에 실패하였습니다.");
            }

            // DB에 업로드 정보 저장
            try {
                saveUploadInfo(s3SaverRequest.getKey(), s3SaverRequest.getRefId());
            } catch (Exception e) {
                throw new DatabaseException("프로필 이미지 정보 저장에 실패하였습니다.");
            }

            // 업데이트인 경우 기존 S3 객체 삭제
            if (isUpdate && existingKey != null) {
                try {
                    amazonS3.deleteObject(bucket, existingKey);
                } catch (Exception e) {
                    // S3 삭제 실패는 치명적이지 않으므로 경고만 남기고 계속 진행
                }
            }

            S3SaveResponse response = new S3SaveResponse();
            response.setSuccess(true);

            if (isUpdate) {
                response.setMessage("이미지 업데이트가 성공적으로 완료되었습니다. (기존 이미지 삭제됨)");
            } else {
                response.setMessage("새로운 이미지 업로드가 성공적으로 완료되었습니다.");
            }

            return response;
        } catch (IllegalArgumentException | UserProfileKeyQueryException | DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new SaveUploadKeyException("이미지 파일 저장에 실패하였습니다..");   // 데이터 베이스 키 저장 오류
        }
    }

    // DB에 업로드 정보 저장 - UserProfile 전용
    private void saveUploadInfo(String key, Long refId) {
        userMapper.updateUserProfileImage(refId, key);
    }

    // Presigned Download URL 생성 - UserProfileㄴ 전용


    /**
     * 이미지 다운로드 url 발급 (조회용)
     *
     * @param s3DownloadRequest 요청
     * @return S3DownloadResponse  임시 URl 발급
     */
    @Transactional
    @Override
    public S3DownloadResponse getImageUrlsByTypeAndRefId(S3DownloadRequest s3DownloadRequest) {
        try {
            // 파라미터 검증
            if (s3DownloadRequest == null) {
                throw new IllegalArgumentException("요청 객체가 null입니다.");
            }

            if (s3DownloadRequest.getRefId() == null || s3DownloadRequest.getRefId() <= 0) {
                throw new IllegalArgumentException("유효하지 않은 참조 ID입니다.");
            }

            S3DownloadResponse response = new S3DownloadResponse();

            // 해당 사용자 키 조회
            String profileImageKey;
            try {
                profileImageKey = userMapper.getUserProfileImageKey(s3DownloadRequest.getRefId());
            } catch (Exception e) {
                response.setDownloadUrl(null);
                response.setMessage("사용자 정보 조회에 실패하였습니다.");
                return response;
            }

            if (profileImageKey != null && !profileImageKey.isEmpty()) {
                // GET하기 위한 임시 접근 URL을 발급해 준다.
                try {
                    String downloadUrl = generatePresignedGetUrl(profileImageKey);
                    response.setDownloadUrl(downloadUrl);
                    response.setMessage("프로필 이미지 URL 생성 성공");
                } catch (Exception e) {
                    response.setDownloadUrl(null);
                    response.setMessage("이미지 URL 생성에 실패하였습니다.");
                    return response;
                }
            } else {
                response.setDownloadUrl(null);
                response.setMessage("프로필 이미지가 없습니다");
            }

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageDownloadUrlGenerationException("이미지 업로드에 실패하였습니다. ");
        }
    }

    private String generatePresignedGetUrl(String key) {
        try {
            // UTC 시간대를 명시적으로 사용
            java.util.Calendar cal = java.util.Calendar.getInstance(
                java.util.TimeZone.getTimeZone("UTC"));
            cal.add(java.util.Calendar.HOUR, 10); // 10시간 후로 설정
            Date expiration = cal.getTime();

            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket,
                key).withMethod(HttpMethod.GET).withExpiration(expiration);

            return amazonS3.generatePresignedUrl(request).toString();
        } catch (Exception e) {
            throw new PresignedUrlGenerationException("Presigned URL 생성에 실패하였습니다.");
        }
    }

    // 버킷 설정 검증
    private void validateBucketConfiguration() {
        if (bucket == null || bucket.trim().isEmpty()) {
            throw new IllegalStateException("S3 버킷 설정이 올바르지 않습니다.");
        }
    }
} 