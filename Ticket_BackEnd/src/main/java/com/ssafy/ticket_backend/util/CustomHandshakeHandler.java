package com.ssafy.ticket_backend.util;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(
        ServerHttpRequest request,
        WebSocketHandler wsHandler,
        Map<String, Object> attributes) {

        String email = (String) attributes.get("userEmail"); // Interceptor에서 넣은 값
        if (email != null) {
            return () -> email;  // Principal.getName() 으로 사용됨
        }
        return null;
    }
}
