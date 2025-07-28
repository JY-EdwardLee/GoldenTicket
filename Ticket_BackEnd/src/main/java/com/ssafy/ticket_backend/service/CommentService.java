package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import java.util.List;

public interface CommentService {

    void createComment(String email, CommentRequest commentRequest);

    void deleteComment(String email, Long commentId);

    void updateComment(String email, Long commentId, CommentUpdateRequest commentUpdateRequest);

    List<CommentDetailResponse> getComments(Long post_id);

    void likeComment(String email, Long commentId);
}