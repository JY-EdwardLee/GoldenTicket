package com.ssafy.ticket_backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String mailUsername;

    @Override
    public void sendGroupFullNotification(Long groupId, String gameInfo) {
        try {
            String subject = "[Golden Ticket] 그룹 정원이 다 찼습니다!";
            String content = String.format(
                "안녕하세요!\n\n" +
                "참가하신 그룹의 정원이 모두 찼습니다.\n" +
                "그룹 ID: %d\n" +
                "게임 정보: %s\n\n" +
                "곧 경기 일정과 관련된 추가 안내를 받으실 수 있습니다.\n\n" +
                "감사합니다.\n" +
                "Golden Ticket 팀", 
                groupId, gameInfo
            );
            
            // 환경 변수에서 설정된 이메일 주소로 전송
            sendEmail(mailUsername, subject, content);
            log.info("그룹 {} 정원 만료 알림 메일 전송 완료: {}", groupId, mailUsername);
            
        } catch (Exception e) {
            log.error("그룹 정원 만료 알림 메일 전송 중 오류 발생: {}", e.getMessage(), e);
            // 메일 전송 실패 시에도 애플리케이션은 계속 실행
        }
    }

    @Override
    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
            log.info("메일 전송 완료: {}", to);
            
        } catch (Exception e) {
            log.error("메일 전송 중 오류 발생 - 수신자: {}, 오류: {}", to, e.getMessage(), e);
            // 메일 전송 실패 시에도 예외를 던지지 않고 로그만 남김
        }
    }

    @Override
    public void sendEmailWithHtmlAttachment(String to, String subject, String htmlContent, String fileName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText("단체 관람 신청서가 첨부되었습니다.", false); // HTML이 아닌 일반 텍스트
            
            // HTML 파일을 첨부
            ByteArrayResource htmlResource = new ByteArrayResource(htmlContent.getBytes(StandardCharsets.UTF_8));
            helper.addAttachment(fileName, htmlResource, "text/html");
            
            mailSender.send(message);
            log.info("HTML 첨부 메일 전송 완료: {}", to);
            
        } catch (MessagingException e) {
            log.error("HTML 첨부 메일 전송 중 오류 발생 - 수신자: {}, 오류: {}", to, e.getMessage(), e);
            // 메일 전송 실패 시에도 예외를 던지지 않고 로그만 남김
        }
    }
}
