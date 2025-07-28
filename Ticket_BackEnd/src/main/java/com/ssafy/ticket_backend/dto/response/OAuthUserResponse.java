package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// OAuth 응답 통합 DTO
public class OAuthUserResponse {

    private boolean isRegistered; // 등록 여부
    private JwtTokenResponse token; // 로그인 시

    // 미등록 시 사용자 정보

    private String email;
    private String nickname;
    private String socialProvider;
    private String profilePhotoUrl;
    private String name;
    private String gender;
    private String birthday;
    private String birthyear;
}
