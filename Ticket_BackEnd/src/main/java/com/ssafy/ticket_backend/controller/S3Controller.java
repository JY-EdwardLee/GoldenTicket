package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;
import com.ssafy.ticket_backend.service.S3Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/s3")
public class S3Controller {

  private final S3Service s3Service;

  public S3Controller(S3Service s3Service) {
    this.s3Service = s3Service;
  }

  @GetMapping("/upload-url")
  public ResponseEntity<S3UploadUrlResponse> getPresignedUploadUrl(
      @RequestParam String type,
      @RequestParam Long refId,
      @RequestParam String fileName
  ) {
    S3UploadUrlResponse response = s3Service.generatePresignedUploadUrl(type, refId, fileName);

    return ResponseEntity.ok(response);
  }

  @PostMapping("/save-key")
  public ResponseEntity<S3SaveResponse> saveUploadKey(
      @RequestBody S3SaverRequest request) {
    S3SaveResponse response = s3Service.saveUploadKey(request);

    return ResponseEntity.ok(response);
  }

//  @GetMapping("/download")
//  public ResponseEntity<Map<String, Object>> getImageUrls(
//      @RequestParam String type,
//      @RequestParam Long refId
//  ) {
//    Map<String, Object> response = s3Service.getImageUrlsByTypeAndRefId(type, refId);
//    
//    return ResponseEntity.ok(response);
//  }
}
