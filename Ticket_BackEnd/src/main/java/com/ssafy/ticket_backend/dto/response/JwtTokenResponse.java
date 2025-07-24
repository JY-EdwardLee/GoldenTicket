package com.ssafy.ticket_backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 클라이언트에게 엑세스,리프레시 토큰 반환하는 DTO
public class JwtTokenResponse {

    private String accessToken;
    private String refreshToken;
}
