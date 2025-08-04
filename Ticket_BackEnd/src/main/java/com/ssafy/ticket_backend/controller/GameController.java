package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.GameCheckRequest;
import com.ssafy.ticket_backend.dto.response.ApplicationGameResponse;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.GameService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    /**
     * 게임 목록 가져오기
     *
     * @param gameCheckRequest
     * @return
     */
    @PostMapping("")
    public ResponseEntity<List<Game>> selectGame(@RequestBody GameCheckRequest gameCheckRequest) {
        List<Game> games = gameService.selectGame(gameCheckRequest);

        return ResponseEntity.ok(games);
    }

    /**
     * 응모하기
     *
     * @param userDetails
     * @param gameId
     * @return
     */
    @PostMapping("/{gameId}/applications")
    public ResponseEntity<ApplicationGameResponse> applicationGame(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long gameId) {
        gameService.applicationGame(userDetails.getUsername(), gameId);

        return ResponseEntity.ok(new ApplicationGameResponse(true, "응모 완료"));
    }

    /**
     * 응모 취소
     *
     * @param userDetails
     * @param gameId
     * @return
     */
    @DeleteMapping("/{gameId}/applications")
    public ResponseEntity<ApplicationGameResponse> cancelGame(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long gameId) {
        gameService.cancelGame(userDetails.getUsername(), gameId);

        return ResponseEntity.ok(new ApplicationGameResponse(true, "취소 완료"));
    }

    @PatchMapping("/{gameId}/applications")
    public ResponseEntity<ApplicationGameResponse> cancelPaying(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long gameId) {
        gameService.cancelPaying(userDetails.getUsername(), gameId);

        return null;
    }
}