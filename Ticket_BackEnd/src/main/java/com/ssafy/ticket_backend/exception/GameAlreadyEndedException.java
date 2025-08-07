package com.ssafy.ticket_backend.exception;

public class GameAlreadyEndedException extends RuntimeException {

    public GameAlreadyEndedException(String message) {
        super(message);
    }
}
