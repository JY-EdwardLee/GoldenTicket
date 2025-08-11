package com.ssafy.ticket_backend.controller;


import com.ssafy.ticket_backend.dto.request.ChatbotRequest;
import com.ssafy.ticket_backend.dto.response.ChatbotHistoryResponse;
import com.ssafy.ticket_backend.dto.response.ChatbotResponse;
import com.ssafy.ticket_backend.service.ChatbotService;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

        // 안전하게 accessToken 추출
        String accessToken = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            accessToken = authorizationHeader.substring(7); // "Bearer " 이후의 실제 토큰만 추출
            userEmail = userDetails.getUsername();
        }

        System.out.println("userEmail: " + userEmail);

        ChatbotResponse response = chatbotService.askPythonChatbot(request, userEmail, accessToken);

        return response;
    }

    @GetMapping("/history")
    public ResponseEntity<?>  getHistory(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        if (userDetails == null) {
            // 로그인 안 된 사용자에 대해 401 Unauthorized 응답
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("챗봇 대화 내역 불러오기는 로그인이 필요한 서비스입니다.");
        }

        String userEmail = userDetails.getUsername();
        List<ChatbotHistoryResponse> history = chatbotService.getChatHistory(userEmail);
        return ResponseEntity.ok(history);
    }


}
