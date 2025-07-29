package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.response.TransactionResponse;
import com.ssafy.ticket_backend.model.KakaoTransaction;
import com.ssafy.ticket_backend.model.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TransactionMapper {

    void insertKakaoTransaction(
        @Param("kakaoPayApproveResponse") KakaoTransaction kakaoTransaction);

    Ticket selectTicketByTicketId(@Param("ticketId") long ticketId);

    void transactionComplete(@Param("ticketId") Long ticketId);

    TransactionResponse selectTransactionByUserId(Long userId);
}
