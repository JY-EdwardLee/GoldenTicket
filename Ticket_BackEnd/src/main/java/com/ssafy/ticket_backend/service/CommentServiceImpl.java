package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    public boolean createComment(CommentRequest commentRequest) {
        // security
        commentRequest.setUserId(1);

        try {

        } catch (Exception e) {

        }
        commentRequest.setUserId(1);

        commentMapper.insertComment(commentRequest);

        return true;
    }

}
