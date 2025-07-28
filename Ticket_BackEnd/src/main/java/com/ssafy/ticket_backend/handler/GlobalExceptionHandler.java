package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.dto.response.ErrorResponse;
import com.ssafy.ticket_backend.exception.BlockedUserException;
import com.ssafy.ticket_backend.exception.BoardException;
import com.ssafy.ticket_backend.exception.CommentCreateFailException;
import com.ssafy.ticket_backend.exception.CommentDeleteFailException;
import com.ssafy.ticket_backend.exception.CommentLikeFailException;
import com.ssafy.ticket_backend.exception.CommentUpdateFailException;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import com.ssafy.ticket_backend.exception.PostDeleteException;
import com.ssafy.ticket_backend.exception.PostDeleteFailException;
import com.ssafy.ticket_backend.exception.PostLikeException;
import com.ssafy.ticket_backend.exception.PostNotFoundException;
import com.ssafy.ticket_backend.exception.PostRetrievalException;
import com.ssafy.ticket_backend.exception.PostUpdateException;
import com.ssafy.ticket_backend.exception.PostUpdateFailException;
import com.ssafy.ticket_backend.exception.PostUserNotFoundException;
import com.ssafy.ticket_backend.exception.UserSignupException;
import com.ssafy.ticket_backend.model.BoardType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    // Board

    /**
     * 검색 중 Enum타입에 있는 값이 아닌 다른 값을 검색하였을 경우
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleEnumBindingException(
        MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() == BoardType.class) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 게시판입니다.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("요청 파라미터 오류");
    }

    @ExceptionHandler(BoardException.class)
    public ResponseEntity<ErrorResponse> handleBoardException(BoardException e) {
        ErrorResponse response = new ErrorResponse("BOARD_ERROR", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    @ExceptionHandler(PostCreateFailException.class)
    public ResponseEntity<ErrorResponse> handlePostCreateFailException(PostCreateFailException e) {
        ErrorResponse response = new ErrorResponse("POST_Create_Fail", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Comment
    @ExceptionHandler(CommentCreateFailException.class)
    public ResponseEntity<ErrorResponse> handleCommentCreateFailException(
        CommentCreateFailException e) {
        ErrorResponse response = new ErrorResponse("Comment_Create_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(PostRetrievalException.class)
    public ResponseEntity<ErrorResponse> handlePostRetrievalException(PostRetrievalException e) {
        ErrorResponse response = new ErrorResponse("POST_RETRIEVAL_FAIL", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(PostUpdateException.class)
    public ResponseEntity<ErrorResponse> handlePostUpdateException(PostUpdateException e) {
        ErrorResponse response = new ErrorResponse("POST_UPDATE_FAIL", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(PostDeleteException.class)
    public ResponseEntity<ErrorResponse> handlePostDeleteException(PostDeleteException e) {
        ErrorResponse response = new ErrorResponse("POST_DELETE_FAIL", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


    @ExceptionHandler(PostLikeException.class)
    public ResponseEntity<ErrorResponse> handlePostLikeException(PostLikeException e) {
        ErrorResponse response = new ErrorResponse("POST_LIKE_FAIL", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
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