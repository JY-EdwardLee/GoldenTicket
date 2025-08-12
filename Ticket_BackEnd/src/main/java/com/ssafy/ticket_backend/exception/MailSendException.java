package com.ssafy.ticket_backend.exception;

public class MailSendException extends RuntimeException {

    public MailSendException(String message) {
        super(message);
    }
}
