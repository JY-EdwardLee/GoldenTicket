package com.ssafy.ticket_backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.ticket_backend.dto.request.UserPatchRequest;
import com.ssafy.ticket_backend.dto.request.UserSignupRequest;
import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.LoginUserResponse;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.dto.response.OAuthUserResponse;
import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.exception.UserSignupException;
import com.ssafy.ticket_backend.mapper.PostMapper;
import com.ssafy.ticket_backend.mapper.TransactionMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.util.JwtUtil;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
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
    private final TransactionMapper transactionMapper;
    private final PostMapper postMapper;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    private static final String TEMP_USER_KEY_PREFIX = "tempUser:";


    // 카카오 api 키
    @Value("${kakao.rest.api.key}")
    private String kakaoApiKey;

    // 네이버 api 키
    @Value("${naver.client.id}")
    private String naverClientId;
    @Value("${naver.client.secret}")
    private String naverClientSecret;

    /**
     * 이메일로 사용자 정보 조회
     *
     * @param email 조회할 사용자의 이메일
     * @return User 객체 (존재하지 않으면 null)
     */
    @Override
    public User selectUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    /**
     * 일반 회원가입 처리
     *
     * @param userSignupRequest 회원가입 요청 정보
     * @return JWT 토큰(access, refresh)
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

    /**
     * 카카오 로그인 처리
     *
     * @param code 카카오 인가 코드
     * @return OAuthUserResponse (회원 가입 여부 및 사용자 정보/JWT 포함)
     */
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
        tokenParams.add("redirect_uri", "http://localhost:8080/users/auth/kakao/callback");
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
            // 시연을 위해 더미데이터 강제 추가
            oauthUserResponse = OAuthUserResponse.builder().isRegistered(false).email(email)
                .userName("시니어 이름").nickName(nickname).birthDay("05-22").birthYear("1960")
                .gender("M").socialProvider("KAKAO").profilePhotoUrl(profilePhotoUrl).build();
            return oauthUserResponse;
        }

        // 4. 이미 가입된 유저 -> JWT 발급
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        oauthUserResponse = OAuthUserResponse.builder().isRegistered(true)
            .token(new JwtTokenResponse(accessToken, refreshToken)).build();
        return oauthUserResponse;
    }

    /**
     * 네이버 로그인 처리
     *
     * @param code 네이버 인가 코드
     * @return OAuthUserResponse (회원 가입 여부 및 사용자 정보/JWT 포함)
     */
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
                .nickName(nickname).socialProvider("NAVER").gender(gender).birthDay(birthday)
                .birthYear(birthyear).profilePhotoUrl(profilePhotoUrl).userName(name).build();
            return oauthUserResponse;
        }

        // 4. 유저가 회원가입 되어있으면 JWT 발급
        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        oauthUserResponse = OAuthUserResponse.builder().isRegistered(true)
            .token(new JwtTokenResponse(accessToken, refreshToken)).build();
        return oauthUserResponse;
    }

    /**
     * OAuth 로그인 중 수집된 사용자 정보를 Redis에 임시 저장
     *
     * @param userResponse OAuth 로그인으로 받은 사용자 정보
     * @return Redis에 저장된 임시 사용자 ID
     */

    @Override
    public String storeTempUserInfo(OAuthUserResponse userResponse) {
        String tempUserId = UUID.randomUUID().toString(); // 고유한 임시 ID 생성
        String redisKey = TEMP_USER_KEY_PREFIX + tempUserId; // 키에 접두사 붙임

        try {
            // DTO를 JSON 문자열로 직렬화
            String json = objectMapper.writeValueAsString(userResponse);

            // Redis에 저장 (10분 TTL)
            redisTemplate.opsForValue().set(redisKey, json, 10, TimeUnit.MINUTES);

            return tempUserId;
        } catch (JsonProcessingException e) {
            // 예외 처리 로직
            throw new RuntimeException("OAuthUserResponse 직렬화 실패", e);
        }
    }

    /**
     * 임시 사용자 ID로 Redis에서 OAuthUserResponse 조회
     *
     * @param tempUserId Redis에 저장된 임시 사용자 ID
     * @return OAuthUserResponse
     */
    public OAuthUserResponse getTempUserInfo(String tempUserId) {
        String redisKey = TEMP_USER_KEY_PREFIX + tempUserId;
        String json = redisTemplate.opsForValue().get(redisKey);

        if (json == null) {
            throw new RuntimeException("임시 사용자 정보가 Redis에 존재하지 않습니다.");
        }

        try {
            return objectMapper.readValue(json, OAuthUserResponse.class); // JSON → DTO
        } catch (JsonProcessingException e) {
            throw new RuntimeException("OAuthUserResponse 역직렬화 실패", e);
        }
    }


    /**
     * 로그인 한 유저 정보 조회
     *
     * @param 엑세스 토큰
     * @return LoginUserResponse
     */
    @Override
    public LoginUserResponse getLoginUser(String accessToken) {
        // 1. accessToken이 유효한지 검사
        if (accessToken == null || !jwtUtil.validateToken(accessToken)) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        // 2. 토큰에서 이메일 추출
        String email = jwtUtil.getUserEmail(accessToken);

        // 3. 이메일로 사용자 조회
        LoginUserResponse loginUserResponse = userMapper.selectLogingUserByEmail(email);

        return loginUserResponse;
    }


    /**
     * 로그아웃 처리 (액세스 토큰 블랙리스트 등록 및 리프레시 토큰 제거)
     *
     * @param token 액세스 토큰
     */
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
     * 리프레시 토큰으로 액세스 토큰 재발급
     *
     * @param refreshToken 유효한 리프레시 토큰
     * @return 새로 발급된 액세스 토큰과 기존 리프레시 토큰
     */
    @Override
    public JwtTokenResponse refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token invalid");
        }

        String email = jwtUtil.getUserEmail(refreshToken);

        // Redis에 저장된 refresh 토큰과 일치하는지 확인
        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + email);

        if (!refreshToken.equals(storedRefreshToken)) {
            throw new IllegalArgumentException("Refresh token mismatch");
        }

        String newAccessToken = jwtUtil.generateAccessToken(email);
        return new JwtTokenResponse(newAccessToken, refreshToken);
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

    @Override
    public void selectApplicationsByUser(String email) {
        User user = userMapper.selectUserByEmail(email);


    }

    @Override
    public void selectPaymentsByUser(String email) {
        User user = userMapper.selectUserByEmail(email);

        transactionMapper.selectTransactionByUserId(user.getUserId());
    }

    @Override
    public void selectTicketsByUser(String email) {

    }

    @Override
    public List<PostAllResponse> selectPostsByUser(String email) {
        User user = userMapper.selectUserByEmail(email);

        return postMapper.selectPostsByUserId(user.getUserId());
    }

    /**
     * 회원 탈퇴
     *
     * @param email
     */
    @Override
    public void deleteUserByEmail(String email) {
        userMapper.deleteUserByEmail(email);
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
