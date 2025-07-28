package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;

public interface PostService {

    // 게시글 작성
    boolean createPost(String email, PostRequest postRequest);

    // 게시글 상세보기
    PostDetailResponse getPostDetail(Long postId);

    // 게시글 수정
    boolean updatePost(String email, PostUpdateRequest postUpdateRequest);

    // 게시글 삭제
    boolean deletePost(String email, Long postId);

    void likePost(String email, Long postId);
}