package com.ssafy.ticket_backend.controller;


import com.ssafy.ticket_backend.dto.request.ChatbotRequest;
import com.ssafy.ticket_backend.dto.response.ChatbotResponse;
import com.ssafy.ticket_backend.service.ChatbotService;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping
    public ChatbotResponse getChatResponse(
        @RequestBody ChatbotRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {

        String userEmail = null;
        if (request.isLogined()) {
            userEmail = userDetails.getUsername();
        }

        // 안전하게 accessToken 추출
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7); // "Bearer " 이후의 실제 토큰만 추출
        }

        ChatbotResponse response = chatbotService.askPythonChatbot(request, userEmail, accessToken);

        return response;
    }


}
