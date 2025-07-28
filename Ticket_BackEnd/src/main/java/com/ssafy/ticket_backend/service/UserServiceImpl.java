package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.UserPatchRequest;
import com.ssafy.ticket_backend.dto.request.UserSignupRequest;
import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.dto.response.OAuthUserResponse;
import com.ssafy.ticket_backend.exception.UserSignupException;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.util.JwtUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    /**
     * @param userSignupRequest
     * @return
     */
    @Transactional
    @Override
    public JwtTokenResponse signup(UserSignupRequest userSignupRequest) {
        try {
            userMapper.insertUser(userSignupRequest);
        } catch (DuplicateKeyException e) {
            throw new UserSignupException("중복된 이메일입니다.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new UserSignupException("회원가입 중 오류가 발생하였습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(userSignupRequest.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(userSignupRequest.getEmail());

        return new JwtTokenResponse(accessToken, refreshToken);
    }

    // 카카오 로그인, 유저 회원가입 안되어 있으면 카카오 정보 반환
    @Override
    public OAuthUserResponse loginWithKakao(String code) {
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
        String profilePhotoUrl = (String) ((Map) userInfoRespose.getBody().get("properties")).get(
            "profile_image");

        // 3. DB에 유저 존재 확인 또는 저장
        User user = userMapper.selectUserByEmail(email);
        OAuthUserResponse oauthUserResponse;

        if (user == null) {
            // 회원가입 창에 필요한 데이터(카카오에서 받아온) 전달
            oauthUserResponse = OAuthUserResponse.builder().isRegistered(false).email(email)
                .nickname(nickname).socialProvider("KAKAO").profilePhotoUrl(profilePhotoUrl)
                .build();
            return oauthUserResponse;
        }

        // 4. 이미 가입된 유저 -> JWT 발급
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        oauthUserResponse = OAuthUserResponse.builder().isRegistered(true)
            .token(new JwtTokenResponse(accessToken, refreshToken)).build();
        return oauthUserResponse;
    }

    // 네이버 로그인
    @Override
    public OAuthUserResponse loginWithNaver(String code) {
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

        Map<String, Object> responseBody = userInfoResponse.getBody();
        Map<String, Object> responseMap = (Map<String, Object>) responseBody.get("response");

        String email = (String) responseMap.get("email");
        String nickname = (String) responseMap.get("nickname");
        String name = (String) responseMap.get("name");
        String gender = (String) responseMap.get("gender");
        String birthday = (String) responseMap.get("birthday");
        String birthyear = (String) responseMap.get("birthyear");
        String profilePhotoUrl = (String) responseMap.get("profile_image");

        // 3. 유저 DB 조회
        User user = userMapper.selectUserByEmail(email);
        OAuthUserResponse oauthUserResponse;

        if (user == null) {
            // 회원가입 창에 필요한 데이터(카카오에서 받아온) 전달
            oauthUserResponse = OAuthUserResponse.builder().isRegistered(false).email(email)
                .nickname(nickname).socialProvider("NAVER").gender(gender).birthday(birthday)
                .birthyear(birthyear).profilePhotoUrl(profilePhotoUrl).name(name).build();
            return oauthUserResponse;
        }

        // 4. 유저가 회원가입 되어있으면 JWT 발급
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        oauthUserResponse = OAuthUserResponse.builder().isRegistered(true)
            .token(new JwtTokenResponse(accessToken, refreshToken)).build();
        return oauthUserResponse;
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

    @Transactional
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
        String accessToken = jwtUtil.generateAccessToken("honggildong@example.com");
        String refreshToken = jwtUtil.generateRefreshToken("honggildong@example.com");

        return new JwtTokenResponse(accessToken, refreshToken);
    }
}