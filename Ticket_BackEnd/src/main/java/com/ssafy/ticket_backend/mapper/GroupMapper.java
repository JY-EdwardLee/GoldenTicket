package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.model.Application;
import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.Group;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GroupMapper {

    List<Game> getGames();

    void insertGroup(Long groupId);

    List<GroupDetailResponse> getGroupList(BaseballTeams baseballTeams);

    Group selectOneGroup(Long groupId);

    int insertApplication(Application application);

    int updateGroupCount(Long groupId);
    
    /**
     * 오늘 날짜 + 7일보다 이전 게임들의 그룹들의 is_ended를 true로 업데이트
     */
    int updateEndedGroups();
    
    /**
     * 그룹의 현재 참가자 수를 조회
     */
    int selectCountByGroupId(Long groupId);
    
    /**
     * 그룹을 Lock으로 조회 (동시성 제어용)
     */
    Group selectOneGroupForUpdate(Long groupId);
}
