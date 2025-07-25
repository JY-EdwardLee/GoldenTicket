package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.TossPayReadyResponse;
import com.ssafy.ticket_backend.dto.response.TossPayResponse;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TossPayService {

    private final RestTemplate restTemplate;

    private static final String TOSS_API_URL = "https://api.tosspayments.com/v1/payments/confirm";
    @Value("${toss.secret-key}")
    private String tossSecretKey;
    @Value("${toss.client-key}")
    private String tossClientKey;

    public TossPayResponse confirmPayment(String paymentKey, String orderId, int amount) {
        HttpHeaders headers = new HttpHeaders();
        // Base64 인코딩된 시크릿 키를 Authorization 헤더에 추가
        String encodedSecretKey = Base64.getEncoder()
            .encodeToString((tossSecretKey + ":").getBytes());
        headers.set("Authorization", "Basic " + encodedSecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("paymentKey", paymentKey);
        body.put("orderId", orderId);
        body.put("amount", amount);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // Toss Payments API 호출
        ResponseEntity<TossPayResponse> response = restTemplate.postForEntity(TOSS_API_URL, entity,
            TossPayResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            // TODO: 결제 성공 후 데이터베이스에 관련 정보 저장 (예: 티켓 상태 변경)
            return response.getBody();
        } else {
            // TODO: 결제 실패 시 예외 처리
            throw new RuntimeException("Toss Payments 승인 실패");
        }
    }

    public TossPayReadyResponse readyPayment(String orderId, String orderName, int amount,
        String customerName) {
        // TODO: 이 단계에서 데이터베이스에 주문 정보를 'PENDING' 상태로 저장하는 로직 추가 가능

        return TossPayReadyResponse.builder().clientKey(tossClientKey).orderId(orderId)
            .orderName(orderName).amount(amount).customerName(customerName)
            .successUrl("http://[YOUR_FRONTEND_DOMAIN]/toss/success") // 프론트엔드의 성공 콜백 URL
            .failUrl("http://[YOUR_FRONTEND_DOMAIN]/toss/fail")     // 프론트엔드의 실패 콜백 URL
            .build();
    }
}