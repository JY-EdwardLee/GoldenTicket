package com.ssafy.ticket_backend.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDetailResponse {

    private Long commentId;
    private Long postId;
    private Long userId;
    private String content;
    private int likeCount;
    private boolean isDelete;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
