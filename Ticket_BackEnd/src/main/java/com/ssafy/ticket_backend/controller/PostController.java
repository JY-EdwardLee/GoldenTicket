package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostResponse;
import com.ssafy.ticket_backend.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    /**
     * 게시글 작성 함수
     *
     * @param postRequest
     * @return
     */
    @PostMapping("")
    public ResponseEntity<PostResponse> createPosts(@RequestBody PostRequest postRequest) {
        // TODO postService.createPost();

        return ResponseEntity.ok(new PostResponse(true, "게시글 작성 성공"));
    }

    /**
     * 게시글 상세보기
     *
     * @param postId
     * @return
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        //TODO
        PostDetailResponse postDetailResponse = null;

        return ResponseEntity.ok(postDetailResponse);
    }

    /**
     * 게시글 수정
     *
     * @param postId
     * @return
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> editPosts(@PathVariable Long postId) {
        // TODO

        return ResponseEntity.ok(new PostResponse(true, "게시글 수정 성공"));
    }

    /**
     * 게시글 삭제
     *
     * @param postId
     * @return
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResponse> deletePost(@PathVariable Long postId) {
        // TODO
        return ResponseEntity.ok(new PostResponse(true, "게시글 삭제 성공"));
    }
}