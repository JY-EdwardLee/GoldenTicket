package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.model.OtherPlatformTicket;
import com.ssafy.ticket_backend.model.Ticket;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TicketMapper {

    Ticket selectTicketByTicketId(Long ticketId);

    void updateTicket(Ticket ticket);

    // TODO 오류날 가능성 높음. 테스트 불가능한 상황
    List<OtherPlatformTicket> selectTicketsFromOtherPlatform(@Param("userEmail") String userEmail,
        @Param("platform") String platform);

    void insertTicket(Ticket ticket);
}
