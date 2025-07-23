package com.ssafy.ticket_backend.exception;

/**
 * 구체화 필요 -> 너무 추상적인 에러임
 */
public class PostCreateFailException extends RuntimeException {

    public PostCreateFailException(String message) {
        super(message);
    }
}
