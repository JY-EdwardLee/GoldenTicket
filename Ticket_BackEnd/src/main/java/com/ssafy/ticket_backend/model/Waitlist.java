package com.ssafy.ticket_backend.model;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class Waitlist {

    Long id;
    Long userId;
    Long gameId;
    LocalDateTime createdAt;
}
