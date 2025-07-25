package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {

    int insertComment(CommentRequest commentRequest);

    int deleteComment(Long commentId);

    int updateComment(CommentUpdateRequest commentUpdateRequest);

    List<CommentDetailResponse> getComments(Long post_id);

}
