package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.model.GroupTransaction;
import com.ssafy.ticket_backend.model.GroupWaitlist;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.Transaction;
import com.ssafy.ticket_backend.model.Waitlist;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionMapper {

    Ticket selectTicketByTicketId(@Param("ticketId") long ticketId);

    void transactionComplete(@Param("ticketId") Long ticketId);

    List<Transaction> selectBuyListByUserId(@Param("userId") Long userId);

    List<GroupTransaction> selectGroupBuyListByUserId(@Param("userId") Long userId);

    Ticket selectTicketByTransactionId(Long transactionId);

    void cancelTransactionByTransactionId(@Param("transactionId") Long transactionId);

    void insertTransaction(Transaction transaction);

    void insertGroupTransaction(GroupTransaction groupTransaction);

    Waitlist selectWaitlistByUserIdAndGameId(@Param("userId") Long userId,
        @Param("gameId") Long gameId);

    GroupWaitlist selectGroupWaitlistByUserIdAndGameId(@Param("userId") Long userId,
        @Param("gameId") Long gameId);

    void updateWaitlist(Waitlist waitlist);

    void updateGroupWaitlist(GroupWaitlist groupWaitlist);

    Transaction selectTransactionByTicketId(@Param("ticketId") Long ticketId);

    void updateTransaction(Transaction transaction);
}
