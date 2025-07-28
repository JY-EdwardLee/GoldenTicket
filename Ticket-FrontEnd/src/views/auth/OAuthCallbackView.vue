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
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import axios from 'axios';
import { API_CONFIG } from '@/config/api.config';

const router = useRouter();
const authStore = useAuthStore();

const isLoading = ref(true);
const error = ref(null);

// OAuth 처리 핸들러
const handleOAuthCallback = async () => {
  try {
    // URL에서 쿼리 파라미터 파싱
    const urlParams = new URLSearchParams(window.location.search);
    const registered = urlParams.get('registered');
    const redirect = urlParams.get('redirect') || '/';
    
    // 필수 파라미터 검증
    if (registered === null) {
      throw new Error('필수 파라미터가 누락되었습니다.');
    }
    
    if (registered === 'true') {
      // HTTP-only 쿠키에 토큰이 저장되어 있으므로, 사용자 정보를 가져옴
      try {
        // 백엔드에서 사용자 정보 요청 (withCredentials로 쿠키 포함)
        const response = await axios.get(`${API_CONFIG.USER.PROFILE}`, {
          withCredentials: true
        });

        if (response.data && response.data.id) {
          // 사용자 정보를 store에 저장
          authStore.setUser(response.data);
          
          // 저장된 리다이렉트 경로 가져오기 (없으면 '/'로 기본값)
          const redirectTo = authStore.getAndClearRedirectPath();
          await router.push(redirectTo);
        } else {
          throw new Error('사용자 정보를 가져오는데 실패했습니다.');
        }
      } catch (err) {
        console.error('사용자 정보 요청 실패:', err);
        throw new Error('로그인은 성공했지만 사용자 정보를 가져오는데 실패했습니다.');
      }
    }
  } catch (err) {
    console.error('소셜 로그인 처리 중 오류 발생:', err);
    error.value = '로그인 처리 중 오류가 발생했습니다. ' + (err.message || '');
  } finally {
    isLoading.value = false;
  }
};

const handleRetry = () => {
  // 에러 발생 시 로그인 페이지로 이동
  router.push('/login');
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
