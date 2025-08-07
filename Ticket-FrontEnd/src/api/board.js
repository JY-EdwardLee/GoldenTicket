import { authApiClient, publicApiClient, apiErrorHandler } from './index.js';

// 게시판 관련 API
export const boardAPI = {
  // 게시판별 게시글 조회 (카테고리별)
  getPostsByCategory: async (boardType) => {
    try {
      const response = await publicApiClient.get(`/boards/category/${boardType}`);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 게시글 목록 조회 (페이징 포함)
  getPosts: async (params = {}) => {
    try {
      const response = await publicApiClient.get('/boards', { params });
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 게시판별 게시글 검색
  searchPosts: async (boardType, searchType, searchValue) => {
    try {
      const params = {};
      params[searchType] = searchValue;
      
      const response = await publicApiClient.get(`/boards/${boardType}`, { params });
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 게시글 상세 조회
  getPostDetail: async (postId) => {
    try {
      const response = await publicApiClient.get(`/posts/${postId}`);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 게시글 좋아요 토글
  togglePostLike: async (postId) => {
    try {
      const response = await authApiClient.post(`/posts/${postId}/like`);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 게시글 작성 (인증 필요)
  createPost: async (postData) => {
    try {
      const response = await authApiClient.post('/posts', postData);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 게시글 수정 (인증 필요)
  updatePost: async (postId, postData) => {
    try {
      const response = await authApiClient.patch(`/posts/${postId}`, postData);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 게시글 삭제 (인증 필요)
  deletePost: async (postId) => {
    try {
      const response = await authApiClient.delete(`/posts/${postId}`);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 게시글 좋아요 (인증 필요)
  likePost: async (postId) => {
    try {
      const response = await authApiClient.post(`/posts/${postId}/like`);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 댓글 목록 조회
  getComments: async (postId) => {
    try {
      const response = await publicApiClient.get(`/boards/${postId}/comments`);
      console.log(response.data);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 댓글 작성 (인증 필요)
  createComment: async (postId, commentData) => {
    try {
      const response = await authApiClient.post(`/comments`, {
        postId: postId,
        content: commentData.content
      });
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 댓글 수정 (인증 필요)
  updateComment: async (commentId, commentData) => {
    try {
      const response = await authApiClient.patch(`/comments/${commentId}`, commentData);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 댓글 삭제 (인증 필요)
  deleteComment: async (commentId) => {
    try {
      const response = await authApiClient.delete(`/comments/${commentId}`);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 댓글 좋아요 (인증 필요)
  likeComment: async (commentId) => {
    try {
      const response = await authApiClient.post(`/comments/${commentId}/like`);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // Presigned URL 요청 (S3 업로드용)
  getPresignedUploadUrl: async (type, refId, fileName) => {
    try {
      const response = await authApiClient.get('/s3/upload-url', {
        params: {
          type,
          refId,
          fileName
        }
      });
      
      return response.data;
    } catch (error) {
      console.error('Presigned URL 요청 실패:', error);
      
      // 403 Forbidden 오류인 경우 인증 문제로 처리
      if (error.response?.status === 403) {
        throw new Error('인증이 필요합니다. 다시 로그인해주세요.');
      }
      
      // 401 Unauthorized 오류인 경우도 인증 문제로 처리
      if (error.response?.status === 401) {
        throw new Error('로그인이 만료되었습니다. 다시 로그인해주세요.');
      }
      
      throw apiErrorHandler(error);
    }
  }
};

// 게시판 타입 매핑
export const BOARD_TYPES = {
  NOTICE: 'NOTICE',
  FREE: 'FREE', 
  GROUPVIEW: 'GROUPVIEW'
};

// 게시판 타입 한글명
export const BOARD_TYPE_NAMES = {
  [BOARD_TYPES.NOTICE]: '공지사항',
  [BOARD_TYPES.FREE]: '자유게시판',
  [BOARD_TYPES.GROUPVIEW]: '단체 관련 게시판'
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

  // content가 문자열이 아닐 수 있으므로 안전하게 처리
  let contentText = '';
  if (postData.content) {
    if (typeof postData.content === 'string') {
      contentText = postData.content.trim();
    } else {
      // Delta 객체나 다른 형태인 경우 문자열로 변환
      contentText = String(postData.content).trim();
    }
  }

  if (!contentText) {
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

// S3 업로드 관련 함수들
export const getS3UploadUrl = async (type, refId, fileName) => {
  try {
    console.log('S3 업로드 URL 요청 시작:', { type, refId, fileName });
    
    const response = await authApiClient.get('/s3/upload-url', {
      params: {
        type,
        refId,
        fileName
      }
    });
    
    console.log('S3 업로드 URL 요청 성공:', response.data);
    return response.data;
  } catch (error) {
    console.error('S3 업로드 URL 요청 실패:', error);
    console.error('에러 응답:', error.response);
    console.error('에러 상태:', error.response?.status);
    console.error('에러 데이터:', error.response?.data);
    
    // 403 Forbidden 오류인 경우 인증 문제로 처리
    if (error.response?.status === 403) {
      throw new Error('인증이 필요합니다. 다시 로그인해주세요.');
    }
    
    // 401 Unauthorized 오류인 경우도 인증 문제로 처리
    if (error.response?.status === 401) {
      throw new Error('로그인이 만료되었습니다. 다시 로그인해주세요.');
    }
    
    throw error;
  }
};



// S3에 직접 업로드하는 함수 (Presigned URL 사용)
export const uploadToS3 = async (putUrl, file) => {
  try {
    const uploadResponse = await fetch(putUrl, {
      method: 'PUT',
      body: file,
      headers: {
        'Content-Type': file.type
      }
    });
    
    if (!uploadResponse.ok) {
      const errorText = await uploadResponse.text();
      console.error('S3 업로드 실패 응답:', errorText);
      throw new Error(`S3 업로드에 실패했습니다. 상태: ${uploadResponse.status}`);
    }
    
    return {
      success: true
    };
    
  } catch (error) {
    console.error('S3 직접 업로드 실패:', error);
    throw error;
  }
};

// 이미지 업로드 함수 (기존 호환성 유지)
export const uploadImageToS3 = async (file, postId) => {
  try {
    // 1. S3 업로드 URL 요청
    const uploadUrlResponse = await getS3UploadUrl('PostImage', postId, file.name);
    
    console.log('S3 업로드 URL 응답:', uploadUrlResponse);
    
    // 2. S3에 직접 업로드 (presignedUrl 사용)
    const uploadResponse = await fetch(uploadUrlResponse.presignedUrl, {
      method: 'PUT',
      body: file,
      headers: {
        'Content-Type': file.type
      }
    });
    
    if (!uploadResponse.ok) {
      throw new Error('S3 업로드에 실패했습니다.');
    }
    
    console.log('S3 업로드 성공, key:', uploadUrlResponse.key);
    
    return {
      success: true,
      imageUrl: uploadUrlResponse.key // key 값을 imageUrl로 사용
    };
    
  } catch (error) {
    console.error('이미지 업로드 실패:', error);
    throw error;
  }
};

export default boardAPI;
