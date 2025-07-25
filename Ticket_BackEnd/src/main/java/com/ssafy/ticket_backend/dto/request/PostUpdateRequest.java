package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.BoardType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostUpdateRequest {

    Long postId;
    String title;
    String content;
    BoardType boardType;
    String imageUrl;
}
