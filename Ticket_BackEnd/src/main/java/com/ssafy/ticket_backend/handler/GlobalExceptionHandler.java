package com.ssafy.ticket_backend.handler;

import com.ssafy.ticket_backend.dto.response.ErrorResponse;
import com.ssafy.ticket_backend.exception.ApplicationNotFoundException;
import com.ssafy.ticket_backend.exception.BoardException;
import com.ssafy.ticket_backend.exception.CommentException;
import com.ssafy.ticket_backend.exception.DatabaseException;
import com.ssafy.ticket_backend.exception.DuplicateApplicationException;
import com.ssafy.ticket_backend.exception.GameAlreadyEndedException;
import com.ssafy.ticket_backend.exception.GameApplyException;
import com.ssafy.ticket_backend.exception.GroupCapacityExceededException;
import com.ssafy.ticket_backend.exception.GroupJoinCountException;
import com.ssafy.ticket_backend.exception.GroupParticipationException;
import com.ssafy.ticket_backend.exception.ImageDownloadUrlGenerationException;
import com.ssafy.ticket_backend.exception.MailSendException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import com.ssafy.ticket_backend.exception.PostDeleteFailException;
import com.ssafy.ticket_backend.exception.PostNotFoundException;
import com.ssafy.ticket_backend.exception.PostRetrievalException;
import com.ssafy.ticket_backend.exception.PostUpdateException;
import com.ssafy.ticket_backend.exception.PostUserNotFoundException;
import com.ssafy.ticket_backend.exception.PresignedUrlGenerationException;
import com.ssafy.ticket_backend.exception.SaveUploadKeyException;
import com.ssafy.ticket_backend.exception.TicketException;
import com.ssafy.ticket_backend.exception.UserBlockException;
import com.ssafy.ticket_backend.exception.UserProfileKeyQueryException;
import com.ssafy.ticket_backend.exception.UserSignupException;
import com.ssafy.ticket_backend.model.BoardType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // DB
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDb(DatabaseException e) {
        ErrorResponse response = new ErrorResponse("DB_ERROR", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // User
    @ExceptionHandler(UserSignupException.class)
    public ResponseEntity<ErrorResponse> handleUserSignupException(UserSignupException e) {
        ErrorResponse response = new ErrorResponse("USER_SIGNUP_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserBlockException.class)
    public ResponseEntity<ErrorResponse> handleBlockedUserException(UserBlockException e) {
        ErrorResponse response = new ErrorResponse("BLOCKED_USER", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
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

    @ExceptionHandler(PostCreateFailException.class)
    public ResponseEntity<ErrorResponse> handlePostCreateFailException(PostCreateFailException e) {
        ErrorResponse response = new ErrorResponse("POST_Create_Fail", e.getMessage());

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

    @ExceptionHandler(PostDeleteFailException.class)
    public ResponseEntity<ErrorResponse> handlePostDeleteException(PostDeleteFailException e) {
        ErrorResponse response = new ErrorResponse("POST_DELETE_FAIL", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // Comment
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<ErrorResponse> handleCommentCreateFailException(CommentException e) {
        ErrorResponse response = new ErrorResponse("Comment_Create_Fail", e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // S3
    @ExceptionHandler(PresignedUrlGenerationException.class)
    public ResponseEntity<ErrorResponse> handleCommentLikeFailException(
        PresignedUrlGenerationException e) {
        ErrorResponse response = new ErrorResponse("PRESIGNED_URL_GENERATION_FAILED",
            e.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(SaveUploadKeyException.class)
    public ResponseEntity<ErrorResponse> handleSaveUploadKeyException(SaveUploadKeyException e) {
        ErrorResponse response = new ErrorResponse("SAVE_UPLOAD_KEY_FAILED", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(UserProfileKeyQueryException.class)
    public ResponseEntity<ErrorResponse> handleUserProfileKeyQueryException(
        UserProfileKeyQueryException e) {
        ErrorResponse response = new ErrorResponse("USER_PROFILE_KEY_QUERY_FAILED", e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ImageDownloadUrlGenerationException.class)
    public ResponseEntity<ErrorResponse> handleImageDownloadUrlGenerationException(
        ImageDownloadUrlGenerationException e) {

        ErrorResponse response = new ErrorResponse("IMAGE_DOWNLOAD_URL_GENERATION_FAILED",
            e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(GameApplyException.class)
    public ResponseEntity<ErrorResponse> handleGameApplyException(GameApplyException e) {
        ErrorResponse response = new ErrorResponse("GAME_APPLY_ERROR", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Ticket
    @ExceptionHandler(TicketException.class)
    public ResponseEntity<ErrorResponse> handleTicketTransferException(TicketException e) {
        ErrorResponse response = new ErrorResponse("TICKET_TRANSFER_ERROR", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Group
    @ExceptionHandler(GameAlreadyEndedException.class)
    public ResponseEntity<ErrorResponse> handleGameAlreadyEnded(GameAlreadyEndedException e) {
        ErrorResponse response = new ErrorResponse("GAME_ALREADY_ENDED", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(GroupCapacityExceededException.class)
    public ResponseEntity<ErrorResponse> handleGroupCapacityExceeded(
        GroupCapacityExceededException e) {
        ErrorResponse response = new ErrorResponse("GROUP_CAPACITY_EXCEEDED", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(GroupJoinCountException.class)
    public ResponseEntity<ErrorResponse> handleGroupJoinCount(GroupJoinCountException e) {
        ErrorResponse response = new ErrorResponse("GROUP_JOIN_COUNT_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(GroupParticipationException.class)
    public ResponseEntity<ErrorResponse> handleGroupParticipation(GroupParticipationException e) {
        ErrorResponse response = new ErrorResponse("GROUP_PARTICIPATION_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(DuplicateApplicationException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateApplication(
        DuplicateApplicationException e) {
        ErrorResponse response = new ErrorResponse("DUPLICATE_APPLICATION_ERROR", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleApplicationNotFound(ApplicationNotFoundException e) {
        ErrorResponse response = new ErrorResponse(
            "APPLICATION_NOT_FOUND_ERROR", e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // mail
    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<ErrorResponse> handleMailSendException(MailSendException e) {
        ErrorResponse response = new ErrorResponse(
            "MAIL_SEND_ERROR", e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }


}