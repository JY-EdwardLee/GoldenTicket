package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.S3DownloadRequest;
import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3DownloadResponse;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;

public interface S3UserService {

    S3UploadUrlResponse generatePresignedUploadUrl(Long refId, String fileName);

    S3SaveResponse saveUploadKey(S3SaverRequest request);

    S3DownloadResponse getImageUrlsByTypeAndRefId(S3DownloadRequest request);
} 