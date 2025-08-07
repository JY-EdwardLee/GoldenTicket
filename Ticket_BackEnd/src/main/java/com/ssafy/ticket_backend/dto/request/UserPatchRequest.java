package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.BaseballTeams;
import lombok.Data;

@Data
public class UserPatchRequest {

    String nickName;
    String gender;
    String profileImageUrl;
    BaseballTeams myTeam;
}
