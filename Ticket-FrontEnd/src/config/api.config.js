// API 기본 URL 설정
const API_BASE_URL =
  // 임시 수정
  import.meta.env.VITE_API_BASE_URL || "http://i13a109.p.ssafy.io:8080";
// import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";
import axios from "axios";
axios.defaults.baseURL = API_BASE_URL;

const MAIN_PAGE = {
  USER: `${API_BASE_URL}/rank/user`,
  TEAM: `${API_BASE_URL}/rank/team`,
  CHAT: `${API_BASE_URL}/chat`,
};

// 인증 관련 엔드포인트
const AUTH = {
  KAKAO: `${API_BASE_URL}/users/auth/kakao`,
  NAVER: `${API_BASE_URL}/users/auth/naver`,
  SIGNUP: `${API_BASE_URL}/users/signup`,
  REFRESH: `${API_BASE_URL}/users/auth/refresh`,
  LOGOUT: `${API_BASE_URL}/users/logout`,
  TEMP_USER: `${API_BASE_URL}/users/auth/temp-user`,
};

// 사용자 관련 엔드포인트
const USER = {
  LOGIN: `${API_BASE_URL}/users/login-user`,
  PROFILE: `${API_BASE_URL}/users/me`,
  UPDATE_PROFILE: `${API_BASE_URL}/users/me`,
  APPLICANTS: `${API_BASE_URL}/users/me/applications`,
  PAYMENTS: `${API_BASE_URL}/users/me/payments`,
  TICKETS: `${API_BASE_URL}/users/me/tickets`,
  POSTS: `${API_BASE_URL}/users/me/posts`,
  PLATFORMS: `${API_BASE_URL}/users/platform-link`,
  CHANGE_PASSWORD: `${API_BASE_URL}/users/me/password`,
  DELETE_ACCOUNT: `${API_BASE_URL}/users/me`,
  PAYMENT: {
    KAKAO: {
      READY: `${API_BASE_URL}/payment/kakao/ready`,
      COMPLETE: `${API_BASE_URL}/payment/kakao/complete`,
    },
    NAVER: {
      READY: `${API_BASE_URL}/payment/naver/ready`,
      COMPLETE: `${API_BASE_URL}/payment/naver/complete`,
    },
  },
};

// 티켓 관련 엔드포인트
const TICKET = {
  LIST: `${API_BASE_URL}/tickets`,
  DETAIL: (id) => `${API_BASE_URL}/tickets/details/${id}`,
  QR: (id) => `${API_BASE_URL}/qrcode/${id}`,
  APPLY: `${API_BASE_URL}/tickets/apply`,
  GAMES: `${API_BASE_URL}/games`,   // 응모 - 경기 목록 불러오기(0729)
  CANCEL: (id) => `${API_BASE_URL}/games/${id}/applications`,
  TRANSFER: `${API_BASE_URL}/tickets/transfer`,
  MY_TICKETS: `${API_BASE_URL}/tickets`,
};

// 게시판 관련 엔드포인트
const BOARD = {
  LIST: `${API_BASE_URL}/posts`,
  DETAIL: (id) => `${API_BASE_URL}/posts/${id}`,
  CREATE: `${API_BASE_URL}/posts`,
  UPDATE: (id) => `${API_BASE_URL}/posts/${id}`,
  DELETE: (id) => `${API_BASE_URL}/posts/${id}`,
  COMMENTS: (boardId) => `${API_BASE_URL}/posts/${boardId}/comments`,
};

// API 설정 내보내기
export const API_CONFIG = {
  BASE_URL: API_BASE_URL,
  MAIN_PAGE,
  AUTH,
  USER,
  TICKET,
  BOARD,
};

export default API_CONFIG;
