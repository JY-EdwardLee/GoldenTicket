package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.GameApplicationRequest;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @PostMapping("/applications")
    public ResponseEntity<?> ApplicationGame(@AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody GameApplicationRequest gameApplicationRequest) {
        gameService.applicationGame(userDetails.getUsername(), gameApplicationRequest);

        return null;
    }
}