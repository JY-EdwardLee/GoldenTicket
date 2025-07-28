package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.model.Game;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GameMapper {

    Game selectGameByGameId(@Param("gameId") Long gameId);

    List<Game> selectGameByDate(LocalDateTime gameDateTime);
}
