package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.OAuthResponse;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.service.UserService;
import com.ssafy.ticket_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

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

    // 로그아웃
//    @PostMapping("/logout")
//    public ResponseEntity<Void> logout(@RequestParam String token) {
//
//    }

    // 회원가입 - 유저 객체 받고 토큰 발급 후 반환
    @PostMapping("/signup")
    public ResponseEntity<JwtTokenResponse> signup(@RequestBody User user) {
        JwtTokenResponse tokens = userService.signup(user);
        return ResponseEntity.ok(tokens);

    }


}
