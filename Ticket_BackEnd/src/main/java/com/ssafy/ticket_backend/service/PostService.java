package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;

public interface PostService {

    public boolean createPost(PostRequest postRequest);

    PostDetailResponse getPostDetail(Long postId);
}