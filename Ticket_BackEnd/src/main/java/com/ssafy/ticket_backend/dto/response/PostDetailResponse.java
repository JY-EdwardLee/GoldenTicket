package com.ssafy.ticket_backend.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostDetailResponse {

    private Long postId;
    private String boardId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int viewCount;
    private String title;
    private String content;
    private String imageUrl;  // 작성자 프로필 이미지 URL
    private String postImageUrl;  // 게시글 이미지 URL

    private int likeCount;

    private List<CommentDetailResponse> commentList;  // 댓글 목록
    private PostUserResponse postUser;
}
