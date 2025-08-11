package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.response.PostAllResponse;
import com.ssafy.ticket_backend.exception.BoardException;
import com.ssafy.ticket_backend.mapper.BoardMapper;
import com.ssafy.ticket_backend.model.BoardType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    public List<PostAllResponse> getPostsByCategory(BoardType category) {
        List<PostAllResponse> result = boardMapper.getPostsByCategory(category);

        if (result == null || result.isEmpty()) {
            throw new BoardException("검색 결과가 없습니다.");
        }

        return result;
    }

    /**
     * @param type
     * @param title
     * @param content
     * @param writer
     * @return
     */
    public List<PostAllResponse> searchPostBy(BoardType type, String title, String content,
        String writer) {
        if (title != null) {
            if (title.length() < 2) {
                throw new BoardException("검색어의 길이는 최소 2글자 입니다!");
            }
        } else if (content != null) {
            if (content.length() < 2) {
                throw new BoardException("검색어의 길이는 최소 2글자 입니다!");
            }
        } else if (writer != null) {
            if (writer.length() < 2) {
                throw new BoardException("검색어의 길이는 최소 2글자 입니다!");
            }
        }

        List<PostAllResponse> result = boardMapper.searchPosts(type, title, content, writer);

        if (result == null || result.isEmpty()) {
            throw new BoardException("검색 결과가 없습니다.");
        }

        return result;
    }
}
