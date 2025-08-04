package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRankingResponse {

    private int rank; // 유저 랭킹
    private String userName; // 유저 이름
    private int transferAllCount; // 유저의 총 양도 횟수
}