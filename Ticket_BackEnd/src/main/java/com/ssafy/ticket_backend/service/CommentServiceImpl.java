package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentLikeResponse;
import com.ssafy.ticket_backend.exception.CommentCreateFailException;
import com.ssafy.ticket_backend.exception.CommentDeleteFailException;
import com.ssafy.ticket_backend.exception.CommentUpdateFailException;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
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
     */
    @Transactional
    @Override
    public void createComment(String email, CommentRequest commentRequest) {

        User user = userMapper.selectUserByEmail(email);

        if (user.getIsBlock()) {
            throw new CommentCreateFailException("차단된 사용자는 댓글을 작성할 수 없습니다.");
        }

        commentRequest.setUserId(user.getUserId());

        try {
            int result = commentMapper.insertComment(commentRequest);

            if (result != 1) {
                throw new DatabaseOperationException("댓글 저장 중 오류가 발생했습니다.");
            }
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("존재하지 않는 게시글입니다.");
        } catch (Exception e) {
            throw new CommentCreateFailException("댓글 저장 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 댓글 삭제
     *
     * @param email     작성자 정보
     * @param commentId 댓글 기본키
     */
    @Transactional
    @Override
    public void deleteComment(String email, Long commentId) {

        User user = userMapper.selectUserByEmail(email);

        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            Long result = commentMapper.selectUserIdByCommentId(commentId);
            if (user.getUserId() != result) {
                throw new CommentDeleteFailException("권한이 없습니다.");
            }
        }

        // 댓글 삭제시 true
        int deleteTrue = commentMapper.CommentDeleteTrue(commentId);

        if (deleteTrue != 1) {
            throw new DatabaseOperationException("댓글 삭제 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 댓글 수정
     *
     * @param email                작성자 정보
     * @param commentUpdateRequest 수정 내용
     */
    @Transactional
    @Override
    public void updateComment(String email, Long commentId,
        CommentUpdateRequest commentUpdateRequest) {
        User user = userMapper.selectUserByEmail(email);

        Long result = commentMapper.selectUserIdByCommentId(commentId);

        if (result == null) {
            throw new CommentUpdateFailException("존재하지 않는 댓글입니다.");
        } else if (user.getUserId() != commentMapper.selectUserIdByCommentId(commentId)) {
            throw new CommentUpdateFailException("권한이 없습니다.");
        }

        int updateResult = commentMapper.updateComment(commentId, commentUpdateRequest);

        if (updateResult != 1) {
            throw new DatabaseOperationException("댓글 수정 중 오류가 발생하였습니다.");
        }
    }


    /**
     * 댓글 좋아요
     *
     * @param email     작성자 정보
     * @param commentId 댓글 기본키
     * @return CommentLikeResponse
     */
    @Override
    public CommentLikeResponse likeComment(String email, Long commentId) {
        User user = userMapper.selectUserByEmail(email);

        // 이미 좋아요를 눌렀다면
        if (commentMapper.selectLike(user.getUserId(), commentId)) {
            // 삭제
            commentMapper.minusLike(commentId);
            commentMapper.deletePostLike(user.getUserId(), commentId);
        } else {
            commentMapper.plusLike(commentId);
            commentMapper.insertCommentLike(user.getUserId(), commentId);
        }

        // 정보 가져오기
        Long likeCount = commentMapper.selectCommentLike(commentId);

        return new CommentLikeResponse(commentId, likeCount);
    }
}