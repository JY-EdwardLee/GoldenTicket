package com.ssafy.ticket_backend.model;

import com.ssafy.ticket_backend.dto.response.GameResponse;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Game {

  Long gameId;
  LocalDateTime gameDateTime;
  BaseballTeams homeTeam;
  BaseballTeams awayTeam;
  boolean isCanceled;
  boolean isEnded;
  // 추가
  Stadium stadium;

  // MyBatis 매핑을 위한 문자열 필드 (not null 문제 혹시나)
  String homeTeamString;
  String awayTeamString;
  String stadiumString;

  public GameResponse toGameResponse() {
    GameResponse gameResponse = new GameResponse();

    gameResponse.setId(gameId);
    gameResponse.setDate(gameDateTime);
    gameResponse.setHome(homeTeam);
    gameResponse.setAway(awayTeam);

    return gameResponse;
  }
}
