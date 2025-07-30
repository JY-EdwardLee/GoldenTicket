package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyApplicationResponse {

    Long waitlist_id;
    private GameResponse game;
}
