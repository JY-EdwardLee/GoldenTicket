package com.ssafy.ticket_backend.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotRequest {

    private Long userId;
    private String question;
    private boolean isLogined;
}
