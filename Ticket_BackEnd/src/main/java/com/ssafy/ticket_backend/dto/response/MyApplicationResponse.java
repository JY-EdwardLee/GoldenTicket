package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.WaitlistStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyApplicationResponse {

    private Long waitlist_id;
    private WaitlistStatus status;
    private GameResponse game;
}
