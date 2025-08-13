package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.model.Application;
import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.Group;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GroupMapper {

    List<Game> getGames();

    void insertGroup(Long groupId);

    List<GroupDetailResponse> getGroupList(BaseballTeams baseballTeams);

    Group selectOneGroup(Long groupId);

    int insertApplication(Application application);

    int updateGroupCount(Long groupId);

    int updateEndedGroups();

    int selectCountByGroupId(Long groupId);

    // 그룹을 Lock으로 조회 (동시성 제어용)
    Group selectOneGroupForUpdate(Long groupId);

    List<Application> selectGroupInfo(long groupId);

    // 삭제
    int updateGroupCountForDelete(long groupId);

    // 지원서 삭제
    int deleteApplication(Application application);
}
