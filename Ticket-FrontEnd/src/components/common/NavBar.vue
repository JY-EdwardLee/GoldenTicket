<template>
  <nav class="navbar">
    <div class="logo">
      <router-link to="/">
        <span>골든 티켓</span>
        <!-- <img src="@/assets/images/logo1.png" alt="logo" /> -->
      </router-link>
    </div>
    <ul class="nav-links">
      <li><a href="#" @click.prevent="goToApplication">응모</a></li>
      <li><a href="#" @click.prevent="goToTransfer">양도</a></li>
      <li><router-link to="/bulletin">게시판</router-link></li>
      <li><router-link to="/guide">이용가이드</router-link></li>
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
const pendingRedirect = ref(null); // 로그인 후 리다이렉트할 경로 저장

// 컴포넌트 마운트 시 로그인 상태 확인
onMounted(() => {
  isLoggedIn.value = isAuthenticated.value;
});

// isAuthenticated 상태가 변경될 때마다 isLoggedIn 업데이트
watch(isAuthenticated, (newValue) => {
  isLoggedIn.value = newValue;
  
  // 로그인 성공 시 대기 중인 리다이렉트 처리
  if (newValue && pendingRedirect.value) {
    router.push(pendingRedirect.value);
    pendingRedirect.value = null;
    isLoginModalVisible.value = false;
  }
});

// 로그인 모달 열기
const openLoginModal = () => {
  isLoginModalVisible.value = true;
};

// 로그인 모달 닫기
const closeLoginModal = () => {
  isLoginModalVisible.value = false;
  pendingRedirect.value = null; // 모달 닫을 때 대기 중인 리다이렉트 초기화
};

// 응모 페이지로 이동
const goToApplication = () => {
  if (!isAuthenticated.value) {
    pendingRedirect.value = '/application'; // 로그인 후 이동할 경로 저장
    isLoginModalVisible.value = true;
    return;
  }
  router.push('/application');
};

// 양도 페이지로 이동
const goToTransfer = () => {
  if (!isAuthenticated.value) {
    pendingRedirect.value = '/transfer'; // 로그인 후 이동할 경로 저장
    isLoginModalVisible.value = true;
    return;
  }
  router.push('/transfer');
};

// In NavBar.vue
const handleLogout = async () => {
  try {
    await authStore.logout();
    isLoggedIn.value = false;
    authStore.setRedirectPath('/');
    window.location.href = '/';
  } catch (error) {
    console.error('Logout failed:', error);
  }
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
  font-size: 2rem;
  font-family: "Bagel Fat One", system-ui;
  font-style: normal;
}

.logo span {
  color: #d79508;
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
