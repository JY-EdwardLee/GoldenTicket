package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.request.S3DownloadRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostLikeResponse;
import com.ssafy.ticket_backend.dto.response.PostUserResponse;
import com.ssafy.ticket_backend.dto.response.S3DownloadResponse;
import com.ssafy.ticket_backend.exception.DatabaseException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import com.ssafy.ticket_backend.exception.PostDeleteFailException;
import com.ssafy.ticket_backend.exception.PostLikeFailException;
import com.ssafy.ticket_backend.exception.PostNotFoundException;
import com.ssafy.ticket_backend.exception.PostRetrievalException;
import com.ssafy.ticket_backend.exception.PostUpdateException;
import com.ssafy.ticket_backend.exception.PostUserNotFoundException;
import com.ssafy.ticket_backend.exception.UserBlockException;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import com.ssafy.ticket_backend.mapper.PostMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
import com.ssafy.ticket_backend.model.S3Type;
import com.ssafy.ticket_backend.model.User;
import com.ssafy.ticket_backend.model.UserRole;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final UserMapper userMapper;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;

    private final S3UserService s3UserService;
    private final S3PostService s3PostService;

    /**
     * 게시글 작성
     *
     * @param email       작성자 정보
     * @param postRequest 게시글 내용
     */
    @Transactional
    @Override
    public void createPost(String email, PostRequest postRequest) {
        try {
            User user = userMapper.selectUserByEmail(email);
            postRequest.setUserId(user.getUserId());

            if (user.getIsBlock()) {
                throw new UserBlockException("차단된 사용자는 글을 작성할 수 없습니다.");
            }

            // 게시글 먼저 생성하여 postId 획득
            Long result = postMapper.insertPost(postRequest);

            Long actualPostId = postRequest.getPostId();

            //  TODO 이미지 처리 성공 -> 단 나중에 확인 하겠습니다.
            //  생성된 게시글의 postId를 가져와서 이미지 키 변경
            if (postRequest.getImageUrl() != null && !postRequest.getImageUrl().trim().isEmpty()) {
                try {

                    // 임시 키를 실제 postId로 변경
                    String updatedImageUrl = s3PostService.updateTempKeyToActualKey(
                        postRequest.getImageUrl(), actualPostId);

                    // 변경된 이미지 URL로 게시글 업데이트
                    if (updatedImageUrl != null) {
                        int updateResult = postMapper.updatePostImage(actualPostId,
                            updatedImageUrl);

                        if (updateResult != 1) {
                            System.err.println("이미지 URL 업데이트 실패");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("이미지 키 변경 실패: " + e.getMessage());
                    // 이미지 키 변경 실패해도 게시글 작성은 성공으로 처리
                }
            }
        } catch (DataAccessException | UserBlockException e) {
            throw e;
        } catch (Exception e) {
            throw new PostCreateFailException("게시글 작성 중 오류가 발생하였습니다.");
        }
    }

    /**
     * 게시글 상세보기
     *
     * @param postId
     * @return PostDetailResponse
     */
    @Transactional
    @Override
    public PostDetailResponse getPostDetail(Long postId) {
        try {
            // 게시물 정보
            PostDetailResponse postDetailResponse = postMapper.selectPostById(postId);

            if (postDetailResponse == null) {
                throw new PostNotFoundException("해당 게시글이 존재하지 않습니다.");
            }

            // 조회수
            postDetailResponse.setViewCount(postDetailResponse.getViewCount() + 1);
            int result = postMapper.plusView(postId);

            if (result == 0) {
                throw new DatabaseException("조회수 증가에 실패했습니다.");
            }

            // 댓글
            List<CommentDetailResponse> comments = commentMapper.getComments(postId);

            for (CommentDetailResponse cd : comments) {
                User user = userMapper.selectUserByUserId(cd.getUserId());

                S3DownloadResponse CommentUserProfileUrl = s3UserService.getImageUrlsByTypeAndRefId(
                    new S3DownloadRequest(S3Type.UserProfile, cd.getUserId()));

                cd.setCommentUserUrl(CommentUserProfileUrl.getDownloadUrl());
                cd.setNickName(user.getNickname());
            }

            postDetailResponse.setCommentList(comments);

            // 사용자 정보
            Long userId = postMapper.selectUserIdByPostId(postId);

            // 게시글 작성자 프로필 이미지 URL 추가
            S3DownloadResponse userProfileUrl = s3UserService.getImageUrlsByTypeAndRefId(
                new S3DownloadRequest(S3Type.UserProfile, userId));
            postDetailResponse.setImageUrl(userProfileUrl.getDownloadUrl());

            // 게시글 이미지 URL 추가
            String postImageKey = postMapper.getPostImageKey(postId); // DB에서 게시글 이미지 키 조회

            if (postImageKey != null && !postImageKey.isEmpty()) {
                S3DownloadResponse postImageUrl = s3PostService.getImageUrlsByTypeAndRefId(
                    new S3DownloadRequest(S3Type.PostImage, postId));
                postDetailResponse.setPostImageUrl(postImageUrl.getDownloadUrl());
            }

            if (userId == null) {
                throw new PostUserNotFoundException("게시글 작성자 정보가 존재하지 않습니다.");
            }

            PostUserResponse postUserResponse = userMapper.selectUserByPostId(userId);

            if (postUserResponse == null) {
                throw new PostUserNotFoundException("게시글 작성자 정보가 존재하지 않습니다.");
            }

            postDetailResponse.setPostUser(postUserResponse);

            System.out.println(postDetailResponse);
            return postDetailResponse;

        } catch (PostUserNotFoundException | PostNotFoundException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new PostRetrievalException("게시글 조회시 오류가 발생하였습니다.");
        }
    }

    /**
     * 게시글 수정
     *
     * @param email             수정을 시도하려는 사람의 이메일
     * @param postUpdateRequest 수정 내용
     */
    @Transactional
    @Override
    public void updatePost(String email, PostUpdateRequest postUpdateRequest) {
        try {
            User user = userMapper.selectUserByEmail(email);

            if (user.getIsBlock()) {
                throw new UserBlockException("차단된 사용자는 글을 작성할 수 없습니다.");
            }

            if (!user.getUserRole().equals(UserRole.ADMIN)) {
                if (user.getUserId() != postMapper.selectUserIdByPostId(
                    postUpdateRequest.getPostId())) {
                    throw new PostUpdateException("권한이 없습니다.");
                }
            }

            int result = postMapper.updatePost(postUpdateRequest);

            // 이미지 URL은 updatePost 쿼리에서 이미 처리됨
            // imageUrl이 null이면 DB에서도 null로 업데이트됨

            if (result != 1) {
                throw new DatabaseException("게시물 수정중 오류가 발생하였습니다.");
            }
        } catch (UserBlockException | PostUpdateException | DatabaseException e) {
            throw e;
        } catch (Exception e) {
            throw new PostUpdateException("게시글 수정시 오류가 발생하였습니다.");
        }

    }

    /**
     * 게시글 삭제
     *
     * @param email  삭제를 시도하려는 사람의 이메일
     * @param PostId 삭제하려는 게시글
     */
    @Transactional
    @Override
    public void deletePost(String email, Long PostId) {
        try {
            User user = userMapper.selectUserByEmail(email);

            if (!user.getUserRole().equals(UserRole.ADMIN)) {
                if (user.getUserId() != postMapper.selectUserIdByPostId(PostId)) {
                    throw new PostDeleteFailException("권한이 없습니다.");
                }
            }

            int deleteTrue = postMapper.deleteTrue(PostId);

            if (deleteTrue != 1) {
                throw new DatabaseException("게시물 삭제 중 오류가 발생하였습니다.");
            }
        } catch (PostDeleteFailException e) {
            throw e;
        } catch (Exception e) {
            throw new PostDeleteFailException("게시글 삭제시 오류가 발생하였습니다.");
        }
    }

    /**
     * 좋아요 누르기
     *
     * @param email  좋아요를 누른 사용자
     * @param postId 게시글 번호
     */
    @Transactional
    @Override
    public PostLikeResponse likePost(String email, Long postId) {
        try {
            User user = userMapper.selectUserByEmail(email);

            // 이미 좋아요를 눌렀다면 -> 좋아요 삭제
            if (postMapper.selectLike(user.getUserId(), postId)) {
                // 좋아요 삭제 메서드
                postMapper.minusLike(postId);
                postMapper.deletePostLike(user.getUserId(), postId);

            } else {
                postMapper.plusLike(postId);
                postMapper.insertPostLike(user.getUserId(), postId);
            }

            // 좋아요 수 조회
            Long likeCount = postMapper.selectPostLike(postId);

            return new PostLikeResponse(postId, likeCount);
        } catch (Exception e) {
            throw new PostLikeFailException("게시글 좋아요 시 오류가 발생하였습니다.");
        }
    }
}
