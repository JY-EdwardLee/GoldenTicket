package com.ssafy.ticket_backend.exception;

public class PostCreateFailException extends RuntimeException {

  public PostCreateFailException(String message) {
    super(message);
  }
}
