package com.ssafy.ticket_backend.util;

import com.ssafy.ticket_backend.model.BaseballTeams;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class OtherplatformDummyGenerator {

  private static final String[] PLATFORMS = {"NOL", "TICKETLINK"};
  private static final String[] EMAIL_DOMAINS = {"example.com", "mail.com", "naver.com",
      "gmail.com"};
  private static final Random RANDOM = new Random();

  // TODO 대체하기
  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
      "yyyy-MM-dd HH:mm:ss");

  public static void main(String[] args) {
    int numberOfGames = 15;
    int numberOfTickets = 30;

    Set<String> gameSet = new HashSet<>();
    List<Game> games = new ArrayList<>();

    // 1. 게임 생성
    while (games.size() < numberOfGames) {
      String gameDatetime = generateGameDateTime();
      BaseballTeams homeTeam = randomTeam();
      BaseballTeams awayTeam;
      do {
        awayTeam = randomTeam();
      } while (awayTeam == homeTeam);

      String key = gameDatetime + "_" + homeTeam + "_" + awayTeam;
      if (gameSet.add(key)) {
        Game game = new Game(gameDatetime, homeTeam, awayTeam);
        games.add(game);

        System.out.printf("INSERT INTO games (game_datetime, home_team, away_team)\n"
            + "VALUES ('%s', '%s', '%s');\n", gameDatetime, homeTeam, awayTeam);
      }
    }

    // 2. 티켓 생성 - 경기 리스트에서 참조
    for (int i = 0; i < numberOfTickets; i++) {
      Game game = games.get(RANDOM.nextInt(games.size())); // 경기 참조

      String platform = randomFromArray(PLATFORMS);
      int price = 9000 + RANDOM.nextInt(11000); // 9000 ~ 20000원
      String seat = generateSeat();
      String userEmail = generateEmail(i);

      System.out.printf(
          "INSERT INTO other_platform (platform, price, seat, game_datetime, home_team, away_team, user_email)\n"
              + "VALUES ('%s', %d, '%s', '%s', '%s', '%s', '%s');\n", platform, price, seat,
          game.gameDatetime, game.homeTeam, game.awayTeam, userEmail);
    }
  }

  private static BaseballTeams randomTeam() {
    BaseballTeams[] teams = BaseballTeams.values();
    return teams[RANDOM.nextInt(teams.length)];
  }

  private static String randomFromArray(String[] array) {
    return array[RANDOM.nextInt(array.length)];
  }

  private static String generateSeat() {
    int section = 100 + RANDOM.nextInt(300);
    int row = 1 + RANDOM.nextInt(20);
    int seat = 1 + RANDOM.nextInt(30);
    return String.format("내야 %d구역 %d열 %d번", section, row, seat);
  }

  private static String generateGameDateTime() {
    int day = 1 + RANDOM.nextInt(20);
    int hour = 17 + RANDOM.nextInt(2);
    int minute = RANDOM.nextBoolean() ? 0 : 30;
    LocalDateTime dateTime = LocalDateTime.of(2025, 8, day, hour, minute);
    return dateTime.format(FORMATTER);
  }

  private static String generateEmail(int index) {
    String name = "user" + (index + 1);
    String domain = randomFromArray(EMAIL_DOMAINS);
//        return name + "@" + domain;
    return "honggildong@example.com";
  }

  static class Game {

    String gameDatetime;
    BaseballTeams homeTeam;
    BaseballTeams awayTeam;

    public Game(String gameDatetime, BaseballTeams homeTeam, BaseballTeams awayTeam) {
      this.gameDatetime = gameDatetime;
      this.homeTeam = homeTeam;
      this.awayTeam = awayTeam;
    }
  }
}
