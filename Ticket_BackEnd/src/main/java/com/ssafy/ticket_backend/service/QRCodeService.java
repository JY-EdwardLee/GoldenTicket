package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.exception.TicketTransferException;
import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.TicketStatus;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.util.AesEncryptor;
import com.ssafy.ticket_backend.util.QRCodeUtil;
import java.sql.Timestamp;
import java.util.Base64;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QRCodeService {

    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;
    private final GameMapper gameMapper;
    private final AesEncryptor aesEncryptor;
    @Value("${FE_BASE_URL}")
    private String FE_BASE_URL;

    public String createQRCode(String userEmail, Long ticketId) {
        try {

            User user = userMapper.selectUserByEmail(userEmail);
            Ticket ticket = ticketMapper.selectTicketByTicketId(ticketId);
            Game game = gameMapper.selectGameByGameId(ticket.getGameId());

            if (!Objects.equals(ticket.getBuyerId(), user.getUserId())) {  // 구매자가 아니라면
                throw new TicketTransferException("티켓과 사용자 정보가 일치하지 않습니다.");
            }

            if (game.isEnded() || game.isCanceled()) {
                throw new TicketTransferException("이미 종료된 게임입니다.");
            }

            if (ticket.getTicketStatus().equals(TicketStatus.USED)) {
                throw new TicketTransferException("이미 사용한 티켓입니다.");
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String qrText = "userId:" + user.getUserId() + ",ticketId:" + ticketId + ",timestamp:"
                + timestamp.getTime();

            byte[] qrImage = QRCodeUtil.generateQRCodeImage(
                FE_BASE_URL + "/qrcode/check/" + aesEncryptor.encrypt(qrText), 300, 300);

            return Base64.getEncoder().encodeToString(qrImage);
        } catch (Exception e) {
            throw new RuntimeException("QR 코드 생성 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional
    public void checkValidQRCode(String userEmail, String qrcode) {
        try {
            String decrypt = aesEncryptor.decrypt(qrcode);

            Long userId = null;
            Long ticketId = null;
            Timestamp timestamp = null;

            for (String part : decrypt.split(",")) {
                String[] keyValue = part.split(":");
                if (keyValue.length == 2) {
                    String key = keyValue[0];
                    String value = keyValue[1];

                    switch (key) {
                        case "userId":
                            userId = Long.parseLong(value);
                            break;
                        case "ticketId":
                            ticketId = Long.parseLong(value);
                            break;
                        case "timestamp":
                            timestamp = new Timestamp(Long.parseLong(value));
                            break;
                    }
                }
            }

            User user = userMapper.selectUserByEmail(userEmail);
            Ticket ticket = ticketMapper.selectTicketByTicketId(ticketId);
            Game game = gameMapper.selectGameByGameId(ticket.getGameId());

            if (!Objects.equals(user.getUserId(), userId)) {
                throw new TicketTransferException("사용자 정보가 일치하지 않습니다.");
            }

            if (!Objects.equals(ticket.getBuyerId(), user.getUserId())) {  // 구매자가 아니라면
                throw new TicketTransferException("티켓과 사용자 정보가 일치하지 않습니다.");
            }

            if (game.isEnded() || game.isCanceled()) {
                throw new TicketTransferException("이미 종료된 게임입니다.");
            }

            if (timestamp == null) {
                throw new TicketTransferException("QR 코드에 타임스탬프 정보가 없습니다.");
            }

            if (System.currentTimeMillis() - timestamp.getTime() > 60000) {
                throw new TicketTransferException("만료된 QR코드입니다.");
            }

            ticket.setTicketStatus(TicketStatus.USED);
            ticketMapper.updateTicket(ticket);
        } catch (Exception e) {
            throw new RuntimeException("QR 코드 검증 중 오류가 발생했습니다.", e);
        }
    }
}
