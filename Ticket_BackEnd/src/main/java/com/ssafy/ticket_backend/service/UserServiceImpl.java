package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.OAuthResponse;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.util.JwtUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;


    // 유저 정보 조회
    @Override
    public User selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }


    // 회원가입
    @Override
    public JwtTokenResponse signup(User user) {
        userMapper.insertUser(user);
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        return new JwtTokenResponse(accessToken, refreshToken);
    }

    // 카카오 로그인, 유저 회원가입 안되어 있으면 카카오 정보 반환
    @Override
    public OAuthResponse loginWithKakao(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 인가 코드로 Access Token 요청
        String tokenUrl = "https://kakao.com/oauth/token";

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("client_id", "카카오 REST API 키");
        tokenParams.add("redirect_uri", "http://localhost:8080/users/auth/oauth/callback");
        tokenParams.add("code", code);

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams,
            tokenHeaders);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest,
            Map.class);

        String kakaoAccessToken = tokenResponse.getBody().get("access_token").toString();

        // 2. access token으로 사용자 정보 요청
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(kakaoAccessToken);

        HttpEntity<?> userInfoRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userInfoRespose = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
            userInfoRequest, Map.class);

        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfoRespose.getBody()
            .get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String nickname = (String) ((Map) userInfoRespose.getBody().get("properties")).get(
            "nickname");

        // 3. DB에 유저 존재 확인 또는 저장
        User user = userMapper.selectUserByEmail(email);
        OAuthResponse response = new OAuthResponse();

        if (user == null) {
            // 회원가입 창에 필요한 데이터(카카오에서 받아온) 전달
            User tempUser = User.builder()
                .email(email)
                .nickname(nickname)
                .socialProvider("KAKAO")
                .build();

            response.setRegistered(false);
            response.setUser(tempUser);
            return response;
        }

        // 4. 이미 가입된 유저 -> JWT 발급
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        response.setRegistered(true);
        response.setToken(new JwtTokenResponse(accessToken, refreshToken));
        return response;
    }
}
