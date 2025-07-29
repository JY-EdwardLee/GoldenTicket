package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.ApplicationGameResponse;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @GetMapping("/{gameId}/applications")
    public ResponseEntity<ApplicationGameResponse> ApplicationGame(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long gameId) {
        gameService.applicationGame(userDetails.getUsername(), gameId);

        return ResponseEntity.ok(new ApplicationGameResponse(true, "응모 완료"));
    }

    @DeleteMapping("/applications") }