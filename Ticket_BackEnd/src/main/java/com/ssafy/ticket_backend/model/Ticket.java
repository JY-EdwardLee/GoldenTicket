package com.ssafy.ticket_backend.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Ticket {

    Long ticketId;
    TicketStatus ticketStatus;
    int price;
    Long gameId;
    String seat;
    Long sellerId;
    Long buyerId;
    LocalDateTime transactionDate;
    LocalDateTime matchedDate;
    String image;
}
