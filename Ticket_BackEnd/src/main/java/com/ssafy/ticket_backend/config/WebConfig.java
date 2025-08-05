package com.ssafy.ticket_backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

  @Value("${VITE_PROD_API_URL}")
  private String Prod_api_url;

  @Value("${VITE_PROD_FRONT_URL}")
  private String Prod_front_url;

  @Value("${VITE_DEV_API_URL}")
  private String Local_api_url;

  @Value("${VITE_DEV_FRONT_URL}")
  private String Local_front_url;

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 API 경로
            .allowedOrigins(Local_front_url, Prod_front_url,
                Prod_api_url) // Vue dev 서버 주소
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS").allowedHeaders("*")
            .allowCredentials(true); // 필요시 쿠키 허용
      }
    };
  }
}
