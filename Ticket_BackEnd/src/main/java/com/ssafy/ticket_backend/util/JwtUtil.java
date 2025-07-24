package com.ssafy.ticket_backend.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    // JWT 서명에 사용 할 비밀 키
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    // 엑세스 토큰 만료 시간: 1시간
    private final long EXPIRATION_TIME = 1000 * 60 * 60;

    // 리프레시 토큰 만료 시간 : 7일
    private final long REFRESH_TIME = 1000 * 60 * 60 * 24 * 7;

    /**
     * 액세스 토큰 생성 메서드
     *
     * @param email 토큰에 담길 정보
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
     * 리프레시 토큰 생성 메서드
     *
     * @param email 토큰에 담길 정보
     * @return 생성된 리프레시 토큰 문자열
     */
    public String generateRefreshToken(String email) {
        return Jwts.builder().setSubject(email) // 토큰 제목에 email
            .setIssuedAt(new Date()) // 발급 시간
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY).compact();
    }

    /**
     * JWT에서 email 가져오기
     *
     * @param token JWT 문자열
     * @return email (subject 필드)
     */
    public String getUserId(String token) {
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
}