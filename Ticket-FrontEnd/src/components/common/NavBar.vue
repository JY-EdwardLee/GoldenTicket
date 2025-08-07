<template>
  <nav class="navbar">
    <div class="logo">
      <router-link to="/">
        <span>골든 티켓</span>
        <!-- <img src="@@/assets/images/logo1.png" alt="logo" /> -->
      </router-link>
    </div>
    
    <!-- Hamburger menu button for mobile -->
    <div class="hamburger" @click="toggleMobileMenu" :class="{ 'active': isMobileMenuOpen }">
      <span></span>
      <span></span>
      <span></span>
    </div>
    
    <!-- Desktop navigation -->
    <ul class="nav-links">
      <li><a href="#" @click.prevent="goToApplication">응모</a></li>
      <li><a href="#" @click.prevent="goToTransfer">양도</a></li>
      <li><router-link to="/bulletin">게시판</router-link></li>
      <li><router-link to="/guide">이용가이드</router-link></li>
    </ul>
    
    <!-- Desktop auth links -->
    <div class="auth-links">
      <template v-if="isLoggedIn">
        <router-link to="/mypage" class="auth-link">마이페이지</router-link>
        <a href="#" @click.prevent="handleLogout" class="auth-link">로그아웃</a>
      </template>
      <a v-else href="#" @click.prevent="openLoginModal" class="auth-link">로그인 또는 회원가입</a>
    </div>
    
    <!-- Mobile menu overlay -->
    <div class="mobile-menu-overlay" :class="{ 'active': isMobileMenuOpen }" @click="closeMobileMenu"></div>
    
    <!-- Mobile menu -->
    <div class="mobile-menu" :class="{ 'active': isMobileMenuOpen }">
      <div class="mobile-menu-header">
        <div class="logo mobile-logo">
          <router-link to="/" @click="closeMobileMenu">
            <span>골든 티켓</span>
          </router-link>
        </div>
        <button class="close-btn" @click="closeMobileMenu">×</button>
      </div>
      
      <ul class="mobile-nav-links">
        <li><a href="#" @click.prevent="handleMobileNav('application')">응모</a></li>
        <li><a href="#" @click.prevent="handleMobileNav('transfer')">양도</a></li>
        <li><router-link to="/bulletin" @click="closeMobileMenu">게시판</router-link></li>
        <li><router-link to="/guide" @click="closeMobileMenu">이용가이드</router-link></li>
      </ul>
      
      <div class="mobile-auth-links">
        <template v-if="isLoggedIn">
          <router-link to="/mypage" class="mobile-auth-link" @click="closeMobileMenu">마이페이지</router-link>
          <a href="#" @click.prevent="handleMobileLogout" class="mobile-auth-link">로그아웃</a>
        </template>
        <a v-else href="#" @click.prevent="handleMobileLogin" class="mobile-auth-link">로그인 또는 회원가입</a>
      </div>
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

// Mobile menu state
const isMobileMenuOpen = ref(false);

// 컴포넌트 마운트 시 로그인 상태 확인
onMounted(() => {
  isLoggedIn.value = isAuthenticated.value;
  
  // Close mobile menu when window is resized to desktop size
  const handleResize = () => {
    if (window.innerWidth > 768 && isMobileMenuOpen.value) {
      closeMobileMenu();
    }
  };
  
  window.addEventListener('resize', handleResize);
  
  // Cleanup event listener
  return () => {
    window.removeEventListener('resize', handleResize);
  };
});

// isAuthenticated 상태가 변경될 때마다 isLoggedIn 업데이트
watch(isAuthenticated, (newValue) => {
  isLoggedIn.value = newValue;
  
  // 로그인 성공 시 대기 중인 리다이렉트 처리
  if (newValue && pendingRedirect.value) {
    router.push(pendingRedirect.value);
    pendingRedirect.value = null;
    isLoginModalVisible.value = false;
    closeMobileMenu(); // Close mobile menu after successful login
  }
});

// Mobile menu functions
const toggleMobileMenu = () => {
  isMobileMenuOpen.value = !isMobileMenuOpen.value;
  // Prevent body scroll when mobile menu is open
  if (isMobileMenuOpen.value) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = '';
  }
};

const closeMobileMenu = () => {
  isMobileMenuOpen.value = false;
  document.body.style.overflow = '';
};

// Mobile navigation handlers
const handleMobileNav = (route) => {
  closeMobileMenu();
  if (route === 'application') {
    goToApplication();
  } else if (route === 'transfer') {
    goToTransfer();
  }
};

const handleMobileLogin = () => {
  closeMobileMenu();
  openLoginModal();
};

const handleMobileLogout = () => {
  closeMobileMenu();
  handleLogout();
};

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

/* Hamburger menu button */
.hamburger {
  display: none;
  flex-direction: column;
  cursor: pointer;
  padding: 8px;
  z-index: 1001;
}

.hamburger span {
  width: 25px;
  height: 3px;
  background-color: #333;
  margin: 3px 0;
  transition: 0.3s;
  border-radius: 2px;
}

.hamburger.active span:nth-child(1) {
  transform: rotate(-45deg) translate(-5px, 6px);
}

.hamburger.active span:nth-child(2) {
  opacity: 0;
}

.hamburger.active span:nth-child(3) {
  transform: rotate(45deg) translate(-5px, -6px);
}

/* Mobile menu overlay */
.mobile-menu-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.mobile-menu-overlay.active {
  opacity: 1;
  visibility: visible;
}

/* Mobile menu */
.mobile-menu {
  position: fixed;
  top: 0;
  right: -300px;
  width: 300px;
  height: 100vh;
  background-color: #fff;
  z-index: 1000;
  transition: right 0.3s ease;
  display: flex;
  flex-direction: column;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
}

.mobile-menu.active {
  right: 0;
}

.mobile-menu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid #eee;
}

.mobile-logo {
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 2rem;
  cursor: pointer;
  color: #333;
  padding: 0;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mobile-nav-links {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
}

.mobile-nav-links li {
  border-bottom: 1px solid #f0f0f0;
}

.mobile-nav-links a {
  display: block;
  padding: 20px;
  text-decoration: none;
  color: #333;
  font-weight: 500;
  transition: background-color 0.2s;
}

.mobile-nav-links a:hover {
  background-color: #f8f9fa;
}

.mobile-nav-links a.router-link-active {
  color: #ffb43a;
  background-color: #fff8e1;
}

.mobile-auth-links {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.mobile-auth-link {
  color: #888;
  text-decoration: none;
  font-size: 0.95rem;
  padding: 8px 0;
  transition: color 0.2s;
}

.mobile-auth-link:hover {
  color: #333;
}

/* Responsive design */
@media (max-width: 768px) {
  .navbar {
    padding: 0 16px;
  }
  
  .hamburger {
    display: flex;
  }
  
  .nav-links,
  .auth-links {
    display: none;
  }
  
  .logo {
    font-size: 1.5rem;
  }
}

@media (min-width: 769px) {
  .mobile-menu,
  .mobile-menu-overlay,
  .hamburger {
    display: none !important;
  }
}
</style>
