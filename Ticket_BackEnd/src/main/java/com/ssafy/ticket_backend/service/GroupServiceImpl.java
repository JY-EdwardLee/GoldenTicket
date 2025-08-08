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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

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
     * 단체관람 신청하기
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

        if (cnt == 20) {
            try {
                // HTML 신청서 생성
                String htmlContent = generateGroupApplicationHtml(groupId, user, group);
                System.out.println("확인 :");
                System.out.println(htmlContent);

                String subject = "[Golden Ticket] 단체 관람 신청서";
                
                if (emailService != null && mailUsername != null && !mailUsername.isEmpty()) {
                    // HTML 내용을 이메일 본문으로 전송
                    emailService.sendEmailWithHtmlAttachment(mailUsername, subject, htmlContent, "");
                    log.info("단체 관람 신청서 HTML 메일 전송 완료: 그룹 ID {}", groupId);
                } else {
                    log.warn("이메일 서비스가 구성되지 않아 메일을 전송할 수 없습니다. 그룹 ID: {}", groupId);
                }

            } catch (IOException e) {
                log.error("HTML 신청서 생성 중 오류 발생: {}", e.getMessage(), e);
            }
        }

        if (result2 != 1) {
            throw new GroupJoinCountException("그룹 집계 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 단체 관람 신청서 HTML을 생성
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

        // 게임 정보 가져오기
        Game game = gameMapper.selectGameByGameId(group.getGameId());

        // 홈팀
        String teamName = String.valueOf(game.getHomeTeam());

        // 관람일자 설정
        String viewingDate = "2024-08-15"; // 기본값
        if (game != null && game.getGameDateTime() != null) {
            viewingDate = game.getGameDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        // HTML 템플릿에서 사용되는 플레이스홀더들에 맞게 데이터 설정
        data.put("teamName", teamName); // 팀 이름
        data.put("viewingDate", viewingDate); // 관람일자

        // 대표자 정보
        long userId = gameMapper.getUserIdByGroupId(groupId);
        User delegation = userMapper.selectUserByUserId(userId);
        data.put("applicantName", delegation.getUserName());

        // groupId 이용해 해당 groupId 회원 이메일 모두 스트링으로 가져오기
        List<Application> groupList = groupMapper.selectGroupInfo(groupId);
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

        data.put("emailList", "참가자 이메일 목록:\n" + emailInfo.toString()); // 이메일 리스트

        // 로고 파일명 추가
        String logoFileName = getTeamLogoFileName(game != null ? game.getHomeTeam() : null);
        data.put("teamLogoFileName", logoFileName);

        return htmlTemplateUtil.generateGroupApplicationHtml(data);
    }

    /**
     * 팀별 로고 파일명을 반환합니다.
     *
     * @param baseballTeam 야구팀
     * @return 로고 파일명
     */
    private String getTeamLogoFileName(BaseballTeams baseballTeam) {
        if (baseballTeam == null) {
            return "SAMSUNG_LIONS";
        }

        switch (baseballTeam) {
            case SAMSUNG_LIONS:
                return "SAMSUNG_LIONS";
            case KIA_TIGERS:
                return "KIA_TIGERS";
            case LG_TWINS:
                return "LG_TWINS";
            case DOOSAN_BEARS:
                return "DOOSAN_BEARS";
            case KT_WIZ:
                return "KT_WIZ";
            case SSG_LANDERS:
                return "SSG_LANDERS";
            case LOTTE_GIANTS:
                return "LOTTE_GIANTS";
            case HANHWA_EAGLES:
                return "HANHWA_EAGLES";
            case NC_DINOS:
                return "NC_DINOS";
            case KIWOOM_HEROES:
                return "KIWOOM_HEROES";
            default:
                return "SAMSUNG_LIONS";
        }
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
