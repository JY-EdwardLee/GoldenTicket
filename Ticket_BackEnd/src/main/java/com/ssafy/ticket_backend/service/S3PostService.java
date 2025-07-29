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
import com.ssafy.ticket_backend.model.S3Type;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3PostService {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;
  // TODO: PostMapper 추가 필요
  // private final PostMapper postMapper;

  public S3PostService(AmazonS3 amazonS3) {
    this.amazonS3 = amazonS3;
    // TODO: PostMapper 주입 추가
    // this.postMapper = postMapper;
  }

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

      // TODO: 기존 key가 있는지 확인 (PostMapper 구현 후)
      // existingKey = postMapper.getPostImageKey(refId);
      // if (existingKey != null && !existingKey.isEmpty()) {
      //   isUpdate = true;
      //   System.out.println("기존 이미지 업데이트 모드 - 기존 key: " + existingKey);
      // } else {
      //   System.out.println("새로운 이미지 업로드 모드");
      // }

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

  // 2. 업로드 키 저장 (S3 업로드 성공 이후) - PostImage 전용
  public S3SaveResponse saveUploadKey(S3SaverRequest request) {
    try {
      // TODO: 기존 key가 있는지 확인하여 업데이트 여부 판단 (PostMapper 구현 후)
      boolean isUpdate = false;
      String existingKey = null;
      
      // existingKey = postMapper.getPostImageKey(request.getRefId());
      // isUpdate = (existingKey != null && !existingKey.isEmpty());

      // TODO: DB에 업로드 정보 저장 (PostMapper 구현 후)
      // saveUploadInfo(request.getKey(), request.getRefId());

      // TODO: 업데이트인 경우 기존 S3 객체 삭제
      // if (isUpdate && existingKey != null) {
      //   try {
      //     amazonS3.deleteObject(bucket, existingKey);
      //     System.out.println("기존 S3 객체 삭제 완료: " + existingKey);
      //   } catch (Exception e) {
      //     System.out.println("기존 S3 객체 삭제 실패 (무시): " + e.getMessage());
      //   }
      // }

      S3SaveResponse response = new S3SaveResponse();
      response.setSuccess(false);
      response.setMessage("게시글 이미지 업로드 기능은 아직 구현되지 않았습니다.");

      return response;
    } catch (Exception e) {
      S3SaveResponse errorResponse = new S3SaveResponse();
      errorResponse.setSuccess(false);
      errorResponse.setMessage("S3 키 저장에 실패했습니다: " + e.getMessage());
      return errorResponse;
    }
  }

  // TODO: DB에 업로드 정보 저장 - PostImage 전용 (PostMapper 구현 후)
  // private void saveUploadInfo(String key, Long refId) {
  //   postMapper.updatePostImage(refId, key);
  // }

  // Presigned Download URL 생성 - PostImage 전용
  public S3DownloadResponse getImageUrlsByTypeAndRefId(S3DownloadRequest request) {
    try {
      S3DownloadResponse response = new S3DownloadResponse();

      // TODO: 해당 게시글 키 조회 (PostMapper 구현 후)
      // String postImageKey = postMapper.getPostImageKey(request.getRefId());
      // System.out.println("DB에서 키를 조회하겠습니다.: " + postImageKey);

      // if (postImageKey != null && !postImageKey.isEmpty()) {
      //   String downloadUrl = generatePresignedGetUrl(postImageKey);
      //   response.setDownloadUrl(downloadUrl);
      //   response.setMessage("게시글 이미지 URL 생성 성공");
      // } else {
      //   response.setDownloadUrl(null);
      //   response.setMessage("게시글 이미지가 없습니다");
      // }

      response.setDownloadUrl(null);
      response.setMessage("게시글 이미지 조회 기능은 아직 구현되지 않았습니다");

      return response;
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
} 