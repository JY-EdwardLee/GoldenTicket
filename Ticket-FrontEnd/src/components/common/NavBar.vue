<template>
  <nav class="navbar">
    <div class="logo">
      <router-link to="/">
        <span>골든 티켓</span>
        <!-- <img src="@/assets/images/logo1.png" alt="logo" /> -->
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
        <!-- 알림 아이콘 -->
        <div class="notification-container">
          <button class="notification-btn" @click="toggleNotificationModal" :class="{ 'active': isNotificationModalOpen }">
            <span class="notification-icon">🔔</span>
            <span v-if="notificationCount > 0" class="notification-badge">{{ notificationCount }}</span>
          </button>
          
          <!-- 알림 모달 -->
          <div v-if="isNotificationModalOpen" class="notification-modal">
            <div class="notification-header">
              <h3>알림</h3>
              <button class="close-notification-btn" @click="closeNotificationModal">×</button>
            </div>
            <div class="notification-content">
              <div v-if="notifications.length === 0" class="no-notifications">
                새로운 알림이 없습니다.
              </div>
              <div v-else class="notification-list">
                <div 
                  v-for="notification in sortedNotifications" 
                  :key="notification.id" 
                  class="notification-card"
                  :class="{ 'unread': !notification.read }"
                  @click="markAsRead(notification.id)"
                >
                  <div class="notification-card-header">
                    <div class="notification-icon-container">
                      <div class="notification-app-icon" :class="{ 'unread-icon': !notification.read }">⚾</div>
                        <div class="notification-app-info">
                          <span class="app-name">골든 티켓</span>
                          <span class="notification-date">{{ formatTime(notification.createdAt) }}</span>
                        </div>
                      </div>
                      <div class="notification-options" @click.stop="openActionSheet(notification.id)">
                         <span class="options-dots">⋯</span>
                      </div>
                    </div>
                    <div class="notification-card-content">
                      <div class="notification-message" v-html="notification.message"></div>
                    </div>
                 </div>
               </div>
              </div>
              
              <!-- 액션 시트 모달 -->
              <div v-if="isActionSheetOpen" class="action-sheet-overlay" @click="closeActionSheet">
                <div class="action-sheet" @click.stop>
                  <div class="action-sheet-item" @click="deleteNotification">
                    <span class="action-icon">🗑️</span>
                    <span class="action-text">삭제하기</span>
                  </div>
                  <div class="action-sheet-cancel" @click="closeActionSheet">
                    취소
                  </div>
                </div>
              </div>
            </div>
          </div>
        
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
          <!-- 모바일 알림 아이콘 -->
          <div class="mobile-notification-container">
            <button class="mobile-notification-btn" @click="handleMobileNotification">
              <span class="notification-icon">🔔</span>
              <span v-if="notificationCount > 0" class="notification-badge">{{ notificationCount }}</span>
              <span class="notification-text">알림</span>
            </button>
          </div>
          
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
import { ref, onMounted, watch, computed } from "vue";
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

// 알림 관련 상태
const isNotificationModalOpen = ref(false);
const isActionSheetOpen = ref(false);
const selectedNotificationId = ref(null);
const notifications = ref([
  // 샘플 알림 데이터 (실제로는 API에서 가져올 예정)
  {
    id: 1,
    message: "내가 쓴 게시글(100)에 새로운 댓글이 달렸습니다!\n 확인해 보세요!",
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24), // 1일 전
    read: false,
  },
  {
    id: 2,
    message: "응모하신 (날짜, 시간, 경기)에 당첨되었습니다! 결제를 진행해주세요!\n <b>※ 10분 내로 결제 필요!</b>",
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 2), // 2일 전
    read: true,
  },
  {
    id: 3,
    message: "응모하신 (날짜, 시간, 경기)에 미당첨 되었습니다.\n 현재 당첨 가중치(4)",
    createdAt: new Date(Date.now() - 1000 * 60 * 5), // 30분 전
    read: false,
  },
  {
    id: 4,
    message: "응모하신 (날짜, 시간, 경기)의 결제 시간이 지나 자동으로 취소 됩니다.",
    createdAt: new Date(Date.now() - 1000 * 60 * 60 * 24 * 1.9), // 30분 전
    read: true,
  }
]);

// 읽지 않은 알림 개수 계산
const notificationCount = computed(() => {
  return notifications.value.filter(n => !n.read).length;
});

// 알림 목록을 최신 날짜 기준으로 내림차순 정렬
const sortedNotifications = computed(() => {
  return [...notifications.value].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
});

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

// 알림 모달 토글
const toggleNotificationModal = () => {
  isNotificationModalOpen.value = !isNotificationModalOpen.value;
};

// 알림 모달 닫기
const closeNotificationModal = () => {
  isNotificationModalOpen.value = false;
};

// 모바일 알림 처리
const handleMobileNotification = () => {
  closeMobileMenu();
  toggleNotificationModal();
};

// 액션 시트 열기
const openActionSheet = (notificationId) => {
  selectedNotificationId.value = notificationId;
  isActionSheetOpen.value = true;
};

// 액션 시트 닫기
const closeActionSheet = () => {
  isActionSheetOpen.value = false;
  selectedNotificationId.value = null;
};

// 알림 삭제
const deleteNotification = () => {
  if (selectedNotificationId.value) {
    const index = notifications.value.findIndex(n => n.id === selectedNotificationId.value);
    if (index !== -1) {
      notifications.value.splice(index, 1);
    }
  }
  closeActionSheet();
};

// 알림을 읽음으로 표시
const markAsRead = (notificationId) => {
  const notification = notifications.value.find(n => n.id === notificationId);
  if (notification && !notification.read) {
    notification.read = true;
  }
};

// 시간 포맷팅 함수
const formatTime = (date) => {
  const now = new Date();
  const diff = now - date;
  const minutes = Math.floor(diff / (1000 * 60));
  const hours = Math.floor(diff / (1000 * 60 * 60));
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  
  if (minutes < 1) return '방금 전';
  if (minutes < 60) return `${minutes}분 전`;
  if (hours < 24) return `${hours}시간 전`;
  if (days < 7) return `${days}일 전`;
  
  return date.toLocaleDateString('ko-KR');
};

// 날짜 포맷팅 함수 (네이버 스타일)
const formatDate = (date) => {
  const month = date.getMonth() + 1;
  const day = date.getDate();
  return `${month}월 ${day}일`;
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
  margin-top: 12px;
}

/* 알림 컨테이너 */
.notification-container {
  position: relative;
  display: flex;
  align-items: center;
}

.notification-btn {
  background: none;
  border: none;
  outline: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  position: relative;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-btn:hover {
  background-color: #f5f5f5;
}

.notification-btn.active {
  background-color: #e0e0e0;
}

.notification-icon {
  font-size: 1.2rem;
  color: #666;
}

.notification-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  background-color: #e11d48;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

/* 알림 모달 */
.notification-modal {
  position: absolute;
  top: 100%;
  right: 0;
  width: 380px;
  max-height: 500px;
  background: #f5f5f5;
  border: none;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  z-index: 1000;
  margin-top: 12px;
  overflow: hidden;
}

.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: white;
  border-bottom: 1px solid #e5e5e5;
}

.notification-header h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #333;
}

.close-notification-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-notification-btn:hover {
  background: #f0f0f0;
}

.notification-content {
  max-height: 400px;
  overflow-y: auto;
  background: #f5f5f5;
  padding: 12px;
}

.notification-content::-webkit-scrollbar {
  width: 4px;
}

.notification-content::-webkit-scrollbar-track {
  background: transparent;
}

.notification-content::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

.notification-content::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

.no-notifications {
  padding: 48px 24px;
  text-align: center;
  color: #8b8b8b;
  font-size: 0.95rem;
  background: white;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.no-notifications::before {
  content: "🔔";
  font-size: 2rem;
  opacity: 0.5;
}

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 네이버 스타일 알림 카드 */
.notification-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.notification-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.notification-card.unread {
  border-left: 3px solid #1e40af;
  background: #f0f4ff;
}

.notification-card.unread:hover {
  background: #e0e7ff;
}

/* 읽은 알림 스타일 */
.notification-card:not(.unread) {
  background: #f8f9fa;
  opacity: 0.8;
}

.notification-card:not(.unread):hover {
  background: #e9ecef;
  opacity: 0.9;
}

.notification-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.notification-icon-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notification-app-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  background: #6b7280;
  transition: background-color 0.2s ease;
}

.notification-app-icon.unread-icon {
  background: #1e40af;
}

.notification-app-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.app-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: #333;
}

.notification-date {
  font-size: 0.75rem;
  color: #999;
}

.notification-options {
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.notification-options:hover {
  background-color: #f0f0f0;
}

.options-dots {
  font-size: 1.2rem;
  color: #999;
  font-weight: bold;
}

.notification-card-content {
  display: flex;
  align-items: flex-start;
}

.notification-message {
  font-size: 0.9rem;
  color: #333;
  line-height: 1.4;
  font-weight: 400;
  white-space: pre-line;
}

/* 모바일 알림 스타일 */
.mobile-notification-container {
  width: 100%;
}

.mobile-notification-btn {
  width: 100%;
  background: none;
  border: none;
  padding: 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #888;
  font-size: 0.95rem;
  transition: color 0.2s;
  position: relative;
}

.mobile-notification-btn:hover {
  color: #333;
}

.mobile-notification-btn .notification-icon {
  font-size: 1rem;
}

.mobile-notification-btn .notification-badge {
  position: static;
  margin-left: auto;
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
  
  /* 모바일에서 알림 모달 위치 조정 */
  .notification-modal {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 90%;
    max-width: 380px;
    max-height: 70vh;
    margin-top: 0;
    border-radius: 16px;
  }
  
  .notification-header {
    padding: 16px 20px;
  }
  
  .notification-card {
    padding: 14px 16px;
  }
  
  .notification-content {
    padding: 10px;
  }
  
  .no-notifications {
    padding: 40px 20px;
  }
}



/* 액션 시트 스타일 */
.action-sheet-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 3000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-sheet {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 400px;
  overflow: hidden;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.action-sheet-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.action-sheet-item:hover {
  background-color: #f8f9fa;
}

.action-sheet-item:active {
  background-color: #e9ecef;
}

.action-icon {
  font-size: 1.2rem;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.action-text {
  font-size: 1rem;
  color: #333;
  font-weight: 500;
}

.action-sheet-cancel {
  padding: 16px 20px;
  text-align: center;
  font-size: 1rem;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  border-top: 8px solid #f0f0f0;
}

.action-sheet-cancel:hover {
  background-color: #f8f9fa;
}

.action-sheet-cancel:active {
  background-color: #e9ecef;
}

@media (min-width: 769px) {
  .mobile-menu,
  .mobile-menu-overlay,
  .hamburger {
    display: none !important;
  }
}
</style>
