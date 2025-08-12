package com.ssafy.ticket_backend.service;


import com.ssafy.ticket_backend.dto.request.ChatbotRequest;
import com.ssafy.ticket_backend.dto.response.ChatbotHistoryResponse;
import com.ssafy.ticket_backend.dto.response.ChatbotResponse;
import java.util.List;

public interface ChatbotService {

    // 챗봇 질문 (파이썬 호출)
    ChatbotResponse askPythonChatbot(ChatbotRequest request, String userEmail, String accessToken);

    // 챗봇 대화 기록 10개 호출
    List<ChatbotHistoryResponse> getChatHistory(String sessionId);
}
