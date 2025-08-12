package com.ssafy.ticket_backend.scheduler;

import com.ssafy.ticket_backend.mapper.GameMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GameScheduler {

    private final GameMapper gameMapper;

    /**
     * 매일 자정에 게임을 ended로 만들어주는 스케줄러
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void checkGameEnded() {
        gameMapper.PatchGameToEnd();
    }
}
