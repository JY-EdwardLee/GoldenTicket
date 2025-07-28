package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.GameApplicationRequest;

public interface GameService {

    void applicationGame(String username, GameApplicationRequest gameApplicationRequest);
}
