package com.ssafy.ticket_backend.dto.response;

import com.ssafy.ticket_backend.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// OAuth 응답 통합 DTO
public class OAuthResponse {

    private boolean isRegistered; // 등록 여부
    private JwtTokenResponse token; // 로그인 시
    private User user; // 미등록 시

}
