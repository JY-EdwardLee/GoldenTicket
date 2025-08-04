package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.service.NotificationService;
import com.ssafy.ticket_backend.service.NotificationServiceImpl;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    // 테스트 코드
    @PostMapping("/test/sendDelayedNotification")
    public ResponseEntity<String> sendDelayedNotification(Principal principal) {
        // UserDetails에서 이메일 가져오기 (보통 username 필드가 이메일임)
        String userEmail = principal.getName();
//        System.out.println("컨트롤러 호출됨 - 요청한 유저 이메일: " + userEmail);


        // 5초(5000ms) 지연 후 알림 전송
        notificationService.sendDelayedNotification(userEmail, "5초 후 도착한 자동 알림입니다!", 5000);
//        System.out.println("알림 서비스 호출 완료");

        return ResponseEntity.ok("5초 후 알림이 발송됩니다.");
    }

}
