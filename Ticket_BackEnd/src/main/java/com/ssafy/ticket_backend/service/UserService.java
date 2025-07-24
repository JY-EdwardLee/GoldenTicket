package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.OAuthResponse;
import com.ssafy.ticket_backend.model.User;


public interface UserService {

    // 유저 정보 조회
    User selectUserByEmail(String email);

    // 회원가입
    JwtTokenResponse signup(User user);

    // 카카오 로그인
    OAuthResponse loginWithKakao(String code);

    // 네이버 로그인
    OAuthResponse loginWithNaver(String code);
}
