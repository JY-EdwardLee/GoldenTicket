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
    Stadium stadium;
    boolean isCanceled;
    boolean isEnded;

    public GameResponse toGameResponse() {
        GameResponse gameResponse = new GameResponse();

        gameResponse.setId(gameId);
        gameResponse.setDate(gameDateTime);
        gameResponse.setHome(homeTeam);
        gameResponse.setAway(awayTeam);
        gameResponse.setStadium(stadium);

        return gameResponse;
    }
}