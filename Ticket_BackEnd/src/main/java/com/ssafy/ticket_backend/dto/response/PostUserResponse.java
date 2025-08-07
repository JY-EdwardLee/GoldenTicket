package com.ssafy.ticket_backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostUserResponse {

    private Long userId;
    private String nickname;
}
