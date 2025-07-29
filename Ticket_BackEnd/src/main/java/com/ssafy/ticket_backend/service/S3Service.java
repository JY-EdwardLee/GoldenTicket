package com.ssafy.ticket_backend.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;
import com.ssafy.ticket_backend.exception.PresignedUrlGenerationException;
import com.ssafy.ticket_backend.mapper.UserMapper;
import java.util.Date;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class S3Service {

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  private final AmazonS3 amazonS3;
  private final UserMapper userMapper;

  public S3Service(AmazonS3 amazonS3, UserMapper userMapper) {
    this.amazonS3 = amazonS3;
    this.userMapper = userMapper;
  }

  // 1. Presigned Upload URL 생성
  public S3UploadUrlResponse generatePresignedUploadUrl(String type, Long refId, String fileName) {
    try {
      // 파라미터 검증
      if (type == null || type.trim().isEmpty()) {
        throw new IllegalArgumentException("타입이 비어있습니다.");
      }

      if (refId == null || refId <= 0) {
        throw new IllegalArgumentException("유효하지 않은 참조 ID입니다.");
      }

      if (fileName == null || fileName.trim().isEmpty()) {
        throw new IllegalArgumentException("파일명이 비어있습니다.");
      }

      String key = generateKey(type, refId, fileName);
      String url = generatePresignedPutUrl(key);

      S3UploadUrlResponse response = new S3UploadUrlResponse();
      response.setKey(key);
      response.setPresignedUrl(url);

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

  // 2. 업로드 키 저장 (S3 업로드 성공 이후)
  public S3SaveResponse saveUploadKey(S3SaverRequest request) {
    System.out.println("test1");

    try {
      // DB에 업로드 정보 저장
      saveUploadInfo(request.getKey(), request.getType().name(), request.getRefId());

      S3SaveResponse response = new S3SaveResponse();
      response.setMessage("성공메세지");
      response.setKey(request.getKey());

      return response;
    } catch (Exception e) {
      S3SaveResponse errorResponse = new S3SaveResponse();
      errorResponse.setMessage("실패메세지: " + e.getMessage());
      throw new RuntimeException("실패메세지", e);
    }
  }

  // DB에 업로드 정보 저장
  private void saveUploadInfo(String key, String type, Long refId) {
    System.out.println("test2");
    // 1. 사용자 프로필인 경우
    if ("UserProfile".equals(type)) {
      System.out.println("test3");
      userMapper.updateUserProfileImage(refId, key);
    }

    // 2. 게시글 이미지인 경우 -> 이미지 파일 여러개 이건 좀 생각을 해보자... (나중에)
    else if ("PostImage".equals(type)) {
      // TODO: 게시글 이미지 저장 로직 구현
    }
  }

  // Presigned Download URL 생성 (컨트롤러에서 호출)
  // type과 refId로 KEY조회 -> 임시 URL 발급
//  public Map<String, Object> getImageUrlsByTypeAndRefId(String type, Long refId) {
//    try {
//      Map<String, Object> response = new HashMap<>();
//
//      if ("userprofile".equals(type)) {
//        // 사용자 프로필 이미지 조회
//        String profileImageKey = userMapper.getUserProfileImageKey(refId);
//        if (profileImageKey != null && !profileImageKey.isEmpty()) {
//          String downloadUrl = generatePresignedGetUrl(profileImageKey);
//          response.put("imageUrls", new String[]{downloadUrl});
//          response.put("message", "프로필 이미지 URL 생성 성공");
//        } else {
//          response.put("imageUrls", new String[]{});
//          response.put("message", "프로필 이미지가 없습니다");
//        }
//      } else {
//        // 다른 타입의 이미지들 조회 (게시글 이미지 등)
//        // TODO: 게시글 이미지 매퍼 구현 후 추가
//      }
//
//      return response;
//    } catch (Exception e) {
//      Map<String, Object> errorResponse = new HashMap<>();
//      errorResponse.put("error", "이미지 URL 생성 실패: " + e.getMessage());
//      throw new RuntimeException("이미지 URL 생성 실패", e);
//    }
//  }
//
//  private String generatePresignedGetUrl(String key) {
//    Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 3); // 3분
//    GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucket, key)
//        .withMethod(HttpMethod.GET)
//        .withExpiration(expiration);
//    return amazonS3.generatePresignedUrl(request).toString();
//  }


}
