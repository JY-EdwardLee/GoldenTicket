package com.ssafy.ticket_backend.config;

import com.ssafy.ticket_backend.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//        http
//            .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (JWT 기반 인증에서는 불필요함)
//            .authorizeHttpRequests(auth -> auth
//                // JWT 없이 접근 가능한 경로들
//                .requestMatchers(HttpMethod.POST, "/users/signup").permitAll()
//                .requestMatchers(HttpMethod.GET, "/users/auth/oauth/callback").permitAll()
//                .requestMatchers(HttpMethod.GET, "/users/auth/naver/callback").permitAll()
//                .requestMatchers(HttpMethod.GET, "/games/**").permitAll()
//                .requestMatchers(HttpMethod.GET, "/boards/**").permitAll()
//                .requestMatchers(HttpMethod.GET, "/posts/*").permitAll()
//                // 그 외 요청들은 인증 필요
//                .anyRequest().authenticated()
//            )
//            // JWT 필터 등록
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
