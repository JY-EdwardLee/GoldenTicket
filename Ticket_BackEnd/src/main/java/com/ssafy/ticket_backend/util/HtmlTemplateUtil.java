package com.ssafy.ticket_backend.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class HtmlTemplateUtil {

    /**
     * HTML 템플릿 처리
     */
    public String processTemplate(String templatePath, Map<String, String> data) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + templatePath);
        InputStream inputStream = resource.getInputStream();
        String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        String result = template;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            
            // 모든 매치를 치환
            result = result.replace(placeholder, value);
        }
        
        return result;
    }

    /**
     * 단체 관람 신청서 HTML 생성
     */
    public String generateGroupApplicationHtml(Map<String, String> applicationData) throws IOException {
        return processTemplate("group-application.html", applicationData);
    }
}
