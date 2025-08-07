package com.ssafy.ticket_backend.model;

/**
 * 티켓의 상태
 */
public enum TicketStatus {
    BEFORE_ASSIGNMENT,  // 양도 전
    BEING_ASSIGNMENT,  // 양도 중
    BEING_PAYING,  // 결제 중
    TRANSACTION_COMPLETE,  // 거래 완료
    CANCEL_WAITING,  // 응모 취소
    USED  // 사용 완료
}
