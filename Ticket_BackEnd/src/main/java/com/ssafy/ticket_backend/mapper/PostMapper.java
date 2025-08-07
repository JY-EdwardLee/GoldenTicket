package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PostMapper {

    Long insertPost(PostRequest postRequest); // PostgreSQL에서 생성된 postId를 직접 반환

    // 이미지 URL 업데이트 메서드 추가
    int updatePostImage(@Param("postId") Long postId, @Param("imageUrl") String imageUrl);

    // 게시글 이미지 키 조회 메서드 추가
    String getPostImageKey(@Param("postId") Long postId);

    PostDetailResponse selectPostById(Long postId);

    int plusView(Long post_id);

    Long selectUserIdByPostId(Long postId);

    int updatePost(PostUpdateRequest postUpdateRequest);

    boolean selectLike(Long userId, Long postId);

    void plusLike(@Param("postId") Long postId);

    void insertPostLike(@Param("userId") Long userId, @Param("postId") Long postId);

    void minusLike(Long postId);

    void deletePostLike(Long userId, Long postId);

    Long selectPostLike(Long postId);

    int deleteTrue(Long postId);

    List<PostAllResponse> selectPostsByUserId(Long userId);
}