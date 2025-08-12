package com.ssafy.ticket_backend.config;

import com.ssafy.ticket_backend.util.JwtAuthenticationFilter;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

//        http.csrf(AbstractHttpConfigurer::disable)
//            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 활성화
//            .authorizeHttpRequests(auth -> auth.requestMatchers("/users/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
//                .requestMatchers(HttpMethod.POST, "/users/signup").permitAll()
//                .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
//                .requestMatchers(HttpMethod.POST, "/games/**").permitAll()
//                // 이 외에는 인증 필요
//                .anyRequest().authenticated())
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 허용할 도메인 설정
        config.setAllowedOrigins(Arrays.asList(FE_BASE_URL, BE_BASE_URL));

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
