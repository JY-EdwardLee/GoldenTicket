package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.ChatbotRequest;
import com.ssafy.ticket_backend.dto.request.GameForChatbotRequest;
import com.ssafy.ticket_backend.dto.response.ChatbotResponse;
import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.model.Game;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final RestTemplate restTemplate;
    private final GameMapper gameMapper;
    private String pythonApiUrl = "http://localhost:8000/query";


    @Override
    public ChatbotResponse askPythonChatbot(ChatbotRequest request, String userEmail,
        String accessToken) {
        // 요청 바디 구성
        Map<String, Object> body = new HashMap<>();
        body.put("question", request.getQuestion()); // 질문 내용
        body.put("sessionId", accessToken); // 토큰으로 세션ID 처리

        // 경기 정보 DB에서 조회
        List<GameForChatbotRequest> games = gameMapper.selectGameForChatbot();

        List<Map<String, Object>> gameList = games.stream().map(game -> {
            Map<String, Object> gameMap = new HashMap<>();
            gameMap.put("game_datetime", game.getGameDatetime().toString());
            gameMap.put("home_team", game.getHomeTeam());
            gameMap.put("away_team", game.getAwayTeam());
            gameMap.put("stadium", game.getStadium());
            return gameMap;
        }).toList();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("gameInfo", gameList);

        body.put("userInfo", userInfo);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonApiUrl, requestEntity,
                Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                String answer = responseBody.getOrDefault("reply", "답변이 없습니다.").toString();

                Object linkObj = responseBody.get("link");
                String link =
                    (linkObj != null && !"null".equals(linkObj.toString())) ? linkObj.toString()
                        : null;

                ChatbotResponse.Action action = null;
                Object actionObj = responseBody.get("action");
                if (actionObj instanceof Map) {
                    Map<String, Object> actionMap = (Map<String, Object>) actionObj;
                    String type = (String) actionMap.get("type");
                    String target = (String) actionMap.get("target");
                    Object params = actionMap.get("params");
                    action = new ChatbotResponse.Action(type, target, params);
                }

                return new ChatbotResponse(answer, link, action);
            } else {
                return new ChatbotResponse("챗봇 서버와 통신에 실패했습니다.", null, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatbotResponse("챗봇 요청 중 오류가 발생했습니다.", null, null);
        }


    }

}

