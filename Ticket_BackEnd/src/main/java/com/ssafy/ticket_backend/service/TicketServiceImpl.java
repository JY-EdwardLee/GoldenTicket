package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.GameResponse;
import com.ssafy.ticket_backend.dto.response.TicketResponse;
import com.ssafy.ticket_backend.exception.TicketTransferException;
import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.mapper.TransactionMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.OtherPlatformTicket;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.TicketStatus;
import com.ssafy.ticket_backend.model.Transaction;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.model.Waitlist;
import com.ssafy.ticket_backend.model.WaitlistStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final UserMapper userMapper;
    private final TicketMapper ticketMapper;
    private final GameMapper gameMapper;
    private final TransactionMapper transactionMapper;

    private final SMSService smsService;

    @Transactional
    @Override
    public TicketResponse transferTicket(String userEmail, Long ticketId) {
        try {
            User seller = userMapper.selectUserByEmail(userEmail);
            Ticket ticket = ticketMapper.selectTicketByTicketId(ticketId);
            Game game = gameMapper.selectGameByGameId(ticket.getGameId());

            if (!seller.getUserId().equals(ticket.getSellerId())) {  // 판매자의 티켓이 아니라면
                throw new TicketTransferException("잘못된 티켓입니다.");
            }

            if (!ticket.getTicketStatus()
                .equals(TicketStatus.BEFORE_ASSIGNMENT)) {  // 양도 전 티켓이 아니라면
                throw new TicketTransferException("양도 전 티켓이 아닙니다.");
            }

            if (game.isEnded()) {  // 이미 끝난 경기라면
                throw new TicketTransferException("이미 종료된 경기입니다.");
            }
            List<Waitlist> waitlists = ticketMapper.selectWaitingWaitListByGameId(game.getGameId());
            // 대기열이 있다면
            if (!waitlists.isEmpty()) {
                // 무작위 추첨
                List<Long> randomPicks = new ArrayList<>();
                for (Waitlist w : waitlists) {
                    User u = userMapper.selectUserByUserId(w.getUserId());

                    do {
                        randomPicks.add(u.getUserId());

                        u.setWeight(u.getWeight() / 10);
                    } while (u.getWeight() > 0);
                }

                Long buyer = randomPicks.get(
                    ThreadLocalRandom.current().nextInt(randomPicks.size()));

                Waitlist waitlist = transactionMapper.selectWaitlistByUserIdAndGameId(buyer,
                    game.getGameId());

                ticket.setBuyerId(buyer);
                ticket.setTicketStatus(TicketStatus.BEING_PAYING);
                ticket.setMatchedDate(LocalDateTime.now());

                Transaction transaction = new Transaction();
                transaction.setTicketId(ticket.getTicketId());
                transaction.setBuyerId(ticket.getBuyerId());
                transaction.setSellerId(ticket.getSellerId());
                transaction.setTransactionStatus("WAITING_PAYING");

                ticketMapper.updateTicket(ticket);
                userMapper.decreaseWeightByUserId(buyer);  // 가중치 감소

                transactionMapper.insertTransaction(transaction);
                Long transactionId = transaction.getTransactionId();

                waitlist.setStatus(WaitlistStatus.WAITING_PAYING);
                waitlist.setTransactionId(transactionId);
                transactionMapper.updateWaitlist(waitlist);

                User buyUser = userMapper.selectUserByUserId(buyer);

                String text =
                    "[골든티켓] 티켓 결제 안내" + "\n" + waitlist.getCreatedAt().getMonthValue() + "월 "
                        + waitlist.getCreatedAt().getDayOfMonth() + "일 응모하신 티켓이 당첨되었습니다." + "\n"
                        + "30분 이내 결제해주시기 바랍니다." + "\n";
                smsService.sendSMS(buyUser.getPhoneNumber(), text);
            } else {  // 대기열이 없다면
                throw new TicketTransferException("응모자가 없습니다.");
            }

            TicketResponse ticketResponse = new TicketResponse(ticket);
            ticketResponse.setGame(
                new GameResponse(game.getGameId(), game.getGameDateTime(), game.getHomeTeam(),
                    game.getAwayTeam(), game.getStadium()));

            return ticketResponse;
        } catch (TicketTransferException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
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
                // 티켓 정보에 등록
                Ticket ticket;
                // 티켓이 등록되어 있으면
                if (ticketMapper.checkTicketByGame(g.getGameId(), opt.getSeat(),
                    user.getUserId())) {
                    ticket = ticketMapper.selectTicketByGame(g.getGameId(), opt.getSeat(),
                        user.getUserId());
                } else {  // 티켓이 등록되어 있지 않으면
                    ticket = new Ticket();

                    ticket.setPrice(opt.getPrice());
                    ticket.setSeat(opt.getSeat());
                    ticket.setTicketStatus(TicketStatus.BEFORE_ASSIGNMENT);
                    ticket.setGameId(g.getGameId());
                    ticket.setSellerId(user.getUserId());

                    ticketMapper.insertTicket(ticket);  // 티켓 등록
                }

                tickets.add(ticket);
            }
        }

        for (Ticket ticket : tickets) {
            Game game = gameMapper.selectGameByGameId(ticket.getGameId());

            // 게임이 취소되거나 종료되었다면
            if (game.isCanceled() || game.isEnded()) {
                continue;
            }

            TicketResponse ticketResponse = new TicketResponse(ticket);
            ticketResponse.setGame(game.toGameResponse());

            ticketResponse.setWaitNumber(ticketMapper.countWaitListByGameId(game.getGameId()));

            ticketResponses.add(ticketResponse);
        }

        return ticketResponses;
    }

    /**
     * 결제 완료에 따른 상태 변환
     *
     * @param ticketId
     * @param userEmail
     */
    @Transactional
    @Override
    public void completeTransfer(String ticketId, String userEmail) {
        User user = userMapper.selectUserByEmail(userEmail);
        Ticket ticket = ticketMapper.selectTicketByTicketId(Long.valueOf(ticketId));

        ticketMapper.transferTicket(Long.valueOf(ticketId), user.getUserId());
        Waitlist waitlist = transactionMapper.selectWaitlistByUserIdAndGameId(user.getUserId(),
            ticket.getGameId());

        waitlist.setStatus(WaitlistStatus.TRANSACTION_COMPLETE);
        transactionMapper.updateWaitlist(waitlist);
    }

    /**
     * 티켓의 상세 정보
     *
     * @param userEmail 사용자 이메일
     * @param ticketId  티켓 id
     * @return 티켓 상세 정보
     */
    @Override
    public TicketResponse getTicketDetail(String userEmail, Long ticketId) {
        Ticket ticket = ticketMapper.selectTicketByTicketId(ticketId);
        TicketResponse ticketResponse = new TicketResponse(ticket);
        Game game = gameMapper.selectGameByGameId(ticket.getGameId());
        ticketResponse.setGame(game.toGameResponse());

        return ticketResponse;
    }
}
