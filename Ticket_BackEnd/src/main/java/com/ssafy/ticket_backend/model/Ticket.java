package com.ssafy.ticket_backend.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Ticket {

    Long ticketId;
    String ticketStatus;
    int price;
    Long gameId;
    String seat;
    Long sellerId;
    Long buyerId;
    String transactionState;
    LocalDateTime matchedDate;
    String image;
}
