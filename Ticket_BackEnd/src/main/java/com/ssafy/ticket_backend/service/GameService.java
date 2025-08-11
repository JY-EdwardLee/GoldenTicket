package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.GameCheckRequest;
import com.ssafy.ticket_backend.model.Game;
import java.util.List;

public interface GameService {

    List<Game> selectGame(GameCheckRequest gameCheckRequest);

    void applicationGame(String userEmail, Long gameId);

    void groupApplicationGame(String userEmail, Long gameId, Long numberOfPeople);

    void cancelApplication(String userEmail, Long gameId);

    void cancelGroupApplication(String userEmail, Long gameId, Long numberOfPeople);

    void cancelPaying(String userEmail, Long gameId);
}
