package com.ssafy.ticket_backend.service;

import org.springframework.stereotype.Service;


public interface NotificationService {

    void sendNotificationToUser(String userEmail, String message);

    void sendDelayedNotification(String userEmail, String message, long delayMillis);
}
