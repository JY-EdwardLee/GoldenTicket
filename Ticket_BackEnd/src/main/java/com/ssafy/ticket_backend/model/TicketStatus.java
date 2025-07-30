package com.ssafy.ticket_backend.model;

public enum TicketStatus {
    BEFORE_ASSIGNMENT,  // 양도 전
    BEING_ASSIGNMENT,  // 양도 중
    BEING_PAYING,  // 결제 중
    TRANSACTION_COMPLETE  // 거래 완료
}
