package com.ssafy.ticket_backend.config;

import com.ssafy.ticket_backend.handler.CustomHandshakeHandler;
import com.ssafy.ticket_backend.util.JwtHandshakeInterceptor;
import com.ssafy.ticket_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@EnableAsync
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;
    @Value("${FE_BASE_URL}")
    private String FE_BASE_URL;
    @Value("${BE_BASE_URL}")
    private String BE_BASE_URL;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 웹소켓 연결 엔드포인트 (SockJS 포함)
        registry.addEndpoint("api/ws-notify")
            .setAllowedOriginPatterns(FE_BASE_URL, BE_BASE_URL, "http://localhost:8080/",
                "http://localhost:5173/")  // CORS 허용
            .setHandshakeHandler(new CustomHandshakeHandler()) //
            .setAllowedOriginPatterns("*") // CORS 허용, 배포 환경에 맞게 조정 필요
            .addInterceptors(new JwtHandshakeInterceptor(jwtUtil))  // 여기서 JWT 인증 처리 가능
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 메시지 보낼 때 prefix
        registry.setApplicationDestinationPrefixes("/app");
        // 1:1 메시징 (특정 유저 대상)
        registry.enableSimpleBroker("/topic", "/queue");
        // 유저 대상 메시지 전송 시 사용하는 prefix
        registry.setUserDestinationPrefix("/user");
    }
}
