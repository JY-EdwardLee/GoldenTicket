package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.GameApplicationRequest;
import com.ssafy.ticket_backend.exception.BlockedUserException;
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

    @Override
    public void applicationGame(String username, GameApplicationRequest gameApplicationRequest) {
        // 게임에 대한 정보를 가져온 후
        // 그 게임이나 같은 날에 있는 다른 게임에 응모하였는지 확인
        // 아니라면 취소

        User user = userMapper.selectUserByEmail(username);

        // 정지된 사용자라면
        if (user.getIsBlock()) {
            throw new BlockedUserException("정지된 사용자입니다.");
        }

        // 게임이 있는지 확인
        Game game = gameMapper.selectGameByGameId(gameApplicationRequest.getGameId());

        List<Game> games = gameMapper.selectGameByDate(game.getGameDateTime());
    }
}
