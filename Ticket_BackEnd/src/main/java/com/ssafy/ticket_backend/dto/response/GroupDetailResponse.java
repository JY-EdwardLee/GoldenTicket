package com.ssafy.ticket_backend.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupDetailResponse {

  // 그룹정보
  private Long groupId;
  private Long gameId;
  private Integer applicantsCount;    // 현재 신청 인원
  // 경기정보
  GameResponse gameResponse;

}
