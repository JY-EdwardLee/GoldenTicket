package com.ssafy.ticket_backend.mapper;

import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.model.BoardType;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BoardMapper {

    List<PostAllResponse> getPostsByCategory(BoardType boardType);

    List<PostAllResponse> searchPosts(BoardType boardType, String title, String content,
        String writer);
}
