package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.BaseballTeams;
import java.time.LocalDate;
import lombok.Data;

@Data
public class GameCheckRequest {

    private LocalDate date;
    private BaseballTeams team;
}
