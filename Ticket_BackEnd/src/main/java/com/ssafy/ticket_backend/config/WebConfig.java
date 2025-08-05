package com.ssafy.ticket_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

  @Value("${fe_base_url}")
  private String fe_base_url;

  @Value("${be_base_url}")
  private String be_base_url;

  @Value("${fe_local_url}")
  private String fe_local_url;

  @Value("${be_local_url}")
  private String be_local_url;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로
            .allowedOrigins(fe_local_url, fe_base_url,
                be_base_url) // Vue dev 서버 주소
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS").allowedHeaders("*")
            .allowCredentials(true); // 필요시 쿠키 허용
      }
    };
  }
}
