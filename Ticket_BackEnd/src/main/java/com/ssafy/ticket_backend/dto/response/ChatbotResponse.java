package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 챗봇 응답 DTO - answer: 챗봇이 사용자에게 보낼 자연어 답변 - link: 특정 페이지 URL (없으면 null) - action: 사용자가 특정 행동(예: 페이지
 * 이동)을 하도록 지시하는 객체 (없으면 null)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatbotResponse {

    // 사용자에게 보여줄 자연스러운 답변 메시지
    private String answer;

    // 특정 페이지로 이동해야 할 때 제공하는 링크 (없으면 null)
    private String link;

    // 클라이언트에서 처리할 행동 지시 (예: 페이지 이동, 모달 띄우기 등)
    private Action action;

    /**
     * 페이지 이동 등 행동 지시용 내부 클래스
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Action {

        // 행동 종류 (예: "navigate", "alert", "modal" 등)
        private String type;

        // 행동 대상 (예: "application" 페이지 이름)
        private String target;

        // 행동에 필요한 추가 파라미터 (예: 팀명, 날짜 등)
        private Object params;
    }
}

