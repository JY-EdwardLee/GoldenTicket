package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// 로그인한 유저 응답  DTO
public class LoginUserResponse {

    private Long userId;
    private String accessToken;
    private String userName;
    private String email;
    private String nickName;
    private String socialProvider;
    private String profilePhotoUrl;
    private String gender;
    private String birthDate;
}

