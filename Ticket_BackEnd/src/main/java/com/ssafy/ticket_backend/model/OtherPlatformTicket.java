package com.ssafy.ticket_backend.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class OtherPlatformTicket {

  private Long ticketId;
  private String platform;
  private int price;
  private String seat;
  LocalDateTime gameDatetime;
  private BaseballTeams homeTeam;
  private BaseballTeams awayTeam;
  private String userEmail;
  // 추가
  Stadium stadium;
}
