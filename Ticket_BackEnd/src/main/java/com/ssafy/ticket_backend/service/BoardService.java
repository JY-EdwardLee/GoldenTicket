package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.model.BoardType;
import java.util.List;

public interface BoardService {

    List<PostAllResponse> getPostsByCategory(BoardType category);

    List<PostAllResponse> searchPostBy(BoardType boardType, String title, String content,
        String writer);
}
