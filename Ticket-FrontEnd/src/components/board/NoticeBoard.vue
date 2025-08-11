<template>
  <div>
    <BoardHeader 
      title="공지사항"
      v-model:searchValue="searchValue"
      @search="handleSearch"
      @searchTypeChange="handleSearchTypeChange"
      @clearSearch="handleClearSearch"
    />

    <!-- 상단 우측 액션 (관리자 전용) -->
    <div class="actions-row" v-if="isAdmin">
      <button class="write-btn" @click="goToCreate">글쓰기</button>
    </div>
    
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>공지사항을 불러오는 중...</p>
    </div>
    
    <!-- 에러 상태 -->
    <div v-else-if="error" class="error-container">
      <p class="error-message">{{ error }}</p>
      <button @click="loadPosts" class="retry-btn">다시 시도</button>
    </div>
    
    <!-- 공지사항 목록 -->
    <BoardTable 
      v-else
      :posts="noticePosts"
      boardType="notice"
      :currentPage="currentPage"
      :totalPosts="allPosts.length"
      @postClick="handlePostClick"
    />
    
    <!-- 검색 결과 메시지 -->
    <div v-if="searchResultMessage" class="search-result-message">
      {{ searchResultMessage }}
    </div>
    
    <BoardPagination 
      :currentPage="currentPage"
      :totalPages="totalPages"
      :hasWriteButton="isAdmin"
      @pageChange="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import BoardHeader from './BoardHeader.vue';
import BoardTable from './BoardTable.vue';
import BoardPagination from './BoardPagination.vue';
import { boardAPI, BOARD_TYPES } from '@/api/board.js';

const router = useRouter();
const authStore = useAuthStore();

const searchValue = ref('');
const searchType = ref('title');
const searchResultMessage = ref('');
const currentPage = ref(1);
const itemsPerPage = ref(10); // 페이지당 게시글 수
const isLoading = ref(false);
const error = ref('');

const noticePosts = ref([]);
const allPosts = ref([]); // 전체 게시글 저장

// 관리자 여부 판별
const isAdmin = computed(() => {
  const u = authStore.user || JSON.parse(localStorage.getItem('user') || 'null');
  if (!u) return false;
  if (u.role && (u.role === 'ADMIN' || u.role === 'ROLE_ADMIN')) return true;
  if (Array.isArray(u.roles) && (u.roles.includes('ADMIN') || u.roles.includes('ROLE_ADMIN'))) return true;
  if (Array.isArray(u.authorities)) {
    return u.authorities.some(a => {
      const v = a?.authority ?? a;
      return v === 'ADMIN' || v === 'ROLE_ADMIN';
    });
  }
  return false;
});

// 글쓰기 이동
const goToCreate = () => {
  router.push({ name: 'BulletinCreate', query: { type: 'notice' } });
};

// 게시글 목록 로드
const loadPosts = async () => {
  isLoading.value = true;
  error.value = '';
  searchResultMessage.value = '';
  
  try {
    const result = await boardAPI.getPostsByCategory(BOARD_TYPES.NOTICE);
    
    if (Array.isArray(result)) {
      allPosts.value = result;
      updateDisplayedPosts();
    } else {
      error.value = '데이터 형식이 올바르지 않습니다.';
      console.error('공지사항 목록 로드 실패: 잘못된 데이터 형식');
    }
  } catch (err) {
    error.value = '공지사항 목록을 불러오는 중 오류가 발생했습니다.';
    console.error('공지사항 목록 로드 중 오류:', err);
  } finally {
    isLoading.value = false;
  }
};

// 검색 타입 변경
const handleSearchTypeChange = (type) => {
  searchType.value = type;
  console.log('검색 타입 변경:', type);
};

// 검색 실행
const handleSearch = async (searchTypeParam, searchValueParam) => {
  if (!searchValueParam || !searchValueParam.trim()) {
    handleClearSearch();
    return;
  }

  isLoading.value = true;
  error.value = '';
  
  try {
    const result = await boardAPI.searchPosts(BOARD_TYPES.NOTICE, searchTypeParam, searchValueParam.trim());
    
    if (Array.isArray(result)) {
      allPosts.value = result;
      currentPage.value = 1;
      updateDisplayedPosts();
      
    } else {
      error.value = '검색 결과 형식이 올바르지 않습니다.';
      console.error('검색 결과 로드 실패: 잘못된 데이터 형식');
    }
  } catch (err) {
    error.value = '검색 중 오류가 발생했습니다.';
    console.error('검색 중 오류:', err);
  } finally {
    isLoading.value = false;
  }
};

// 검색 초기화
const handleClearSearch = () => {
  searchValue.value = '';
  currentPage.value = 1;
  updateDisplayedPosts();
  searchResultMessage.value = '';
};

// 페이지네이션 계산
const totalPages = computed(() => Math.ceil(allPosts.value.length / itemsPerPage.value));

const updateDisplayedPosts = () => {
  const startIndex = (currentPage.value - 1) * itemsPerPage.value;
  const endIndex = startIndex + itemsPerPage.value;
  noticePosts.value = allPosts.value.slice(startIndex, endIndex);
};

// 페이지 변경
const handlePageChange = (page) => {
  currentPage.value = page;
  updateDisplayedPosts();
};

// 게시글 클릭
const handlePostClick = (post) => {
  router.push({ name: 'BoardDetail', params: { postId: post.postId } });
};

onMounted(() => {
  loadPosts();
});
</script>

<style scoped>
.actions-row {
  display: flex;
  justify-content: flex-end;
  margin: 12px 0;
}
.write-btn {
  background: var(--theme-primary, #ff6b35);
  color: #fff;
  border: 1px solid var(--theme-primary, #ff6b35);
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  font-weight: 600;
}
.write-btn:hover {
  filter: brightness(90%);
}
</style> 