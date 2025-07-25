package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import com.ssafy.ticket_backend.mapper.PostMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    public boolean createPost(PostRequest postRequest) {
        try {

            // TODO: 실제 로그인 사용자 ID로 대체
            postRequest.setUserId(1);

            int result = postMapper.insertPost(postRequest);

            if (result != 1) {
                throw new DatabaseOperationException("게시글 DB저장중 오류");
            }
            return true;
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            throw new PostCreateFailException("게시글 등록중 오류");
        }
    }

    public PostDetailResponse getPostDetail(Long postId) {

        PostDetailResponse postDetailResponse = postMapper.findPostById(postId);
        // viewcnt+1
        postMapper.plusView(postDetailResponse.getBoardId());

        // 댓글정보 가져오기
        List<CommentDetailResponse> comments = commentMapper.getComments(postId);
        postDetailResponse.setComments(comments);

        // TODO 사용자 정보 가져오기

        return postDetailResponse;
    }

    public boolean deletePost(Long PostId) {
        int result = postMapper.deletePost(PostId);
        if (result != 1) {
            throw new DatabaseOperationException("게시물 DB 삭제중 오류");
        }
        return true;
    }

    public boolean updatePost(PostUpdateRequest postUpdateRequest) {
        int result = postMapper.updatePost(postUpdateRequest);
        if (result != 1) {
            throw new DatabaseOperationException("게시글 DB업데이트중 오류");
        }
        return true;
    }

}