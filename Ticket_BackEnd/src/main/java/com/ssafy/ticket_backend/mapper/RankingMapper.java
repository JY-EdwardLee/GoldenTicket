package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.response.TeamRankingResponse;
import com.ssafy.ticket_backend.dto.response.UserRankingResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RankingMapper {

    // 유저 양도 랭킹 조회
    List<UserRankingResponse> selectUserRanking();

    // 팀별 양도 랭킹 조회
    List<TeamRankingResponse> selectTeamRanking();
}
