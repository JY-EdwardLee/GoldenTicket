package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.QRCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/qrcode")
public class QRCodeController {

    private final QRCodeService qrCodeService;

    @PostMapping("/{ticketId}")
    public ResponseEntity<String> createQRCode(
        @AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long ticketId) {
        String qrCode = qrCodeService.createQRCode(userDetails.getUsername(), ticketId);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);

        return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
    }
}
