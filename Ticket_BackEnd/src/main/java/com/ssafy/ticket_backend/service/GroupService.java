package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.model.BaseballTeams;
import java.util.List;

public interface GroupService {

    public void getGames();


    public List<GroupDetailResponse> getGroupList(BaseballTeams baseballTeams);


    public void insertParticipate(String email, long groupId);


    public void deleteApplication(String email, long groupId);


    public void updateEndedGroups();


}
