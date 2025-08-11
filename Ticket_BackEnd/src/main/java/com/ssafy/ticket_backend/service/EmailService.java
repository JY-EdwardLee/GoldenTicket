package com.ssafy.ticket_backend.service;

public interface EmailService {

    /**
     * 그룹 정원이 다 찼을 때 참가자들에게 메일을 전송
     *
     * @param groupId  그룹 ID
     * @param gameInfo 게임 정보
     */
    void sendGroupFullNotification(Long groupId, String gameInfo);

    /**
     * 일반 메일 전송
     *
     * @param to      수신자 이메일
     * @param subject 메일 제목
     * @param content 메일 내용
     */
    void sendEmail(String to, String subject, String content);
}
