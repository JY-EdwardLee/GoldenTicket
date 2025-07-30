<template>
  <div v-if="isVisible" class="modal-overlay" @click="closeModal">
    <div class="modal-container" @click.stop>
      <!-- 닫기 버튼 -->
      <button class="close-button" @click="closeModal">
        <div class="close-icon">
          <svg
            width="25"
            height="24"
            viewBox="0 0 25 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M18.5 6L6.5 18M6.5 6L18.5 18"
              stroke="#111827"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            />
          </svg>
        </div>
      </button>

      <!-- 로고 섹션 -->
      <div class="logo-section">
        <div class="logo-container">
          <div class="logo-icon">
            <h4 class="logo-text">logo</h4>
          </div>
        </div>
      </div>

      <!-- 제목 섹션 -->
      <div class="title-section">
        <h3 class="main-title">티켓 예매 서비스</h3>
        <h5 class="sub-title">간편하게 로그인하고 티켓을 예매하세요</h5>
      </div>

      <!-- 소셜 로그인 버튼들 -->
      <div class="login-button-section">
        <SocialLoginButton
          provider="kakao"
          label="카카오로 계속하기"
          :authUrl="KAKAO_AUTH_URL"
          @click="handleKakaoLogin"
        />
      </div>

      <div class="login-button-section">
        <SocialLoginButton
          provider="naver"
          label="네이버로 계속하기"
          :authUrl="NAVER_AUTH_URL"
          @click="handleNaverLogin"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import SocialLoginButton from "./SocialLoginButton.vue";
// import { KAKAO_AUTH_URL, NAVER_AUTH_URL } from '@/config/oauth';
import { useAuthStore } from "@/stores/auth";
import { API_CONFIG } from "@/config/api.config";

const route = useRoute();
const authStore = useAuthStore();
const router = useRouter();
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["close"]);

// OAuth 인증 URL
const KAKAO_AUTH_URL = computed(() => {
  return `${API_CONFIG.AUTH.KAKAO}`;
});

const NAVER_AUTH_URL = computed(() => {
  return `${API_CONFIG.AUTH.NAVER}`;
});

const closeModal = () => {
  emit("close");
};

const handleKakaoLogin = (e) => {
  e.preventDefault();
  const state = encodeURIComponent(window.location.pathname);
  const url = new URL(KAKAO_AUTH_URL.value);
  // url.searchParams.set('state', state);
  window.location.href = url.toString();
};

const handleNaverLogin = (e) => {
  e.preventDefault();
  const state = encodeURIComponent(window.location.pathname);
  const url = new URL(NAVER_AUTH_URL.value);
  // url.searchParams.set("state", state);
  window.location.href = url.toString();
};
</script>

<style scoped>
/* 모달 오버레이 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 모달 컨테이너 */
.modal-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 32px;
  isolation: isolate;
  position: relative;
  width: 448px;
  max-width: 448px;
  height: 412px;
  background: #ffffff;
  mix-blend-mode: normal;
  border-radius: 24px;
}

/* 닫기 버튼 */
.close-button {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 8px;
  position: absolute;
  width: 36px;
  height: 36px;
  right: 24px;
  top: 24px;
  background: none;
  border: none;
  cursor: pointer;
  mix-blend-mode: normal;
  border-radius: 9999px;
  z-index: 4;
}

.close-button:hover {
  background-color: #f3f4f6;
}

.close-icon {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 0px;
  width: 20px;
  height: 20px;
  mix-blend-mode: normal;
  border-radius: 0px;
}

/* 로고 섹션 */
.logo-section {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  padding: 0px 0px 32px;
  width: 384px;
  height: 96px;
  flex: none;
  order: 0;
  flex-grow: 0;
  z-index: 0;
}

.logo-container {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  padding: 0px 160px 16px;
  width: 384px;
  height: 80px;
  flex: none;
  order: 0;
  flex-grow: 0;
}

.logo-icon {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 0px;
  width: 64px;
  height: 64px;
  background: linear-gradient(90deg, #3b82f6 0%, #9333ea 100%);
  mix-blend-mode: normal;
  border-radius: 16px;
  flex: none;
  order: 0;
  flex-grow: 0;
}

.logo-text {
  width: 57px;
  height: 36px;
  font-family: "Roboto", sans-serif;
  font-style: normal;
  font-weight: 700;
  font-size: 30px;
  line-height: 36px;
  display: flex;
  align-items: center;
  letter-spacing: -0.75px;
  color: #ffffff;
  mix-blend-mode: normal;
  flex: none;
  order: 0;
  flex-grow: 0;
  margin: 0;
}

/* 제목 섹션 */
.title-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 0px 0px 32px;
  width: 384px;
  height: 112px;
  flex: none;
  order: 1;
  flex-grow: 0;
  z-index: 1;
}

.main-title {
  width: auto;
  height: 40px;
  font-family: "Roboto", sans-serif;
  font-style: normal;
  font-weight: 700;
  font-size: 36px;
  line-height: 40px;
  display: flex;
  align-items: center;
  letter-spacing: -0.9px;
  color: #111827;
  mix-blend-mode: normal;
  flex: none;
  order: 0;
  flex-grow: 0;
  margin: 0 0 8px 0;
}

.sub-title {
  width: auto;
  height: 32px;
  font-family: "Roboto", sans-serif;
  font-style: normal;
  font-weight: 400;
  font-size: 24px;
  line-height: 32px;
  display: flex;
  align-items: center;
  letter-spacing: -0.6px;
  color: #111827;
  mix-blend-mode: normal;
  flex: none;
  order: 0;
  flex-grow: 0;
  margin: 0;
}

/* 로그인 버튼 섹션 */
.login-button-section {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  padding: 0px 0px 16px;
  width: 384px;
  height: 74px;
  flex: none;
  flex-grow: 0;
}

.login-button-section:nth-child(4) {
  order: 2;
  z-index: 2;
}

.login-button-section:nth-child(5) {
  order: 3;
  z-index: 3;
}

/* 카카오 로그인 버튼 */
.kakao-login-button {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 0;
  gap: 12px;
  width: 384px;
  height: 58px;
  background: #fee500;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  flex: none;
  order: 0;
  flex-grow: 0;
  transition: background-color 0.2s ease;
}

.kakao-login-button:hover {
  background: #fdd835;
}

.kakao-login-button:active {
  background: #f9a825;
}

.kakao-symbol {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 18px;
  flex-shrink: 0;
}

.kakao-symbol svg {
  width: 100%;
  height: 100%;
}

.kakao-label {
  font-family: "Roboto", -apple-system, BlinkMacSystemFont, "Segoe UI",
    sans-serif;
  font-style: normal;
  font-weight: 500;
  font-size: 16px;
  line-height: 19px;
  color: #3c1e1e;
  text-align: center;
  flex-shrink: 0;
  /* 레이블 높이가 컨테이너 높이의 1/3을 넘지 않도록 제한 (58px의 1/3 ≈ 19px) */
  max-height: 19px;
}

/* 네이버 로그인 버튼 */
.naver-login-button {
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 0;
  gap: 12px;
  width: 384px;
  height: 58px;
  background: #03c75a;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  flex: none;
  order: 0;
  flex-grow: 0;
  transition: background-color 0.2s ease;
}

.naver-login-button:hover {
  background: #02b350;
}

.naver-login-button:active {
  background: #029f46;
}

.naver-symbol {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
  flex-shrink: 0;
}

.naver-symbol svg {
  width: 100%;
  height: 100%;
}

.naver-label {
  font-family: "Roboto", -apple-system, BlinkMacSystemFont, "Segoe UI",
    sans-serif;
  font-style: normal;
  font-weight: 500;
  font-size: 16px;
  line-height: 19px;
  color: white;
  text-align: center;
  flex-shrink: 0;
  /* 레이블 높이가 컨테이너 높이의 1/3을 넘지 않도록 제한 (58px의 1/3 ≈ 19px) */
  max-height: 19px;
}
</style>
