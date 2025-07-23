package com.ssafy.ticket_backend.dto.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDetailResponse {

    int id;  // 게시글 id
    // TODO UserResponse user;  // 작성자
    String title;  // 제목
    String content;  // 내용
    String imageUrl;  // 이미지 url
    Date createDate;  // 작성 시간
    Date updateDate;  // 수정 시간
    int view;  // 조회수
    int like;  // 좋아요
    // TODO List<Comments> comments  // 댓글 목록
}
