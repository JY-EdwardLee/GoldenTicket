import axios from 'axios';

// 환경 변수에서 API 기본 URL 가져오기
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// JWT 토큰을 헤더에 포함하는 API 클라이언트 (인증이 필요한 요청용)
export const authApiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  }
});



// JWT 토큰을 헤더에 포함하지 않는 API 클라이언트 (인증이 필요없는 요청용)
export const publicApiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  }
});

// 토큰 관리 유틸리티
export const tokenUtils = {
  // 토큰 저장
  setToken: (token) => {
    localStorage.setItem('accessToken', token);
  },

  // 토큰 가져오기
  getToken: () => {
    return localStorage.getItem('accessToken');
  },

  // 토큰 제거
  removeToken: () => {
    localStorage.removeItem('accessToken');
  },

  // 토큰 유효성 검사
  isTokenValid: () => {
    const token = localStorage.getItem('accessToken');
    if (!token) return false;
    
    try {
      // JWT 토큰의 만료 시간 확인
      const payload = JSON.parse(atob(token.split('.')[1]));
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch (error) {
      return false;
    }
  },

  // 토큰 만료 시간 가져오기
  getTokenExpiration: () => {
    const token = localStorage.getItem('accessToken');
    if (!token) return null;
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.exp * 1000; // 밀리초로 변환
    } catch (error) {
      return null;
    }
  }
};

// 인증이 필요한 API 클라이언트에 요청 인터셉터 추가
authApiClient.interceptors.request.use(
  (config) => {
    const token = tokenUtils.getToken();
    console.log('토큰 확인:', token ? '토큰 존재' : '토큰 없음');
    console.log('토큰 유효성:', tokenUtils.isTokenValid());
    
    if (token && tokenUtils.isTokenValid()) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('Authorization 헤더 설정됨:', config.headers.Authorization);
    } else {
      console.log('토큰이 없거나 유효하지 않음');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 인증이 필요한 API 클라이언트에 응답 인터셉터 추가
authApiClient.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const originalRequest = error.config;

    // 401 에러이고 토큰이 만료된 경우
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // 리프레시 토큰으로 새로운 액세스 토큰 요청
        const refreshToken = localStorage.getItem('refreshToken');
        if (refreshToken) {
          const response = await publicApiClient.post('/auth/refresh', {
            refreshToken: refreshToken
          });

          if (response.data.accessToken) {
            tokenUtils.setToken(response.data.accessToken);
            originalRequest.headers.Authorization = `Bearer ${response.data.accessToken}`;
            return authApiClient(originalRequest);
          }
        }
      } catch (refreshError) {
        // 리프레시 토큰도 만료된 경우 로그아웃 처리
        tokenUtils.removeToken();
        localStorage.removeItem('refreshToken');
        // 로그인 페이지로 리다이렉트 또는 로그아웃 처리
        window.location.href = '/';
      }
    }

    return Promise.reject(error);
  }
);

// API 에러 핸들러
export const apiErrorHandler = (error) => {
  if (error.response) {
    // 서버에서 응답이 온 경우
    const { status, data } = error.response;
    
    switch (status) {
      case 400:
        return {
          success: false,
          message: data.message || '잘못된 요청입니다.',
          errorCode: data.errorCode || 'BadRequest'
        };
      case 401:
        return {
          success: false,
          message: '인증이 필요합니다.',
          errorCode: 'Unauthorized'
        };
      case 403:
        return {
          success: false,
          message: data.message || '접근 권한이 없습니다.',
          errorCode: data.errorCode || 'Forbidden'
        };
      case 404:
        return {
          success: false,
          message: data.message || '요청한 리소스를 찾을 수 없습니다.',
          errorCode: data.errorCode || 'NotFound'
        };
      case 500:
        return {
          success: false,
          message: data.message || '서버 오류가 발생했습니다.',
          errorCode: data.errorCode || 'InternalServerError'
        };
      default:
        return {
          success: false,
          message: data.message || '알 수 없는 오류가 발생했습니다.',
          errorCode: data.errorCode || 'UnknownError'
        };
    }
  } else if (error.request) {
    // 요청은 보냈지만 응답을 받지 못한 경우
    return {
      success: false,
      message: '서버에 연결할 수 없습니다.',
      errorCode: 'NetworkError'
    };
  } else {
    // 요청 자체를 보내지 못한 경우
    return {
      success: false,
      message: '요청을 보낼 수 없습니다.',
      errorCode: 'RequestError'
    };
  }
};

// 인증 관련 API
export const authAPI = {
  // 로그인 (토큰 필요 없음)
  login: async (credentials) => {
    try {
      const response = await publicApiClient.post('/auth/login', credentials);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 회원가입 (토큰 필요 없음)
  register: async (userData) => {
    try {
      const response = await publicApiClient.post('/auth/register', userData);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 로그아웃 (토큰 필요)
  logout: async () => {
    try {
      const response = await authApiClient.post('/auth/logout');
      tokenUtils.removeToken();
      localStorage.removeItem('refreshToken');
      return response.data;
    } catch (error) {
      // 로그아웃 실패해도 클라이언트에서는 토큰 제거
      tokenUtils.removeToken();
      localStorage.removeItem('refreshToken');
      throw apiErrorHandler(error);
    }
  },

  // 토큰 갱신 (토큰 필요 없음)
  refreshToken: async (refreshToken) => {
    try {
      const response = await publicApiClient.post('/auth/refresh', { refreshToken });
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 현재 사용자 정보 조회 (토큰 필요)
  getCurrentUser: async () => {
    try {
      const response = await authApiClient.get('/auth/me');
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  },

  // 비밀번호 변경 (토큰 필요)
  changePassword: async (passwordData) => {
    try {
      const response = await authApiClient.put('/auth/password', passwordData);
      return response.data;
    } catch (error) {
      throw apiErrorHandler(error);
    }
  }
};

export default {
  authApiClient,
  publicApiClient,
  tokenUtils,
  authAPI,
  apiErrorHandler
};
