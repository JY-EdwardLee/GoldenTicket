package com.ssafy.ticket_backend.scheduler;

import com.ssafy.ticket_backend.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GroupScheduler {

    private final GroupService groupService;

    /**
     * 매일 자정(00:00)에 실행되는 스케줄러 오늘 날짜 + 7일보다 이전 게임들의 그룹들의 is_ended를 true로 업데이트
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 00:00에 실행
    public void updateEndedGroupsScheduler() {
        groupService.updateEndedGroups();
    }
}
