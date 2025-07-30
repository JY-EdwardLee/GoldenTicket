package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.model.KakaoTransaction;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.Transaction;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionMapper {

    void insertKakaoTransaction(
        @Param("kakaoPayApproveResponse") KakaoTransaction kakaoTransaction);

    Ticket selectTicketByTicketId(@Param("ticketId") long ticketId);

    void transactionComplete(@Param("ticketId") Long ticketId);

    List<Transaction> selectBuyListByUserId(Long userId);
}
