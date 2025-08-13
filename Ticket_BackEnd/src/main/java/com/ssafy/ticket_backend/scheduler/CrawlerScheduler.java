package com.ssafy.ticket_backend.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlerScheduler {

    private final RestTemplate restTemplate;

    @Value("${BE_BASE_URL}")
    private String BE_BASE_URL;

    /**
     * 매일 오후 11시에 오늘 경기 일정 크롤링 실행 (우천취소 정보 업데이트) cron = "초 분 시 일 월 요일" "0 0 23 * * *" = 매일 23:00:00
     */
    @Scheduled(cron = "0 0 23 * * *")
    public void crawlTodaySchedule() {
//        try {
//            log.info("매일 오후 11시 오늘 경기 일정 크롤링 시작");
//
//            // 일별 크롤러 컨트롤러의 엔드포인트 호출
//            String crawlResult = restTemplate.getForObject(
//                BE_BASE_URL + "/crawler/today-schedule",
//                String.class
//            );
//
//            log.info("일별 크롤링 완료: {}", crawlResult);
//
//        } catch (Exception e) {
//            log.error("일별 크롤링 스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
//        }
    }


    /**
     * 매월 1일 자정에 KBO 경기 일정 크롤링 실행 [games, other_platform, groups] 매달 새로운 정보 insert
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void crawlKboSchedule() {
        try {
            log.info("매월 1일 KBO 경기 일정 크롤링 시작");

            // 1단계: 크롤러 컨트롤러의 엔드포인트 호출
            String crawlResult = restTemplate.getForObject(BE_BASE_URL + "/crawler/kbo-schedule",
                String.class);

            log.info("크롤링 완료: {}", crawlResult);

            // 2단계: 크롤링 성공 후 더미 데이터 생성
            if (crawlResult != null && !crawlResult.contains("실패")) {
                log.info("other_platform 데이터 생성 시작");

                String dummyResult = restTemplate.getForObject(
                    BE_BASE_URL + "/other-platform/generate-dummy", String.class);

                log.info("더미 데이터 생성 완료: {}", dummyResult);

                // 3단계: 더미 데이터 생성 성공 후 그룹 생성
                if (dummyResult != null && !dummyResult.contains("실패")) {
                    log.info("그룹 데이터 생성 시작");

                    String groupResult = restTemplate.getForObject(BE_BASE_URL + "/group/generator",
                        String.class);

                    log.info("그룹 데이터 생성 완료: {}", groupResult);
                } else {
                    log.warn("더미 데이터 생성 실패로 인해 그룹 생성을 건너뜁니다");
                }
            } else {
                log.warn("크롤링 실패로 인해 더미 데이터 생성을 건너뜁니다");
            }

        } catch (Exception e) {
            log.error("스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
