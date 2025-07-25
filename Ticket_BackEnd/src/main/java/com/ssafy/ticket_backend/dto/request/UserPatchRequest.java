package com.ssafy.ticket_backend.dto.request;

import lombok.Data;

@Data
public class UserPatchRequest {

    String nickName;
    String profileImageUrl;
    String myTeam;
    String gender;
}
