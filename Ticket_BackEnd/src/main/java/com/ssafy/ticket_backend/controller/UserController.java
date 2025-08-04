package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.UserPatchRequest;
import com.ssafy.ticket_backend.dto.request.UserSignupRequest;
import com.ssafy.ticket_backend.dto.response.JwtTokenResponse;
import com.ssafy.ticket_backend.dto.response.LoginUserResponse;
import com.ssafy.ticket_backend.dto.response.MyApplicationResponse;
import com.ssafy.ticket_backend.dto.response.MyPageResponse;
import com.ssafy.ticket_backend.dto.response.OAuthUserResponse;
import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.dto.response.TicketResponse;
import com.ssafy.ticket_backend.dto.response.TransactionResponse;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.UserService;
import com.ssafy.ticket_backend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    private final JwtUtil jwtUtil;

    @Value("${kakao.rest.api.key}")
    private String KakaoRestApiKey;
    @Value("${naver.client.id}")
    private String NaverClientId;

    /**
     * 카카오 로그인 페이지로 리다이렉트
     *
     * @return RedirectView 카카오 인증 URL로 리다이렉션
     */
    @GetMapping("/auth/kakao")
    public RedirectView redirectToKakaoLogin() {
        String kakaoAuthUrl =
            "https://kauth.kakao.com/oauth/authorize" + "?client_id=" + KakaoRestApiKey
                + "&redirect_uri=" + "http://i13a109.p.ssafy.io:8080" + "/users/auth/kakao/callback"
                + "&response_type=code";

        return new RedirectView(kakaoAuthUrl);
    }

    /**
     * 네이버 로그인 페이지로 리다이렉트
     *
     * @return RedirectView 네이버 인증 URL로 리다이렉션
     */
    @GetMapping("/auth/naver")
    public RedirectView redirectToNaverLogin() {
        String state = "random_state_string"; // CSRF 방지용 (랜덤 문자열 생성 권장)

        String naverAuthUrl =

            "https://nid.naver.com/oauth2.0/authorize" + "?response_type=code" + "&client_id="
                + NaverClientId + "&redirect_uri="
                + "http://i13a109.p.ssafy.io:8080/users/auth/naver/callback" + "&state=" + state;

        return new RedirectView(naverAuthUrl);
    }

    /**
     * 카카오 로그인 콜백 처리
     *
     * @param code     카카오에서 발급한 인가 코드
     * @param response HttpServletResponse (JWT 쿠키 저장용)
     * @return OAuthUserResponse 또는 리다이렉션 응답
     */
    @GetMapping("/auth/kakao/callback")
    public ResponseEntity<OAuthUserResponse> kakaoCallback(@RequestParam String code,
        HttpServletResponse response) {
        OAuthUserResponse userResponse = userService.loginWithKakao(code);

        if (!userResponse.isRegistered()) {
            // 1) 비회원인 경우, 임시 사용자 정보 저장하고 식별자 반환
            String tempUserId = userService.storeTempUserInfo(userResponse);

            // 2) 회원가입 페이지로 리다이렉트하면서 tempUserId 전달
            return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://i13a109.p.ssafy.io/signup?tempUserId=" + tempUserId)
                .build();
        }

        // 이미 가입된 유저라면 JWT를 HttpOnly 쿠키에 저장
        JwtTokenResponse tokens = userResponse.getToken();

        ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.getAccessToken())
            .httpOnly(true).secure(false) // 배포시 true로 변경
            .path("/").sameSite("Lax").maxAge(60 * 60) // 1시간
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token",
                tokens.getRefreshToken()).httpOnly(true).secure(false).path("/").sameSite("Lax")
            .maxAge(7 * 24 * 60 * 60) // 7일
            .build();
        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // 로그인 완료 후 프론트 리다이렉트 (인증 상태 확인 페이지)
        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", "http://i13a109.p.ssafy.io/oauth/callback").build();
    }

    /**
     * 네이버 로그인 콜백 처리
     *
     * @param code     네이버에서 발급한 인가 코드
     * @param response HttpServletResponse (JWT 쿠키 저장용)
     * @return OAuthUserResponse 또는 리다이렉션 응답
     */
    @GetMapping("/auth/naver/callback")
    public ResponseEntity<OAuthUserResponse> naverCallback(@RequestParam String code,
        HttpServletResponse response) {

        OAuthUserResponse userResponse = userService.loginWithNaver(code);

        if (!userResponse.isRegistered()) {
            // 1. 비회원인 경우, Redis에 임시 유저 정보 저장
            String tempUserId = userService.storeTempUserInfo(userResponse);

            // 2. 회원가입 페이지로 리다이렉트 + tempUserId 쿼리파라미터로 전달
            return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "http://i13a109.p.ssafy.io/signup?tempUserId=" + tempUserId)
                .build();
        }

        // 이미 가입된 회원이라면 토큰을 쿠키에 저장
        JwtTokenResponse tokens = userResponse.getToken();

        ResponseCookie accessCookie = ResponseCookie.from("access_token", tokens.getAccessToken())
            .httpOnly(true).secure(false) // 배포 시 true
            .path("/").sameSite("Lax").maxAge(60 * 60) // 1시간
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token",
                tokens.getRefreshToken()).httpOnly(true).secure(false).path("/").sameSite("Lax")
            .maxAge(7 * 24 * 60 * 60) // 7일
            .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // 로그인 성공 후 프론트엔드로 리다이렉트
        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", "http://i13a109.p.ssafy.io/oauth/callback").build();
    }

    /**
     * 로그인 유저 정보 반환
     *
     * @return LoginUserResponse 로그인 유저 정보 응답 DTO
     */
    @GetMapping("/login-user")
    public ResponseEntity<LoginUserResponse> getLoginUser(HttpServletRequest request) {
        // 1. 쿠키에서 accessToken 추출
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("access_token".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        LoginUserResponse loginUserResponse = userService.getLoginUser(accessToken);
        loginUserResponse.setAccessToken(accessToken);

        return ResponseEntity.ok(loginUserResponse);
    }


    /**
     * tempUserId로 Redis에 저장된 임시 사용자 정보 조회
     *
     * @param tempUserId 임시 사용자 ID
     * @return OAuthUserResponse 사용자 정보
     */
    @GetMapping("/auth/temp-user")
    public ResponseEntity<OAuthUserResponse> getTempUserInfo(@RequestParam String tempUserId) {
        OAuthUserResponse oAuthUserResponse = userService.getTempUserInfo(tempUserId);
        return ResponseEntity.ok(oAuthUserResponse);
    }


    /**
     * 리프레시 토큰으로 JWT 재발급
     *
     * @param request refreshToken 포함한 요청 body
     * @return 새롭게 발급된 JwtTokenResponse
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        JwtTokenResponse jwtTokenResponse = userService.refreshToken(refreshToken);
        return ResponseEntity.ok(jwtTokenResponse);
    }

    /**
     * 회원가입
     *
     * @param userSignupRequest 회원가입 요청 정보
     * @return JwtTokenResponse 토큰 응답
     */
    @PostMapping("/signup")
    public ResponseEntity<JwtTokenResponse> signup(@RequestBody UserSignupRequest userSignupRequest,
        HttpServletResponse response) {
        JwtTokenResponse tokens = userService.signup(userSignupRequest);
        // accessToken 쿠키 설정
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", tokens.getAccessToken())
            .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(Duration.ofMinutes(30))
            .build();

        // refreshToken 쿠키 설정
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
            .httpOnly(true).secure(false).sameSite("Lax").path("/auth/refresh")
            .maxAge(Duration.ofDays(14)).build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        return ResponseEntity.ok().build();
    }

    /**
     * 로그아웃 처리
     *
     * @param authHeader Authorization 헤더 (Bearer 토큰)
     * @return 로그아웃 성공/실패 메시지
     */
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
     * 회원 탈퇴
     *
     * @return
     */
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeader) {

        String accessToken = authHeader.substring(7);

        userService.deleteUserByEmail(accessToken);

        return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
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

    /**
     * 나의 응모 목록
     *
     * @param userDetails
     * @return
     */
    @GetMapping("/me/applications")
    public ResponseEntity<List<MyApplicationResponse>> getMyApplications(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<MyApplicationResponse> myApplicationResponses = userService.selectApplicationsByUser(
            userDetails.getUsername());

        return ResponseEntity.ok(myApplicationResponses);
    }

    /**
     * 나의 결제
     *
     * @param userDetails
     * @return
     */
    @GetMapping("/me/payments")
    public ResponseEntity<List<TransactionResponse>> getMyPayments(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TransactionResponse> transactionResponses = userService.selectBuyListByUserId(
            userDetails.getUsername());

        return ResponseEntity.ok(transactionResponses);
    }

    /**
     * 나의 티켓 목록
     *
     * @return
     */
    @GetMapping("/me/tickets")
    public ResponseEntity<List<TicketResponse>> getMyTickets(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<TicketResponse> ticketResponses = userService.selectTicketsByUser(
            userDetails.getUsername());

        return ResponseEntity.ok(ticketResponses);
    }

    /**
     * 나의 게시글 목록
     *
     * @return
     */
    @GetMapping("/me/posts")
    public ResponseEntity<List<PostAllResponse>> getMyPosts(
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<PostAllResponse> postAllResponses = userService.selectPostsByUser(
            userDetails.getUsername());
        return ResponseEntity.ok(postAllResponses);
    }

    // 로그인 - 테스트 용 로그인이므로 실제 서비스에서는 사용 금지

    @PostMapping("/testlogin")
    public ResponseEntity<JwtTokenResponse> testLogin() {
        JwtTokenResponse tokens = userService.testUser();

        return ResponseEntity.ok(tokens);
    }
}
