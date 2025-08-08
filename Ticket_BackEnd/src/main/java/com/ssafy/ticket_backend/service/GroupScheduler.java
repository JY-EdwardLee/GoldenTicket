package com.ssafy.ticket_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class GroupScheduler {

    private final GroupService groupService;

    /**
     * 매일 자정(00:00)에 실행되는 스케줄러
     * 오늘 날짜 + 7일보다 이전 게임들의 그룹들의 is_ended를 true로 업데이트
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 00:00에 실행
    public void updateEndedGroupsScheduler() {
        log.info("=== 그룹 종료 스케줄러 실행 시작 (매일 자정) ===");
        try {
            groupService.updateEndedGroups();
            log.info("=== 그룹 종료 스케줄러 실행 완료 (매일 자정) ===");
        } catch (Exception e) {
            log.error("그룹 종료 스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
