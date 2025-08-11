package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.handler.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
}
