package com.ssafy.ticket_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    int userId;      // 시큐리티를 통해서 값 가져올 예정. (HTTP 요청시 정보 포함X)
    Long postId;     // 댓글이 달릴 게시글 ID
    String content;  // 댓글 내용
}