package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.KakaoPayRequest;
import com.ssafy.ticket_backend.dto.response.KakaoPayApproveResponse;
import com.ssafy.ticket_backend.dto.response.KakaoPayReadyResponse;
import com.ssafy.ticket_backend.mapper.TransactionMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.Ticket;
import com.ssafy.ticket_backend.model.TicketStatus;
import com.ssafy.ticket_backend.model.Transaction;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

    private final TransactionMapper transactionMapper;
    private final UserMapper userMapper;
    @Value("${kakaopay.api-key}")
    private String apiKey;
    @Value("${kakaopay.cid}")
    private String cid;
    @Value("${kakaopay.host}")
    private String host;
    @Value("${kakaopay.ready-url}")
    private String readyUrl;
    @Value("${BE_BASE_URL}")
    private String BE_BASE_URL;
    @Value("${FE_BASE_URL}")
    private String FE_BASE_URL;

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "kakaopay:order:";

    public KakaoPayReadyResponse ready(String partnerOrderId, String partnerUserId,
        KakaoPayRequest kakaoPayRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + apiKey);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String successUrl =
            BE_BASE_URL + "/payment/kakao/success?partner_order_id=" + partnerOrderId;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("partner_order_id", partnerOrderId);
        params.add("partner_user_id", partnerUserId);
        params.add("item_name", kakaoPayRequest.getItemName());
        params.add("item_code", String.valueOf(kakaoPayRequest.getTicketId()));
        params.add("quantity", String.valueOf(kakaoPayRequest.getQuantity()));
        params.add("total_amount", String.valueOf(kakaoPayRequest.getTotalAmount()));
        params.add("tax_free_amount", "0");
        params.add("approval_url", successUrl);
        params.add("cancel_url", BE_BASE_URL + "/payment/kakao/cancel");
        params.add("fail_url", BE_BASE_URL + "/payment/kakao/fail");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        KakaoPayReadyResponse response = restTemplate.postForObject(host + readyUrl, request,
            KakaoPayReadyResponse.class);

        if (response != null) {
            // Store tid and user_id in Redis, with partner_order_id as the key
            Map<String, String> redisValues = new HashMap<>();
            redisValues.put("tid", response.getTid());
            redisValues.put("user_id", partnerUserId);
            redisTemplate.opsForValue()
                .set(REDIS_KEY_PREFIX + partnerOrderId, redisValues, 15, TimeUnit.MINUTES);
        }

        return response;
    }

    public KakaoPayApproveResponse approve(String pgToken, String partnerOrderId) {
        // Retrieve tid and user_id from Redis
        Map<String, String> redisValues = (Map<String, String>) redisTemplate.opsForValue()
            .get(REDIS_KEY_PREFIX + partnerOrderId);
        if (redisValues == null) {
            throw new IllegalStateException("Payment information has expired or is invalid.");
        }
        String tid = redisValues.get("tid");
        String partnerUserId = redisValues.get("user_id");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + apiKey);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("tid", tid);
        params.add("partner_order_id", partnerOrderId);
        params.add("partner_user_id", partnerUserId);
        params.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        KakaoPayApproveResponse response = restTemplate.postForObject(
            "https://kapi.kakao.com/v1/payment/approve", request, KakaoPayApproveResponse.class);

        if (response != null) {
            // Clean up Redis
            redisTemplate.delete(REDIS_KEY_PREFIX + partnerOrderId);
        }

        return response;
    }

    /**
     * 결제 정보 DB 저장
     *
     * @param approveResponse
     */
    @Transactional
    public void insertKakaoTransaction(KakaoPayApproveResponse approveResponse) {
        Ticket ticket = transactionMapper.selectTicketByTicketId(
            Long.parseLong(approveResponse.getItem_code()));

        // 거래 기록 갱신
        Transaction transaction = transactionMapper.selectTransactionByTicketId(
            ticket.getTicketId());
        transaction.setTransactionStatus(String.valueOf(TicketStatus.TRANSACTION_COMPLETE));
        transactionMapper.updateTransaction(transaction);

        transactionMapper.transactionComplete(ticket.getTicketId());  // 티켓의 거래 상태 변경
    }
}