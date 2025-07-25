package com.ssafy.ticket_backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TossPayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("토스 결제 준비 요청 테스트")
    void readyTossPaymentTest() throws Exception {
        // when & then
        mockMvc.perform(post("/payment/toss/ready").contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk()).andDo(print()); // 응답 본문을 콘솔에 출력
    }
}
