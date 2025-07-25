package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.KakaoPayApproveResponse;
import com.ssafy.ticket_backend.dto.response.KakaoPayReadyResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class KakaoPayService {

    @Value("${kakaopay.api-key}")
    private String apiKey;
    @Value("${kakaopay.cid}")
    private String cid;
    @Value("${kakaopay.host}")
    private String host;
    @Value("${kakaopay.ready-url}")
    private String readyUrl;
    @Value("${kakaopay.approve-url}")
    private String approveUrl;
    @Value("${kakaopay.base-url}")
    private String baseUrl;
    @Value("${kakaopay.cancel-url}")
    private String cancelUrl;
    @Value("${kakaopay.fail-url}")
    private String failUrl;

    private final RestTemplate restTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "kakaopay:order:";

    public KakaoPayReadyResponse ready(String partnerOrderId, String partnerUserId, String itemName,
        Integer quantity, Integer totalAmount) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "KakaoAK " + apiKey);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // The success_url needs to include the partner_order_id to retrieve context later
        String successUrl = baseUrl + "/api/v1/payment/success?partner_order_id=" + partnerOrderId;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", cid);
        params.add("partner_order_id", partnerOrderId);
        params.add("partner_user_id", partnerUserId);
        params.add("item_name", itemName);
        params.add("quantity", String.valueOf(quantity));
        params.add("total_amount", String.valueOf(totalAmount));
        params.add("tax_free_amount", "0");
        params.add("approval_url", successUrl);
        params.add("cancel_url", baseUrl + cancelUrl);
        params.add("fail_url", baseUrl + failUrl);

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

        KakaoPayApproveResponse response = restTemplate.postForObject(host + approveUrl, request,
            KakaoPayApproveResponse.class);

        if (response != null) {
            // Clean up Redis
            redisTemplate.delete(REDIS_KEY_PREFIX + partnerOrderId);
        }

        return response;
    }
}