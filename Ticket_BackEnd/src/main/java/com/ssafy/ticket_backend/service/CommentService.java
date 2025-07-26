package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import java.util.List;

public interface CommentService {

    public boolean createComment(String email, CommentRequest commentRequest);

    public boolean deleteComment(String email, Long commentId);

    public boolean updateComment(String email, CommentUpdateRequest commentUpdateRequest);

    public List<CommentDetailResponse> getComments(Long post_id);
}