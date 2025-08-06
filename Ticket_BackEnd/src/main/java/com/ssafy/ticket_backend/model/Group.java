package com.ssafy.ticket_backend.model;

import lombok.Data;

@Data
public class Group {

  private Long groupId;
  private Long gameId;
  private Boolean isActive;           // 모집 중 여부 (정원 초과 여부)
  private Boolean isEnded;            // 경기 종료 여부
  private Integer applicantsCount;    // 현재 신청 인원

}