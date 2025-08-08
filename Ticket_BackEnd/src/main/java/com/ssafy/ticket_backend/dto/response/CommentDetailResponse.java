package com.ssafy.ticket_backend.dto.response;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentDetailResponse {

    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private int likeCount;
    private boolean isDelete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String nickName;
    
    // 댓글 작성자 이미지 추가
    private String commentUserUrl;
}
