package com.ssafy.ticket_backend.dto.request;


import java.time.LocalDateTime;
import lombok.Data;

@Data
// 챗봇에 넘길 게임 데이터 담는
public class GameForChatbotRequest {

    private LocalDateTime gameDatetime;
    private String homeTeam;
    private String awayTeam;
    private String stadium;


}
