package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
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
        postService.createPost(postRequest);

        return ResponseEntity.ok(new PostResponse(true, "게시글 작성 성공"));
    }

    /**
     * 게시글 상세보기
     *
     * @param postId
     * @return PostDetailResponse
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        PostDetailResponse postDetailResponse = postService.getPostDetail(postId);
        return ResponseEntity.ok(postDetailResponse);
    }

    /**
     * 게시글 수정
     *
     * @param postUpdateRequest
     * @return
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> editPosts(
        @RequestBody PostUpdateRequest postUpdateRequest) {
        postService.updatePost(postUpdateRequest);

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
        postService.deletePost(postId);

        return ResponseEntity.ok(new PostResponse(true, "게시글 삭제 성공"));
    }

    /**
     * 좋아요 누르기
     *
     * @param postId
     * @return
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostResponse> post(@PathVariable Long postId) {
        // 사용자 정보 시큐리티로 가져오기.

        return ResponseEntity.ok(new PostResponse(true, "좋아요 누르기 성공"));
    }


}