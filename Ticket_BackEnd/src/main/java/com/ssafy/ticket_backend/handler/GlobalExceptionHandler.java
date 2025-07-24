package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.dto.response.ErrorResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostCreateFailException.class)
    public ResponseEntity<ErrorResponse> handlePostCreateFailException(PostCreateFailException e) {
        ErrorResponse response = new ErrorResponse("Post_Create_Fail", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArg(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse("INVALID_INPUT", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDb(DatabaseOperationException e) {
        ErrorResponse response = new ErrorResponse("DB_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}