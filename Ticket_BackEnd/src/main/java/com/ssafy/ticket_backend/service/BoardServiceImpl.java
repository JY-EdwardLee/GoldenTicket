package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.mapper.BoardMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl {

    private final BoardMapper boardMapper;

    public List<PostAllResponse> getPostsByCategory(String category) {
        List<PostAllResponse> list = boardMapper.getPostsByCategory(category);
        if (list.size() == 0 || list == null) {
            throw new DatabaseOperationException("카테고리별 DB조회 중 오류");
        }
        return list;
    }


}
