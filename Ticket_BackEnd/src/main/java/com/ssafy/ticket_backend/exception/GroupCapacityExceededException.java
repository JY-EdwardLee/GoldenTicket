package com.ssafy.ticket_backend.exception;

public class GroupCapacityExceededException extends RuntimeException {

    public GroupCapacityExceededException(String message) {
        super(message);
    }

}
