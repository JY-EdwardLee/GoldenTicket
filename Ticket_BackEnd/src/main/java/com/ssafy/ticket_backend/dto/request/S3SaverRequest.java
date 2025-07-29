package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.S3Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3SaverRequest {

  String key;   // 키값
  S3Type type;  // UserUrl or postUrl
  Long refId;   // 기본키

}
