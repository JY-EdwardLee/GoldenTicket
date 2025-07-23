package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostResponse {

    private boolean success;  // 성공 여부
    private String message;   // 결과 메시지
}
