// API 기본 URL 설정
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// 인증 관련 엔드포인트
const AUTH = {
  KAKAO: `${API_BASE_URL}/auth/kakao`,
  NAVER: `${API_BASE_URL}/auth/naver`,
  SIGNUP: `${API_BASE_URL}/auth/signup`,
  REFRESH: `${API_BASE_URL}/auth/refresh`,
  LOGOUT: `${API_BASE_URL}/auth/logout`,
};

// 사용자 관련 엔드포인트
const USER = {
  PROFILE: `${API_BASE_URL}/users/me`,
  UPDATE_PROFILE: `${API_BASE_URL}/users/me`,
  CHANGE_PASSWORD: `${API_BASE_URL}/users/me/password`,
  DELETE_ACCOUNT: `${API_BASE_URL}/users/me`,
};

// 티켓 관련 엔드포인트
const TICKET = {
  LIST: `${API_BASE_URL}/tickets`,
  DETAIL: (id) => `${API_BASE_URL}/tickets/${id}`,
  APPLY: `${API_BASE_URL}/tickets/apply`,
  TRANSFER: `${API_BASE_URL}/tickets/transfer`,
  MY_TICKETS: `${API_BASE_URL}/tickets/me`,
};

// 게시판 관련 엔드포인트
const BOARD = {
  LIST: `${API_BASE_URL}/boards`,
  DETAIL: (id) => `${API_BASE_URL}/boards/${id}`,
  CREATE: `${API_BASE_URL}/boards`,
  UPDATE: (id) => `${API_BASE_URL}/boards/${id}`,
  DELETE: (id) => `${API_BASE_URL}/boards/${id}`,
  COMMENTS: (boardId) => `${API_BASE_URL}/boards/${boardId}/comments`,
};

// API 설정 내보내기
export const API_CONFIG = {
  BASE_URL: API_BASE_URL,
  AUTH,
  USER,
  TICKET,
  BOARD,
};

export default API_CONFIG;
