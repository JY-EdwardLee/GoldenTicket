package com.ssafy.ticket_backend.dto.response;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostAllResponse {

    private Long postId;
    private String boardId;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int viewCount;
    private String title;
    private String content;
    private String imageUrl;
    private int likeCount;
    private boolean isDelete;

    // 사용자 닉네임
    private String nickname;

}
