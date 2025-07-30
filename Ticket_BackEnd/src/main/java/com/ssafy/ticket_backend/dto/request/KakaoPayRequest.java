package com.ssafy.ticket_backend.dto.request;

import lombok.Data;

@Data
public class KakaoPayRequest {

    Long ticketId;  // 티켓 id
    String itemName;  // 상품명
    int quantity;  // 수량
    Long totalAmount;  // 총액
}