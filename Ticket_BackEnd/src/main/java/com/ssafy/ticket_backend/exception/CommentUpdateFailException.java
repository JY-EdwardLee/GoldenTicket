package com.ssafy.ticket_backend.exception;

public class CommentUpdateFailException extends RuntimeException {

    public CommentUpdateFailException(String message) {
        super(message);
    }
}
