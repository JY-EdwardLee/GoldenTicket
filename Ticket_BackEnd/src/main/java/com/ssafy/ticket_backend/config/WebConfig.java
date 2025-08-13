package com.ssafy.ticket_backend.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

//    @Value("${FE_BASE_URL}")
//    private String FE_BASE_URL;
//
//    @Value("${BE_BASE_URL}")
//    private String BE_BASE_URL;
//
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // 모든 API 경로
//                    .allowedOrigins("http://localhost:8080/", FE_BASE_URL,
//                        BE_BASE_URL) // Vue dev 서버 주소
//                    .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
//                    .allowedHeaders("*").allowCredentials(true); // 필요시 쿠키 허용
//            }
//        };
//    }
}
