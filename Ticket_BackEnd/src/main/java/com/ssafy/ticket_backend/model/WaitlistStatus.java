package com.ssafy.ticket_backend.model;

/**
 * 응모의 상태
 */
public enum WaitlistStatus {
    BEING_WAITING,  // 응모 중
    WAITING_PAYING,  // 결제 준비
    CANCEL_WAITING,  // 응모 취소
    TRANSACTION_COMPLETE  // 결제 완료
}
