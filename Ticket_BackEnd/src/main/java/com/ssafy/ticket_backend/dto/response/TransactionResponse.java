package com.ssafy.ticket_backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionResponse {

    private Long transactionId;
    private TicketResponse ticket;
}
