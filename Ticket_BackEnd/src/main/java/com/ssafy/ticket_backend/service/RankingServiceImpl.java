package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.TeamRankingResponse;
import com.ssafy.ticket_backend.dto.response.UserRankingResponse;
import com.ssafy.ticket_backend.mapper.RankingMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final RankingMapper rankingMapper;

    // 유저 양도 랭킹 조회
    @Override
    public List<UserRankingResponse> getUserRanking() {
        List<UserRankingResponse> userRankingList = rankingMapper.selectUserRanking();

        int rank = 1;
        for (UserRankingResponse userRankingResponse : userRankingList) {
            userRankingResponse.setRank(rank++);

        }
        return userRankingList;
    }

    // 팀별 양도 랭킹 조회
    @Override
    public List<TeamRankingResponse> getTeamRanking() {
        List<TeamRankingResponse> teamRankingList = rankingMapper.selectTeamRanking();

        int rank = 1;
        for (TeamRankingResponse teamRankingResponse : teamRankingList) {
            teamRankingResponse.setRank(rank++);
        }

        return teamRankingList;
    }
}
