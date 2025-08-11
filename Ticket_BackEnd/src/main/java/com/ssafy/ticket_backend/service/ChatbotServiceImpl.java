package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.ChatbotRequest;
import com.ssafy.ticket_backend.dto.request.GameForChatbotRequest;
import com.ssafy.ticket_backend.dto.response.ChatbotHistoryResponse;
import com.ssafy.ticket_backend.dto.response.ChatbotResponse;
import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.model.ChatbotSender;
import com.ssafy.ticket_backend.model.Game;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
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
    private final StringRedisTemplate redisTemplate;
    private String pythonApiUrl = "http://localhost:8000/query";

    private static final int MAX_HISTORY_SIZE = 10;
    private static final Duration HISTORY_TTL = Duration.ofDays(7);

    /**
     * 사용자의 질문을 받아서 Python 챗봇 API에 전달하고, 응답을 받아서 저장 및 반환한다.
     *
     * @param request     사용자 질문이 포함된 요청 DTO
     * @param userEmail   사용자 이메일 (세션 ID 역할)
     * @param accessToken 인증 토큰
     * @return 챗봇의 응답과 링크, 액션 정보를 포함한 ChatbotResponse 객체
     */
    @Override
    public ChatbotResponse askPythonChatbot(ChatbotRequest request, String userEmail,
        String accessToken) {
        // 요청 바디 구성
        Map<String, Object> body = new HashMap<>();
        body.put("question", request.getQuestion()); // 질문 내용
        body.put("sessionId", userEmail); // 유저 이메일로 세션ID 처리

        // 질문 저장
        appendChatHistory(accessToken, "user", request.getQuestion());

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

                // 챗봇 답변 저장
                appendChatHistory(accessToken, "bot", answer);

                return new ChatbotResponse(answer, link, action);
            } else {
                return new ChatbotResponse("챗봇 서버와 통신에 실패했습니다.", null, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatbotResponse("챗봇 요청 중 오류가 발생했습니다.", null, null);
        }


    }

    /**
     * Redis에서 세션 ID 기준으로 저장된 최근 대화 히스토리를 불러와 질문-답변 쌍으로 반환한다.
     *
     * @param sessionId 사용자 세션 또는 토큰 식별자
     * @return 질문, 답변, 링크, 타임스탬프를 담은 대화 기록 리스트
     */
    @Override
    public List<ChatbotHistoryResponse> getChatHistory(String sessionId) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();

        List<String> entries = listOps.range(sessionId, 0, MAX_HISTORY_SIZE - 1);
        if (entries == null || entries.isEmpty()) {
            return List.of();
        }

        List<ChatbotHistoryResponse> qnaList = new ArrayList<>();

        String lastQuestion = null;
        LocalDateTime questionTimestamp = null;

        for (String entry : entries) {
            // entry: "role::message::timestamp"
            String[] parts = entry.split("::", 3);
            if (parts.length < 3) {
                continue;
            }

            String role = parts[0];
            String message = parts[1];
            LocalDateTime timestamp = LocalDateTime.parse(parts[2]);

            if ("user".equalsIgnoreCase(role)) {
                // user 메시지는 질문이니까 저장해두고 다음 봇 메시지 기다림
                lastQuestion = message;
                questionTimestamp = timestamp;

            } else if ("bot".equalsIgnoreCase(role)) {
                // 봇 메시지는 답변 -> 질문-답변 쌍으로 응답 리스트에 추가
                if (lastQuestion != null) {
                    // 링크 정보가 있으면 여기서 넣어야 하는데, 지금 Redis엔 없으니 null로 둠
                    qnaList.add(
                        new ChatbotHistoryResponse(lastQuestion, message, null, questionTimestamp));
                    lastQuestion = null;
                    questionTimestamp = null;
                }
            }
        }

        return qnaList;
    }


    /**
     * Redis에 새 대화 기록 추가 (role, 내용, 타임스탬프 포함), 최대 저장 개수 유지 및 TTL 갱신 처리.
     *
     * @param sessionId 사용자 세션 또는 토큰 식별자
     * @param role      메시지 발신자 구분 (user 또는 bot)
     * @param content   메시지 내용
     */
    private void appendChatHistory(String sessionId, String role, String content) {
        ListOperations<String, String> listOps = redisTemplate.opsForList();
        String entry = role + "::" + content + "::" + LocalDateTime.now().toString();

        listOps.rightPush(sessionId, entry);  // key: sessionId, value: entry

        // 최대 히스토리 개수 유지 (최근 10개만)
        listOps.trim(sessionId, -MAX_HISTORY_SIZE, -1);

        // TTL 설정 (매번 갱신)
        redisTemplate.expire(sessionId, HISTORY_TTL);
    }
}


