package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.BoardType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequest {

    int userId;    // 시큐리티를 통해서 값 가져올 예정. (HTTP 요청시 정보 포함X)
    String title;  // 제목
    String content;  // 내용
    BoardType boardType;  // 게시판 종류
    String imageUrl;  // 이미지 URL
}