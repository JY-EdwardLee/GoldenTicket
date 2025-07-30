package com.ssafy.ticket_backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplicationGameResponse {

    private boolean success;
    private String message;
}
