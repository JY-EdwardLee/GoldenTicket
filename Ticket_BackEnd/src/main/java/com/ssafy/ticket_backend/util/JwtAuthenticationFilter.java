package com.ssafy.ticket_backend.util;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
        System.out.println("doFilterInternal 들어감!!!!!!!!!!");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                System.out.println(cookie.toString());
            }
        }


        final String authorizationHeader = request.getHeader("Authorization");
        String jwt = null;
        String userEmail = null;

        // Authorization 헤더가 있고 Bearer로 시작하면 토큰 추출
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);

            // 블랙 리스트 확인
            if (jwtUtil.isBlacklisted(jwt)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Access token is blacklisted.");
                return;
            }

            // 토큰 유효성 검증 및 사용자 정보 추출
            try {
                if (jwtUtil.validateToken(jwt)) {
                    userEmail = jwtUtil.getUserEmail(jwt);
                }
            } catch (ExpiredJwtException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"message\": \"Access token expired\"}");
                return;
            }
        }

        // 인증정보가 없고 userEmail 존재하면 인증처리 진행
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // UserDetailsService에서 유저 정보 로드 (여기선 userId가 PK로 사용된다고 가정)
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 다음 필터 체인 계속 진행
        filterChain.doFilter(request, response);
    }
}