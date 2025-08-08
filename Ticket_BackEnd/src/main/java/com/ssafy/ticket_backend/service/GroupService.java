package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.model.BaseballTeams;
import java.util.List;

public interface GroupService {


    // 그룹 생성기
    public void getGames();

    // 그룹 조회(팀 선택 옵션)
    public List<GroupDetailResponse> getGroupList(BaseballTeams baseballTeams);

    // 그룹 신청하기
    public void insertParticipate(String email, long groupId);

    // 스케줄러: 오늘 날짜 + 7일보다 이전 게임들의 그룹들의 is_ended를 true로 업데이트
    public void updateEndedGroups();

}
