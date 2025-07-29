<template>
  <nav class="navbar">
    <div class="logo">
      <router-link to="/">골든티켓</router-link>
    </div>
    <ul class="nav-links">
      <li><router-link to="/application">응모</router-link></li>
      <li><router-link to="/transfer">양도</router-link></li>
      <li><router-link to="/bulletin">게시판</router-link></li>
      <li><router-link to="/guide">FAQ</router-link></li>
    </ul>
    <div class="auth-links">
      <template v-if="isLoggedIn">
        <router-link to="/mypage" class="auth-link">마이페이지</router-link>
        <a href="#" @click.prevent="handleLogout" class="auth-link">로그아웃</a>
      </template>
      <a v-else href="#" @click.prevent="openLoginModal" class="auth-link">로그인 또는 회원가입</a>
    </div>
  </nav>

  <!-- 로그인 모달 -->
  <LoginModal :isVisible="isLoginModalVisible" @close="closeLoginModal" />
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import LoginModal from "./LoginModal.vue";
import { useAuthStore } from "@/stores/auth";
import axios from "axios";
import { API_CONFIG } from "@/config/api.config";
import { storeToRefs } from "pinia";
// 스토어 및 라우터 초기화
const authStore = useAuthStore();
const router = useRouter();

// store의 isAuthenticated를 반응형 참조로 가져옴
const { isAuthenticated } = storeToRefs(authStore);

// 로그인 상태 관리
const isLoggedIn = ref(false);
const isLoginModalVisible = ref(false);

// 컴포넌트 마운트 시 로그인 상태 확인
onMounted(() => {
  isLoggedIn.value = isAuthenticated.value;
});

// isAuthenticated 상태가 변경될 때마다 isLoggedIn 업데이트
watch(isAuthenticated, (newValue) => {
  isLoggedIn.value = newValue;
});

// 로그인 모달 열기
const openLoginModal = () => {
  isLoginModalVisible.value = true;
};

// 로그인 모달 닫기
const closeLoginModal = () => {
  isLoginModalVisible.value = false;
};

// 로그아웃 처리
const handleLogout = () => {
  authStore.logout();
  isLoggedIn.value = isAuthenticated.value;
  const response = axios.get(`${API_CONFIG.USER.LOGOUT}`, {
    withCredentials: true,
  });
  console.log(response.data)
  router.push('/');
};
</script>

<style scoped>
.navbar {
  width: 100%;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee;
  padding: 0 32px;
  position: sticky;
  top: 0;
  z-index: 100;
}
.logo {
  font-weight: bold;
  font-size: 1.2rem;
}
.nav-links {
  display: flex;
  gap: 32px;
  list-style: none;
  padding: 0;
  margin: 0;
}
.nav-links li {
  font-weight: 500;
}
.nav-links a {
  text-decoration: none;
  color: #222;
}
.nav-links a.router-link-active {
  color: #ffb43a;
}
.auth-links {
  display: flex;
  gap: 16px;
}

.auth-links a {
  color: #888;
  text-decoration: none;
  font-size: 0.95rem;
}
</style>
