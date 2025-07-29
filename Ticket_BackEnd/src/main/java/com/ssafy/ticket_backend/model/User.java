package com.ssafy.ticket_backend.model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    // 게시판 api 테스트용 임시 수정
    private Long userId;

    private String email;
    private String gender;
    private String userName;
    private UserRole userRole;
    private String nickname;
    private LocalDate birthDate;
    private String profilePhotoUrl;
    private String phoneNumber;

    private String myTeam;
    private String socialProvider;
    private Double weight;
    private Integer transferNumber;
    private Integer receiveNumber;
    private Integer paneltyPoint;
    private Boolean isBlock;
    private Boolean isDelete;
}
