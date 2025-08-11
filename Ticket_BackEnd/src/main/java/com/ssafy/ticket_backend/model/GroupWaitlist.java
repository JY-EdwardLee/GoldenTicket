package com.ssafy.ticket_backend.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class GroupWaitlist {

    private Long groupWaitlistId;
    private List<Long> ticketIds;
    private Long userId;
    private Long gameId;
    private LocalDateTime createdAt;
    private WaitlistStatus waitlistStatus;
    private Long transactionId;
    private int numberOfPeople;
}
