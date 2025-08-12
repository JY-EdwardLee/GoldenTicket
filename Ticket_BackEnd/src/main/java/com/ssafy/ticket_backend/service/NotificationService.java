package com.ssafy.ticket_backend.service;

public interface NotificationService {

    void sendNotificationToUser(String userEmail, String message);

}
