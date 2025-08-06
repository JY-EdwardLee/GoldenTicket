package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.SMSVerificationCheckRequest;
import com.ssafy.ticket_backend.exception.UserSignupException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SMSService {

    private final DefaultMessageService messageService;
    private final RedisTemplate<String, String> redisTemplate;

    public void sendVerificationCode(String phoneNumber) {
        try {
            String millisStr = String.valueOf(System.currentTimeMillis());  // 무작위 6자리 숫자 생성
            String verificationCode = millisStr.substring(millisStr.length() - 6);  // 무작위 6자리 숫자 생성

            redisTemplate.opsForValue().set(phoneNumber, verificationCode, 5, TimeUnit.MINUTES);

            String text = "안녕하세요. 골든티켓입니다." + "\n" + "인증번호 : " + verificationCode;
            this.sendSMS(phoneNumber, text);
        } catch (Exception e) {
            throw new UserSignupException("메시지 발송 도중 오류가 발생하였습니다.");
        }
    }

    public void checkVerificationCode(SMSVerificationCheckRequest smsVerificationCheckRequest) {
        if (redisTemplate.hasKey(smsVerificationCheckRequest.getPhoneNumber())) {  // 키가 있다면
            if (redisTemplate.opsForValue().get(smsVerificationCheckRequest.getPhoneNumber())
                .equals(smsVerificationCheckRequest.getVerificationCode())) {  // 인증 코드 일치
                redisTemplate.delete(smsVerificationCheckRequest.getPhoneNumber());
            } else {
                throw new UserSignupException("인증번호가 일치하지 않습니다.");
            }
        } else {
            throw new UserSignupException("유효하지 않은 인증번호입니다.");
        }
    }

    /**
     * 메시지 발송
     *
     * @param phoneNumber 핸드폰 번호
     * @param text        보낼 문자
     */
    public void sendSMS(String phoneNumber, String text) {
        try {
            Message message = new Message();

            message.setFrom("01094267242");
            message.setTo(phoneNumber);
            message.setText(text);

            messageService.sendOne(new SingleMessageSendingRequest(message));
        } catch (Exception e) {
            throw new RuntimeException("메시지 발송 도중 오류가 발생하였습니다.");
        }
    }
}
