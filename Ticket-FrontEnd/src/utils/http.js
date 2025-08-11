import axios from 'axios';
import API_CONFIG from '@/config/api.config';

// Axios 인스턴스 생성
const http = axios.create({
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // 쿠키를 포함한 요청을 위해 필요
});

// 요청 인터셉터
http.interceptors.request.use(
  (config) => {
    console.log('요청 인터셉터');
    // 요청 전에 토큰이 있으면 헤더에 추가
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 응답 인터셉터
http.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    console.log('에러메시지', error);
    // 401 에러이고, 토큰 갱신이 필요한 경우
    if (error.response?.status === 401 && error.response.data.message === 'Access token expired') {
      console.log('Access token expired');
      try {
        console.log('리프레시 토큰으로 새로운 액세스 토큰 요청');
        // 리프레시 토큰으로 새로운 액세스 토큰 요청
        const { data } = await axios.post(API_CONFIG.AUTH.REFRESH, {}, {
          withCredentials: true,
        });
        
        const { accessToken } = data;
        localStorage.setItem('accessToken', accessToken);
        
        // 원래 요청을 새로운 토큰으로 재시도
        originalRequest.headers.Authorization = `Bearer ${accessToken}`;
        return http(originalRequest);
      } catch (refreshError) {
        console.log('리프레시 토큰도 만료된 경우 로그아웃 처리');
        // 리프레시 토큰도 만료된 경우 로그아웃 처리
        localStorage.removeItem('accessToken');
        localStorage.removeItem('refreshToken');
        return Promise.reject(refreshError);
      }
    }
    
    return Promise.reject(error);
  }
);

export default http;
