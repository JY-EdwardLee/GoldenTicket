package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CommentMapper {

    int insertComment(CommentRequest commentRequest);

    int deleteComment(Long commentId);

    int updateComment(@Param("commentId") Long commentId,
        CommentUpdateRequest commentUpdateRequest);

    List<CommentDetailResponse> getComments(Long post_id);

    Long selectUserIdByCommentId(Long postId);

    boolean selectLike(@Param("userId") Long userId, @Param("commentId") Long commentId);

    void plusLike(@Param("commentId") Long commentId);

    void insertCommentLike(@Param("userId") Long userId, @Param("commentId") Long commentId);
}
