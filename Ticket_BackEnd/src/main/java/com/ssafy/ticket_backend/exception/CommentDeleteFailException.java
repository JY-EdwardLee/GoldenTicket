package com.ssafy.ticket_backend.exception;

public class CommentDeleteFailException extends RuntimeException {

    public CommentDeleteFailException(String message) {
        super(message);
    }
}
