package com.ssafy.ticket_backend.util;

import com.ssafy.ticket_backend.model.BaseballTeams;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class OtherplatformDummyGenerator {

    private BaseballTeams baseballTeams;

    private static final String[] PLATFORMS = {"NOL", "TICKETLINK"};
    private static final String[] EMAIL_DOMAINS = {"example.com", "mail.com", "naver.com",
        "gmail.com"};
    private static final Random RANDOM = new Random();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
        "yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        int numberOfRecords = 30;

        for (int i = 0; i < numberOfRecords; i++) {
            String platform = randomFromArray(PLATFORMS);
            int price = 9000 + RANDOM.nextInt(11000); // 9000 ~ 20000원 사이
            String seat = generateSeat();
            String gameDatetime = generateGameDateTime();
            BaseballTeams homeTeam = randomTeam();
            BaseballTeams awayTeam;
            do {
                awayTeam = randomTeam();
            } while (awayTeam == homeTeam);
            String userEmail = generateEmail(i);

            String insert = String.format(
                "INSERT INTO other_platform (platform, price, seat, game_datetime, home_team, away_team, user_email)\n"
                    + "VALUES ('%s', %d, '%s', '%s', '%s', '%s', '%s');", platform, price, seat,
                gameDatetime, homeTeam, awayTeam, userEmail);

            System.out.println(insert);
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
        int section = 100 + RANDOM.nextInt(300); // 구역 번호
        int row = 1 + RANDOM.nextInt(20);
        int seat = 1 + RANDOM.nextInt(30);

        return String.format("내야 %d구역 %d열 %d번", section, row, seat);
    }

    private static String generateGameDateTime() {
        // 2025년 8월 1일 ~ 8월 20일 사이 랜덤 날짜
        int day = 1 + RANDOM.nextInt(20);
        int hour = 17 + RANDOM.nextInt(2); // 17시 또는 18시
        int minute = RANDOM.nextBoolean() ? 0 : 30;
        LocalDateTime dateTime = LocalDateTime.of(2025, 8, day, hour, minute);

        return dateTime.format(FORMATTER);
    }

    private static String generateEmail(int index) {
        String name = "user" + (index + 1);
        String domain = randomFromArray(EMAIL_DOMAINS);

        return name + "@" + domain;
    }
}