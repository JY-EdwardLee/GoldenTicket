package com.ssafy.ticket_backend.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String userId = null;
        
        // Authorization 헤더가 있고 Bearer로 시작하면 토큰 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            try {
                // 토큰에서 유저 ID 추출
                userId = jwtUtil.getUserId(jwt);
            } catch (Exception e) {
                // 토큰 파싱 에러 처리
                System.out.printf("JWT 파싱 에러: " + e.getMessage());
            }
        }

        // 인증정보가 없고 userId가 존재하면 인증처리 진행
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // UserDetailsService에서 유저 정보 로드 (여기선 userId가 PK로 사용된다고 가정)
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userId);

            // 토큰이 유효한지 확인
            if (jwtUtil.validateToken(jwt)) {
                // 인증 토큰 생성 (권한 포함)
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContext에 인증 정보 넣기
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 다음 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}