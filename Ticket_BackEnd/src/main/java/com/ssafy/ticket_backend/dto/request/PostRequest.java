package com.ssafy.ticket_backend.dto.request;

import com.ssafy.ticket_backend.model.BoardType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PostRequest {

  Long postId;        // insert 이후 기본키 반환값으로 받아와야해서 추가

  Long userId;
  String title;
  String content;
  BoardType boardType;
  String imageUrl;
}