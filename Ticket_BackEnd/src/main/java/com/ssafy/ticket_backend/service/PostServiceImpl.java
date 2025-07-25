package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import com.ssafy.ticket_backend.exception.PostDeleteFailException;
import com.ssafy.ticket_backend.exception.PostLikeFailException;
import com.ssafy.ticket_backend.exception.PostUpdateFailException;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import com.ssafy.ticket_backend.mapper.PostMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.model.UserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public void createPost(String email, PostRequest postRequest) {
        try {
            User user = userMapper.selectUserByEmail(email);
            postRequest.setUserId(user.getId());

            // TODO user테이블 user_id를 id로 바꿔야함!!!!!!!

            int result = postMapper.insertPost(postRequest);

            if (result != 1) {
                throw new DatabaseOperationException("게시글 DB저장중 오류");
            }
        } catch (DatabaseOperationException e) {
            e.printStackTrace();

            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PostCreateFailException("게시글 등록중 오류");
        }
    }

    @Override
    public PostDetailResponse getPostDetail(Long postId) {

        PostDetailResponse postDetailResponse = postMapper.selectPostById(postId);
        // viewcnt+1
        postMapper.plusView(postDetailResponse.getBoardId());

        // 댓글정보 가져오기
        List<CommentDetailResponse> comments = commentMapper.getComments(postId);
        postDetailResponse.setComments(comments);

        // TODO 사용자 정보 가져오기

        return postDetailResponse;
    }

    /**
     * 게시글 삭제
     *
     * @param email  삭제를 시도하려는 사람의 이메일
     * @param PostId 삭제하려는 게시글
     * @return
     */
    @Transactional
    @Override
    public boolean deletePost(String email, Long PostId) {
        User user = userMapper.selectUserByEmail(email);

        // 삭제를 하려는 사람이 운영자가 아니고
        if (!user.getUserRole().equals(UserRole.ADMIN)) {
            // 글을 작성한 사람이 아니라면
            if (user.getId() != postMapper.selectPostById(PostId).getUserId()) {
                throw new PostDeleteFailException("권한이 없습니다.");
            }
        }

        int result = postMapper.deletePost(PostId);

        if (result != 1) {
            throw new DatabaseOperationException("게시물 DB 삭제중 오류");
        }

        return true;
    }

    /**
     * 게시글 수정
     *
     * @param email             수정을 시도하려는 사람의 이메일
     * @param postUpdateRequest 수정 내용
     * @return
     */
    @Transactional
    @Override
    public boolean updatePost(String email, PostUpdateRequest postUpdateRequest) {
        PostDetailResponse post = postMapper.selectPostById(postUpdateRequest.getPostId());

        // 게시글을 작성하는 사람과 수정하려는 사람이 다르다면
        if (post.getUserId() != userMapper.selectUserByEmail(email).getId()) {
            throw new PostUpdateFailException("수정 권한이 없습니다.");
        }

        int result = postMapper.updatePost(postUpdateRequest);

        if (result != 1) {
            throw new DatabaseOperationException("게시글 DB업데이트중 오류");
        }

        return true;
    }

    /**
     * 좋아요 누르기
     *
     * @param email  좋아요를 누른 사용자
     * @param postId
     */
    @Transactional
    @Override
    public void likePost(String email, Long postId) {

        User user = userMapper.selectUserByEmail(email);

        // 이미 좋아요를 눌렀다면
        if (postMapper.selectLike(user.getId(), postId)) {
            throw new PostLikeFailException("이미 좋아요를 눌렀습니다.");
        }

        postMapper.plusLike(postId);
        postMapper.insertPostLike(user.getId(), postId);
    }
}