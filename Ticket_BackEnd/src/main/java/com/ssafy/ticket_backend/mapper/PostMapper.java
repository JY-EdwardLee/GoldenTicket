package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostMapper {

    int insertPost(PostRequest postRequest);

    PostDetailResponse selectPostById(Long postId);

    int plusView(String post_id);

    int deletePost(Long postId);

    int updatePost(PostUpdateRequest postUpdateRequest);

    boolean selectLike(Long userId, Long postId);

    void plusLike(@Param("{postId}") Long postId);

    void insertPostLike(@Param("{userId}") Long userId, @Param("{postId}") Long postId);
}