package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.dto.response.ErrorResponse;
import com.ssafy.ticket_backend.exception.BlockedUserException;
import com.ssafy.ticket_backend.exception.CommentCreateFailException;
import com.ssafy.ticket_backend.exception.CommentDeleteFailException;
import com.ssafy.ticket_backend.exception.CommentLikeFailException;
import com.ssafy.ticket_backend.exception.CommentUpdateFailException;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.exception.PostDeleteFailException;
import com.ssafy.ticket_backend.exception.PostLikeFailException;
import com.ssafy.ticket_backend.exception.PostNotFoundException;
import com.ssafy.ticket_backend.exception.PostUpdateFailException;
import com.ssafy.ticket_backend.exception.PostUserNotFoundException;
import com.ssafy.ticket_backend.exception.UserSignupException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // User
    @ExceptionHandler(UserSignupException.class)
    public ResponseEntity<ErrorResponse> handleUserSignupException(UserSignupException e) {
        ErrorResponse response = new ErrorResponse("USER_SIGNUP_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DatabaseOperationException.class)
    public ResponseEntity<ErrorResponse> handleDb(DatabaseOperationException e) {
        ErrorResponse response = new ErrorResponse("DB_ERROR", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Post
    @ExceptionHandler(BlockedUserException.class)
    public ResponseEntity<ErrorResponse> handleBlockedUserException(BlockedUserException e) {
        ErrorResponse response = new ErrorResponse("BLOCKED_USER", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException e) {
        ErrorResponse response = new ErrorResponse("POST_NOT_FOUND", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PostUserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostUserNotFoundException(
        PostUserNotFoundException e) {
        ErrorResponse response = new ErrorResponse("POST_USER_NOT_FOUND", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
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

    // Comment
    @ExceptionHandler(CommentCreateFailException.class)
    public ResponseEntity<ErrorResponse> handleCommentCreateFailException(
        CommentCreateFailException e) {
        ErrorResponse response = new ErrorResponse("Comment_Create_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(CommentDeleteFailException.class)
    public ResponseEntity<ErrorResponse> handleCommentDeleteFailException(
        CommentDeleteFailException e) {
        ErrorResponse response = new ErrorResponse("Comment_Delete_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(CommentUpdateFailException.class)
    public ResponseEntity<ErrorResponse> handleCommentUpdateFailException(
        CommentUpdateFailException e) {
        ErrorResponse response = new ErrorResponse("Comment_Update_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(CommentLikeFailException.class)
    public ResponseEntity<ErrorResponse> handleCommentLikeFailException(
        CommentLikeFailException e) {
        ErrorResponse response = new ErrorResponse("Comment_Like_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }


}