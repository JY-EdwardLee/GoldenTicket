package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.CommentRequest;
import com.ssafy.ticket_backend.dto.response.CommentResponse;
import com.ssafy.ticket_backend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}