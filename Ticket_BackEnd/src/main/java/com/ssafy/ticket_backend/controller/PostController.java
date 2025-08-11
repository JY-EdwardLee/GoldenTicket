package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostLikeResponse;
import com.ssafy.ticket_backend.dto.response.PostResponse;
import com.ssafy.ticket_backend.handler.service.CustomUserDetails;
import com.ssafy.ticket_backend.handler.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
     * 게시글 작성
     *
     * @param userDetails 작성자 정보
     * @param postRequest 게시글 내용
     * @return 성공/실패 메세지
     */
    @PostMapping("")
    public ResponseEntity<PostResponse> createPosts(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody PostRequest postRequest) {
        postService.createPost(userDetails.getUsername(), postRequest);

        return ResponseEntity.ok(new PostResponse(true, "게시글 작성 성공"));
    }

    /**
     * 게시글 상세보기
     *
     * @param postId 게시글 기본키
     * @return PostDetailResponse 게시글 내용
     */
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        PostDetailResponse postDetailResponse = postService.getPostDetail(postId);

        return ResponseEntity.ok(postDetailResponse);
    }

    /**
     * 게시글 수정
     *
     * @param userDetails       수정을 시도하려는 사람의 정보
     * @param postUpdateRequest 수정 내용
     * @return 성공/실패 메세지
     */
    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePosts(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody PostUpdateRequest postUpdateRequest) {
        postService.updatePost(userDetails.getUsername(), postUpdateRequest);

        return ResponseEntity.ok(new PostResponse(true, "게시글 수정 성공"));
    }

    /**
     * 게시글 삭제
     *
     * @param userDetails 삭제를 시도하려는 사람의 정보
     * @param postId      삭제하려는 게시글의 ID
     * @return 성공/실패 메세지
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<PostResponse> deletePost(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        postService.deletePost(userDetails.getUsername(), postId);

        return ResponseEntity.ok(new PostResponse(true, "게시글 삭제 성공"));
    }

    /**
     * 좋아요 누르기
     *
     * @param userDetails 좋아요를 누른 사람
     * @param postId      게시글 기본키
     * @return 해당 게시물 좋아요 수 반환
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<PostLikeResponse> post(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long postId) {
        PostLikeResponse postLikeResponse = postService.likePost(userDetails.getUsername(), postId);

        return ResponseEntity.ok(postLikeResponse);
    }
}