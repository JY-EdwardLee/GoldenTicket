package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.GameResponse;
import com.ssafy.ticket_backend.dto.response.TicketResponse;
import com.ssafy.ticket_backend.exception.TicketTransferException;
import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.OtherPlatformTicket;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.TicketStatus;
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

            if (!ticket.getTicketStatus()
                .equals(TicketStatus.BEFORE_ASSIGNMENT)) {  // 양도 전 티켓이 아니라면
                throw new TicketTransferException("양도 전 티켓이 아닙니다.");
            }

            if (game.isEnded()) {  // 이미 끝난 경기라면
                throw new TicketTransferException("이미 종료된 경기입니다.");
            }

            ticket.setTicketStatus(TicketStatus.BEING_ASSIGNMENT);

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
    @Transactional
    @Override
    public List<TicketResponse> getTicketsFromOtherPlatform(String userEmail, String platform) {

        User user = userMapper.selectUserByEmail(userEmail);
        List<OtherPlatformTicket> otherPlatformTickets = ticketMapper.selectTicketsFromOtherPlatform(
            userEmail, platform);

        List<TicketResponse> ticketResponses = new ArrayList<>();
        List<Ticket> tickets = new ArrayList<>();

        // 다른 플랫폼에서 티켓을 가져온 후, 선별하여 tickets에 등록
        for (OtherPlatformTicket opt : otherPlatformTickets) {
            List<Game> games = gameMapper.selectGameByDateAndTeam(
                opt.getGameDatetime().toLocalDate(), opt.getHomeTeam());

            for (Game g : games) {
                // 티켓이 이미 등록되어 있으면 생략
                if (ticketMapper.selectTicketByGame(g.getGameId(), opt.getSeat(),
                    user.getUserId())) {
                    continue;
                }

                // 티켓 정보에 등록
                Ticket ticket = new Ticket();

                ticket.setPrice(opt.getPrice());
                ticket.setSeat(opt.getSeat());
                ticket.setTicketStatus(TicketStatus.BEFORE_ASSIGNMENT);
                ticket.setGameId(g.getGameId());
                ticket.setSellerId(user.getUserId());

                ticketMapper.insertTicket(ticket);
                tickets.add(ticket);
            }
        }

        System.out.println(tickets.size() + " tickets");
        for (Ticket ticket : tickets) {
            Game game = gameMapper.selectGameByGameId(ticket.getGameId());

            // 게임이 취소되거나 종료되었다면
            if (game.isCanceled() || game.isEnded()) {
                continue;
            }

            TicketResponse ticketResponse = new TicketResponse();

            ticketResponse.setTicketId(ticket.getTicketId());
            ticketResponse.setStatus(ticket.getTicketStatus());
            ticketResponse.setPrice(ticket.getPrice());
            ticketResponse.setSeat(ticket.getSeat());
            ticketResponse.setGame(
                new GameResponse(game.getGameId(), game.getGameDateTime(), game.getHomeTeam(),
                    game.getAwayTeam()));

            ticketResponse.setWaitNumber(ticketMapper.selectWaitListByGameId(game.getGameId()));

            ticketResponses.add(ticketResponse);
        }

        return ticketResponses;
    }

    @Override
    public void insertTicketsFromOtherPlatform(String userEmail, String platform) {

    }
}
