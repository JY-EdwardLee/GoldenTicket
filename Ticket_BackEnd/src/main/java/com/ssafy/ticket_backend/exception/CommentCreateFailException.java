package com.ssafy.ticket_backend.exception;

public class CommentCreateFailException extends RuntimeException {

    public CommentCreateFailException(String message) {
        super(message);
    }
}
