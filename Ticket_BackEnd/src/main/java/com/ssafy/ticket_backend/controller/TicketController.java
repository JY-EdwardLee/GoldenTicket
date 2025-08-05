package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.TicketResponse;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.TicketService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    /**
     * 티켓 양도
     *
     * @param userDetails 사용자 정보
     * @param ticketId    티켓의 id
     * @return 티켓의 정보
     */
    @GetMapping("/transfer/{ticketId}")
    public ResponseEntity<Map<String, Object>> transferTicket(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long ticketId) {
        TicketResponse ticketResponse = ticketService.transferTicket(userDetails.getUsername(),
            ticketId);
//        return ResponseEntity.ok(ticketResponse);

        return ResponseEntity.ok(Map.of("success", true, "message", "양도완료"));
    }

    /**
     * 다른 플랫폼에서 티켓 가져오기
     *
     * @param userDetails 사용자 정보
     * @param platform    NOL, TICKETLINK 중 하나
     * @return 티켓의 정보
     */
    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<TicketResponse>> getTicketsFromOtherPlatform(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String platform) {
        List<TicketResponse> TicketResponse = ticketService.getTicketsFromOtherPlatform(
            userDetails.getUsername(), platform);

        return ResponseEntity.ok(TicketResponse);
    }

    @GetMapping("/details/{ticketId}")
    public ResponseEntity<TicketResponse> getTicketDetail(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long ticketId) {
        TicketResponse ticketDetail = ticketService.getTicketDetail(userDetails.getUsername(),
            ticketId);

        return ResponseEntity.ok(ticketDetail);
    }
}
