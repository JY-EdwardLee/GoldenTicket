package com.ssafy.ticket_backend.scheduler;

import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.mapper.TransactionMapper;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.Transaction;
import com.ssafy.ticket_backend.model.Waitlist;
import com.ssafy.ticket_backend.model.WaitlistStatus;
import com.ssafy.ticket_backend.service.TicketService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PayScheduler {

    private final TransactionMapper transactionMapper;
    private final TicketMapper ticketMapper;
    private final TicketService ticketService;

    /**
     * 결제를 하였는지 확인하는 API
     */
    @Scheduled(fixedRate = 1000 * 60)
    public void checkGameEnded() {
        List<Ticket> tickets = ticketMapper.checkPayingOver30Minutes();

        for (Ticket ticket : tickets) {
            Transaction transaction = transactionMapper.selectTransactionByTicketId(
                ticket.getTicketId());

            transaction.setTicketId(null);
            transaction.setTransactionStatus(WaitlistStatus.CANCEL_WAITING.toString());
            transactionMapper.updateTransaction(transaction);

            Waitlist waitlist = transactionMapper.selectWaitlistByTransactionId(
                transaction.getTransactionId());

            waitlist.setStatus(WaitlistStatus.CANCEL_WAITING);
            transactionMapper.updateWaitlist(waitlist);

            ticketService.reTransferTicket(ticket);
        }
    }
}
