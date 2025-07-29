package com.ssafy.ticket_backend.model;

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
}
