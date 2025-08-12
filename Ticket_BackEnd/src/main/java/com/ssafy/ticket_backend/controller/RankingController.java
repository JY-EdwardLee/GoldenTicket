package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.TeamRankingResponse;
import com.ssafy.ticket_backend.dto.response.UserRankingResponse;
import com.ssafy.ticket_backend.service.RankingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rank")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * 이달의 양도자 랭킹 순위 (상위 3명)
     *
     * @return UserRankingResponse가 담긴 List
     */
    @GetMapping("/user")
    public List<UserRankingResponse> getUserRanking() {
        return rankingService.getUserRanking();
    }

    /**
     * 팀별 양도 랭킹 순위 (상위 3명)
     *
     * @return TeamRankingResponse가 담긴 List
     */
    @GetMapping("/team")
    public List<TeamRankingResponse> getTeamRanking() {
        return rankingService.getTeamRanking();
    }
}
