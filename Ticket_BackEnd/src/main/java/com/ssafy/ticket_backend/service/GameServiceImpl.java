package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.exception.BlockedUserException;
import com.ssafy.ticket_backend.exception.GameApplyException;
import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {

    private final UserMapper userMapper;
    private final GameMapper gameMapper;

    /**
     * 티켓 응모 함수
     *
     * @param username 사용자 이메일
     * @param gameId   응모할 게임의 id
     */
    @Override
    public void applicationGame(String username, Long gameId) {
        try {
            User user = userMapper.selectUserByEmail(username);

            // 정지된 사용자라면
            if (user.getIsBlock()) {
                throw new BlockedUserException("정지된 사용자입니다.");
            }

            // 게임이 있는지 확인
            Game game = gameMapper.selectGameByGameId(gameId);

            if (game == null) {
                throw new GameApplyException("게임이 존재하지 않습니다.");
            }

            // 해당 날짜에 있는 게임 중 하나에 응모하였는지 확인
            List<Game> games = gameMapper.selectGameByDate(game.getGameDateTime().toLocalDate());
            for (Game g : games) {
                if (gameMapper.selectWaitlistsByGameIdAndUserId(user.getUserId(), g.getGameId())) {
                    throw new GameApplyException("이미 같은 날짜에 응모를 하셨습니다.");
                }
            }

            // 게임 응모
            gameMapper.insertWaitlist(user.getUserId(), game.getGameId());
        } catch (Exception e) {
            e.printStackTrace();
            throw new GameApplyException("응모간 오류가 발생하였습니다.");
        }
    }
}
