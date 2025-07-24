package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {

    int insertPost(PostRequest postRequest);

    PostDetailResponse findPostById(Long postId);

//    List<PostDetailResponse> findAllPosts();
//
//    void updatePost(PostRequest postRequest);
//
//    void deletePost(int postId);
}