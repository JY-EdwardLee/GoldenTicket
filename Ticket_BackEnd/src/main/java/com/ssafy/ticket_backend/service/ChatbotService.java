package com.ssafy.ticket_backend.service;

import org.springframework.stereotype.Service;

@Service
public interface ChatbotService {
    String askPythonChatbot(String question);
}
