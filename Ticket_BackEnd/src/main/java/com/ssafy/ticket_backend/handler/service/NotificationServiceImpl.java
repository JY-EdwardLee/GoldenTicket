package com.ssafy.ticket_backend.handler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;


    // 당첨 된 사람에게 알림
    @Override
    public void sendNotificationToUser(String userEmail, String message) {

        // userEmail을 기준으로 /user/{userEmail}/queue/notify 경로로 알림 전송
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/notify", message);
    }

}
