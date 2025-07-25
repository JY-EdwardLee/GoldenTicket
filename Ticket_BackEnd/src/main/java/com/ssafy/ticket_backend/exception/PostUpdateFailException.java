package com.ssafy.ticket_backend.exception;

public class PostUpdateFailException extends RuntimeException {

    public PostUpdateFailException(String message) {
        super(message);
    }
}
