package com.ssafy.ticket_backend.handler.service;

import com.ssafy.ticket_backend.dto.response.TeamRankingResponse;
import com.ssafy.ticket_backend.dto.response.UserRankingResponse;
import java.util.List;

public interface RankingService {

    // 유저 양도 랭킹 조회
    List<UserRankingResponse> getUserRanking();

    // 팀별 양도 랭킹 조회
    List<TeamRankingResponse> getTeamRanking();
}
