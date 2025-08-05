package com.ssafy.ticket_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendNotificationToUser(String userEmail, String message) {
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/notify", message);
    }

    // 비동기 알림 (5초 지연 후 전송)
    @Async
    public void sendDelayedNotification(String userEmail, String message, long delayMillis) {
        message = "실시간 알림 테스트!";

        try {
            Thread.sleep(delayMillis);

            sendNotificationToUser(userEmail, message);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
