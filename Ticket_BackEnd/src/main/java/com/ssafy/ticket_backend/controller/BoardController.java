package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.model.BoardType;
import com.ssafy.ticket_backend.service.BoardService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    /**
     * 게시판별 게시글 조회
     *
     * @param boardType
     * @return List<PostAllResponse>
     */
    @GetMapping("category/{boardType}")
    public ResponseEntity<List<PostAllResponse>> getPostsByCategory(
        @PathVariable BoardType boardType) {
        List<PostAllResponse> list = boardService.getPostsByCategory(boardType);

        return ResponseEntity.status(HttpStatus.OK).body(list);
    }

    /**
     * 게시판별 게시글 검색
     * TODO 로직 수정 필요
     *
     * @param type
     * @return List<PostAllResponse>
     */
    @GetMapping("/{type}")
    public ResponseEntity<List<PostAllResponse>> searchPosts(@PathVariable String type,
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String content,
        @RequestParam(required = false) String writer) {
        List<PostAllResponse> list = boardService.searchPosts(type, title, content, writer);
        
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }
}
