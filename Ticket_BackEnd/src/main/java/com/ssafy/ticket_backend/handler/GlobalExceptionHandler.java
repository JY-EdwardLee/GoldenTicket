package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.dto.response.ErrorResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import com.ssafy.ticket_backend.exception.PostDeleteFailException;
import com.ssafy.ticket_backend.exception.PostLikeFailException;
import com.ssafy.ticket_backend.exception.PostUpdateFailException;
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

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDb(DatabaseOperationException e) {
        ErrorResponse response = new ErrorResponse("DB_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(PostUpdateFailException.class)
    public ResponseEntity<ErrorResponse> handlePostUpdateFailException(PostUpdateFailException e) {
        ErrorResponse response = new ErrorResponse("Post_Update_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(PostDeleteFailException.class)
    public ResponseEntity<ErrorResponse> handlePostDeleteFailException(PostDeleteFailException e) {
        ErrorResponse response = new ErrorResponse("Post_Delete_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(PostLikeFailException.class)
    public ResponseEntity<ErrorResponse> handlePostLikeFailException(PostLikeFailException e) {
        ErrorResponse response = new ErrorResponse("Post_Like_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
}