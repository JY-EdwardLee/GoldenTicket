package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.TicketStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class TicketResponse {

    private Long ticketId;  // 티켓 ID
    private TicketStatus status;  // 티켓 상태
    private int price;  // 가격
    private GameResponse game;  // 경기 정보
    private String seat;  // 좌석 정보
    private int waitNumber;  // 대기열 인원
    private GameResponse gameResponse;
}