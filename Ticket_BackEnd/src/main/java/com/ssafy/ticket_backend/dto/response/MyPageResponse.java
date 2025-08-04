package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.UserRole;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyPageResponse {

    String email;
    String userName;
    String nickName;
    String gender;
    String profilePhotoUrl;
    String myTeam;
    String phoneNumber;
    UserRole userRole;
    Date birthDate;
    int transferNumber;
    int receiveNumber;
    int paneltyPoint;
}
