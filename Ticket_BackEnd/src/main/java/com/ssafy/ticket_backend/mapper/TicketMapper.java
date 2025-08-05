package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.model.OtherPlatformTicket;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.Waitlist;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TicketMapper {

    Ticket selectTicketByTicketId(Long ticketId);

    void updateTicket(Ticket ticket);

    List<OtherPlatformTicket> selectTicketsFromOtherPlatform(@Param("userEmail") String userEmail,
        @Param("platform") String platform);

    void insertTicket(Ticket ticket);

    boolean checkTicketByGame(Long gameId, String seat, Long userId);

    List<Ticket> selectTicketsByBuyerId(Long userId);

    List<Waitlist> selectWaitListByGameId(@Param("gameId") Long gameId);

    List<Waitlist> selectWaitingWaitListByGameId(@Param("gameId") Long gameId);

    int countWaitListByGameId(Long gameId);

    List<Waitlist> selectWaitlistByUserId(@Param("userId") Long userId);

    void transferTicket(@Param("ticketId") String ticketId, @Param("userId") String userId);

    // 더미 데이터 생성용
    int insertOtherPlatformTicket(OtherPlatformTicket otherPlatformTicket);

    Ticket selectTicketByGame(Long gameId, String seat, Long userId);

    void deleteWaitListByUserIdAndGameId(@Param("buyer") Long buyer, @Param("gameId") Long gameId);
}
