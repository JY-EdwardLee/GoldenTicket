package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.TicketStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TicketResponse {

    private Long ticketId;  // 티켓 ID
    private TicketStatus status;  // 티켓 상태
    private int price;  // 가격
    private GameResponse game;  // 경기 정보
    private String seat;  // 좌석 정보
    private int waitNumber;  // 대기열 인원

    @Data
    @AllArgsConstructor
    public static class GameResponse {

        private Long id;            // 경기 ID
        private LocalDateTime date; // 경기 날짜
        private BaseballTeams home;        // 홈 팀
        private BaseballTeams away;        // 어웨이 팀
    }
}