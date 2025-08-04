<script setup>
import { ref } from 'vue'
import ProfileImageTest from './components/ProfileImageTest.vue'
import PostImageTest from './components/PostImageTest.vue'

// 현재 페이지 상태
const currentPage = ref('main') // 'main', 'profile', 'page2'

// 페이지 이동 함수
const goToPage = (page) => {
  currentPage.value = page
}
</script>

<template>
  <div class="container">
    <!-- 메인 페이지 -->
    <div v-if="currentPage === 'main'">
      <h1>S3 이미지 관리 테스트</h1>
      <div class="main-buttons">
        <button @click="goToPage('profile')" class="main-button">
          1번 - 사용자 마이페이지 프로필 이미지 테스트
        </button>
        <button @click="goToPage('page2')" class="main-button">
          2번 - 게시물 이미지 업로드 테스트
        </button>
      </div>
    </div>

    <!-- 1번 페이지 - 사용자 마이페이지 프로필 이미지 테스트 -->
    <div v-if="currentPage === 'profile'">
      <div class="page-header">
        <button @click="goToPage('main')" class="back-button">← 메인으로 돌아가기</button>
      </div>
      <ProfileImageTest />
    </div>

    <!-- 2번 페이지 - 게시물 이미지 업로드 테스트 -->
    <div v-if="currentPage === 'page2'">
      <div class="page-header">
        <button @click="goToPage('main')" class="back-button">← 메인으로 돌아가기</button>
      </div>
      <PostImageTest />
    </div>
  </div>
</template>

<style scoped>
.container {
  max-width: 800px;
  margin: 20px auto;
  padding: 20px;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.main-buttons {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-top: 40px;
}

.main-button {
  padding: 20px;
  font-size: 18px;
  border: none;
  border-radius: 8px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s;
}

.main-button:hover {
  background-color: #0056b3;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.back-button {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  background-color: #6c757d;
  color: white;
  cursor: pointer;
}

.back-button:hover {
  background-color: #545b62;
}
</style>
