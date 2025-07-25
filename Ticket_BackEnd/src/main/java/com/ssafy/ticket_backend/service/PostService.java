package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;

public interface PostService {

    boolean createPost(PostRequest postRequest);

    PostDetailResponse getPostDetail(Long postId);

    boolean deletePost(Long postId);

    boolean updatePost(PostUpdateRequest postUpdateRequest);

}