package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import java.util.List;


public interface BoardService {

    List<PostAllResponse> getPostsByCategory(String category);
    List<PostAllResponse> searchPosts(String type, String title, String content, String writer);
}
