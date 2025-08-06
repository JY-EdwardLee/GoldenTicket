package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.exception.DatabaseException;
import com.ssafy.ticket_backend.exception.GameAlreadyEndedException;
import com.ssafy.ticket_backend.exception.GroupCapacityExceededException;
import com.ssafy.ticket_backend.exception.GroupJoinCountException;
import com.ssafy.ticket_backend.exception.GroupParticipationException;
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
     * 그룹 신청하기 Lock 처리 해야할듯.
     *
     * @param email, groupId
     * @return
     */
    public void insertParticipate(String email, long groupId) {

        try {
            User user = userMapper.selectUserByEmail(email);
            Group group = groupMapper.selectOneGroup(groupId);

            if (!group.getIsActive()) {
                throw new GroupCapacityExceededException("정원 초과 하였습니다.");
            } else if (group.getIsEnded()) {
                throw new GameAlreadyEndedException("종료된 경기입니다.");
            }

            int result = groupMapper.insertApplication(new Application(groupId, user.getUserId()));
            if (result != 1) {
                throw new DatabaseException("회원 정보를 확인해 주세요.");
            }

            int result2 = groupMapper.updateGroupCount(groupId);
            if (result2 != 1) {
                throw new GroupJoinCountException("그룹 집계 중 오류가 발생하였습니다.");
            }

        } catch (GroupCapacityExceededException | GameAlreadyEndedException |
                 GroupJoinCountException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new GroupParticipationException("그룹 참여 중 오류가 발생하였습니다.");
        }
    }


}
