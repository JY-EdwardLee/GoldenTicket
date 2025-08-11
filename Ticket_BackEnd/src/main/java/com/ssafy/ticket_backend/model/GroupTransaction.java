package com.ssafy.ticket_backend.model;

import java.util.List;
import lombok.Data;

@Data
public class GroupTransaction {

    private Long transactionId;
    private List<Long> ticketIds;
    private Long sellerId;
    private Long buyerId;
    private String transactionStatus;
}
