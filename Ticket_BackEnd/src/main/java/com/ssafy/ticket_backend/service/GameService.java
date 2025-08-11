package com.ssafy.ticket_backend.handler.service;

import com.ssafy.ticket_backend.dto.request.GameCheckRequest;
import com.ssafy.ticket_backend.model.Game;
import java.util.List;

public interface GameService {

    List<Game> selectGame(GameCheckRequest gameCheckRequest);

    void applicationGame(String userEmail, Long gameId);

    void cancelGame(String userEmail, Long gameId);

    void cancelPaying(String userEmail, Long gameId);
}
