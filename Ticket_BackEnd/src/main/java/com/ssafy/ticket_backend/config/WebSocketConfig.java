package com.ssafy.ticket_backend.config;

import com.ssafy.ticket_backend.util.JwtHandshakeInterceptor;
import com.ssafy.ticket_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 연결 엔드포인트 (SockJS 포함)
        registry.addEndpoint("/ws-notify")
            .setAllowedOriginPatterns("*") // CORS 허용, 배포 환경에 맞게 조정 필요
            .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))  // 여기서 JWT 인증 처리 가능
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 메시지 보낼 때 prefix
        registry.setApplicationDestinationPrefixes("/app");
        // 클라이언트가 구독하는 경로 prefix
        registry.enableSimpleBroker("/topic");
    }

}
