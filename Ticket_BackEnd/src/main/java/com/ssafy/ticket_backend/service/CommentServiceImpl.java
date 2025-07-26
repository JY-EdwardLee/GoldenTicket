package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import com.ssafy.ticket_backend.exception.CommentCreateFailException;
import com.ssafy.ticket_backend.exception.CommentDeleteFailException;
import com.ssafy.ticket_backend.exception.CommentUpdateFailException;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.model.UserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final UserMapper userMapper;
    private final CommentMapper commentMapper;

    /**
     * 댓글 작성
     *
     * @param email          작성자 정보
     * @param commentRequest 댓글 내용
     * @return true
     */
    @Transactional
    @Override
    public boolean createComment(String email, CommentRequest commentRequest) {
        User user = userMapper.selectUserByEmail(email);

        if (user.getIsBlock()) {
            throw new CommentCreateFailException("차단된 사용자는 댓글을 작성할 수 없습니다.");
        }

        commentRequest.setUserId(user.getUserId());

        int result = commentMapper.insertComment(commentRequest);
        if (result != 1) {
            throw new DatabaseOperationException("댓글 저장 중 데이터베이스 오류가 발생했습니다");
        }

        return true;
    }


    /**
     * 댓글목록 가져오기
     *
     * @param postId 게시물 기본키
     * @return List<CommentDetailResponse> 댓글목록
     */
    @Override
    public List<CommentDetailResponse> getComments(Long postId) {
        List<CommentDetailResponse> result = commentMapper.getComments(postId);
        return result;
    }

    /**
     * 댓글 삭제
     *
     * @param email     작성자 정보
     * @param commentId 댓글 기본키
     * @return true
     */
    @Transactional
    @Override
    public boolean deleteComment(String email, Long commentId) {
        User user = userMapper.selectUserByEmail(email);

        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            if (user.getUserId() != commentMapper.selectUserIdByCommentId(commentId)) {
                throw new CommentDeleteFailException("권한이 없습니다.");
            }
        }

        int result = commentMapper.deleteComment(commentId);
        if (result != 1) {
            throw new DatabaseOperationException("댓글 DB삭제 중 오류가 발생하였습니다.");
        }
        return true;
    }

    /**
     * 댓글 수정
     *
     * @param email                작성자 정보
     * @param commentUpdateRequest 수정 내용
     * @return true
     */
    @Transactional
    @Override
    public boolean updateComment(String email, CommentUpdateRequest commentUpdateRequest) {
        User user = userMapper.selectUserByEmail(email);

        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            if (user.getUserId() != commentMapper.selectUserIdByCommentId(
                commentUpdateRequest.getCommentId())) {
                throw new CommentUpdateFailException("권한이 없습니다.");
            }
        }

        int result = commentMapper.updateComment(commentUpdateRequest);
        if (result != 1) {
            throw new DatabaseOperationException("댓글 DB수정 중 오류가 발생하였습니다.");
        }
        return true;
    }


}