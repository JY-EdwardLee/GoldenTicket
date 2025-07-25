package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    public boolean createComment(CommentRequest commentRequest) {

        // TODO security 수정예정
        commentRequest.setUserId(1);

        int result = commentMapper.insertComment(commentRequest);

        if (result != 1) {
            throw new DatabaseOperationException("댓글 DB저장 중 오류");
        }

        return true;
    }

    public boolean deleteComment(Long commentId) {
        int result = commentMapper.deleteComment(commentId);
        if (result != 1) {
            throw new DatabaseOperationException("댓글 DB삭제 중 오류");
        }
        return true;
    }

    public boolean updateComment(CommentUpdateRequest commentUpdateRequest) {
        int result = commentMapper.updateComment(commentUpdateRequest);
        if (result != 1) {
            throw new DatabaseOperationException("댓글 DB수정 중 오류");
        }

        return true;
    }

    public List<CommentDetailResponse> getComments(Long post_id) {
        List<CommentDetailResponse> result = commentMapper.getComments(post_id);
        return result;
    }


}