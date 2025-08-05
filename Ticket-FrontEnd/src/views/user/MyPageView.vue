<template>
  <div class="mypage-container">
    <!-- 모바일 햄버거 메뉴 버튼 -->
    <button class="mobile-menu-toggle" @click="toggleSidebar">
      <span class="hamburger-icon">☰</span>
    </button>

    <!-- 사이드바 -->
    <div class="sidebar" :class="{ 'sidebar-open': isSidebarOpen }">
      <!-- 모바일 닫기 버튼 -->
      <button class="mobile-close-btn" @click="closeSidebar">
        <span class="close-icon">✕</span>
      </button>

      <!-- 사용자 프로필 카드 -->
      <div class="profile-card">
        <div class="avatar">
          <div class="avatar-icon">👤</div>
        </div>
        <div class="user-info">
          <h3 class="username">{{ user?.userName || '' }}</h3>
          <div class="team-section">
            <div class="team-logo-container" v-if="themeStore.selectedTeam.value">
              <img
                :src="themeStore.currentTheme.value?.logo"
                :alt="themeStore.selectedTeam.value"
                class="team-logo"
                @error="handleLogoError"
              >
            </div>
            <div class="team-info">
              <p class="team">{{ enumToTeamName[themeStore.selectedTeam.value] || '관심 야구팀' }}</p>
              <span class="tag">NO LIMITS</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 마이페이지 메뉴 -->
      <nav class="mypage-menu">
        <router-link to="/mypage" class="menu-item" :class="{ active: $route.path === '/mypage' }" @click="closeSidebar">
          <div class="menu-background-logo" v-if="themeStore.selectedTeam.value && $route.path === '/mypage'">
            <img
              :src="themeStore.currentTheme.value.logo"
              :alt="themeStore.selectedTeam.value"
              class="background-team-logo"
              @error="handleLogoError"
            >
          </div>
          <div class="menu-content">
            <span class="menu-icon">👤</span>
            내 정보
          </div>
        </router-link>
        <router-link to="/mypage/applications" class="menu-item" :class="{ active: $route.path === '/mypage/applications' }" @click="closeSidebar">
          <div class="menu-background-logo" v-if="themeStore.selectedTeam.value && $route.path === '/mypage/applications'">
            <img
              :src="themeStore.currentTheme.value.logo"
              :alt="themeStore.selectedTeam.value"
              class="background-team-logo"
              @error="handleLogoError"
            >
          </div>
          <div class="menu-content">
            <span class="menu-icon">🎫</span>
            나의 응모
          </div>
        </router-link>
        <router-link to="/mypage/purchase" class="menu-item" :class="{ active: $route.path.startsWith('/mypage/purchase') }" @click="closeSidebar">
          <div class="menu-background-logo" v-if="themeStore.selectedTeam.value && $route.path.startsWith('/mypage/purchase')">
            <img
              :src="themeStore.currentTheme.value.logo"
              :alt="themeStore.selectedTeam.value"
              class="background-team-logo"
              @error="handleLogoError"
            >
          </div>
          <div class="menu-content">
            <span class="menu-icon">💰</span>
            구매내역
          </div>
        </router-link>
        <router-link to="/mypage/tickets" class="menu-item" :class="{ active: $route.path === '/mypage/tickets' }" @click="closeSidebar">
          <div class="menu-background-logo" v-if="themeStore.selectedTeam.value && $route.path === '/mypage/tickets'">
            <img
              :src="themeStore.currentTheme.value.logo"
              :alt="themeStore.selectedTeam.value"
              class="background-team-logo"
              @error="handleLogoError"
            >
          </div>
          <div class="menu-content">
            <span class="menu-icon">🎟️</span>
            나의 티켓
          </div>
        </router-link>
        <router-link to="/mypage/posts" class="menu-item" :class="{ active: $route.path === '/mypage/posts' }" @click="closeSidebar">
          <div class="menu-background-logo" v-if="themeStore.selectedTeam.value && $route.path === '/mypage/posts'">
            <img
              :src="themeStore.currentTheme.value.logo"
              :alt="themeStore.selectedTeam.value"
              class="background-team-logo"
              @error="handleLogoError"
            >
          </div>
          <div class="menu-content">
            <span class="menu-icon">📝</span>
            내가 쓴 게시글
          </div>
        </router-link>
        <button @click="handleOpenDeleteUserModal" class="menu-item menu-button">
          <div class="menu-content">
            <span class="menu-icon">❌</span>
            <span class="menu-text">회원 탈퇴</span>
          </div>
        </button>
      </nav>
    </div>

    <!-- 모바일 오버레이 -->
    <div class="mobile-overlay" :class="{ 'overlay-active': isSidebarOpen }" @click="closeSidebar"></div>

    <!-- 메인 콘텐츠 영역 -->
    <div class="main-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, onActivated, computed } from 'vue'
import { useDeleteUserModal } from '../../composables/useDeleteUserModal.js'
import { useTeamThemeStore } from '../../stores/teamTheme.js'
import { enumToTeamName, teamNameToLogo } from '@/utils/teamNameMap'

const user = ref(null)
user.value = JSON.parse(localStorage.getItem('user'))
console.log("user", user.value)

// 팀 테마 스토어 (이미 인스턴스로 export됨)
const themeStore = useTeamThemeStore

// 사용자의 선택된 팀으로 테마 초기화
if (user.value?.myTeam) {
  themeStore.setSelectedTeam(user.value.myTeam)
  console.log('Selected team:', user.value.myTeam)
}

// 사이드바 열림/닫힘 상태
const isSidebarOpen = ref(false)

// 회원탈퇴 모달 관리
const { isDeleteUserModalVisible, openDeleteUserModal, closeDeleteUserModal, handleDeleteUser } = useDeleteUserModal()

// 사이드바 토글 함수
const toggleSidebar = () => {
  isSidebarOpen.value = !isSidebarOpen.value
}

// 사이드바 닫기 함수
const closeSidebar = () => {
  isSidebarOpen.value = false
}

// 회원탈퇴 모달 열기 (사이드바 닫기 포함)
const handleOpenDeleteUserModal = () => {
  openDeleteUserModal()
  closeSidebar() // 모바일에서 사이드바 닫기
}

// 로고 에러 핸들링
const handleLogoError = (event) => {
  event.target.src = 'https://via.placeholder.com/30x30/cccccc/666666?text=⚾'
}

</script>

<style scoped>
.mypage-container {
  display: flex;
  min-height: 100vh;
  background: white;
}

.sidebar {
  width: 280px;
  background: white;
  padding: 20px;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  border-right: 1px solid #e0e0e0;
}

.profile-card {
  text-align: center;
  padding: 20px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.avatar {
  margin-bottom: 15px;
}

.avatar-icon {
  width: 80px;
  height: 80px;
  background: #ff6b35;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  margin: 0 auto;
  color: white;
}

.username {
  font-size: 20px;
  font-weight: bold;
  margin: 0 0 15px 0;
  color: #333;
}

.team-section {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: center;
}

.team-logo-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.team-logo {
  width: 60px;
  height: 60px;
  object-fit: contain;
  border-radius: 8px;
}

.team-info {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.team {
  color: #666;
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

.tag {
  background: var(--theme-primary, #ff6b35);
  color: white;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.mypage-menu {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  text-decoration: none;
  color: #666;
  border-radius: 8px;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
  position: relative;
  overflow: hidden;
}

.menu-item:hover {
  background: #f5f5f5;
  border-left-color: var(--theme-primary, #ff6b35);
}

.menu-item.active {
  background: var(--theme-primary, #ff6b35);
  color: white;
  border-left-color: var(--theme-accent, #e55a2e);
}

.menu-background-logo {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
  z-index: 1;
}

.background-team-logo {
  width: 40px;
  height: 40px;
  object-fit: contain;
  opacity: 1;
  transition: opacity 0.3s ease;
}

.menu-content {
  display: flex;
  align-items: center;
  width: 100%;
  position: relative;
  z-index: 2;
}

.menu-button {
  background: none;
  border: none;
  width: 100%;
  text-align: left;
  font-size: inherit;
  font-family: inherit;
  cursor: pointer;
}

.menu-button .menu-text {
  flex: 1;
}

.menu-icon {
  margin-right: 10px;
  font-size: 18px;
}

.main-content {
  flex: 1;
  padding: 30px;
  background: white;
}

/* 모바일 햄버거 메뉴 버튼 */
.mobile-menu-toggle {
  display: none;
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 1000;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px;
  cursor: pointer;
  font-size: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
}

.mobile-close-btn {
  display: none;
  position: absolute;
  top: 15px;
  right: 15px;
  background: #ff6b35;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  cursor: pointer;
  font-size: 18px;
  z-index: 1001;
}

.mobile-overlay {
  display: none;
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 998;
  opacity: 0;
  visibility: hidden;
  transition: all 0.3s ease;
}

.overlay-active {
  opacity: 1;
  visibility: visible;
}

@media (max-width: 768px) {
  .mypage-container {
    flex-direction: column;
    min-height: auto;
  }
  
  .mobile-menu-toggle {
    display: block;
  }
  
  .mobile-close-btn {
    display: block;
  }
  
  .mobile-overlay {
    display: block;
  }
  
  .sidebar {
    position: fixed;
    top: 0;
    left: -280px;
    width: 280px;
    height: 100vh;
    z-index: 999;
    background: white;
    transition: left 0.3s ease;
    overflow-y: auto;
    box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);
  }
  
  .sidebar-open {
    left: 0;
  }
  
  .main-content {
    width: 100%;
    padding: 20px;
    padding-top: 80px;
    flex: 1;
  }
}
</style> 