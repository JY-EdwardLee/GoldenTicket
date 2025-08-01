package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.S3Type;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class S3SaverRequest {

  String key;
  S3Type type;
  Long refId;

}
