package com.ssafy.ticket_backend.handler.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.ssafy.ticket_backend.dto.request.S3DownloadRequest;
import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3DownloadResponse;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;
import com.ssafy.ticket_backend.exception.PresignedUrlGenerationException;
import com.ssafy.ticket_backend.exception.SaveUploadKeyException;
import com.ssafy.ticket_backend.mapper.PostMapper;
import com.ssafy.ticket_backend.model.S3Type;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class S3PostServiceImpl implements S3PostService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final PostMapper postMapper;

    /**
     * 게시글 이미지 업로드 위한 key,url 발급 수정 시에는 기존 키를 재사용하고, 신규 등록 시에는 새 키 생성 (임시 키)
     *
     * @param refId    postId
     * @param fileName 원본파일 이름
     * @return S3UploadUrlResponse
     */
    @Override
    public S3UploadUrlResponse generatePresignedUploadUrl(Long refId, String fileName) {
        try {
            // 파라미터 검증
            if (refId == null || refId <= 0) {
                throw new IllegalArgumentException("유효하지 않은 참조 ID입니다.");
            }

            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("파일명이 비어있습니다.");
            }

            String key;
            String existingKey = null;
            boolean isUpdate = false;

            try {

                // 게시물 등록시에는 catch에 걸림 postId가 없음
                existingKey = postMapper.getPostImageKey(refId);
            } catch (Exception e) {
                // 임시 ID인 경우 무시하고 새 키 생성
                existingKey = null;

            }

            if (existingKey != null && !existingKey.trim().isEmpty()) {
                // 기존 이미지가 있는 경우 (수정 시) - 기존 키 재사용
                key = existingKey;
                isUpdate = true;

            } else {
                // 기존 이미지가 없는 경우 (신규 등록 시) - 새 키 생성
                key = generateKey(S3Type.PostImage.name(), refId, fileName);
                isUpdate = false;

            }

            String url = generatePresignedPutUrl(key);

            S3UploadUrlResponse response = new S3UploadUrlResponse();
            response.setKey(key);
            response.setPresignedUrl(url);
            response.setUpdate(isUpdate); // 업데이트 여부 추가

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
    }

    /**
     * 사용안함
     *
     * @param s3SaverRequest
     * @return S3SaveResponse
     */
    @Override
    @Transactional
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
                existingKey = postMapper.getPostImageKey(s3SaverRequest.getRefId());
                isUpdate = (existingKey != null && !existingKey.isEmpty());
            } catch (Exception e) {
                throw new SaveUploadKeyException("게시글 정보 조회에 실패하였습니다.");
            }

            // DB에 업로드 정보 저장
            try {
                saveUploadInfo(s3SaverRequest.getKey(), s3SaverRequest.getRefId());
            } catch (Exception e) {
                throw new SaveUploadKeyException("게시글 이미지 정보 저장에 실패하였습니다.");
            }

            S3SaveResponse response = new S3SaveResponse();
            response.setSuccess(true);

            if (isUpdate) {
                response.setMessage("게시글 이미지 업데이트가 성공적으로 완료되었습니다. (덮어쓰기 처리)");
            } else {
                response.setMessage("새로운 게시글 이미지 업로드가 성공적으로 완료되었습니다.");
            }

            return response;
        } catch (IllegalArgumentException | SaveUploadKeyException e) {
            throw e;
        } catch (Exception e) {
            throw new SaveUploadKeyException("게시글 이미지 파일 저장에 실패하였습니다.");
        }
    }

    // DB에 업로드 정보 저장 - PostImage 전용
    private void saveUploadInfo(String key, Long refId) {
        postMapper.updatePostImage(refId, key);
    }

    /**
     * 게시글 이미지 조회용 다운로드 URL 발급 [게시글조회] -> db에서 postId와 type을 이용해서 -> key값 조회 -> url 발급
     *
     * @param request 게시글 ID 정보
     * @return S3DownloadResponse 임시 다운로드 URL
     */
    @Override
    public S3DownloadResponse getImageUrlsByTypeAndRefId(S3DownloadRequest request) {
        try {
            // 파라미터 검증
            if (request == null) {
                throw new IllegalArgumentException("요청 객체가 null입니다.");
            }

            if (request.getRefId() == null || request.getRefId() <= 0) {
                throw new IllegalArgumentException("유효하지 않은 게시글 ID입니다.");
            }

            S3DownloadResponse response = new S3DownloadResponse();

            // 해당 게시글 이미지 키 조회
            String postImageKey;
            try {
                postImageKey = postMapper.getPostImageKey(request.getRefId());
            } catch (Exception e) {
                response.setDownloadUrl(null);
                response.setMessage("게시글 정보 조회에 실패하였습니다.");
                return response;
            }

            if (postImageKey != null && !postImageKey.isEmpty()) {
                // GET용 임시 다운로드 URL 생성
                try {
                    String downloadUrl = generatePresignedGetUrl(postImageKey);
                    response.setDownloadUrl(downloadUrl);
                    response.setMessage("게시글 이미지 URL 생성 성공");

                } catch (Exception e) {
                    response.setDownloadUrl(null);
                    response.setMessage("이미지 URL 생성에 실패하였습니다.");
                    return response;
                }
            } else {
                response.setDownloadUrl(null);
                response.setMessage("게시글 이미지가 없습니다");
            }

            return response;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            S3DownloadResponse errorResponse = new S3DownloadResponse();
            errorResponse.setDownloadUrl(null);
            errorResponse.setMessage("이미지 URL 생성 실패: " + e.getMessage());
            return errorResponse;
        }
    }

    private String generatePresignedGetUrl(String key) {
        // UTC 시간대를 명시적으로 사용
        java.util.Calendar cal = java.util.Calendar.getInstance(
            java.util.TimeZone.getTimeZone("UTC"));
        cal.add(java.util.Calendar.HOUR, 10); // 10시간 후로 설정
        Date expiration = cal.getTime();

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket,
            key).withMethod(HttpMethod.GET).withExpiration(expiration);
        return amazonS3.generatePresignedUrl(request).toString();
    }

    /**
     * 임시 키를 실제 postId로 변경 (게시물 최초 등록 시 postId 적용)
     *
     * @param tempKey      임시 키
     * @param actualPostId 실제 postId
     * @return 변경된 키
     */
    @Override
    public String updateTempKeyToActualKey(String tempKey, Long actualPostId) {
        try {
            if (tempKey == null || tempKey.trim().isEmpty()) {
                return null; // 이미지가 없는 경우
            }

            if (actualPostId == null || actualPostId <= 0) {
                throw new IllegalArgumentException("유효하지 않은 postId입니다.");
            }

            // 임시 키에서 파일명 추출 (PostImage/123456/uuid.jpg -> uuid.jpg)
            String[] keyParts = tempKey.split("/");
            if (keyParts.length < 3) {
                throw new IllegalArgumentException("잘못된 키 형식입니다: " + tempKey);
            }

            String fileName = keyParts[keyParts.length - 1]; // uuid.jpg

            // 새로운 키 생성 (PostImage/actualPostId/uuid.jpg)
            String newKey = S3Type.PostImage.name() + "/" + actualPostId + "/" + fileName;

            // S3에서 파일 복사 (임시 위치 -> 실제 위치)
            amazonS3.copyObject(bucket, tempKey, bucket, newKey);

            // 임시 파일 삭제
            amazonS3.deleteObject(bucket, tempKey);

            return newKey;
        } catch (Exception e) {
            throw new RuntimeException("키 변경에 실패하였습니다: " + e.getMessage());
        }
    }
} 