package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.request.CommentUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentResponse;
import com.ssafy.ticket_backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     * @param commentRequest
     * @return
     */
    @PostMapping()
    public ResponseEntity<CommentResponse> createPosts(@RequestBody CommentRequest commentRequest) {
        commentService.createComment(commentRequest);

        return ResponseEntity.ok(new CommentResponse(true, "댓글 작성 성공"));
    }

    /**
     * 댓글 삭제
     *
     * @param commentId
     * @return
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommentResponse> deleteComments(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new CommentResponse(true, "댓글 삭제 성공"));
    }

    /**
     * 댓글 수정
     *
     * @param commentUpdateRequest
     * @return
     */
    @PatchMapping()
    public ResponseEntity<CommentResponse> editComments(
        @RequestBody CommentUpdateRequest commentUpdateRequest) {
        commentService.updateComment(commentUpdateRequest);

        return ResponseEntity.ok(new CommentResponse(true, "댓글 수정 성공"));
    }
}