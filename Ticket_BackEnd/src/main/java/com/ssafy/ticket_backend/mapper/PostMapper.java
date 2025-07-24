package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper {
    void insertPost(PostRequest postRequest);
    List<PostDetailResponse> findAllPosts();
    PostDetailResponse findPostById(int postId);
    void updatePost(PostRequest postRequest);
    void deletePost(int postId);
}