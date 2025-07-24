package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.mapper.PostMapper;
import com.ssafy.ticket_backend.model.BoardType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    public boolean createPost(PostRequest postRequest) {
        // TODO: 실제 로그인 사용자 ID로 대체
        postRequest.setUserId(1);

        String boardTypeStr = postRequest.getBoardType().toString();
        BoardType.fromString(boardTypeStr); // enum 검증

        // DB 처리
        int result = postMapper.insertPost(postRequest);
        if (result != 1) {
            throw new DatabaseOperationException("게시글 DB저장 중 오류");
        }

        return true;
    }


    // 조회수 증가여부 처리해야 함.
    public PostDetailResponse getPostDetail(Long postId) {

        PostDetailResponse postDetailResponse = postMapper.findPostById(postId);
        Long userId = postDetailResponse.getUserId();
        // 사용자 정보 가져오기 + 댓글정보 가져오기

        return postDetailResponse;
    }
}