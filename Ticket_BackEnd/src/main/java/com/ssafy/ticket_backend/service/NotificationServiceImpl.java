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

//        System.out.println("알림 예약됨: " + userEmail + ", 메시지: " + message);


        try {
            Thread.sleep(delayMillis);
//            System.out.println("[sendDelayedNotification] 지연 완료, 알림 전송 시작: " + userEmail);

            sendNotificationToUser(userEmail, message);
//            System.out.println("[sendDelayedNotification] 알림 전송 완료: " + userEmail);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
