package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.TicketResponse;
import com.ssafy.ticket_backend.dto.response.TicketResponse.GameResponse;
import com.ssafy.ticket_backend.exception.TicketTransferException;
import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.OtherPlatformTicket;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.User;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;
    private final GameMapper gameMapper;

    @Transactional
    @Override
    public TicketResponse transferTicket(String userEmail, Long ticketId) {
        try {
            User user = userMapper.selectUserByEmail(userEmail);
            Ticket ticket = ticketMapper.selectTicketByTicketId(ticketId);
            Game game = gameMapper.selectGameByGameId(ticket.getGameId());

            if (!user.getUserId().equals(ticket.getSellerId())) {  // 판매자의 티켓이 아니라면
                throw new TicketTransferException("잘못된 티켓입니다.");
            }

            if (!ticket.getTicketStatus().equals("양도 전")) {  // 양도 전 티켓이 아니라면
                throw new TicketTransferException("잘못된 티켓입니다.");
            }

            if (game.isEnded()) {  // 이미 끝난 경기라면
                throw new TicketTransferException("이미 종료된 경기입니다.");
            }

            ticket.setTicketStatus("양도 중");

            ticketMapper.updateTicket(ticket);

            TicketResponse ticketResponse = new TicketResponse();

            ticketResponse.setTicketId(ticket.getTicketId());
            ticketResponse.setStatus(ticket.getTicketStatus());
            ticketResponse.setPrice(ticket.getPrice());
            ticketResponse.setSeat(ticket.getSeat());
            ticketResponse.setGame(
                new GameResponse(game.getGameId(), game.getGameDateTime(), game.getHomeTeam(),
                    game.getAwayTeam()));

            return ticketResponse;
        } catch (TicketTransferException e) {
            throw e;
        } catch (Exception e) {
            throw new TicketTransferException("티켓 양도 중 오류가 발생했습니다.");
        }
    }

    /**
     * 다른 플랫폼에서 티켓 가져오기
     *
     * @param userEmail
     * @param platform
     * @return
     */
    @Override
    public List<TicketResponse> getTicketsFromOtherPlatform(String userEmail, String platform) {

        User user = userMapper.selectUserByEmail(userEmail);
        List<OtherPlatformTicket> otherPlatformTickets = ticketMapper.selectTicketsFromOtherPlatform(
            userEmail, platform);

        List<Ticket> tickets = new ArrayList<>();
        List<TicketResponse> ticketResponses = new ArrayList<>();

        for (OtherPlatformTicket opt : otherPlatformTickets) {
            Ticket ticket = new Ticket();

            ticket.setPrice(opt.getPrice());
            ticket.setSeat(opt.getSeat());

            Game game = gameMapper.selectGameByDateAndTeam(opt.getGameDatetime().toLocalDate(),
                opt.getHomeTeam()).get(0);

            ticket.setGameId(game.getGameId());
            ticket.setSellerId(user.getUserId());

            tickets.add(ticket);
            ticketMapper.insertTicket(ticket);

            TicketResponse.GameResponse gameResponse = new TicketResponse.GameResponse(
                game.getGameId(), game.getGameDateTime(), game.getHomeTeam(), game.getAwayTeam());

            TicketResponse ticketResponse = new TicketResponse();
            ticketResponse.setTicketId(ticket.getTicketId());
            ticketResponse.setStatus(ticket.getTicketStatus());
            ticketResponse.setPrice(ticket.getPrice());
            ticketResponse.setSeat(ticket.getSeat());
            ticketResponse.setGame(gameResponse);

            ticketResponses.add(ticketResponse);
        }

        return ticketResponses;
    }
}
