package com.ssafy.ticket_backend.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Ticket {

    private Long ticketId;                    // 기본 키
    private TicketStatus ticketStatus;        // 더미:  BEFORE_ASSIGNMENT 고정
    private int price;
    private Long gameId;                      // games테이블 game_id 참조
    private String seat;
    private Long sellerId;                    //
    private Long buyerId;
    private LocalDateTime transactionDate;
    private LocalDateTime matchedDate;
    private String image;
}
