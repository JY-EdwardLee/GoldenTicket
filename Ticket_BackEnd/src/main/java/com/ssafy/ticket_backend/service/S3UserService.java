package com.ssafy.ticket_backend.handler.service;

import com.ssafy.ticket_backend.dto.request.S3DownloadRequest;
import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3DownloadResponse;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;

public interface S3UserService {

    // 1. Presigned Upload URL 생성 (UserProfile 전용)
    S3UploadUrlResponse generatePresignedUploadUrl(Long refId, String fileName);

    // 2. 업로드 키 저장 (S3 업로드 성공 이후) - UserProfile 전용
    S3SaveResponse saveUploadKey(S3SaverRequest request);

    // Presigned Download URL 생성 - UserProfile 전용
    S3DownloadResponse getImageUrlsByTypeAndRefId(S3DownloadRequest request);
} 