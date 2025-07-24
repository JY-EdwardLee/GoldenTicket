package com.ssafy.ticket_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    int userId;      // 시큐리티를 통해서 값 가져올 예정.
    Long postId;
    String content;
}