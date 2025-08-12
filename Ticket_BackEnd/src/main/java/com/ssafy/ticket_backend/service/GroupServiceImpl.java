package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.exception.DuplicateApplicationException;
import com.ssafy.ticket_backend.exception.GameAlreadyEndedException;
import com.ssafy.ticket_backend.exception.GroupCapacityExceededException;
import com.ssafy.ticket_backend.exception.GroupJoinCountException;
import com.ssafy.ticket_backend.mapper.GameMapper;
import com.ssafy.ticket_backend.mapper.GroupMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Application;
import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.Group;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.util.HtmlTemplateUtil;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final UserMapper userMapper;
    private final GameMapper gameMapper;
    private final HtmlTemplateUtil htmlTemplateUtil;

    @Autowired(required = false)
    private EmailService emailService;

    @Value("${spring.mail.username:}")
    private String mailUsername;


    /**
     * 그룹 생성
     */
    @Transactional
    @Override
    public void getGames() {
        List<Game> gameList = groupMapper.getGames();
        for (Game game : gameList) {
            groupMapper.insertGroup(game.getGameId());
        }
    }

    /**
     * 그룹 조회 (팀별)
     */
    @Transactional
    @Override
    public List<GroupDetailResponse> getGroupList(BaseballTeams baseballTeams) {
        return groupMapper.getGroupList(baseballTeams);
    }

    /**
     * 단체관람 신청
     */
    @Transactional
    @Override
    public void insertParticipate(String email, long groupId) {

        User user = userMapper.selectUserByEmail(email);

        // Lock으로 그룹 정보 조회
        Group group = groupMapper.selectOneGroupForUpdate(groupId);

        if (!group.getIsActive()) {
            throw new GroupCapacityExceededException("정원 초과 하였습니다.");
        } else if (group.getIsEnded()) {
            throw new GameAlreadyEndedException("종료된 경기입니다.");
        }

        try {
            int result = groupMapper.insertApplication(new Application(groupId, user.getUserId()));
        } catch (Exception e) {
            throw new DuplicateApplicationException("중복 지원은 불가합니다.");
        }

        int result2 = groupMapper.updateGroupCount(groupId);

        if (result2 != 1) {
            throw new GroupJoinCountException("그룹 집계 중 오류가 발생하였습니다.");
        }

        // 그룹 정원이 다 찼을 때 메일 전송
        int cnt = groupMapper.selectCountByGroupId(groupId);

        if (cnt == 20) {
            try {
                String htmlContent = generateGroupApplicationHtml(groupId, user, group);
                String subject = "[Golden Ticket] 단체 관람 신청서";

                if (emailService != null && mailUsername != null && !mailUsername.isEmpty()) {
                    emailService.sendEmailWithHtmlAttachment(mailUsername, subject, htmlContent,
                        "");
//                    log.info("단체 관람 신청서 HTML 본문 메일 전송 완료: 그룹 ID {}", groupId);
                } else {
//                    log.warn("이메일 서비스가 구성되지 않아 메일을 전송할 수 없습니다. 그룹 ID: {}", groupId);
                }
            } catch (IOException e) {
//                log.error("HTML 신청서 생성 중 오류 발생: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 단체 관람 신청서 HTML 생성
     */
    private String generateGroupApplicationHtml(long groupId, User user, Group group)
        throws IOException {
        Map<String, String> data = new HashMap<>();

        Game game = gameMapper.selectGameByGameId(group.getGameId());
        String teamName = String.valueOf(game.getHomeTeam());

        // 게임 날짜 포맷팅
        String gameDate = "날짜 미정";
        if (game != null && game.getGameDateTime() != null) {
            gameDate = game.getGameDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        data.put("teamName", teamName);
        data.put("gameDate", gameDate);

        // 신청자 정보 가져오기
        List<Application> groupList = groupMapper.selectGroupInfo(groupId);
        String applicantName = "Unknown";
        String phoneNumber = "Unknown";
        if (!groupList.isEmpty()) {
            long firstUserId = groupList.get(0).getUserId();
            User firstUser = userMapper.selectUserByUserId(firstUserId);
            if (firstUser != null) {
                applicantName = firstUser.getUserName();
                phoneNumber = firstUser.getPhoneNumber();
            }
        }
        data.put("applicantName", applicantName);
        data.put("phoneNumber", phoneNumber);

        // 참가자 이메일 목록 생성
        StringBuilder emailInfo = new StringBuilder();
        for (Application ap : groupList) {
            long groupUserId = ap.getUserId();
            User user2 = userMapper.selectUserByUserId(groupUserId);
            if (user2 != null && user2.getEmail() != null) {
                if (emailInfo.length() > 0) {
                    emailInfo.append(", ");
                }
                emailInfo.append(user2.getEmail());
            }
        }

        data.put("emailList", "참가자 이메일 목록:\n" + emailInfo.toString());

//        log.info("HTML 생성 데이터 - 팀명: {}, 게임날짜: {}, 신청자: {}, 전화번호: {}", teamName, gameDate, applicantName, data.get("phoneNumber"));

        String htmlContent = htmlTemplateUtil.generateGroupApplicationHtml(data);

        return htmlContent;
    }

    /**
     * 스케줄러: 종료된 그룹 업데이트
     */
    @Override
    public void updateEndedGroups() {
        try {
            int updatedGroupsCount = groupMapper.updateEndedGroups();
            if (updatedGroupsCount > 0) {
//                log.info("스케줄러: {}개의 그룹이 종료 처리되었습니다.", updatedGroupsCount);
            } else {
//                log.info("스케줄러: 종료할 그룹이 없습니다.");
            }
        } catch (Exception e) {
//            log.error("스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

}
