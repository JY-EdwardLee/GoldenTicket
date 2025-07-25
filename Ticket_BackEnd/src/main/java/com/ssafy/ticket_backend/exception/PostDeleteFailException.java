package com.ssafy.ticket_backend.exception;

public class PostDeleteFailException extends RuntimeException {

    public PostDeleteFailException(String message) {
        super(message);
    }
}
