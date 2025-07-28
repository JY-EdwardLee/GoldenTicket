package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.UserPatchRequest;
import com.ssafy.ticket_backend.dto.request.UserSignupRequest;
import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.dto.response.OAuthUserResponse;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.UserService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Value("${kakao.rest.api.key}")
    private String KakaoRestApiKey;
    @Value("${naver.client.id}")
    private String NaverClientId;

    // 카카오 로그인 페이지 호출
    @GetMapping("/auth/kakao")
    public RedirectView redirectToKakaoLogin() {
        String kakaoAuthUrl =
            "https://kauth.kakao.com/oauth/authorize" + "?client_id=" + KakaoRestApiKey
                + "&redirect_uri=" + "http://localhost:8080/users/auth/kakao/callback"
                + "&response_type=code";

        return new RedirectView(kakaoAuthUrl);
    }

    // 네이버 로그인 페이지 호출
    @GetMapping("/auth/naver")
    public RedirectView redirectToNaverLogin() {
        String state = "random_state_string"; // CSRF 방지용 (랜덤 문자열 생성 권장)

        String naverAuthUrl =
            "https://nid.naver.com/oauth2.0/authorize" + "?response_type=code" + "&client_id="
                + NaverClientId + "&redirect_uri="
                + "http://localhost:8080/users/auth/naver/callback" + "&state=" + state;

        return new RedirectView(naverAuthUrl);
    }

    // 카카오 로그인 인가 코드 받아서 회원가입 유무에 따라 응답 반환
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<OAuthUserResponse> kakaoCallback(@RequestParam String code) {
        OAuthUserResponse response = userService.loginWithKakao(code);

        return ResponseEntity.ok(response);
    }

    // 네이버 로그인
    @GetMapping("/auth/naver/callback")
    public ResponseEntity<OAuthUserResponse> naverCallback(@RequestParam String code) {
        OAuthUserResponse response = userService.loginWithNaver(code);

        return ResponseEntity.ok(response);
    }

    // 액세스 토큰 만료 시, 리프레시 토큰으로 새 토큰 재발급 요청
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        JwtTokenResponse response = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    // 회원가입 - 유저 객체 받고 토큰 발급 후 반환
    @PostMapping("/signup")
    public ResponseEntity<JwtTokenResponse> signup(
        @RequestBody UserSignupRequest userSignupRequest) {
        JwtTokenResponse tokens = userService.signup(userSignupRequest);

        return ResponseEntity.ok(tokens);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
        @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);

        try {
            userService.logout(token);

            return ResponseEntity.ok(Map.of("message", "성공적으로 로그아웃 되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 마이페이지의 정보를 반환
     *
     * @return
     */
    @GetMapping("/me")
    public ResponseEntity<MyPageResponse> getMyPage(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        MyPageResponse myPage = userService.getMyPage(userDetails.getUsername());

        return ResponseEntity.ok().body(myPage);
    }

    /**
     * 내 정보 수정
     *
     * @param userDetails      JWT를 받아와서 사용
     * @param userPatchRequest 변경하려는 정보
     * @return 성공여부
     */
    @PatchMapping("/me")
    public ResponseEntity<Void> updateMyPage(@AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody UserPatchRequest userPatchRequest) {
        userService.patchMyPage(userDetails.getUsername(), userPatchRequest);

        return ResponseEntity.accepted().build();
    }

    // 로그인 - 테스트 용 로그인이므로 실제 서비스에서는 사용 금지
    @PostMapping("/testlogin")
    public ResponseEntity<JwtTokenResponse> testLogin() {
        JwtTokenResponse tokens = userService.testUser();

        return ResponseEntity.ok(tokens);
    }
}
