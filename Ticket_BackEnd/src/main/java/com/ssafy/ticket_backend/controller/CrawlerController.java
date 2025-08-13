package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.mapper.CrawlMapper;
import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.Stadium;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 월별 경기일정 크롤링 (매월 1일 00시)
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/crawler")
public class CrawlerController {

    private final CrawlMapper crawlMapper;

    /**
     * 팀 이름을 BaseballTeams enum으로 매핑하는 메서드
     *
     * @param teamName
     * @return
     */
    private BaseballTeams mapTeamName(String teamName) {
        if (teamName == null || teamName.isEmpty()) {
            return null;
        }

        String trimmedTeamName = teamName.trim();

        switch (trimmedTeamName) {
            case "KIA":
                return BaseballTeams.KIA_TIGERS;
            case "삼성":
                return BaseballTeams.SAMSUNG_LIONS;
            case "LG":
                return BaseballTeams.LG_TWINS;
            case "두산":
                return BaseballTeams.DOOSAN_BEARS;
            case "KT":
                return BaseballTeams.KT_WIZ;
            case "SSG":
                return BaseballTeams.SSG_LANDERS;
            case "롯데":
                return BaseballTeams.LOTTE_GIANTS;
            case "한화":
                return BaseballTeams.HANHWA_EAGLES;
            case "NC":
                return BaseballTeams.NC_DINOS;
            case "키움":
                return BaseballTeams.KIWOOM_HEROES;
            default:
                return null;
        }
    }

    /**
     * 경기장 이름을 Stadium enum으로 매핑
     *
     * @param stadiumName
     * @return
     */
    private Stadium mapStadiumName(String stadiumName) {
        if (stadiumName == null || stadiumName.isEmpty()) {
            return null;
        }

        String trimmedStadiumName = stadiumName.trim();

        switch (trimmedStadiumName) {
            case "잠실":
                return Stadium.JAMSIL;
            case "문학":
                return Stadium.MUNHAK;
            case "사직":
                return Stadium.SAJIK;
            case "광주":
                return Stadium.GWANGJU;
            case "창원":
                return Stadium.CHANGWON;
            case "대구":
                return Stadium.DAEGU;
            case "대전":
                return Stadium.DAEJEON;
            case "고척":
                return Stadium.GOCHUK;
            case "수원":
                return Stadium.SUWON;
            default:
                // 알 수 없는 경기장
                return null;
        }
    }

    /**
     * 날짜와 시간을 LocalDateTime으로 변환
     *
     * @param dateInfo
     * @param timeInfo
     * @return
     */
    private LocalDateTime parseDateTime(String dateInfo, String timeInfo) {
        try {
            // 날짜 형식: "7월 1일 (화)" -> "2025-07-01"
            String dateStr = dateInfo.replaceAll("([0-9]+)월 ([0-9]+)일.*", "2025-$1-$2");

            // 한 자리 월/일을 두 자리로 변환
            dateStr = dateStr.replaceAll("2025-([0-9])-", "2025-0$1-");
            dateStr = dateStr.replaceAll("-([0-9])$", "-0$1");

            // 시간 형식: "18:30" -> "18:30:00"
            String timeStr = timeInfo + ":00";

            String dateTimeStr = dateStr + "T" + timeStr;

            return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (Exception e) {
            // 날짜 시간 파싱 실패
            return null;
        }
    }

    /**
     * @return
     */
    @GetMapping("/kbo-schedule")
    public String crawlKboSchedule() {
        StringBuilder result = new StringBuilder();

        // ChromeDriver 경로 설정
        System.setProperty("webdriver.chrome.driver", "C:/SSAFY/chromedriver.exe");

        WebDriver driver = new ChromeDriver();

        try {
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String url =
                "https://m.sports.naver.com/kbaseball/schedule/index?category=kbo&date=" + today;
            driver.get(url);

            // 페이지 전체 로딩 대기
            Thread.sleep(2000);

            // (디버깅용)
            String pageSource = driver.getPageSource();

            // 새로운 네이버 스포츠 구조에 맞는 선택자 사용
            // 날짜별 경기 그룹 찾기
            List<WebElement> matchGroups = driver.findElements(
                By.cssSelector("div[class*='ScheduleLeagueType_match_list_group']"));

            for (WebElement matchGroup : matchGroups) {
                // 각 그룹에서 날짜 정보 찾기
                String dateInfo = "";

                try {
                    WebElement dateElement = matchGroup.findElement(
                        By.cssSelector("em[class*='ScheduleLeagueType_title']"));
                    dateInfo = dateElement.getText().trim();
                } catch (Exception e) {
                    dateInfo = "날짜 정보 없음";
                }

                // 각 그룹에서 경기 목록 찾기
                List<WebElement> matchItems = matchGroup.findElements(
                    By.cssSelector("li[class*='MatchBox_match_item']"));

                for (WebElement matchItem : matchItems) {
                    try {
                        // 경기 시간 추출
                        String time = "";

                        try {
                            WebElement timeElement = matchItem.findElement(
                                By.cssSelector("div[class*='MatchBox_time']"));
                            time = timeElement.getText().replace("경기 시간", "").trim();
                        } catch (Exception e) {
                            time = "시간 정보 없음";
                        }

                        // 경기장 추출
                        String stadium = "";
                        try {
                            WebElement stadiumElement = matchItem.findElement(
                                By.cssSelector("div[class*='MatchBox_stadium']"));
                            stadium = stadiumElement.getText().replace("경기장", "").trim();
                        } catch (Exception e) {
                            stadium = "경기장 정보 없음";
                        }

                        // 혹시 NUll 값이 들어오는 문제점
                        List<WebElement> teamItems = matchItem.findElements(
                            By.cssSelector("div[class*='MatchBoxHeadToHeadArea_team_item']"));
                        String awayTeam = "";
                        String homeTeam = "";
                        String awayScore = "";
                        String homeScore = "";

                        if (teamItems.size() >= 2) {
                            // 첫 번째 팀 (원정팀)
                            try {
                                WebElement awayTeamElement = teamItems.get(0).findElement(
                                    By.cssSelector("strong[class*='MatchBoxHeadToHeadArea_team']"));
                                awayTeam = awayTeamElement.getText().trim();
                            } catch (Exception e) {
                                awayTeam = "원정팀 정보 없음";
                            }

                            // 두 번째 팀 (홈팀)
                            try {
                                WebElement homeTeamElement = teamItems.get(1).findElement(
                                    By.cssSelector("strong[class*='MatchBoxHeadToHeadArea_team']"));
                                homeTeam = homeTeamElement.getText().trim();
                            } catch (Exception e) {
                                homeTeam = "홈팀 정보 없음";
                            }

                            // 스코어 추출
                            try {
                                List<WebElement> scoreElements = matchItem.findElements(
                                    By.cssSelector(
                                        "strong[class*='MatchBoxHeadToHeadArea_score']"));
                                if (scoreElements.size() >= 2) {
                                    awayScore = scoreElements.get(0).getText().trim();
                                    homeScore = scoreElements.get(1).getText().trim();
                                }
                            } catch (Exception e) {
                                awayScore = "스코어 정보 없음";
                                homeScore = "스코어 정보 없음";
                            }
                        }

                        // 경기 상태 추출
                        String status = "";
                        try {
                            WebElement statusElement = matchItem.findElement(
                                By.cssSelector("em[class*='MatchBox_status']"));
                            status = statusElement.getText().trim();
                        } catch (Exception e) {
                            status = "상태 정보 없음";
                        }

                        // Game 객체 생성
                        Game game = new Game();
                        game.setGameDateTime(parseDateTime(dateInfo, time));
                        game.setHomeTeam(mapTeamName(homeTeam));
                        game.setAwayTeam(mapTeamName(awayTeam));
                        game.setCanceled("취소".equals(status));
                        game.setEnded("종료".equals(status));
                        game.setStadium(mapStadiumName(stadium));

                        // null 체크 후 DB 업로드
                        if (game.getHomeTeam() != null && game.getAwayTeam() != null
                            && game.getStadium() != null && game.getGameDateTime() != null) {

                            System.out.println(game);
                            // 임시 수정
                            // crawlMapper.insertGame(game);
                        }

                    } catch (Exception e) {
                        result.append("⚠️ 경기 정보 파싱 실패 : ").append(e.getMessage()).append("\n");
                    }
                }
            }
        } catch (Exception e) {
            result.append("실행 중 오류 : ").append(e.getMessage()).append("\n");
        } finally {
            driver.quit();  // 브라우저 종료
        }

        return result.toString();
    }
}

