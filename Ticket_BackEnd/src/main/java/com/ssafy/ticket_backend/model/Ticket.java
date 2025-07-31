package com.ssafy.ticket_backend.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Ticket {

  Long ticketId;                    // 기본 키
  TicketStatus ticketStatus;        // 더미:  BEFORE_ASSIGNMENT 고정
  /*
  [TicketStatus]
  BEFORE_ASSIGNMENT,  // 양도 전
  BEING_ASSIGNMENT,  // 양도 중
  BEING_PAYING,  // 결제 중
  TRANSACTION_COMPLETE  // 거래 완료
   */
  int price;
  Long gameId;                      // games테이블 game_id 참조
  String seat;
  Long sellerId;                    //
  Long buyerId;
  LocalDateTime transactionDate;
  LocalDateTime matchedDate;
  String image;
}
