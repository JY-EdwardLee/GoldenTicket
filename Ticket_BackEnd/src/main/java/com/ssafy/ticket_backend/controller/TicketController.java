package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.TicketResponse;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.TicketService;
import java.util.List;
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
     * @param userDetails
     * @param ticketId
     * @return
     */
    @GetMapping("/transfer/{ticketId}")
    public ResponseEntity<TicketResponse> transferTicket(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long ticketId) {
        TicketResponse ticketResponse = ticketService.transferTicket(userDetails.getUsername(),
            ticketId);

        return ResponseEntity.ok(ticketResponse);
    }

    /**
     * 다른 플랫폼에서 티켓 가져오기
     *
     * @return
     */
    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<TicketResponse>> getTicketsFromOtherPlatform(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable String platform) {
        List<TicketResponse> TicketResponse = ticketService.getTicketsFromOtherPlatform(
            userDetails.getUsername(), platform);

        return ResponseEntity.ok(TicketResponse);
    }
}
