package com.ssafy.ticket_backend.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TossPayReadyResponse {

    private final String clientKey;
    private final String orderId;
    private final String orderName;
    private final int amount;
    private final String customerName;
    private final String successUrl;
    private final String failUrl;
}