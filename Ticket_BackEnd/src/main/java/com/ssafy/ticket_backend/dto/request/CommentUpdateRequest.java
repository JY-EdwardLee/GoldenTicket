package com.ssafy.ticket_backend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentUpdateRequest {

    Long commentId;
    String content;
}
