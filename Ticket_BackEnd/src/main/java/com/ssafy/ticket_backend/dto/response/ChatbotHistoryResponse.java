package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.ChatbotSender;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotHistoryResponse {

    private ChatbotSender sender; // USER or BOT
    private String message;
    private LocalDateTime timestamp;
}
