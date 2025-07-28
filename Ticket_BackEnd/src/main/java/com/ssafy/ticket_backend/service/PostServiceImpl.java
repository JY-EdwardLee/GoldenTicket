package com.ssafy.ticket_backend.service;

import com.ssafy.ticket_backend.dto.request.PostRequest;
import com.ssafy.ticket_backend.dto.request.PostUpdateRequest;
import com.ssafy.ticket_backend.dto.response.CommentDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostDetailResponse;
import com.ssafy.ticket_backend.dto.response.PostLikeResponse;
import com.ssafy.ticket_backend.dto.response.PostUserResponse;
import com.ssafy.ticket_backend.exception.BlockedUserException;
import com.ssafy.ticket_backend.exception.DatabaseOperationException;
import com.ssafy.ticket_backend.exception.PostCreateFailException;
import com.ssafy.ticket_backend.exception.PostDeleteException;
import com.ssafy.ticket_backend.exception.PostDeleteFailException;
import com.ssafy.ticket_backend.exception.PostLikeFailException;
import com.ssafy.ticket_backend.exception.PostNotFoundException;
import com.ssafy.ticket_backend.exception.PostRetrievalException;
import com.ssafy.ticket_backend.exception.PostUpdateException;
import com.ssafy.ticket_backend.exception.PostUpdateFailException;
import com.ssafy.ticket_backend.exception.PostUserNotFoundException;
import com.ssafy.ticket_backend.mapper.CommentMapper;
import com.ssafy.ticket_backend.mapper.PostMapper;
import com.ssafy.ticket_backend.mapper.UserMapper;
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


  /**
   * 게시글 작성
   *
   * @param email       작성자 정보
   * @param postRequest 게시글 내용
   * @return true
   */
  @Transactional
  @Override
  public boolean createPost(String email, PostRequest postRequest) {
    try {
      User user = userMapper.selectUserByEmail(email);
      postRequest.setUserId(user.getUserId());

      if (user.getIsBlock()) {
        throw new BlockedUserException("차단된 사용자는 글을 작성할 수 없습니다.");
      }

      int result = postMapper.insertPost(postRequest);

      if (result != 1) {
        throw new DatabaseOperationException("게시글 저장중 오류가 발생하였습니다.");
      }
    } catch (DataAccessException e) {
      throw new DatabaseOperationException("게시글 작성에 실패하였습니다.");
    } catch (BlockedUserException e) {
      throw new BlockedUserException("차단된 사용자로 인해 작성에 실패하였습니다.");
    } catch (Exception e) {
      throw new PostCreateFailException("게시글 작성 중 오류가 발생하였습니다.");
    }
    return true;
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
        throw new DatabaseOperationException("조회수 증가에 실패했습니다.");
      }

      // 댓글
      List<CommentDetailResponse> comments = commentMapper.getComments(postId);
      postDetailResponse.setCommentList(comments);

      // 사용자 정보
      Long userId = postMapper.selectUserIdByPostId(postId);

      if (userId == null) {
        throw new PostUserNotFoundException("게시글 작성자 정보가 존재하지 않습니다.");
      }
      PostUserResponse postUserResponse = userMapper.selectUserByPostId(userId);
      if (postUserResponse == null) {
        throw new PostUserNotFoundException("게시글 작성자 정보를 불러오는 데 실패했습니다.");
      }
      postDetailResponse.setPostUser(postUserResponse);

      return postDetailResponse;

    } catch (PostUserNotFoundException e) {
      throw e;
    } catch (DatabaseOperationException e) {
      throw e;
    } catch (PostNotFoundException e) {
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
   * @return true
   */
  @Transactional
  @Override
  public boolean updatePost(String email, PostUpdateRequest postUpdateRequest) {
    try {
      User user = userMapper.selectUserByEmail(email);

      if (user.getIsBlock()) {
        throw new BlockedUserException("차단된 사용자는 글을 작성할 수 없습니다.");
      }

      if (!user.getUserRole().equals(UserRole.ADMIN)) {
        if (user.getUserId() != postMapper.selectUserIdByPostId(
            postUpdateRequest.getPostId())) {
          throw new PostUpdateFailException("권한이 없습니다.");
        }
      }

      int result = postMapper.updatePost(postUpdateRequest);

      if (result != 1) {
        throw new DatabaseOperationException("간단하게 작성하자!!!!");
      }
    } catch (BlockedUserException e) {
      throw new BlockedUserException("차단된 사용자로 인해 작성에 실패하였습니다.");
    } catch (PostUpdateFailException e) {
      throw new PostUpdateFailException("게시글 접근 권한으로 인해 오류가 발생하였습니다");
    } catch (Exception e) {
      throw new PostUpdateException("게시글 수정시 오류가 발생하였습니다.");
    }

    return true;
  }

  /**
   * 게시글 삭제
   *
   * @param email  삭제를 시도하려는 사람의 이메일
   * @param PostId 삭제하려는 게시글
   * @return true
   */
  @Transactional
  @Override
  public boolean deletePost(String email, Long PostId) {
    try {
      User user = userMapper.selectUserByEmail(email);

      if (!user.getUserRole().equals(UserRole.ADMIN)) {
        if (user.getUserId() != postMapper.selectUserIdByPostId(PostId)) {
          throw new PostDeleteFailException("권한이 없습니다.");
        }
      }

      int result = postMapper.deletePost(PostId);

      if (result != 1) {
        throw new DatabaseOperationException("게시물 DB 삭제중 오류");
      }
    } catch (PostDeleteFailException e) {
      throw new PostDeleteFailException("게시글 접근 권한으로 인해 오류가 발생하였습니다");
    } catch (Exception e) {
      e.printStackTrace();
      throw new PostDeleteException("게시글 삭제시 오류가 발생하였습니다.");
    }

    return true;
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