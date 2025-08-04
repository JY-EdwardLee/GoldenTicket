import { authApiClient, publicApiClient, apiErrorHandler } from './index.js';

// 게시판 관련 API
export const boardAPI = {
  // 게시글 작성
  createPost: async (postData) => {
    try {
      const response = await authApiClient.post('/posts', {
        title: postData.title,
        content: postData.content,
        boardType: postData.boardType,
        imageUrl: postData.imageUrl || null
      });
      
      // 성공 응답 처리
      if (response.data.success) {
        return {
          success: true,
          message: response.data.message || '게시글 작성 성공'
        };
      } else {
        return {
          success: false,
          message: response.data.message || '게시글 작성에 실패했습니다.'
        };
      }
    } catch (error) {
      // 에러 응답 처리
      const errorResult = apiErrorHandler(error);
      
      // 403 에러 특별 처리 (차단된 사용자)
      if (error.response?.status === 403) {
        return {
          success: false,
          errorCode: 'Post_Create_Fail',
          message: error.response.data.message || '차단된 사용자는 글을 작성할 수 없습니다.'
        };
      }
      
      return errorResult;
    }
  },

  // 카테고리별 게시글 목록 조회 (JWT 불필요)
  getPostsByCategory: async (boardType) => {
    try {
      const response = await publicApiClient.get(`/boards/category/${boardType}`);
      // 응답 데이터를 프론트엔드에서 사용하기 쉽게 변환
      const posts = response.data.map(post => ({
        id: post.postId,
        title: post.title,
        author: post.nickName,
        date: new Date(post.createdAt).toLocaleDateString('ko-KR'),
        views: post.viewCount,
        likes: post.likeCount,
        content: post.content,
        imageUrl: post.imageUrl,
        boardType: post.boardId,
        userId: post.userId,
        createdAt: post.createdAt,
        updatedAt: post.updatedAt,
        isDeleted: post.delete
      }));

      return {
        success: true,
        data: posts,
        message: '게시글 목록을 성공적으로 조회했습니다.'
      };
    } catch (error) {
      const errorResult = apiErrorHandler(error);
      return {
        success: false,
        data: [],
        message: errorResult.message || '게시글 목록 조회에 실패했습니다.'
      };
    }
  },

  // 게시글 검색 (JWT 불필요)
  searchPosts: async (boardType, searchType, searchTerm) => {
    try {
      let url = `/boards/${boardType}`;
      
      // 검색 타입에 따라 쿼리 파라미터 설정
      switch (searchType) {
        case 'title':
          url += `?title=${encodeURIComponent(searchTerm)}`;
          break;
        case 'content':
          url += `?content=${encodeURIComponent(searchTerm)}`;
          break;
        case 'writer':
          url += `?writer=${encodeURIComponent(searchTerm)}`;
          break;
        default:
          // 검색 타입이 지정되지 않은 경우 제목으로 검색
          url += `?title=${encodeURIComponent(searchTerm)}`;
      }

      const response = await publicApiClient.get(url);
      
      // 응답 데이터를 프론트엔드에서 사용하기 쉽게 변환
      const posts = response.data.map(post => ({
        id: post.postId,
        title: post.title,
        author: post.nickName,
        date: new Date(post.createdAt).toLocaleDateString('ko-KR'),
        views: post.viewCount,
        likes: post.likeCount,
        content: post.content,
        imageUrl: post.imageUrl,
        boardType: post.boardId,
        userId: post.userId,
        createdAt: post.createdAt,
        updatedAt: post.updatedAt,
        isDeleted: post.delete
      }));

      return {
        success: true,
        data: posts,
        message: `검색 결과 ${posts.length}건을 찾았습니다.`
      };
    } catch (error) {
      const errorResult = apiErrorHandler(error);
      return {
        success: false,
        data: [],
        message: errorResult.message || '검색에 실패했습니다.'
      };
    }
  },

  // 게시글 상세 조회 (JWT 불필요)
  getPostDetail: async (postId) => {
    try {
      const response = await publicApiClient.get(`/posts/${postId}`);
      console.log(response.data);
      // 응답 데이터를 프론트엔드에서 사용하기 쉽게 변환
      const postData = response.data;
      
      const post = {
        id: postData.postId,
        title: postData.title,
        content: postData.content,
        imageUrl: postData.imageUrl,
        boardType: postData.boardId,
        views: postData.viewCount,
        likes: postData.likeCount,
        createdAt: postData.createdAt,
        updatedAt: postData.updatedAt,
        isDeleted: postData.delete,
        author: {
          id: postData.postUser?.userId,
          nickname: postData.postUser?.nickname
        },
        comments: postData.commentList?.map(comment => ({
          id: comment.commentId,
          content: comment.content,
          authorId: comment.userId,
          likes: comment.likeCount,
          createdAt: comment.createdAt,
          updatedAt: comment.updatedAt,
          isDeleted: comment.delete
        })) || []
      };

      return {
        success: true,
        data: post,
        message: '게시글을 성공적으로 조회했습니다.'
      };
    } catch (error) {
      const errorResult = apiErrorHandler(error);
      return {
        success: false,
        data: null,
        message: errorResult.message || '게시글 조회에 실패했습니다.'
      };
    }
  },

  // 게시글 수정 (JWT 필요)
  updatePost: async (postId, postData) => {
    try {
      const response = await authApiClient.patch(`/posts/${postId}`, {
        title: postData.title,
        content: postData.content
      });
      
      // 성공 응답 처리
      if (response.data.success) {
        return {
          success: true,
          message: response.data.message || '게시글 수정 성공'
        };
      } else {
        return {
          success: false,
          message: response.data.message || '게시글 수정에 실패했습니다.'
        };
      }
    } catch (error) {
      // 에러 응답 처리
      const errorResult = apiErrorHandler(error);
      
      // 403 에러 특별 처리 (권한 없음)
      if (error.response?.status === 403) {
        return {
          success: false,
          errorCode: 'Post_Update_Fail',
          message: error.response.data.message || '권한이 없습니다.'
        };
      }
      
      return errorResult;
    }
  },

  // 게시글 삭제 (JWT 필요)
  deletePost: async (postId) => {
    try {
      const response = await authApiClient.delete(`/posts/${postId}`);
      
      // 성공 응답 처리
      if (response.data.success) {
        return {
          success: true,
          message: response.data.message || '게시글 삭제 성공'
        };
      } else {
        return {
          success: false,
          message: response.data.message || '게시글 삭제에 실패했습니다.'
        };
      }
    } catch (error) {
      // 에러 응답 처리
      const errorResult = apiErrorHandler(error);
      
      // 403 에러 특별 처리 (권한 없음)
      if (error.response?.status === 403) {
        return {
          success: false,
          errorCode: 'Post_Delete_Fail',
          message: error.response.data.message || '권한이 없습니다.'
        };
      }
      
      return errorResult;
    }
  },

  // 게시글 좋아요 토글 (JWT 필요)
  togglePostLike: async (postId) => {
    try {
      const response = await authApiClient.post(`/posts/${postId}/like`);
      
      // 응답 데이터 반환 (postId와 likeCount 포함)
      return {
        success: true,
        data: {
          postId: response.data.postId,
          likeCount: response.data.likeCount
        },
        message: '좋아요가 성공적으로 처리되었습니다.'
      };
    } catch (error) {
      // 에러 응답 처리
      const errorResult = apiErrorHandler(error);
      
      // 403 에러 특별 처리 (권한 없음)
      if (error.response?.status === 403) {
        return {
          success: false,
          errorCode: 'Post_Like_Fail',
          message: error.response.data.message || '권한이 없습니다.'
        };
      }
      
      return errorResult;
    }
  },

  // 댓글 작성 (JWT 필요)
  createComment: async (commentData) => {
    try {
      const response = await authApiClient.post('/comments', {
        postId: commentData.postId,
        content: commentData.content
      });
      
      // 성공 응답 처리
      if (response.data.success) {
        return {
          success: true,
          message: response.data.message || '댓글 작성 성공'
        };
      } else {
        return {
          success: false,
          message: response.data.message || '댓글 작성에 실패했습니다.'
        };
      }
    } catch (error) {
      // 에러 응답 처리
      const errorResult = apiErrorHandler(error);
      
      // 403 에러 특별 처리 (차단된 사용자)
      if (error.response?.status === 403) {
        return {
          success: false,
          errorCode: 'Comment_Create_Fail',
          message: error.response.data.message || '차단된 사용자는 댓글을 작성할 수 없습니다.'
        };
      }
      
      return errorResult;
    }
  },

  // 댓글 수정 (JWT 필요)
  updateComment: async (commentId, commentData) => {
    try {
      const response = await authApiClient.patch(`/comments/${commentId}`, {
        content: commentData.content
      });
      
      // 성공 응답 처리
      if (response.data.success) {
        return {
          success: true,
          message: response.data.message || '댓글 수정 성공'
        };
      } else {
        return {
          success: false,
          message: response.data.message || '댓글 수정에 실패했습니다.'
        };
      }
    } catch (error) {
      // 에러 응답 처리
      const errorResult = apiErrorHandler(error);
      
      // 403 에러 특별 처리 (권한 없음)
      if (error.response?.status === 403) {
        return {
          success: false,
          errorCode: 'Comment_Update_Fail',
          message: error.response.data.message || '권한이 없습니다.'
        };
      }
      
      return errorResult;
    }
  },

  // 댓글 삭제 (JWT 필요)
  deleteComment: async (commentId) => {
    try {
      const response = await authApiClient.delete(`/comments/${commentId}`);
      
      // 성공 응답 처리
      if (response.data.success) {
        return {
          success: true,
          message: response.data.message || '댓글 삭제 성공'
        };
      } else {
        return {
          success: false,
          message: response.data.message || '댓글 삭제에 실패했습니다.'
        };
      }
    } catch (error) {
      // 에러 응답 처리
      const errorResult = apiErrorHandler(error);
      
      // 403 에러 특별 처리 (권한 없음)
      if (error.response?.status === 403) {
        return {
          success: false,
          errorCode: 'Comment_Delete_Fail',
          message: error.response.data.message || '권한이 없습니다.'
        };
      }
      
      return errorResult;
    }
  }
};

// 게시판 타입 상수
export const BOARD_TYPES = {
  NOTICE: 'NOTICE',
  FREE: 'FREE',
  GROUP: 'GROUPVIEW'
};

// 게시판 타입 한글명
export const BOARD_TYPE_NAMES = {
  [BOARD_TYPES.NOTICE]: '공지사항',
  [BOARD_TYPES.FREE]: '자유게시판',
  [BOARD_TYPES.GROUP]: '단체 관련 게시판'
};

// 검색 타입 상수
export const SEARCH_TYPES = {
  TITLE: 'title',
  CONTENT: 'content',
  WRITER: 'writer'
};

// 검색 타입 한글명
export const SEARCH_TYPE_NAMES = {
  [SEARCH_TYPES.TITLE]: '제목',
  [SEARCH_TYPES.CONTENT]: '내용',
  [SEARCH_TYPES.WRITER]: '작성자'
};

// 게시글 데이터 유효성 검사
export const validatePostData = (postData) => {
  const errors = [];

  if (!postData.title || postData.title.trim() === '') {
    errors.push('제목을 입력해주세요.');
  }

  if (!postData.content || postData.content.trim() === '') {
    errors.push('내용을 입력해주세요.');
  }

  if (!postData.boardType) {
    errors.push('게시판 타입을 선택해주세요.');
  }

  return {
    isValid: errors.length === 0,
    errors
  };
};

// 댓글 데이터 유효성 검사
export const validateCommentData = (commentData) => {
  const errors = [];

  if (!commentData.postId) {
    errors.push('게시글 ID가 필요합니다.');
  }

  if (!commentData.content || commentData.content.trim() === '') {
    errors.push('댓글 내용을 입력해주세요.');
  }

  return {
    isValid: errors.length === 0,
    errors
  };
};

export default boardAPI;
