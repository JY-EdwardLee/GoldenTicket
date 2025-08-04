package com.ssafy.ticket_backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class S3UploadUrlResponse {

  String key;
  String presignedUrl;
  boolean update; // 업데이트 여부 (수정 시 true, 신규 등록 시 false)

}
