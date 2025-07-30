package com.ssafy.ticket_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3SaveResponse {

  boolean success;  // 성공 여부
  String message;   // 결과 메시지

}
