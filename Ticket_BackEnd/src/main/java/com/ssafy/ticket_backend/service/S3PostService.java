package com.ssafy.ticket_backend.service;

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
public class S3PostService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;
  private final PostMapper postMapper;

  /**
   * 게시글 이미지 업로드 위한 key,url 발급 (key값 임시 값 , postId가 없음.)
   *
   * @param refId    postId 임시 값.
   * @param fileName 원본파일 이름
   * @return S3UploadUrlResponse
   */
  // 1. Presigned Upload URL 생성 (PostImage 전용)
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
      // 업데이트 여부
      boolean isUpdate = false;

      // 항상 새로운 key 생성 (캐싱 문제 해결)
      key = generateKey(S3Type.PostImage.name(), refId, fileName);
      System.out.println("새로운 key 생성: " + key);

      String url = generatePresignedPutUrl(key);

      S3UploadUrlResponse response = new S3UploadUrlResponse();
      response.setKey(key);
      response.setPresignedUrl(url);

      System.out.println("1. URL 발급 성공적 (업데이트: " + isUpdate + ")");
      return response;
    } catch (IllegalArgumentException e) {
      throw e;
    } catch (Exception e) {
      throw new PresignedUrlGenerationException("PresignedUpl 생성에 실패하였습니다.");
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
    Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 5); // 5분
    // 해당 bucket에 key에 대해서 PUT(업로드) 요청 5분간 허용 URL 생성
    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
        .withMethod(HttpMethod.PUT)
        .withExpiration(expiration);
    // 임시 URL 클라이언트에 반환
    return amazonS3.generatePresignedUrl(request).toString();
  }


  /**
   * 게시글 수정 시에만 사용한다. (postId 값 존재하는 경우) 업로드 키 저장 (S3 업로드 성공 이후) - PostImage 전용
   *
   * @param s3SaverRequest
   * @return S3SaveResponse
   */
  // 2. 업로드 키 저장 (S3 업로드 성공 이후) - PostImage 전용
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

      // 업데이트인 경우 기존 S3 객체 삭제
      if (isUpdate && existingKey != null) {
        try {
          amazonS3.deleteObject(bucket, existingKey);   // 기존 값 삭제 for 캐싱문제
          System.out.println("기존 S3 객체 삭제 완료: " + existingKey);
        } catch (Exception e) {
          // S3 삭제 실패는 치명적이지 않으므로 경고만 남기고 계속 진행
          System.out.println("기존 S3 객체 삭제 실패 (무시): " + e.getMessage());
        }
      }

      S3SaveResponse response = new S3SaveResponse();
      response.setSuccess(true);

      if (isUpdate) {
        response.setMessage("게시글 이미지 업데이트가 성공적으로 완료되었습니다. (기존 이미지 삭제됨)");
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
        System.out.println("DB에서 게시글 이미지 키 조회: " + postImageKey);
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
          System.out.println("게시글 이미지 다운로드 URL 생성: " + downloadUrl);
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
    Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 3); // 3분
    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
        .withMethod(HttpMethod.GET)
        .withExpiration(expiration);
    return amazonS3.generatePresignedUrl(request).toString();
  }


  /**
   * 임시 키를 실제 postId로 변경 (게시물 최초 등록 시 사용)
   *
   * @param tempKey      임시 키 (예: PostImage/123456/uuid.jpg)
   * @param actualPostId 실제 postId
   * @return 변경된 키
   */
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

      System.out.println("키 변경 완료: " + tempKey + " -> " + newKey);
      return newKey;
    } catch (Exception e) {
      throw new RuntimeException("키 변경에 실패하였습니다: " + e.getMessage());
    }
  }


}