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

    private String question;
    private String reply;
    private String link;      // 답변 관련 링크 (없으면 null)
    private LocalDateTime timestamp;
}
