package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.BoardType;
import lombok.Getter;

@Getter
public class PostRequest {

    String title;  // 제목
    String content;  // 내용
    BoardType boardType;  // 게시판 종류
    String imageUrl;  // 이미지 URL
}