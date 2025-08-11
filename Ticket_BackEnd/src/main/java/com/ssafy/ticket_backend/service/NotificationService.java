package com.ssafy.ticket_backend.handler.service;

public interface NotificationService {

    void sendNotificationToUser(String userEmail, String message);

}
