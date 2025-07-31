package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.Stadium;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResponse {

    private Long id;            // 경기 ID
    private LocalDateTime date; // 경기 날짜
    private BaseballTeams home;        // 홈 팀
    private BaseballTeams away;        // 어웨이 팀
    private Stadium stadium;
}
