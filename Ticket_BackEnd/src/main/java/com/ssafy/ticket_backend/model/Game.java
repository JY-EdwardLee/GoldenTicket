package com.ssafy.ticket_backend.model;

import com.ssafy.ticket_backend.dto.response.GameResponse;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Game {

    private Long gameId;
    private LocalDateTime gameDateTime;
    private BaseballTeams homeTeam;
    private BaseballTeams awayTeam;
    private Stadium stadium;
    private boolean isCanceled;
    private boolean isEnded;

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