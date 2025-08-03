package com.ssafy.ticket_backend.config;

import com.ssafy.ticket_backend.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
//        http.csrf(AbstractHttpConfigurer::disable)
//            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll())
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(cors -> {
        }) // CORS 활성화
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/users/signup").permitAll()
            .requestMatchers(HttpMethod.POST, "/auth/refresh").permitAll()
            .requestMatchers(HttpMethod.POST, "/games/**").permitAll()
            .requestMatchers("/posts/**").permitAll()
            .requestMatchers("/boards/**").permitAll()
            .requestMatchers("/s3/**").permitAll()
            // 이 외에는 인증 필요
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
