package com.ssafy.ticket_backend.exception;

public class PostUserNotFoundException extends RuntimeException {

    public PostUserNotFoundException(String message) {
        super(message);
    }
}
