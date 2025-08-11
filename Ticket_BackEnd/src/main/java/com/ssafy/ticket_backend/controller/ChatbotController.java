package com.ssafy.ticket_backend.controller;


import com.ssafy.ticket_backend.dto.request.ChatbotRequest;
import com.ssafy.ticket_backend.dto.response.ChatbotHistoryResponse;
import com.ssafy.ticket_backend.dto.response.ChatbotResponse;
import com.ssafy.ticket_backend.service.ChatbotService;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/history")
    public List<ChatbotHistoryResponse> getHistory( @RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        String accessToken = null;
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            accessToken = authorizationHeader.substring(7);
        }

        if (accessToken == null) {
            // 토큰 없으면 빈 리스트 반환하거나 401 처리 가능
            return List.of();
        }

        return chatbotService.getChatHistory(accessToken);
    }

}
