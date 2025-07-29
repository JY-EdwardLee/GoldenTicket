// API 기본 URL 설정
const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://i13a109.p.ssafy.io:8080";
import axios from 'axios';
axios.defaults.baseURL = API_BASE_URL;

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
  CHANGE_PASSWORD: `${API_BASE_URL}/users/me/password`,
  DELETE_ACCOUNT: `${API_BASE_URL}/users/me`,
};

// 티켓 관련 엔드포인트
const TICKET = {
  LIST: `${API_BASE_URL}/tickets`,
  DETAIL: (id) => `${API_BASE_URL}/tickets/${id}`,
  APPLY: `${API_BASE_URL}/tickets/apply`,
  GAMES: `${API_BASE_URL}/games`,   // 응모 - 경기 목록 불러오기(0729)
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
