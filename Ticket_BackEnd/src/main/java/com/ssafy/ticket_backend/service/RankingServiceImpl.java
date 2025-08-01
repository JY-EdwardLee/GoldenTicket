package com.ssafy.ticket_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.ticket_backend.dto.response.TeamRankingResponse;
import com.ssafy.ticket_backend.dto.response.UserRankingResponse;
import com.ssafy.ticket_backend.mapper.RankingMapper;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final RankingMapper rankingMapper;
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String USER_RANKING_KEY = "ranking:user";
    private static final String TEAM_RANKING_TODAY_KEY = "ranking:team_today";
    private static final String TEAM_RANKING_YESTERDAY_KEY = "ranking:team_yesterday";
    private static final Duration TTL = Duration.ofDays(1); // 1일 캐시

    // 유저 양도 랭킹 조회
    @Override
    public List<UserRankingResponse> getUserRanking() {
        try {
            // 레디스에서 유저 랭킹 가져오기
            String cached = redisTemplate.opsForValue().get(USER_RANKING_KEY);

            // 캐시에 데이터가 있으면 -> JSON -> List 변환
            if (cached != null) {
                return objectMapper.readValue(cached, new TypeReference<>() {
                });
            }
            // 레디스에 없으면 DB에서 랭킹 조회
            List<UserRankingResponse> userRankingList = rankingMapper.selectUserRanking();

            int rank = 1;
            for (UserRankingResponse userRankingResponse : userRankingList) {
                userRankingResponse.setRank(rank++);
            }

            // 조회한 랭킹 리스트를 JSON 문자열로 변환해서 레디스에 저장
            redisTemplate.opsForValue().set(USER_RANKING_KEY, // 저정할 키
                objectMapper.writeValueAsString(userRankingList), // 저정할 제이슨
                TTL);

            return userRankingList;

        } catch (Exception e) {
            throw new RuntimeException("팀 랭킹 캐시 처리 실패", e);
        }
    }

    // 팀 랭킹 조회 (캐시 사용 + 증가율 계산)
    @Override
    public List<TeamRankingResponse> getTeamRanking() {
        try {
            // Redis에서 캐시된 데이터 조회
            String todayCached = redisTemplate.opsForValue().get(TEAM_RANKING_TODAY_KEY);
            String yesterdayCached = redisTemplate.opsForValue().get(TEAM_RANKING_YESTERDAY_KEY);

            List<TeamRankingResponse> todayList;

            // 오늘 데이터가 캐시에 있다면 → JSON → 객체 리스트로 역직렬화
            if (todayCached != null) {
                todayList = objectMapper.readValue(todayCached, new TypeReference<>() {
                });
            } else {
                //  없으면 DB에서 랭킹 가져오기
                todayList = rankingMapper.selectTeamRanking();

                // 랭크 순위
                int rank = 1;
                for (TeamRankingResponse team : todayList) {
                    team.setRank(rank++);
                }

                // Redis에 저장 (JSON 직렬화)
                redisTemplate.opsForValue().set(
                    TEAM_RANKING_TODAY_KEY,
                    objectMapper.writeValueAsString(todayList),
                    TTL
                );
            }

            // 어제 데이터가 있으면 → 증가율 계산
            if (yesterdayCached != null) {
                List<TeamRankingResponse> yesterdayList = objectMapper.readValue(
                    yesterdayCached, new TypeReference<>() {
                    }
                );

                // Map으로 변환: teamName 기준
                Map<String, Integer> yesterdayMap = yesterdayList.stream()
                    .collect(Collectors.toMap(
                        team -> team.getTeamName().name(),
                        TeamRankingResponse::getTransferAllCount
                    ));

                // 오늘과 비교해서 증가율 계산
                for (TeamRankingResponse today : todayList) {
                    Integer yesterdayCount = yesterdayMap.get(today.getTeamName().name());

                    if (yesterdayCount != null && yesterdayCount > 0) {
                        double growth = ((double) (today.getTransferAllCount() - yesterdayCount)
                            / yesterdayCount) * 100;
                        today.setGrowthRate(Math.round(growth * 10.0) / 10.0); // 소수점 첫째 자리까지 반올림
                    } else {
                        today.setGrowthRate(null); // 어제 데이터 없거나 0이면 계산 안 함
                    }
                }
            }

            return todayList;

        } catch (Exception e) {
            throw new RuntimeException("팀 랭킹 캐시 또는 증가율 처리 중 오류", e);
        }
    }

    // 매일 자정에 실행 -> 오늘 랭킹을 어제 랭킹으로 백업
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void backupTeamRankingForYesterday() {
        String todayData = redisTemplate.opsForValue().get(TEAM_RANKING_TODAY_KEY);
        if (todayData != null) {
            redisTemplate.opsForValue().set(TEAM_RANKING_YESTERDAY_KEY, todayData);
        }
    }
}