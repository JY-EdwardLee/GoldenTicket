package com.ssafy.ticket_backend.config;

import com.ssafy.ticket_backend.util.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${FE_BASE_URL}")
    private String FE_BASE_URL;
    @Value("${BE_BASE_URL}")
    private String BE_BASE_URL;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //        access 토큰 재생성 안될 때, 여기 수정하면 됨
//        http.csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/users/signup", "/users/signup/**", "/users/auth/**",
                    "/users/login-user").permitAll()  // 로그인 관리
                .requestMatchers(HttpMethod.POST, "/s3/download").permitAll()  // S3 이미지 요청
                .requestMatchers(HttpMethod.POST, "/games").permitAll()  // 경기 목록 조회
                .requestMatchers(HttpMethod.GET, "/group/*").permitAll()  // 단체 관람 조회
                .requestMatchers(HttpMethod.GET, "/boards/category/*").permitAll()  // 게시판 별 게시글 조회
                .requestMatchers(HttpMethod.GET, "/boards/*").permitAll()  // 게시판 별 게시글 조회
                .requestMatchers(HttpMethod.GET, "/posts/*").permitAll()  // 게시글 상세보기
                .requestMatchers(HttpMethod.GET, "/rank/**").permitAll()  // 양도 순위
                .requestMatchers(HttpMethod.GET, "/crawler/**").permitAll() // 크롤링
                .anyRequest().authenticated())  // 이 외에는 인증 필요
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 도메인 설정
        config.setAllowedOrigins(Arrays.asList(FE_BASE_URL, BE_BASE_URL, "http://localhost:8080/",
            "http://localhost:5173/"));

        // 허용할 HTTP 메서드 설정
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 허용할 헤더 설정
        config.setAllowedHeaders(List.of("*"));

        // 자격 증명(쿠키, 인증 헤더 등)을 허용할지 여부
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로("/**")에 대해 위에서 설정한 CORS 규칙을 적용
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}
