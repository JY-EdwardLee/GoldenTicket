package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.exception.TicketTransferException;
import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.util.QRCodeUtil;
import java.sql.Timestamp;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;

    public String createQRCode(String username, Long ticketId) {
        try {

            User user = userMapper.selectUserByEmail(username);

            Ticket ticket = ticketMapper.selectTicketByTicketId(ticketId);

            if (!Objects.equals(ticket.getBuyerId(), user.getUserId())) {  // 구매자가 아니라면
                throw new TicketTransferException("티켓과 사용자 정보가 일치하지 않습니다.");
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String qrText = "userId:" + user.getUserId() + ",ticketId:" + ticketId + ",timestamp:" + timestamp.getTime();
            String logoPath = "src/main/resources/static/goldenticketLogo.png";

            return QRCodeUtil.generateQRCodeImage(qrText, 350, 350, logoPath);
        } catch (Exception e) {
            throw new RuntimeException("QR 코드 생성 중 오류가 발생했습니다.", e);
        }
    }
}
