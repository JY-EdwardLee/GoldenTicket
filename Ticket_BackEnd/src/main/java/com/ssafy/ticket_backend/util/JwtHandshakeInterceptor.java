package com.ssafy.ticket_backend.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;


@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    /**
     * 웹소켓 연결 전 실행되는 메서드 - 여기서 JWT 토큰을 확인하고 유저 정보를 세션에 저장할 수 있음
     */
    @Override
    public boolean beforeHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes
    ) {
        // HTTP 요청 객체로 캐스팅
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            // 1. Authorization 헤더 대신 쿠키에서 access_token 추출
            String cookieHeader = httpRequest.getHeader("Cookie");
            if (cookieHeader != null) {
                String accessToken = extractAccessTokenFromCookie(cookieHeader);
                if (accessToken != null) {
                    if (jwtUtil.validateToken(accessToken)) {
                        String email = jwtUtil.getUserEmail(accessToken);
                        System.out.println("JWT 검증 성공, userEmail: " + email);
                        attributes.put("userEmail", email);
                        return true; // 연결 허용
                    } else {
                        System.out.println("JWT 검증 실패");
                    }
                } else {
                    System.out.println("쿠키에서 access_token 없음");
                }
            } else {
                System.out.println("쿠키 헤더 없음");
            }
        }
        return false; // 검증 실패 → 연결 거부
    }


    private String extractAccessTokenFromCookie(String cookieHeader) {
        for (String cookie : cookieHeader.split(";")) {
            String[] parts = cookie.trim().split("=");
            if (parts.length == 2 && "access_token".equals(parts[0])) {
                return parts[1];
            }
        }
        return null;
    }

    @Override
    public void afterHandshake(
        ServerHttpRequest request,
        ServerHttpResponse response,
        WebSocketHandler wsHandler,
        Exception exception
    ) {
        // 연결 후 후처리 (지금은 필요 없음)
    }
}
