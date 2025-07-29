package com.ssafy.ticket_backend.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class S3DownloadResponse {

  String downloadUrl;
  String message;
}
