package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.exception.DuplicateApplicationException;
import com.ssafy.ticket_backend.exception.GameAlreadyEndedException;
import com.ssafy.ticket_backend.exception.GroupCapacityExceededException;
import com.ssafy.ticket_backend.exception.GroupJoinCountException;
import com.ssafy.ticket_backend.mapper.GroupMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Application;
import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.model.Game;
import com.ssafy.ticket_backend.model.Group;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.util.HtmlTemplateUtil;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final UserMapper userMapper;
    private final EmailService emailService;
    private final HtmlTemplateUtil htmlTemplateUtil;

    @Value("${spring.mail.username}")
    private String mailUsername;


    /**
     * 그룹 생성기
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
     * 그룹 조회하기(팀별조회 Home or Away) 최신순
     *
     * @param baseballTeams
     * @return GroupDetailResponse
     */
    @Transactional
    @Override
    public List<GroupDetailResponse> getGroupList(BaseballTeams baseballTeams) {
        return groupMapper.getGroupList(baseballTeams);
    }

    /**
     * 단체관람 신청하기 (동시성 제어 포함)
     *
     * @param email, groupId
     * @return 성공/실패 메세지
     */
    @Transactional
    @Override
    public void insertParticipate(String email, long groupId) {

        User user = userMapper.selectUserByEmail(email);

        // Lock으로 그룹 정보 조회 (동시성 제어)
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

        // 그룹 정원이 다 찼을 때 메일 전송
        int cnt = groupMapper.selectCountByGroupId(groupId);

        // 그룹 정원이 다 찼을 때 HTML 신청서 생성 및 메일 전송
        if (cnt == 20) {
            try {
                // HTML 신청서 생성
                String htmlContent = generateGroupApplicationHtml(groupId, user, group);

                // HTML 파일을 메일에 첨부하여 전송
                String subject = "[Golden Ticket] 단체 관람 신청서";
                String fileName = String.format("group_application_%d.html", groupId);
                emailService.sendEmailWithHtmlAttachment(mailUsername, subject, htmlContent,
                    fileName);

                log.info("단체 관람 신청서 HTML 메일 전송 완료: 그룹 ID {}", groupId);

            } catch (IOException e) {
                log.error("HTML 신청서 생성 중 오류 발생: {}", e.getMessage(), e);
            }
        }

        if (result2 != 1) {
            throw new GroupJoinCountException("그룹 집계 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 단체 관람 신청서 HTML을 생성합니다.
     *
     * @param groupId 그룹 ID
     * @param user    신청자 정보
     * @param group   그룹 정보
     * @return HTML 문자열
     * @throws IOException 템플릿 처리 중 오류 발생 시
     */
    private String generateGroupApplicationHtml(long groupId, User user, Group group)
        throws IOException {
        Map<String, String> data = new HashMap<>();

        // 현재 날짜 포맷팅
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 기본 데이터 설정
        data.put("applicationDate", now.format(formatter));
        data.put("applicantName", user.getUserName());
        data.put("applicantPhone", user.getPhoneNumber());
        data.put("applicantEmail", user.getEmail());
        data.put("groupName", "단체 관람 그룹 #" + groupId);
        data.put("groupType", "일반 단체");
        data.put("groupDescription", "단체 관람을 위한 그룹입니다.");
        data.put("gameSchedule", "2024-08-15 18:30");
        data.put("teamName", "삼성 라이온즈 vs KIA 타이거즈");
        data.put("applicantCount", "20");
        data.put("preferredSeating", "일반석");
        data.put("specialRequests", "특별한 요청사항이 없습니다.");
        data.put("applicationStatus", "신청 완료");
        data.put("processedDate", now.format(formatter));

        return htmlTemplateUtil.generateGroupApplicationHtml(data);
    }

    /**
     * 스케줄러: 오늘 날짜 + 7일보다 이전 게임 is_ended -> true 업데이트 시점 매일 자정(00:00)
     */
    @Override
    public void updateEndedGroups() {
        try {
            // 그룹 테이블만 업데이트 (게임 테이블은 더미 데이터이므로 업데이트하지 않음)
            // 오늘 날짜 + 7일보다 이전 게임들의 그룹들을 종료 처리
            int updatedGroupsCount = groupMapper.updateEndedGroups();
            if (updatedGroupsCount > 0) {
                System.out.println("스케줄러: " + updatedGroupsCount + "개의 그룹이 종료 처리되었습니다.");
            } else {
                System.out.println("스케줄러: 종료할 그룹이 없습니다.");
            }
        } catch (Exception e) {
            System.err.println("스케줄러 실행 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }

}
