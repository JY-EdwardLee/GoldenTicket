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
     * HTML 템플릿을 읽어서 플레이스홀더를 실제 값으로 교체합니다.
     * 
     * @param templatePath 템플릿 파일 경로 (resources/templates/ 기준)
     * @param data 교체할 데이터 맵
     * @return 완성된 HTML 문자열
     * @throws IOException 템플릿 파일을 읽을 수 없는 경우
     */
    public String processTemplate(String templatePath, Map<String, String> data) throws IOException {
        // 템플릿 파일 읽기
        ClassPathResource resource = new ClassPathResource("templates/" + templatePath);
        InputStream inputStream = resource.getInputStream();
        String template = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        
        // 플레이스홀더 교체
        String result = template;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            String placeholder = "${" + entry.getKey() + "}";
            String value = entry.getValue() != null ? entry.getValue() : "";
            result = result.replace(placeholder, value);
        }
        
        return result;
    }

    /**
     * 단체 관람 신청서 HTML을 생성합니다.
     * 
     * @param applicationData 신청서 데이터
     * @return 완성된 HTML 문자열
     * @throws IOException 템플릿 파일을 읽을 수 없는 경우
     */
    public String generateGroupApplicationHtml(Map<String, String> applicationData) throws IOException {
        return processTemplate("group-application.html", applicationData);
    }
}
