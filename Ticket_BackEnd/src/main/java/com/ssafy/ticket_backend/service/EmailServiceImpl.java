package com.ssafy.ticket_backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "spring.mail.host")
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendEmailWithHtmlAttachment(String to, String subject, String htmlContent,
        String fileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false,
                StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            
            // HTML 내용을 이메일 본문으로 직접 설정
            helper.setText(htmlContent, true); // true는 HTML 형식

            mailSender.send(message);
            log.info("HTML 본문 메일 전송 완료: {}", to);

        } catch (MessagingException e) {
            log.error("HTML 본문 메일 전송 중 오류 발생 - 수신자: {}, 오류: {}", to, e.getMessage(), e);
        }
    }

    
}
