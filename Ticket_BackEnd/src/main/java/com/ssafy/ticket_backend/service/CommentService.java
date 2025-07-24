package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;

public interface CommentService {

    public boolean createComment(CommentRequest commentRequest);

}




