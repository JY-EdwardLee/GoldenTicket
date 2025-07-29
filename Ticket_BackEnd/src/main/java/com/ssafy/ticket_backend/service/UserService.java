package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.UserPatchRequest;
import com.ssafy.ticket_backend.dto.request.UserSignupRequest;
import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.dto.response.OAuthUserResponse;
import com.ssafy.ticket_backend.dto.response.LoginUserResponse;
import com.ssafy.ticket_backend.model.User;


public interface UserService {

    // 유저 정보 조회
    User selectUserByEmail(String email);

    // 회원가입
    JwtTokenResponse signup(UserSignupRequest userSignupRequest);

    // 카카오 로그인
    OAuthUserResponse loginWithKakao(String code);

    // 네이버 로그인
    OAuthUserResponse loginWithNaver(String code);

    //  OAuthUserResponse를 임시 저장하고 임의 ID 반환
    String storeTempUserInfo(OAuthUserResponse userResponse);

    // Redis에 임시 저장된 유저 정보 조회
    OAuthUserResponse getTempUserInfo(String tempUserId);

    // 로그인 된 유저 정보 반환
    LoginUserResponse getLoginUser(String accessToken);

    // 로그아웃
    void logout(String token);

    // 액세스 토큰 만료 시, 리프레시 토큰으로 새 토큰 재발급 요청
    JwtTokenResponse refreshToken(String refreshToken);


    // 마이페이지
    MyPageResponse getMyPage(String email);

    // 내 정보 수정
    void patchMyPage(String email, UserPatchRequest userPatchRequest);

    // 테스트 용도
    JwtTokenResponse testUser();
}
