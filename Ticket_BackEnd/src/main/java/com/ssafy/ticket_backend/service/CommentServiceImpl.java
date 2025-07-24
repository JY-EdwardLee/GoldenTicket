package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    public boolean createComment(CommentRequest commentRequest) {
        // security 수정예정
        commentRequest.setUserId(1);

        int result = commentMapper.insertComment(commentRequest);
        if (result != 1) {
            throw new DatabaseOperationException("댓글 DB저장 중 오류");
        }
        return true;
    }


}
