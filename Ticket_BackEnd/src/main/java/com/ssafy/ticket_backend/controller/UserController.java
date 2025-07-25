package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.dto.response.OAuthResponse;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.UserService;
import com.ssafy.ticket_backend.util.JwtUtil;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    // 카카오 로그인 인가 코드 받아서 회원가입 유무에 따라 응답 반환
    @GetMapping("/auth/oauth/callback")
    public ResponseEntity<OAuthResponse> kakaoCallback(@RequestParam String code) {
        OAuthResponse response = userService.loginWithKakao(code);

        return ResponseEntity.ok(response);
    }

    // 네이버 로그인
    @GetMapping("/auth/naver/callback")
    public ResponseEntity<OAuthResponse> naverCallback(@RequestParam String code) {
        OAuthResponse response = userService.loginWithNaver(code);

        return ResponseEntity.ok(response);
    }

    // 액세스 토큰 만료 시, 리프레시 토큰으로 새 토큰 재발급 요청
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token invalid");
        }

        String email = jwtUtil.getUserEmail(refreshToken);

        // Redis에 저장된 refresh 토큰과 일치하는지 확인
        String storedRefreshToken = redisTemplate.opsForValue().get("refresh:" + email);
        if (!refreshToken.equals(storedRefreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token mismatch");
        }

        String newAccessToken = jwtUtil.generateAccessToken(email);
        return ResponseEntity.ok(new JwtTokenResponse(newAccessToken, refreshToken));
    }

    // 회원가입 - 유저 객체 받고 토큰 발급 후 반환
    @PostMapping("/signup")
    public ResponseEntity<JwtTokenResponse> signup(@RequestBody User user) {
        JwtTokenResponse tokens = userService.signup(user);

        return ResponseEntity.ok(tokens);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
        @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("message", "토큰이 없습니다."));
        }

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

    // 로그인 - 테스트 용 로그인이므로 실제 서비스에서는 사용 금지
    @PostMapping("/testlogin")
    public ResponseEntity<JwtTokenResponse> testLogin() {
        JwtTokenResponse tokens = userService.testUser();

        return ResponseEntity.ok(tokens);
    }
}
