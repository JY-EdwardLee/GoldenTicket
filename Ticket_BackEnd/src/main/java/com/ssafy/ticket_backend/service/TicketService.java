package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.TicketResponse;
import java.util.List;

public interface TicketService {

    TicketResponse transferTicket(String userEmail, Long ticketId);

    List<TicketResponse> getTicketsFromOtherPlatform(String userEmail, String platform);

    void insertTicketsFromOtherPlatform(String userEmail, String platform);
}
