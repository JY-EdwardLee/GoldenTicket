package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.BaseballTeams;
import lombok.Data;

@Data
public class UserPatchRequest {

    String nickName;
    String profileImageUrl;
    BaseballTeams myTeam;
    String gender;
}
