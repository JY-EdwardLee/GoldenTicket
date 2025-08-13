package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.GroupDetailResponse;
import com.ssafy.ticket_backend.dto.response.GroupResponse;
import com.ssafy.ticket_backend.model.BaseballTeams;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.GroupService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {

    private final GroupService groupService;


    /**
     * 그룹 생성기
     */
    @GetMapping("/generator")
    public ResponseEntity<Void> selectGames() {
        groupService.getGames();
        return null;
    }
    

    /**
     * 그룹 조회하기(팀별조회 Home or Away) 최신순
     *
     * @param baseballTeams
     * @return GroupDetailResponse
     */
    @GetMapping("/{baseballTeams}")
    public ResponseEntity<List<GroupDetailResponse>> selectGroup(
        @PathVariable BaseballTeams baseballTeams) {
        List<GroupDetailResponse> list = groupService.getGroupList(baseballTeams);
        return ResponseEntity.ok(list);
    }

    /**
     * 그룹 신청하기
     *
     * @param groupId
     * @return GroupResponse
     */
    @PostMapping("/{groupId}")
    public ResponseEntity<GroupResponse> participateGroup(@PathVariable long groupId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        groupService.insertParticipate(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(new GroupResponse(true, "응모 성공"));
    }

    /**
     * 그룹 취소하기
     *
     * @param groupId
     * @return GroupResponse
     */
    @DeleteMapping("/{groupId}")
    public ResponseEntity<GroupResponse> deleteApplication(@PathVariable long groupId,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        groupService.deleteApplication(userDetails.getUsername(), groupId);
        return ResponseEntity.ok(new GroupResponse(true, "응모취소 성공"));
    }
}
