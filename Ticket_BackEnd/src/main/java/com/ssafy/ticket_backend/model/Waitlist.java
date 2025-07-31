package com.ssafy.ticket_backend.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Waitlist {

    private Long id;
    private Long userId;
    private Long gameId;
    private Long transactionId;
    private LocalDateTime createdAt;
    private WaitlistStatus status;
}
