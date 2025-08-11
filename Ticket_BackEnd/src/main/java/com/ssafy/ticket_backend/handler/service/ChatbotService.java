package com.ssafy.ticket_backend.handler.service;


import com.ssafy.ticket_backend.dto.request.ChatbotRequest;
import com.ssafy.ticket_backend.dto.response.ChatbotResponse;

public interface ChatbotService {

    ChatbotResponse askPythonChatbot(ChatbotRequest request, String userEmail, String accessToken);
}
