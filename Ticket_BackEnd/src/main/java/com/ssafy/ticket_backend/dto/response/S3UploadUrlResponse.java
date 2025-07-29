package com.ssafy.ticket_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3UploadUrlResponse {

  String key;
  String presignedUrl;

}
