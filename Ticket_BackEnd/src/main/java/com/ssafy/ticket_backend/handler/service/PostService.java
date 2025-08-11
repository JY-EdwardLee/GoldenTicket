package com.ssafy.ticket_backend.handler.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostLikeResponse;

public interface PostService {

    // 게시글 작성
    void createPost(String email, PostRequest postRequest);

    // 게시글 상세보기
    PostDetailResponse getPostDetail(Long postId);

    // 게시글 수정
    void updatePost(String email, PostUpdateRequest postUpdateRequest);

    // 게시글 삭제
    void deletePost(String email, Long postId);

    PostLikeResponse likePost(String email, Long postId);
}