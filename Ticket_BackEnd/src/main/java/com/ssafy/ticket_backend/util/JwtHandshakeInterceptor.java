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
     * 웹소켓 연결 전 실행되는 메서드
     * - 여기서 JWT 토큰을 확인하고 유저 정보를 세션에 저장할 수 있음
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

            // Authorization 헤더에서 JWT 토큰 추출
            String authHeader = httpRequest.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // "Bearer " 제거

                // JWT 검증
                if (jwtUtil.validateToken(token)) {
                    // 사용자 이메일 같은 정보 저장 가능 (필요 시 attributes에 추가)
                    String email = jwtUtil.getUserEmail(token);
                    attributes.put("userEmail", email);

                    return true; // 검증 통과 → 연결 허용
                }
            }
        }

        return false; // 검증 실패 → 연결 거부
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
