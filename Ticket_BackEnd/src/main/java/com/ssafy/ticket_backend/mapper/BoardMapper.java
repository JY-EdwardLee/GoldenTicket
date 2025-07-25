package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

    List<PostAllResponse> getPostsByCategory(String category);
    List<PostAllResponse> searchPosts(String type, String title, String content, String writer);
}
