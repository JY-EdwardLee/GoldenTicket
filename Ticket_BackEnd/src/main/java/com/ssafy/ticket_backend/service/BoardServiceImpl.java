package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.mapper.BoardMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    public List<PostAllResponse> getPostsByCategory(String category) {
        List<PostAllResponse> list = boardMapper.getPostsByCategory(category);
        if (list.size() == 0 || list == null) {
            throw new DatabaseOperationException("카테고리별 DB조회 중 오류");
        }
        return list;
    }

    public List<PostAllResponse> searchPosts(String type, String title, String content,
        String writer) {
        List<PostAllResponse> list = boardMapper.searchPosts(type, title, content, writer);
        if (list == null || list.isEmpty()) {
            throw new DatabaseOperationException("검색 결과가 없습니다.");
        }
        return list;
    }

}
