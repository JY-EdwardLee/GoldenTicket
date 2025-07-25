package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.response.KakaoPayApproveResponse;
import com.ssafy.ticket_backend.dto.response.KakaoPayReadyResponse;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.KakaoPayService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final KakaoPayService kakaoPayService;

    /**
     * 결제 준비 요청
     */
    @PostMapping("/kakao/ready")
    public ResponseEntity<KakaoPayReadyResponse> kakaoReadyToPay(
        @AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String itemName,
        @RequestParam Integer quantity, @RequestParam Integer totalAmount) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String partnerUserId = userDetails.getUsername();
        String partnerOrderId = UUID.randomUUID().toString();

        KakaoPayReadyResponse response = kakaoPayService.ready(partnerOrderId, partnerUserId,
            itemName, quantity, totalAmount);

        return ResponseEntity.ok(response);
    }

    /**
     * 결제 성공 카카오페이에서 리다이렉트되어 호출되는 엔드포인트
     */
    @GetMapping("/kakao/success")
    public ResponseEntity<KakaoPayApproveResponse> kakaoAfterPayRequest(
        @RequestParam("pg_token") String pgToken,
        @RequestParam("partner_order_id") String partnerOrderId) {

        try {
            KakaoPayApproveResponse approveResponse = kakaoPayService.approve(pgToken,
                partnerOrderId);

            // TODO: 결제 완료 후 비즈니스 로직 처리
            // 1. 데이터베이스에 결제 정보 저장 (approveResponse 참고)
            // 2. 사용자에게 결제 완료 응답
            System.out.println("결제 완료: " + approveResponse);

            // 프론트엔드의 결제 완료 페이지로 리다이렉트 하거나, JSON 응답을 할 수 있습니다.
            // 예: return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://frontend-host/payment/complete")).build();
            return ResponseEntity.ok(approveResponse);
        } catch (IllegalStateException e) {
            // Redis에서 데이터를 찾지 못한 경우 (만료 또는 잘못된 요청)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(null); // Or a proper error response
        }
    }

    /**
     * 결제 취소
     */
    @GetMapping("/kakao/cancel")
    public ResponseEntity<String> kakaoCancel() {
        // TODO: 결제 취소 시 처리할 비즈니스 로직 (예: DB 주문 상태 변경)
        // 프론트엔드의 결제 취소 페이지로 리다이렉트
        return ResponseEntity.status(HttpStatus.OK).body("Payment canceled.");
    }

    /**
     * 결제 실패
     */
    @GetMapping("/kakao/fail")
    public ResponseEntity<String> kakaoFail() {
        // TODO: 결제 실패 시 처리할 비즈니스 로직 (예: DB 주문 상태 변경)
        // 프론트엔드의 결제 실패 페이지로 리다이렉트
        return ResponseEntity.status(HttpStatus.OK).body("Payment failed.");
    }
}
