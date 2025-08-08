package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.SMSVerificationCheckRequest;
import com.ssafy.ticket_backend.dto.request.SMSVerificationRequest;
import com.ssafy.ticket_backend.service.SMSService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SMS 발송 컨트롤러
 * <p>
 * <a
 * href="https://github.com/coolsms/coolsms-java-examples/blob/main/gradle-spring-demo/src/main/java/net/nurigo/gradlespringdemo/ExampleController.java">...</a>
 * <p>
 * 공식문서
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/signup")
public class SMSController {

    private final SMSService smsService;

    /**
     * 인증 메시지 발송
     *
     * @param smsVerificationRequest 사용자 핸드폰 번호
     * @return OK
     */
    @PostMapping("/verification")
    public ResponseEntity<?> sendVerificationCode(
        @RequestBody SMSVerificationRequest smsVerificationRequest) {
        smsService.sendVerificationCode(smsVerificationRequest.getPhoneNumber());

        return ResponseEntity.ok("발송 완료");
    }

    /**
     * @param smsVerificationCheckRequest 사용자 핸드폰 번호, 인증 번호
     * @return OK
     */
    @PostMapping("/verification/check")
    public ResponseEntity<?> checkVerificationCode(
        @RequestBody SMSVerificationCheckRequest smsVerificationCheckRequest) {
        smsService.checkVerificationCode(smsVerificationCheckRequest);

        return ResponseEntity.ok("인증 완료");
    }
}