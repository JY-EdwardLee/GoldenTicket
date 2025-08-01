package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.TicketMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.OtherPlatformTicket;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 경기일정 별 5개의 티켓 랜덤생성 좌석,가격,플랫폼 랜덤선택
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/other-platform")
public class OtherPlatFormController {
    /*
    id            SERIAL PRIMARY KEY,
    platform      VARCHAR(30)  NOT NULL,                    -- NOL", TICKETLINK
    price         INTEGER      NOT NULL,                    -- 가격
    seat          VARCHAR(100) NOT NULL,                    -- 좌석
    game_dateTime TIMESTAMP WITHOUT TIME ZONE NOT NULL,     -- game 정보와 일치
    home_team     VARCHAR(50)  NOT NULL,                    -- game 정보와 일치
    away_team     VARCHAR(50)  NOT NULL,                    -- game 정보와 일치
	  stadium 	  VARCHAR(50)  NOT NULL,					            -- stadium 추가
    user_email character varying(255) COLLATE pg_catalog."default" NOT NULL,    --
   */

  static final String[] PLATFORMS = {"NOL", "TICKETLINK"};   // 2중 랜덤 택1

  // (현재시간과 비교)

  /*
  <위치, 가격>
    내야			    15000~25000
    외야			    7000~15000
    내야통로석		18000~25000

    [구역]
    101 ~ 306
    [열]
    1~30
    [번]
    1~40

    ex. (내야 or 외야 or 내야통로석) 00구역 0열 0번
  */

  private final UserMapper userMapper;
  private final GameMapper gameMapper;
  private final TicketMapper ticketMapper;
  private final Random random = new Random();

  @GetMapping("/generate-dummy")
  public String generateDummyData() {
    try {
      List<String> emailList = userMapper.getUserEmailForDummy();
      System.out.println("사용자 수: " + emailList.size());

      List<Game> gameList = gameMapper.getAllGame();
      System.out.println("게임 수: " + gameList.size());

      int generatedCount = 0;

      // 각 게임당 5개의 양도표 생성
      for (Game game : gameList) {
        for (int i = 0; i < 5; i++) {
          // 랜덤 사용자 선택
          String randomEmail = emailList.get(random.nextInt(emailList.size()));

          // 랜덤 플랫폼 선택
          String randomPlatform = PLATFORMS[random.nextInt(PLATFORMS.length)];

          // 좌석 정보 생성
          String seatInfo = generateRandomSeat();

          // 가격 생성
          int price = generatePriceBySeatType(seatInfo);

          // OtherPlatformTicket 객체 생성
          OtherPlatformTicket ticket = new OtherPlatformTicket();
          ticket.setPlatform(randomPlatform);
          ticket.setPrice(price);
          ticket.setSeat(seatInfo);
          ticket.setGameDatetime(game.getGameDateTime());
          ticket.setHomeTeam(game.getHomeTeam());
          ticket.setAwayTeam(game.getAwayTeam());
          ticket.setUserEmail(randomEmail);
          ticket.setStadium(game.getStadium());

          ticketMapper.insertOtherPlatformTicket(ticket);

          generatedCount++;
          System.out.println("생성된 티켓: " + ticket.getPlatform() + " | " +
              ticket.getSeat() + " | " + ticket.getPrice() + "원 | " +
              game.getHomeTeam() + " vs " + game.getAwayTeam());
        }
      }

      return "더미 데이터 생성 완료 - 생성된 티켓 수: " + generatedCount;

    } catch (Exception e) {
      System.out.println("더미 데이터 생성 중 오류: " + e.getMessage());
      e.printStackTrace();
      return "더미 데이터 생성 실패: " + e.getMessage();
    }
  }

  private String generateRandomSeat() {
    String[] seatTypes = {"내야", "외야", "내야통로석"};
    String seatType = seatTypes[random.nextInt(seatTypes.length)];

    // 구역: 101~306
    int section = 101 + random.nextInt(206); // 101부터 306까지

    // 열: 1~30
    int row = 1 + random.nextInt(30);

    // 번: 1~40
    int number = 1 + random.nextInt(40);

    return seatType + " " + String.format("%03d", section) + "구역 " + row + "열 " + number + "번";
  }

  private int generatePriceBySeatType(String seatInfo) {
    if (seatInfo.contains("내야")) {
      if (seatInfo.contains("통로석")) {
        // 내야통로석: 18000~25000 (1000원 단위)
        return 18000 + (random.nextInt(8) * 1000); // 18000, 19000, 20000, ..., 25000
      } else {
        // 내야: 15000~25000 (1000원 단위)
        return 15000 + (random.nextInt(11) * 1000); // 15000, 16000, 17000, ..., 25000
      }
    } else {
      // 외야: 7000~15000 (1000원 단위)
      return 7000 + (random.nextInt(9) * 1000); // 7000, 8000, 9000, ..., 15000
    }
  }
}
