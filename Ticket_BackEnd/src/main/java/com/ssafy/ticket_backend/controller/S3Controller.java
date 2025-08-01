package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.S3DownloadRequest;
import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3DownloadResponse;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.S3Type;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.S3PostService;
import com.ssafy.ticket_backend.service.S3UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/s3")
public class S3Controller {

  private final S3UserService s3UserService;
  private final S3PostService s3PostService;
  private final UserMapper userMapper;

  /*
  [정리]
  [업로드]
  front 업로드 url 요청 ->
  back 키 발급과 url 발급 ->
  front url통해서 s3에 업로드 -> front 업로드 성공시 key를 재전송 -> back db에 저장


 [게시물 로직]
  게시물 최초 입력시 -> post_id 기본키가 없다.
  (front)임시 refId값 만들어서 전송 ->  (back) 키,url 발급 -> front(s3 업로드 이후 키 전송)
  -> 서버단에서 postId 생성이후 해당 postId로 수정 후 db에 키값 정상저장.

  게시물 업데이트 시
  user 로직 그대로 활용해도 될듯

  [조회]
  타입과 id를 통해 key db에서 조회 -> 조회한걸로 url 발급 -> front에서 이미지 보기 끝.
   */


  /**
   * 이미지 업로드 수정을 위한 임시 Presigned URL요청
   *
   * @param type     UserProfile or PostImage
   * @param refId    userId or postId
   * @param fileName 원본파일 이름
   * @return S3UploadUrlResponse
   */
  @GetMapping("/upload-url")
  public ResponseEntity<S3UploadUrlResponse> getPresignedUploadUrl(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestParam S3Type type,
      @RequestParam Long refId,
      @RequestParam String fileName
  ) {

    S3UploadUrlResponse response;

    // [마이페이지 CASE] => refId 값이 -1 이라면 마이페이지 -> 시큐리티에서 값 빼와
    if (S3Type.UserProfile.equals(type) && refId == -1) {
      String email = userDetails.getUsername();
      User user = userMapper.selectUserByEmail(email);
      refId = user.getUserId();
    }

    if (S3Type.UserProfile.equals(type)) {
      response = s3UserService.generatePresignedUploadUrl(refId, fileName);
    } else if (S3Type.PostImage.equals(type)) {
      response = s3PostService.generatePresignedUploadUrl(refId, fileName);
    } else {
      throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다: " + type);
    }

    return ResponseEntity.ok(response);
  }

  /**
   * Key값 데이터베이스 저장요청 front에서 S3 업로드 성공 후 요청
   *
   * @param s3SaveRequest 키값 업로드 정보
   * @return S3SaveResponse
   */
  @PostMapping("/save-key")
  public ResponseEntity<S3SaveResponse> saveUploadKey(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody S3SaverRequest s3SaveRequest) {
    S3SaveResponse response;

    // [마이페이지 CASE] => refId 값이 -1 이라면 마이페이지 -> 시큐리티에서 값 빼와
    if (S3Type.UserProfile.equals(s3SaveRequest.getType()) && s3SaveRequest.getRefId() == -1) {
      String email = userDetails.getUsername();
      User user = userMapper.selectUserByEmail(email);
      s3SaveRequest.setRefId(user.getUserId());
    }

    if (S3Type.UserProfile.equals(s3SaveRequest.getType())) {
      response = s3UserService.saveUploadKey(s3SaveRequest);
    } else if (S3Type.PostImage.equals(s3SaveRequest.getType())) {
      // 게시글 업데이트 시에만 사용한다.
      response = s3PostService.saveUploadKey(s3SaveRequest);
    } else {
      throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다: " + s3SaveRequest.getType());
    }

    return ResponseEntity.ok(response);
  }

  /**
   * 조회 방식 [마이페이지] 요청 uerId -> -1로 고정 (시큐리티에서 값 뺴올 예정)
   *
   * @param s3DownloadRequest 키 조회정보
   * @return S3DownloadResponse  응답 메세지(URL 포함)
   */
  @PostMapping("/download")
  public ResponseEntity<S3DownloadResponse> getImageUrls(
      @AuthenticationPrincipal CustomUserDetails userDetails,
      @RequestBody S3DownloadRequest s3DownloadRequest
  ) {
    S3DownloadResponse s3DownloadResponse;

    // [마이페이지]
    // s3DownloadRequest.getRefId() 값 -1이면 시큐리티에서 값 빼와.
    if (S3Type.UserProfile.equals(s3DownloadRequest.getType())
        && s3DownloadRequest.getRefId() == -1) {
      String email = userDetails.getUsername();
      User user = userMapper.selectUserByEmail(email);
      s3DownloadRequest.setRefId(user.getUserId());
    }

    if (S3Type.UserProfile.equals(s3DownloadRequest.getType())) {
      s3DownloadResponse = s3UserService.getImageUrlsByTypeAndRefId(s3DownloadRequest);
    } else if (S3Type.PostImage.equals(s3DownloadRequest.getType())) {
      s3DownloadResponse = s3PostService.getImageUrlsByTypeAndRefId(s3DownloadRequest);
    } else {
      throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다: " + s3DownloadRequest.getType());
    }

    return ResponseEntity.ok(s3DownloadResponse);
  }

}
