package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.BaseballTeams;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRankingResponse {

    private int rank; // 팀 랭킹 순위
    private BaseballTeams teamName; // 팀 이름
    private int transferAllCount; // 팀별 총 양도 횟수 
}
