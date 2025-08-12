package com.ssafy.ticket_backend.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayScheduler {

    /**
     * 결제를 하였는지 확인하는 API
     */
    @Scheduled(fixedRate = 60000)
    public void checkGameEnded() {
        gameMapper.PatchGameToEnd();
    }
}
