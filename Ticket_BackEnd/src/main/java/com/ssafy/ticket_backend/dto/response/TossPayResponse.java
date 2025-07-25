package com.ssafy.ticket_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossPayResponse {

    private String orderId;
    private String paymentKey;
    private Long totalAmount;
    private String method; // 결제 수단 (카드, 가상계좌 등)
    private String status; // 결제 상태
}
