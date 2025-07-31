package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.Game;
import java.time.LocalDate;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameMapper {

  // 날짜와 팀으로 게임 검색
  List<Game> selectGameByDateAndTeam(@Param("gameDate") LocalDate date, BaseballTeams team);

  // 게임 검색
  Game selectGameByGameId(@Param("gameId") Long gameId);

  // 같은 날짜에 있는 게임 검색
  List<Game> selectGameByDate(@Param("gameDate") LocalDate gameDate);

  // 같은 날짜에 다른 게임에 응모하였는지 확인
  boolean selectWaitlistsByGameIdAndUserId(@Param("userId") Long userId,
      @Param("gameId") Long gameId);

  // 대기열 추가
  void insertWaitlist(@Param("userId") Long userId, @Param("gameId") Long gameId);

  // 대기열 삭제
  int deleteWaitlist(@Param("userId") Long userId, @Param("gameId") Long gameId);

  // 더미 생성용 게임 가져오기
  List<Game> getAllGame();
}
