<template>
  <div class="oauth-callback">
    <div class="loading-spinner" v-if="isLoading">
      <div class="spinner"></div>
      <p>로그인 처리 중입니다...</p>
    </div>
    <div class="error-message" v-else-if="error">
      <p>로그인 중 오류가 발생했습니다: {{ error }}</p>
      <button @click="handleRetry" class="retry-button">다시 시도</button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router'; // useRoute 추가
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';
import { API_CONFIG } from '@/config/api.config';

const router = useRouter();
const route = useRoute(); // 현재 라우트 정보에 접근하기 위해 추가
const authStore = useAuthStore();

const isLoading = ref(true);
const error = ref(null);

// OAuth 처리 핸들러
const handleOAuthCallback = async () => {
  try {
    // 1. URL에서 카카오가 보내준 인증 코드(code)를 추출합니다.
    const code = route.query.code;

    // 2. 인증 코드가 없으면 에러 처리
    if (!code) {
      throw new Error('인증 코드를 받지 못했습니다.');
    }

    // 3. 추출한 인증 코드를 백엔드로 전송하여 로그인/회원가입 처리를 요청합니다.
    //    백엔드는 이 코드를 받아 카카오 서버와 통신하여 액세스 토큰을 받고, 사용자 정보를 가져옵니다.
    const response = await axios.post(API_CONFIG.AUTH.KAKAO_CALLBACK, { code });

    // 4. 백엔드로부터 받은 데이터(JWT 토큰, 사용자 정보 등)를 처리합니다.
    if (response.data && response.data.accessToken) {
      // 사용자 정보와 토큰을 Pinia 스토어에 저장
      authStore.setUser(response.data);
      authStore.setToken(response.data.accessToken);

      // 이전에 저장된 리다이렉트 경로로 이동하거나, 없으면 홈으로 이동
      const redirectTo = authStore.getAndClearRedirectPath() || '/';
      await router.push(redirectTo);
    } else {
      // 백엔드에서 에러가 발생했거나, 토큰이 없는 경우
      throw new Error('서버로부터 유효한 응답을 받지 못했습니다.');
    }
  } catch (err) {
    console.error('소셜 로그인 처리 중 오류 발생:', err);
    const message = err.response?.data?.message || err.message || '알 수 없는 오류';
    error.value = `로그인 처리 중 오류가 발생했습니다: ${message}`;
  } finally {
    isLoading.value = false;
  }
};

const handleRetry = () => {
  router.push('/');
};

onMounted(() => {
  handleOAuthCallback();
});
</script>

<style scoped>
.oauth-callback {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f5f5;
}

.loading-spinner {
  text-align: center;
}

.spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #f3f3f3;
  border-top: 5px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.retry-button {
  margin-top: 15px;
  padding: 8px 20px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.retry-button:hover {
  background-color: #2980b9;
}
</style>
