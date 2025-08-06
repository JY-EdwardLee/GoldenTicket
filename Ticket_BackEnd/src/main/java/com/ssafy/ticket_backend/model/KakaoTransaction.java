package com.ssafy.ticket_backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KakaoTransaction {

    private Long ticketId;
    private Long sellerId;
    private Long buyerId;
    private String transactionStatus;
}