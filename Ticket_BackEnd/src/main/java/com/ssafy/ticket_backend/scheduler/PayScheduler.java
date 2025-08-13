package com.ssafy.ticket_backend.scheduler;

import com.ssafy.ticket_backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayScheduler {

    private final TicketService ticketService;

    /**
     * 결제를 하였는지 확인하는 API
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void checkGameEnded() {
        ticketService.CancelTicket();
    }
}
