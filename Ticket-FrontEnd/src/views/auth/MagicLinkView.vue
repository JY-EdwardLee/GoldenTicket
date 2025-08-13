<template>
  <div class="magic-link">
    <div class="loading-spinner" v-if="isLoading">
      <div class="spinner"></div>
      <p>로그인 처리 중입니다...</p>
    </div>
    <div class="error-message" v-else-if="error">
      <p>로그인 중 오류가 발생했습니다: {{ error }}</p>
      <button @click="handleRetry" class="retry-button">다시 시도</button>
    </div>
    <div class="success-message" v-else-if="success">
      <h2>✅ 로그인 성공!</h2>
      <div class="user-info">
        <h3>저장된 사용자 정보:</h3>
        <div class="info-grid">
          <div class="info-item">
            <strong>사용자 ID:</strong> {{ userInfo?.userId }}
          </div>
          <div class="info-item">
            <strong>사용자 이름:</strong> {{ userInfo?.userName }}
          </div>
          <div class="info-item">
            <strong>이메일:</strong> {{ userInfo?.email }}
          </div>
          <div class="info-item">
            <strong>닉네임:</strong> {{ userInfo?.nickName }}
          </div>
          <div class="info-item">
            <strong>소셜 제공자:</strong> {{ userInfo?.socialProvider }}
          </div>
          <div class="info-item">
            <strong>성별:</strong> {{ userInfo?.gender }}
          </div>
          <div class="info-item">
            <strong>생년월일:</strong> {{ userInfo?.birthDate }}
          </div>
        </div>
        <div class="token-info">
          <strong>JWT 토큰:</strong>
          <div class="token-display">{{ userInfo?.accessToken }}</div>
        </div>
      </div>
      <div class="actions">
        <button @click="goToMainPage" class="main-page-button">메인페이지로 이동</button>
        <button @click="showStoreInfo" class="store-info-button">Store 정보 확인</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import http from "@/utils/http";
import { API_CONFIG } from "@/config/api.config";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const isLoading = ref(true);
const error = ref(null);
const success = ref(false);
const userInfo = ref(null);

const handleMagicLink = async () => {
  try {
    const type = route.query.type;
    
    if (!type || (type !== 'admin' && type !== 'user')) {
      throw new Error("잘못된 접근입니다. admin 또는 user를 지정해주세요.");
    }

    let endpoint;
    if (type === 'admin') {
      endpoint = API_CONFIG.USER.ADMIN;
    } else {
      endpoint = API_CONFIG.USER.GENERAL_USER;
    }

    const response = await http.post(endpoint);
    
    if (response.data && response.data.accessToken) {
      // userRole이 서버 응답에 포함되어 있다면 저장
      if (response.data.userRole) {
        authStore.setUserRole(response.data.userRole);
      }
      
      authStore.getUserInfo();
      authStore.setUser(response.data);
      authStore.setToken(response.data.accessToken);
      userInfo.value = response.data;
      success.value = true;
      
      const redirectTo = authStore.getAndClearRedirectPath();
      await router.push(redirectTo);
    } else {
      throw new Error("사용자 정보를 가져오지 못했습니다.");
    }
  } catch (err) {
    error.value = err.message || "알 수 없는 오류가 발생했습니다.";
    isLoading.value = false;
  }
};

const handleRetry = () => {
  router.push('/');
};

const goToMainPage = () => {
  router.push('/');
};

const showStoreInfo = () => {
  // Store에 저장된 정보 확인
  const storeUser = authStore.user;
  const storeToken = authStore.token;
  const isAuth = authStore.isAuthenticated;
  
  console.log('=== Store에 저장된 정보 ===');
  console.log('사용자 정보:', storeUser);
  console.log('토큰:', storeToken);
  console.log('인증 상태:', isAuth);
  
  // Alert로도 표시
  const storeInfo = `
Store 정보:
- 사용자: ${JSON.stringify(storeUser, null, 2)}
- 토큰: ${storeToken ? '저장됨' : '없음'}
- 인증상태: ${isAuth}
  `;
  
  alert(storeInfo);
};

onMounted(() => {
  handleMagicLink();
});
</script>

<style scoped>
.magic-link {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
}

.loading-spinner,
.error-message {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-left-color: #007bff;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.error-message p {
  color: #dc3545;
  margin-bottom: 15px;
}

.retry-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

.retry-button:hover {
  background-color: #0056b3;
}

.success-message {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.success-message h2 {
  color: #28a745;
  margin-bottom: 20px;
}

.user-info h3 {
  margin-bottom: 15px;
  color: #333;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 10px;
  margin-bottom: 20px;
}

.info-item {
  background-color: #f8f9fa;
  padding: 10px;
  border-radius: 5px;
  border: 1px solid #e9ecef;
}

.info-item strong {
  color: #007bff;
  margin-right: 5px;
}

.token-info {
  margin-top: 20px;
  padding: 15px;
  background-color: #e9ecef;
  border-radius: 5px;
  border: 1px solid #dee2e6;
}

.token-info strong {
  color: #343a40;
  margin-right: 5px;
}

.token-display {
  font-family: monospace;
  font-size: 0.9em;
  background-color: #fff;
  padding: 5px;
  border-radius: 3px;
  border: 1px solid #ced4da;
  overflow-wrap: break-word;
  word-break: break-all;
}

.actions {
  margin-top: 20px;
}

.main-page-button,
.store-info-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  margin: 0 10px;
}

.main-page-button:hover,
.store-info-button:hover {
  background-color: #0056b3;
}
</style>
