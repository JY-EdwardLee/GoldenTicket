package com.ssafy.ticket_backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDetailResponse {

    private Long id;            // 게시글 ID (PK)
    private String boardId;     // 게시판 종류 (FREE, NOTICE, 등)
    private Long userId;        // 작성자 ID

    private LocalDateTime createdAt;   // 생성일
    private LocalDateTime updatedAt;   // 수정일

    private int viewCount;     // 조회수
    private String title;      // 제목
    private String content;    // 내용
    private String imageUrl;   // 이미지 URL

    private int likeCount;     // 좋아요 수
    private boolean isDelete;  // 삭제 여부

    List<CommentDetailResponse> comments;  // 댓글 목록

    // TODO UserResponse user;  // 작성자

}
