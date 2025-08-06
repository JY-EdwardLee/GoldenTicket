package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.KakaoPayRequest;
import com.ssafy.ticket_backend.dto.response.KakaoPayApproveResponse;
import com.ssafy.ticket_backend.dto.response.KakaoPayReadyResponse;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.KakaoPayService;
import com.ssafy.ticket_backend.service.TicketService;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final TicketService ticketService;
    private final KakaoPayService kakaoPayService;

    @Value("${FE_BASE_URL}")
    private String FE_BASE_URL;

    /**
     * 결제 준비 요청
     */
    @PostMapping("/kakao/ready")
    public ResponseEntity<KakaoPayReadyResponse> kakaoReadyToPay(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody KakaoPayRequest kakaoPayRequest) {
        String partnerUserId = userDetails.getUsername();
        String partnerOrderId = UUID.randomUUID().toString();

        KakaoPayReadyResponse response = kakaoPayService.ready(partnerOrderId, partnerUserId,
            kakaoPayRequest);

        return ResponseEntity.ok(response);
    }

    /**
     * 결제 성공 카카오페이에서 리다이렉트되어 호출되는 엔드포인트
     */
    @GetMapping("/kakao/success")
    public ResponseEntity<Void> kakaoAfterPayRequest(@RequestParam("pg_token") String pgToken,
        @RequestParam("partner_order_id") String partnerOrderId) {
        try {
            KakaoPayApproveResponse approveResponse = kakaoPayService.approve(pgToken,
                partnerOrderId);
            kakaoPayService.insertKakaoTransaction(approveResponse);
            ticketService.completeTransfer(approveResponse.getItem_code(),
                approveResponse.getPartner_user_id());

            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(
                URI.create(FE_BASE_URL + "/mypage/tickets/" + approveResponse.getItem_code()));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (IllegalStateException e) {
            // Redis에서 데이터를 찾지 못한 경우 (만료 또는 잘못된 요청)
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(FE_BASE_URL + "/payment/fail?reason=expired"));

            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        }
    }

    /**
     * 결제 취소
     */
    @GetMapping("/kakao/cancel")
    public ResponseEntity<Void> kakaoCancel() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(FE_BASE_URL + "/payment/cancel"));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * 결제 실패
     */
    @GetMapping("/kakao/fail")
    public ResponseEntity<Void> kakaoFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(FE_BASE_URL + "/payment/fail"));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
