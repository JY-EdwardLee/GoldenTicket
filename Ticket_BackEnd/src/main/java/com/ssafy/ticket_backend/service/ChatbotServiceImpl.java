package com.ssafy.ticket_backend.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final RestTemplate restTemplate;
    private String pythonApiUrl = "http://localhost:8000/query";

    public ChatbotServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String askPythonChatbot(String question) {
        // 요청 바디 구성
        Map<String, String> body = new HashMap<>();
        body.put("question", question);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        // 파이썬 API에서 POST 요청
        ResponseEntity<Map> response = restTemplate.postForEntity(pythonApiUrl, requestEntity,
            Map.class);

        // 파이썬 서버에서 받은 JSON에서 "reply" 값 추출
        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Object replyObj = response.getBody().get("reply");
            return replyObj != null ? replyObj.toString() : "답변이 없습니다";
        } else {
            return "챗봇 서버와 통신에 실패했습니다.";
        }
    }
}
