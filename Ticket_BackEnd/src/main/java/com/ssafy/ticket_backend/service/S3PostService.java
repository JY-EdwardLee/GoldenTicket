package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.S3DownloadRequest;
import com.ssafy.ticket_backend.dto.request.S3SaverRequest;
import com.ssafy.ticket_backend.dto.response.S3DownloadResponse;
import com.ssafy.ticket_backend.dto.response.S3SaveResponse;
import com.ssafy.ticket_backend.dto.response.S3UploadUrlResponse;

/**
 * 게시글 이미지 S3 서비스 인터페이스
 */
public interface S3PostService {

    S3UploadUrlResponse generatePresignedUploadUrl(Long refId, String fileName);

    S3SaveResponse saveUploadKey(S3SaverRequest s3SaverRequest);

    S3DownloadResponse getImageUrlsByTypeAndRefId(S3DownloadRequest request);

    String updateTempKeyToActualKey(String tempKey, Long actualPostId);
}