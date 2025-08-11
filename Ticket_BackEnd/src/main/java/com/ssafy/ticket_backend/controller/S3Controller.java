package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.S3DownloadRequest;
import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3DownloadResponse;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.S3Type;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.handler.service.CustomUserDetails;
import com.ssafy.ticket_backend.handler.service.S3PostService;
import com.ssafy.ticket_backend.handler.service.S3UserService;
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
  [마이페이지 업로드]
  front 업로드 url 요청 -> Front에서 S3 업로드 및 덮어쓰기 -> Save로직 DB 저장->
  보여주기용도 Download 메서드 사용 or 서비스 단에서 로직처리

  [게시물 로직]
  1. 게시물 (수정, 업로드) 시  getPresignedUploadUrl
  게시물 첫 등록 시 임시 RefId 값 전송 -> postService단에서 수정
  게시물 수정 시 덮어쓰기 위해 PUT요청을 위한 KEY, URL 발급 -> Front에서 수정시 덮어쓰기 작업
  게시물 보여주기용도 PostService단에서 Download 메서드 이용해 URL 발급

  캐싱문제는 front단에서 쿼리로 해결
   */

    /**
     * 이미지 업로드, 수정을 위한 임시 Presigned URL요청
     *
     * @param type     UserProfile or PostImage
     * @param refId    userId or postId
     * @param fileName 원본파일 이름
     * @return S3UploadUrlResponse
     */
    @GetMapping("/upload-url")
    public ResponseEntity<S3UploadUrlResponse> getPresignedUploadUrl(
        @AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam S3Type type,
        @RequestParam Long refId, @RequestParam String fileName) {

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
     * Key값 데이터베이스 저장요청 front에서 S3 업로드 성공 후 요청 사용x
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
        @RequestBody S3DownloadRequest s3DownloadRequest) {
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
