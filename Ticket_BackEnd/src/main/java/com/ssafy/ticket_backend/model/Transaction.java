package com.ssafy.ticket_backend.model;

import lombok.Data;

@Data
public class Transaction {

    private Long transactionId;
    private Long ticketId;
    private Long sellerId;
    private Long buyerId;
    private String transactionStatus;
}
