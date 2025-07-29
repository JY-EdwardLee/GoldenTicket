package com.ssafy.ticket_backend.service;

public interface GameService {

    void applicationGame(String userEmail, Long gameId);

    void cancelGame(String userEmail, Long gameId);
}
