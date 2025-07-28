package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.UserGender;
import com.ssafy.ticket_backend.model.UserRole;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 유저 회원가입 요청 DTO
public class UserSignupRequest {

    private Long userId;
    private String email;
    private UserGender gender;
    private String userName;
    private UserRole userRole;
    private String nickname;
    private String birthDate;      // String으로 받고, LocalDate로 변환해도 됨
    private String phoneNumber;
    private BaseballTeams myTeam;
    private String socialProvider; // 예: "naver", "kakao"
}
