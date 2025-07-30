package com.ssafy.ticket_backend.controller;

import com.ssafy.ticket_backend.dto.request.KakaoPayRequest;
import com.ssafy.ticket_backend.dto.response.KakaoPayApproveResponse;
import com.ssafy.ticket_backend.dto.response.KakaoPayReadyResponse;
import com.ssafy.ticket_backend.dto.response.TossPayReadyResponse;
import com.ssafy.ticket_backend.dto.response.TossPayResponse;
import com.ssafy.ticket_backend.service.CustomUserDetails;
import com.ssafy.ticket_backend.service.KakaoPayService;
import com.ssafy.ticket_backend.service.TossPayService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

    private final KakaoPayService kakaoPayService;
    private final TossPayService tossPayService;

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
    public ResponseEntity<KakaoPayApproveResponse> kakaoAfterPayRequest(
        @RequestParam("pg_token") String pgToken,
        @RequestParam("partner_order_id") String partnerOrderId) {

        try {
            KakaoPayApproveResponse approveResponse = kakaoPayService.approve(pgToken,
                partnerOrderId);
            System.out.println("결제 완료: " + approveResponse);
            kakaoPayService.insertKakaoTransaction(approveResponse);

            // TODO 반환 형식 어떻게 할지
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
        return ResponseEntity.status(HttpStatus.OK).body("결제 취소");
    }

    /**
     * 결제 실패
     */
    @GetMapping("/kakao/fail")
    public ResponseEntity<String> kakaoFail() {
        // TODO: 결제 실패 시 처리할 비즈니스 로직 (예: DB 주문 상태 변경)
        // 프론트엔드의 결제 실패 페이지로 리다이렉트
        return ResponseEntity.status(HttpStatus.OK).body("결제 실패");
    }

    @PostMapping("/toss/ready")
    public ResponseEntity<TossPayReadyResponse> readyTossPayment() {
        String orderId = "toss-" + UUID.randomUUID().toString();

        // TODO: request에서 받은 상품 정보로 금액(amount) 및 주문명(orderName) 설정
        // TODO: 현재 로그인한 사용자 정보로 고객명(customerName) 설정

        TossPayReadyResponse readyResponse = tossPayService.readyPayment(orderId, "샘플 주문", 0,
            "SSAFY");

        return ResponseEntity.ok(readyResponse);
    }

    @PostMapping("/toss/confirm")
    public ResponseEntity<TossPayResponse> confirmTossPayment(
        @RequestParam("paymentKey") String paymentKey, @RequestParam("orderId") String orderId,
        @RequestParam("amount") int amount) {

        TossPayResponse approvalResponse = tossPayService.confirmPayment(paymentKey, orderId,
            amount);

        return ResponseEntity.ok(approvalResponse);
    }
}
