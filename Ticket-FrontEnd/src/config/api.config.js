// API 기본 URL 설정
const API_BASE_URL =
  // 임시 수정
  import.meta.env.VITE_API_BASE_URL || "/api";
  // import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

import axios from "axios";
axios.defaults.baseURL = API_BASE_URL;

/**
 * 메인 페이지 API 엔드포인트
 * @typedef {Object} MAIN_PAGE
 * @property {string} USER - 사용자 랭킹 API
 * @property {string} TEAM - 팀 랭킹 API
 * @property {string} CHAT - 채팅 API
 */
const MAIN_PAGE = {
  USER: `/rank/user`,
  TEAM: `/rank/team`,
};

const CHAT = {
  CHAT: `/chat`,
  HISTORY: `/chat/history`,
}

// 인증 관련 엔드포인트
const AUTH = {
  KAKAO: `${API_BASE_URL}/users/auth/kakao`,
  NAVER: `${API_BASE_URL}/users/auth/naver`,
  SIGNUP: `${API_BASE_URL}/users/signup`,
  REFRESH: `${API_BASE_URL}/users/auth/refresh`,
  SMS: `${API_BASE_URL}/users/signup/verification`,
  SMS_VERIFY: `${API_BASE_URL}/users/signup/verification/check`,
  LOGOUT: `${API_BASE_URL}/users/logout`,
  TEMP_USER: `${API_BASE_URL}/users/auth/temp-user`,
};

// 사용자 관련 엔드포인트
const USER = {
  LOGIN: `/users/login-user`,
  PROFILE: `/users/me`,
  UPDATE_PROFILE: `/users/me`,
  APPLICANTS: `/users/me/applications`,
  PAYMENTS: `/users/me/payments`,
  TICKETS: `/users/me/tickets`,
  POSTS: `/users/me/posts`,
  PLATFORMS: `/users/platform-link`,
  CHANGE_PASSWORD: `/users/me/password`,
  DELETE_ACCOUNT: `/users/me`,
  ADMIN: `/users/administrator`,
  GENERAL_USER: `/users/general-user`,
  PAYMENT: {
    KAKAO: {
      READY: `/payment/kakao/ready`,
      COMPLETE: `/payment/kakao/complete`,
    },
    NAVER: {
      READY: `/payment/naver/ready`,
      COMPLETE: `/payment/naver/complete`,
    },
  },
};

// 티켓 관련 엔드포인트
const TICKET = {
  LIST: `/tickets`,
  DETAIL: (id) => `/tickets/details/${id}`,
  QR: (id) => `/qrcode/${id}`,
  APPLY: (gameId) => `games/${gameId}/applications`,
  GAMES: `/games`, // 응모 - 경기 목록 불러오기(0729)
  CANCEL: (id) => `/games/${id}/applications`,
  TRANSFER: `/tickets/transfer`,
  MY_TICKETS: `/tickets`,
};

// 게시판 관련 엔드포인트
const BOARD = {
  LIST: `/posts`,
  DETAIL: (id) => `/posts/${id}`,
  CREATE: `/posts`,
  UPDATE: (id) => `/posts/${id}`,
  DELETE: (id) => `/posts/${id}`,
  COMMENTS: (boardId) => `/posts/${boardId}/comments`,
};

// API 설정 내보내기
export const API_CONFIG = {
  BASE_URL: API_BASE_URL,
  MAIN_PAGE,
  CHAT,
  AUTH,
  USER,
  TICKET,
  BOARD,
};

export default API_CONFIG;
