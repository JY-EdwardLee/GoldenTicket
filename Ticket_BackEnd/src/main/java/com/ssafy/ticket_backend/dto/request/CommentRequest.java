package com.ssafy.ticket_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {

    Long userId;
    Long postId;
    String content;
}