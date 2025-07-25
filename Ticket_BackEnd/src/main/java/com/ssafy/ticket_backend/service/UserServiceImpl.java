package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.UserPatchRequest;
import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.dto.response.OAuthResponse;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.util.JwtUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    // 카카오 api 키
    @Value("${kakao.rest.api.key}")
    private String kakaoApiKey;

    // 네이버 api 키
    @Value("${naver.client.id}")
    private String naverClientId;
    @Value("${naver.client.secret}")
    private String naverClientSecret;

    // 유저 정보 조회
    @Override
    public User selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    // 회원가입
    @Override
    public JwtTokenResponse signup(User user) {
        userMapper.insertUser(user);  // TODO 유저 검증하는 로직 필요
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        return new JwtTokenResponse(accessToken, refreshToken);
    }

    // 카카오 로그인, 유저 회원가입 안되어 있으면 카카오 정보 반환
    @Override
    public OAuthResponse loginWithKakao(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 인가 코드로 Access Token 요청
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("client_id", kakaoApiKey);
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
            User tempUser = User.builder().email(email).nickname(nickname).socialProvider("KAKAO")
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

    // 네이버 로그인
    @Override
    public OAuthResponse loginWithNaver(String code) {
        RestTemplate restTemplate = new RestTemplate();

        // 1. 인가 코드로 Access Token 요청
        String tokenUrl = "https://nid.naver.com/oauth2.0/token";

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
        tokenParams.add("grant_type", "authorization_code");
        tokenParams.add("client_id", naverClientId);
        tokenParams.add("client_secret", naverClientSecret);
        tokenParams.add("code", code);
        tokenParams.add("state", "random_state"); // CSRF 방지용, 임시값

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(tokenParams,
            tokenHeaders);

        ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenUrl, tokenRequest,
            Map.class);

        String naverAccessToken = tokenResponse.getBody().get("access_token").toString();

        // 2. Access Token으로 사용자 정보 요청
        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(naverAccessToken);

        HttpEntity<?> userInfoRequest = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
            userInfoRequest, Map.class);

        Map<String, Object> respose = (Map<String, Object>) userInfoResponse.getBody();
        String email = (String) respose.get("email");
        String nickname = (String) respose.get("nickname");

        // 3. 유저 DB 조회
        User user = userMapper.selectUserByEmail(email);
        OAuthResponse response = new OAuthResponse();

        if (user == null) {
            User tempUser = User.builder().email(email).nickname(nickname).socialProvider("NAVER")
                .build();

            response.setRegistered(false);
            response.setUser(tempUser);
            return response;
        }

        // 4. 유저가 회원가입 되어있으면 JWT 발급
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        response.setRegistered(true);
        response.setToken(new JwtTokenResponse(accessToken, refreshToken));
        return response;
    }

    // 로그아웃
    @Override
    public void logout(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String email = jwtUtil.getUserEmail(token);

        jwtUtil.addToBlackList(token);
        jwtUtil.deleteRefreshToken(email);
    }

    /**
     * 마이페이지 정보
     *
     * @param email
     */
    @Override
    public MyPageResponse getMyPage(String email) {
        return userMapper.getMyPageByEmail(email);
    }

    @Override
    public void patchMyPage(String email, UserPatchRequest userPatchRequest) {
        userMapper.updateUser(email, userPatchRequest);
    }

    /**
     * 테스트 용도. 실제 서비스에서 사용 금지
     *
     * @return
     */
    @Override
    public JwtTokenResponse testUser() {
        String accessToken = jwtUtil.generateAccessToken("user1@example.com");
        String refreshToken = jwtUtil.generateRefreshToken("user1@example.com");

        return new JwtTokenResponse(accessToken, refreshToken);
    }
}