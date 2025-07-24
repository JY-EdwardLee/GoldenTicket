package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import com.ssafy.ticket_backend.mapper.PostMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    public boolean createPost(PostRequest postRequest) {
        // 추후 사용자 정보 포함 (임시정보)
        // postRequest.setUserId(999);

        try {
            if (postRequest.getBoardType() == null) {
                throw new IllegalArgumentException("게시판 타입이 필요합니다.");
            }

            int result = postMapper.insertPost(postRequest);
            if (result == 1) {
                return true;
            } else {
                throw new DatabaseOperationException("DB 과정에서 오류가 발생하였습니다");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new PostCreateFailException("Post 요청 시 오류");
        }
    }

    // 조회수 증가여부 처리해야 함.
    public PostDetailResponse getPostDetail(Long postId) {

        PostDetailResponse postDetailResponse = postMapper.findPostById(postId);
        Long userId = postDetailResponse.getUserId();
        // 사용자 정보 가져오기 + 댓글정보 가져오기

        return postDetailResponse;
    }
}