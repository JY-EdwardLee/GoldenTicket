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
  },

  // 단체관람 목록 조회
  getGroupList: async (team = 'all') => {
    try {
      if (team === 'all') {
        // Deduplicate API team names to avoid duplicate requests when aliases exist
        const allApiTeamNames = Array.from(new Set(Object.values(TEAM_MAPPING)));
        const requests = allApiTeamNames.map((apiName) => 
          publicApiClient.get(`/group/${apiName}`).catch(() => ({ data: [] }))
        );
        const results = await Promise.all(requests);

        // 중복 제거: 같은 groupId가 여러 팀 응답에 섞여 올 수 있으므로 Map으로 유니크 처리
        const byId = new Map();
        results.forEach((res) => {
          const arr = Array.isArray(res.data) ? res.data : [];
          arr.forEach((g) => {
            if (g && g.groupId != null && !byId.has(g.groupId)) {
              byId.set(g.groupId, g);
            }
          });
        });
        return Array.from(byId.values());
      } else {
        const apiTeamName = TEAM_MAPPING[team];
        if (!apiTeamName) throw new Error(`알 수 없는 팀: ${team}`);
        const response = await publicApiClient.get(`/group/${apiTeamName}`);
        const arr = Array.isArray(response.data) ? response.data : [];
        // 선택된 팀 필터가 동작하도록 team 태그를 부여
        return arr.map((g) => ({ ...g, team }));
      }
    } catch (error) {
      console.error('단체관람 목록 조회 실패:', error);
      throw apiErrorHandler(error);
    }
  },

  // 내 응모 목록 조회 (인증 필요)
  getMyApplications: async () => {
    try {
      const response = await authApiClient.get('/users/me/applications');
      return response.data;
    } catch (error) {
      // 비로그인(401) 등은 빈 배열로 처리하여 화면에서 자연스럽게 동작
      if (error?.response?.status === 401) {
        return [];
      }
      throw apiErrorHandler(error);
    }
  },

  // 단체관람 신청
  applyGroup: async (groupId) => {
    try {
      const response = await authApiClient.post(`/group/${groupId}`);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 단체관람 신청 취소
  cancelGroup: async (groupId) => {
    try {
      const response = await authApiClient.delete(`/group/${groupId}`);
      // 스펙: 성공 시 { success: true, message: "응모취소 성공" }
      return {
        success: response.data?.success ?? true,
        message: response.data?.message || '응모취소 성공'
      };
    } catch (error) {
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

// 팀 이름 매핑 (UI 코드 -> API 코드)
export const TEAM_MAPPING = {
  SSG: 'SSG_LANDERS',
  KIA: 'KIA_TIGERS',
  LG: 'LG_TWINS',
  KT: 'KT_WIZ',
  KIWOOM: 'KIWOOM_HEROES',
  SAMSUNG: 'SAMSUNG_LIONS',
  LOTTE: 'LOTTE_GIANTS',
  DOOSAN: 'DOOSAN_BEARS',
  HANHWA: 'HANHWA_EAGLES',
  // Allow common misspelling as alias
  HANWHA: 'HANHWA_EAGLES',
  NC: 'NC_DINOS'
};

// API 팀코드 → 한글 팀명 매핑
const API_TEAM_TO_KOREAN = {
  SSG_LANDERS: 'SSG 랜더스',
  KIA_TIGERS: 'KIA 타이거즈',
  LG_TWINS: 'LG 트윈스',
  KT_WIZ: 'KT 위즈',
  KIWOOM_HEROES: '키움 히어로즈',
  SAMSUNG_LIONS: '삼성 라이온즈',
  LOTTE_GIANTS: '롯데 자이언츠',
  DOOSAN_BEARS: '두산 베어스',
  HANHWA_EAGLES: '한화 이글스',
  NC_DINOS: 'NC 다이노스'
};

const toKoreanTeamName = (apiTeamName) => API_TEAM_TO_KOREAN[apiTeamName] || apiTeamName;

// 홈팀 -> 홈구장 코드 매핑
const TEAM_HOME_STADIUM_CODE = {
  SSG_LANDERS: 'INCHON',
  KIA_TIGERS: 'GWANGJU',
  LG_TWINS: 'JAMSIL',
  KT_WIZ: 'SUWON',
  KIWOOM_HEROES: 'GOCHUK',
  SAMSUNG_LIONS: 'DAEGU',
  LOTTE_GIANTS: 'SAJIK',
  DOOSAN_BEARS: 'JAMSIL',
  HANHWA_EAGLES: 'DAEJEON',
  NC_DINOS: 'CHANGWON'
};

// API 응답을 카드에서 사용하는 형태로 변환
export const transformGroupData = (apiData) => {
  const safeArray = Array.isArray(apiData) ? apiData : [];
  return safeArray.map((group) => {
    const game = group.gameResponse || {};
    const dateStr = game.date || '';
    const [d, t] = dateStr.split('T');

    // 경기장 코드: 응답 stadium 우선, 없으면 홈팀 고정 홈구장 코드 사용
    const stadiumCode = game.stadium || TEAM_HOME_STADIUM_CODE[game.home] || null;
    const stadiumName = getStadiumName(stadiumCode);

    const homeKo = toKoreanTeamName(game.home);
    const awayKo = toKoreanTeamName(game.away);

    return {
      postId: group.groupId,
      gameId: game.id,
      title: `${awayKo || '원정팀'} vs ${homeKo || '홈팀'}`,
      gameDate: d || '',
      gameTime: (t || '').substring(0,5),
      location: stadiumName,
      organizer: group.organizer || '단체관람 모집자',
      currentParticipants: group.applicantsCount || 0,
      maxParticipants: group.maxParticipants || 20,
      description: group.description || `${homeKo || '홈팀'}와 ${awayKo || '원정팀'}의 경기를 함께 관람하실 분들을 모집합니다.`,
      meetingPlace: group.meetingPlace || `${stadiumName} 정문`,
      meetingTime: (t || '').substring(0,5),
      hashtags: group.hashtags || ['단체관람', '함께관람', '응원'],
      status: getStatusByParticipants(group.applicantsCount || 0, group.maxParticipants || 20),
      conditions: group.conditions || '매너 좋은 분들만, 끝까지 응원 가능한 분',
      createdAt: (group.createdAt || dateStr || new Date().toISOString()).split('T')[0],
      imageUrl: getTeamImageUrl(game.home || game.away),
      team: group.team || inferDisplayTeamFromApiTeam(game.home || game.away),
      groupId: group.groupId,
      organizerId: group.organizerId,
      // 서버가 isApplied를 주지 않으므로 기본값은 false.
      // 실제 적용 여부는 화면에서 사용자 응모 목록으로 판별함
      isApplied: false
    };
  });
};

// 구장 이름 매핑
const getStadiumName = (stadiumCode) => {
  const stadiums = {
    SAJIK: '부산 사직 야구장',
    JAMSIL: '잠실 야구장',
    DAEGU: '대구 삼성 라이온즈 파크',
    SUWON: '수원 KT 위즈 파크',
    GOCHUK: '고척 스카이돔',
    MUNHAK: '인천 SSG 랜더스 필드',
    GWANGJU: '광주 기아 챔피언스 필드',
    DAEJEON: '대전 한화생명 이글스파크',
    CHANGWON: '창원 NC 파크'
  };
  return stadiums[stadiumCode] || '야구장';
};

// 팀별 이미지 URL (로컬 자산 사용)
const TEAM_IMAGE_MAP = {
  SSG_LANDERS: new URL('../assets/stadium/INCHON.jpg', import.meta.url).href,
  KIA_TIGERS: new URL('../assets/stadium/GWANGJU.webp', import.meta.url).href,
  LG_TWINS: new URL('../assets/stadium/JAMSIL.webp', import.meta.url).href,
  KT_WIZ: new URL('../assets/stadium/SUWON.jpg', import.meta.url).href,
  KIWOOM_HEROES: new URL('../assets/stadium/GOCHUK.webp', import.meta.url).href,
  SAMSUNG_LIONS: new URL('../assets/stadium/DAEGU.jpg', import.meta.url).href,
  LOTTE_GIANTS: new URL('../assets/stadium/BUSAN.jpg', import.meta.url).href,
  DOOSAN_BEARS: new URL('../assets/stadium/JAMSIL.webp', import.meta.url).href,
  HANHWA_EAGLES: new URL('../assets/stadium/DAEJEON.jpg', import.meta.url).href,
  NC_DINOS: new URL('../assets/stadium/CHANGWON.png', import.meta.url).href
};

const getTeamImageUrl = (teamName) => {
  return TEAM_IMAGE_MAP[teamName] || new URL('../assets/stadium/JAMSIL.webp', import.meta.url).href;
};

// API 팀코드에서 화면 팀코드 추론
const inferDisplayTeamFromApiTeam = (apiTeam) => {
  const entry = Object.entries(TEAM_MAPPING).find(([, v]) => v === apiTeam);
  return entry ? entry[0] : 'SSG';
};

// 참가자 수에 따른 상태 결정
const getStatusByParticipants = (currentParticipants, maxParticipants) => {
  const rate = maxParticipants ? (currentParticipants / maxParticipants) * 100 : 0;
  if (rate >= 100) return 'completed';
  if (rate >= 75) return 'closing';
  return 'recruiting';
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
    const response = await authApiClient.get('/s3/upload-url', {
      params: {
        type,
        refId,
        fileName
      }
    });
    
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
