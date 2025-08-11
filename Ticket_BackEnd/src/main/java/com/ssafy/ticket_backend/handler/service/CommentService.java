package com.ssafy.ticket_backend.handler.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentLikeResponse;

public interface CommentService {

    void createComment(String email, CommentRequest commentRequest);

    void deleteComment(String email, Long commentId);

    void updateComment(String email, Long commentId, CommentUpdateRequest commentUpdateRequest);

    CommentLikeResponse likeComment(String email, Long commentId);
}