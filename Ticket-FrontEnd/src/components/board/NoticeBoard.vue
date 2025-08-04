<template>
  <div>
    <BoardHeader 
      title="공지사항"
      v-model:searchValue="searchValue"
      @search="handleSearch"
      @searchTypeChange="handleSearchTypeChange"
    />
    
    <!-- 검색 결과 메시지 -->
    <div v-if="searchResultMessage" class="search-result-message">
      {{ searchResultMessage }}
    </div>
    
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>게시글을 불러오는 중...</p>
    </div>
    
    <!-- 에러 상태 -->
    <div v-else-if="error" class="error-container">
      <p class="error-message">{{ error }}</p>
      <button class="retry-btn" @click="loadPosts">다시 시도</button>
    </div>
    
    <!-- 게시글 목록 -->
    <div v-else>
      <BoardTable 
        :posts="noticePosts"
        boardType="notice"
        @postClick="handlePostClick"
      />
      
      <BoardPagination 
        :currentPage="currentPage"
        :totalPages="totalPages"
        :hasWriteButton="false"
        @pageChange="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import BoardHeader from './BoardHeader.vue';
import BoardTable from './BoardTable.vue';
import BoardPagination from './BoardPagination.vue';
import { useRouter } from 'vue-router';
import { boardAPI, BOARD_TYPES } from '@/api/board.js';

const router = useRouter();
const searchValue = ref('');
const currentPage = ref(1);
const totalPages = ref(5);
const isLoading = ref(false);
const error = ref('');
const searchType = ref('title');
const searchResultMessage = ref('');
const noticePosts = ref([]);

const loadPosts = async () => {
  isLoading.value = true;
  error.value = '';
  searchResultMessage.value = '';
  try {
    const result = await boardAPI.getPostsByCategory(BOARD_TYPES.NOTICE);
    if (result.success) {
      noticePosts.value = result.data;
    } else {
      error.value = result.message;
    }
  } catch (err) {
    error.value = '게시글 목록을 불러오는 중 오류가 발생했습니다.';
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  loadPosts();
});

const handleSearchTypeChange = (type) => {
  searchType.value = type;
};

const handleSearch = async (searchTypeParam = null) => {
  if (!searchValue.value.trim()) {
    alert('검색어를 입력해주세요.');
    return;
  }
  isLoading.value = true;
  error.value = '';
  try {
    const type = searchTypeParam || searchType.value;
    const result = await boardAPI.searchPosts(BOARD_TYPES.NOTICE, type, searchValue.value.trim());
    if (result.success) {
      noticePosts.value = result.data;
      searchResultMessage.value = result.message;
    } else {
      error.value = result.message;
      searchResultMessage.value = '';
    }
  } catch (err) {
    error.value = '검색 중 오류가 발생했습니다.';
    searchResultMessage.value = '';
  } finally {
    isLoading.value = false;
  }
};

const handlePostClick = (post) => {
  console.log('게시글 클릭:', post);
  // TODO: 게시글 상세 페이지로 이동
};

const handlePageChange = (page) => {
  currentPage.value = page;
  console.log('페이지 변경:', page);
  // TODO: 페이지 변경 로직 구현
};
</script>

<style scoped>
.search-result-message {
  background: #f0f9ff;
  border: 1px solid #0ea5e9;
  color: #0c4a6e;
  padding: 12px 16px;
  border-radius: 8px;
  margin-bottom: 16px;
  font-size: 14px;
  text-align: center;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f4f6;
  border-top: 4px solid #3b82f6;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.error-message {
  color: #dc2626;
  margin-bottom: 16px;
  font-size: 16px;
}

.retry-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.retry-btn:hover {
  background: #2563eb;
}
</style> 