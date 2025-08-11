package com.ssafy.ticket_backend.service;

/**
 * 이메일 서비스 인터페이스
 */
public interface EmailService {

    /**
     * HTML 내용을 이메일 본문으로 전송
     * @param to 수신자 이메일
     * @param subject 메일 제목
     * @param htmlContent HTML 내용
     * @param fileName (사용하지 않음, 호환성을 위해 유지)
     */
    void sendEmailWithHtmlAttachment(String to, String subject, String htmlContent, String fileName);


}
