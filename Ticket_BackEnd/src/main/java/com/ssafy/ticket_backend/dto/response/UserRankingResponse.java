package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRankingResponse {

    private Long userId; // 유저 Id
    private int rank; // 유저 랭킹
    private String userName; // 유저 닉네임
    private int transferAllCount; // 유저의 총 양도 횟수
    private String imageUrl; // 유저 프로필 사진 URL
}