package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.S3Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3DownloadRequest {

  S3Type type;
  Long refId;
}
