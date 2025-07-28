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
      <a href="#" @click.prevent="openLoginModal">로그인 또는 회원가입</a>
    </div>
  </nav>
  
  <!-- 로그인 모달 -->
  <LoginModal :isVisible="isLoginModalVisible" @close="closeLoginModal" />
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import LoginModal from './LoginModal.vue'

// 로그인 모달 상태 관리
const isLoginModalVisible = ref(false)
const router = useRouter()
// 로그인 모달 열기
const openLoginModal = () => {
  isLoginModalVisible.value = true
}

// 로그인 모달 닫기
const closeLoginModal = () => {
  isLoginModalVisible.value = false
}
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
.auth-links a {
  color: #888;
  text-decoration: none;
  font-size: 0.95rem;
}
</style>
