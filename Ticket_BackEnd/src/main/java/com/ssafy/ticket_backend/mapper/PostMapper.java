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

  int plusView(Long post_id);

  Long selectUserIdByPostId(Long postId);

  int deletePost(Long postId);

  int updatePost(PostUpdateRequest postUpdateRequest);

  boolean selectLike(Long userId, Long postId);

  void plusLike(@Param("postId") Long postId);

  void insertPostLike(@Param("userId") Long userId, @Param("postId") Long postId);

  void minusLike(Long postId);

  void deletePostLike(Long userId, Long postId);

  Long selectPostLike(Long postId);

  int deleteTrue(Long postId);
}