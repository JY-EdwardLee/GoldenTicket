package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import java.util.List;

public interface CommentService {

    public boolean createComment(CommentRequest commentRequest);

    public boolean deleteComment(Long commentId);

    public boolean updateComment(CommentUpdateRequest commentUpdateRequest);

    public List<CommentDetailResponse> getComments(Long post_id);
}