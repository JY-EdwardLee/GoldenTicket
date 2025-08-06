package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotResponse {
    private String answer;
    // 만약 유저가 응모하고 싶다고 하면 응모 페이지 링크를 주는 목적
    private String link; // null 이면 링크 없음
}
