package com.ssafy.ticket_backend.model;

import lombok.Data;

@Data
public class Transaction {

    Long transactionId;
    Long ticketId;
    Long sellerId;
    Long buyerId;
    String transactionStatus;
}
