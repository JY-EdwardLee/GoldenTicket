package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentLikeResponse;
import com.ssafy.ticket_backend.dto.response.CommentResponse;
import com.ssafy.ticket_backend.handler.service.CommentService;
import com.ssafy.ticket_backend.handler.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     *
     * @param userDetails    작성자 정보
     * @param commentRequest 댓글 내용
     * @return 성공/실패 메세지
     */
    @PostMapping("")
    public ResponseEntity<CommentResponse> createComment(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody CommentRequest commentRequest) {
        commentService.createComment(userDetails.getUsername(), commentRequest);

        return ResponseEntity.ok(new CommentResponse(true, "댓글 작성 성공"));
    }

    /**
     * 댓글 삭제
     *
     * @param userDetails 작성자 정보
     * @param commentId   댓글 기본키
     * @return 성공/실패 메세지
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponse> deleteComment(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentId) {
        commentService.deleteComment(userDetails.getUsername(), commentId);

        return ResponseEntity.ok(new CommentResponse(true, "댓글 삭제 성공"));
    }

    /**
     * 댓글 수정
     *
     * @param userDetails          작성자 정보
     * @param commentUpdateRequest 수정 내용
     * @return 성공/실패 메세지
     */
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> editComments(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentId,
        @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService.updateComment(userDetails.getUsername(), commentId, commentUpdateRequest);

        return ResponseEntity.ok(new CommentResponse(true, "댓글 수정 성공"));
    }

    /**
     * 댓글 좋아요
     *
     * @param commentId 댓글 기본키
     * @return CommentLikeResponse
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<CommentLikeResponse> addLike(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long commentId) {
        CommentLikeResponse commentLikeResponse = commentService.likeComment(
            userDetails.getUsername(), commentId);
        return ResponseEntity.ok(commentLikeResponse);
    }
}