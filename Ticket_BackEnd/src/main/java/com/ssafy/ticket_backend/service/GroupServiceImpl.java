package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.exception.DuplicateApplicationException;
import com.ssafy.ticket_backend.exception.GameAlreadyEndedException;
import com.ssafy.ticket_backend.exception.GroupCapacityExceededException;
import com.ssafy.ticket_backend.exception.GroupJoinCountException;
import com.ssafy.ticket_backend.mapper.GroupMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Application;
import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.Group;
import com.ssafy.ticket_backend.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final UserMapper userMapper;
    private final EmailService emailService;


    /**
     * 그룹 생성기
     */
    @Transactional
    @Override
    public void getGames() {
        List<Game> gameList = groupMapper.getGames();
        for (Game game : gameList) {
            groupMapper.insertGroup(game.getGameId());
        }
    }

    /**
     * 그룹 조회하기(팀별조회 Home or Away) 최신순
     *
     * @param baseballTeams
     * @return GroupDetailResponse
     */
    @Transactional
    @Override
    public List<GroupDetailResponse> getGroupList(BaseballTeams baseballTeams) {
        return groupMapper.getGroupList(baseballTeams);
    }

    /**
     * 단체관람 신청하기 (동시성 제어 포함)
     *
     * @param email, groupId
     * @return 성공/실패 메세지
     */
    @Transactional
    @Override
    public void insertParticipate(String email, long groupId) {

        User user = userMapper.selectUserByEmail(email);

        // Lock으로 그룹 정보 조회 (동시성 제어)
        Group group = groupMapper.selectOneGroupForUpdate(groupId);

        if (!group.getIsActive()) {
            throw new GroupCapacityExceededException("정원 초과 하였습니다.");
        } else if (group.getIsEnded()) {
            throw new GameAlreadyEndedException("종료된 경기입니다.");
        }

        try {
            int result = groupMapper.insertApplication(new Application(groupId, user.getUserId()));
        } catch (Exception e) {
            throw new DuplicateApplicationException("중복 지원은 불가합니다.");
        }

        int result2 = groupMapper.updateGroupCount(groupId);

        // 그룹 정원이 다 찼을 때 메일 전송
        int cnt = groupMapper.selectCountByGroupId(groupId);

        // TODO 메일 전송: 비동기 처리
        if (cnt == 20) {
            // TODO 파일 생성기 작성해야함.

            String gameInfo = "더미 게임: 삼성 라이온즈 vs KIA 타이거즈 (2024-08-15 18:30)";
            emailService.sendGroupFullNotification(groupId, gameInfo);
        }

        if (result2 != 1) {
            throw new GroupJoinCountException("그룹 집계 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 스케줄러: 오늘 날짜 + 7일보다 이전 게임 is_ended -> true 업데이트 시점 매일 자정(00:00)
     */
    @Override
    public void updateEndedGroups() {
        try {
            // 그룹 테이블만 업데이트 (게임 테이블은 더미 데이터이므로 업데이트하지 않음)
            // 오늘 날짜 + 7일보다 이전 게임들의 그룹들을 종료 처리
            int updatedGroupsCount = groupMapper.updateEndedGroups();
            if (updatedGroupsCount > 0) {
                System.out.println("스케줄러: " + updatedGroupsCount + "개의 그룹이 종료 처리되었습니다.");
            } else {
                System.out.println("스케줄러: 종료할 그룹이 없습니다.");
            }
        } catch (Exception e) {
            System.err.println("스케줄러 실행 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }

}
