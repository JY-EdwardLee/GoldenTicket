package com.ssafy.ticket_backend.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    // JWT 서명에 사용 할 비밀 키
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // Redis와 통신하기 위한 객체
    private final RedisTemplate<String, String> redisTemplate;

    // 엑세스 토큰 만료 시간: 1시간
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    // 리프레시 토큰 만료 시간 : 7일
    private final long REFRESH_TIME = 1000 * 60 * 60 * 24 * 7;

    /**
     * 액세스 토큰 생성 메서드
     *
     * @param email 토큰에 담길 정보 (이메일로 식별)
     * @return 생성된 엑세스 토큰 문자열
     */
    public String generateAccessToken(String email) {
        return Jwts.builder().setSubject(email) // 토큰의 제목
            .setIssuedAt(new Date()) // 발급 시간
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 만료 시간
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 서명 방식 및 비밀키
            .compact();
    }

    /**
     * 리프레시 토큰 생성 및 Redis 저장 메서드
     *
     * @param email 토큰에 담길 정보
     * @return 생성된 리프레시 토큰 문자열
     */
    public String generateRefreshToken(String email) {
        // refresh 토큰 생성
        String refreshToken = Jwts.builder().setSubject(email) // 토큰 제목에 email
            .setIssuedAt(new Date()) // 발급 시간
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();

        // Redis에 refresh 토큰 저장 (key: refresh:{email}, TTL: 7일)
        redisTemplate.opsForValue()
            .set("refresh:" + email, refreshToken, REFRESH_TIME, TimeUnit.MILLISECONDS);

//        System.out.println("[리프레시 토큰 저장됨] 이메일: " + email + ", 토큰: " + refreshToken);

        return refreshToken;
    }

    /**
     * Redis에 저장된 리프레시 토큰 삭제 (로그아웃 시 호출)
     *
     * @param email 사용자 이메일
     */
    public void deleteRefreshToken(String email) {
        Boolean deleted = redisTemplate.delete("refresh:" + email);
//        System.out.println("[리프레시 토큰 삭제] 이메일: " + email + ", 삭제 성공: " + deleted);

    }

    /**
     * 엑세스 토큰을 블랙리스트에 등록 (로그아웃 시 호출)
     *
     * @param 토큰 로그아웃 처리핳 엑세스 토큰 문자열
     */
    public void addToBlackList(String token) {
        // 토큰 만료까지 남은 시간 계산
        long ttl = getRemainingTime(token);

        // Redis 블랙리스트에 저장 (key: blacklist:{token}, TTL: 남은 만료시간)
        redisTemplate.opsForValue().set("blacklist:" + token, "logout", ttl, TimeUnit.MILLISECONDS);

//        System.out.println("[블랙리스트 등록] 엑세스 토큰: " + token + ", TTL(ms): " + ttl);

    }

    /**
     * 해당 토큰이 블랙리스트에 존재하는지 확인
     *
     * @param token 검사할 Access Token 문자열
     * @return 블랙 리스트에 있으면 true, 없으면 false
     */
    public boolean isBlacklisted(String token) {
        boolean isBlack = Boolean.TRUE.equals(redisTemplate.hasKey("blacklist:" + token));
//        System.out.println("[블랙리스트 조회] 토큰: " + token + ", 블랙리스트 여부: " + isBlack);
        return isBlack;
    }


    /**
     * JWT에서 사용자 email 가져오기
     *
     * @param token JWT 문자열
     * @return email (subject 필드)
     */
    public String getUserEmail(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY) // 비밀키로 디코딩
            .parseClaimsJws(token) // JWT 파싱
            .getBody() // JWT  내부의 payload(body) 가져옴
            .getSubject(); // 그 중에서 subject(email)를 꺼냄
    }

    /**
     * 토큰 유효한지 확인하는 메서드
     *
     * @param token 문자열
     * @return 유효하면 true, 문제가 있으면 false
     */
    public boolean validateToken(String token) {
        try {
            // 토큰을 파싱해보고 문제가 없으면 유효하다고 판단
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("토큰이 만료됨: " + e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            System.out.println("지원하지 않는 토큰: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("토큰 형식이 잘못됨: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("서명 오류: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("잘못된 인자: " + e.getMessage());
        }

        return false;
    }

    /**
     * 토큰의 남은 만료 시간(ms) 계산
     *
     * @param token JWT 문자열
     * @return 만료까지 남은 시간 (밀리초 단위)
     */
    public long getRemainingTime(String token) {
        Date expiration = Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody()
            .getExpiration();

        return expiration.getTime() - System.currentTimeMillis();
    }
}