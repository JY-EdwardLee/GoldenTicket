<template>
  <div>
    <BoardHeader 
      title="단체 관련 게시판"
      v-model:searchValue="searchValue"
      @search="handleSearch"
      @searchTypeChange="handleSearchTypeChange"
    />
    
    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>게시글을 불러오는 중...</p>
    </div>
    
    <!-- 에러 상태 -->
    <div v-else-if="error" class="error-container">
      <p class="error-message">{{ error }}</p>
      <button @click="loadPosts" class="retry-btn">다시 시도</button>
    </div>
    
    <!-- 게시글 목록 -->
    <BoardTable 
      v-else
      :posts="groupPosts"
      boardType="group"
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
      :hasWriteButton="true"
      @pageChange="handlePageChange"
    />
    
    <button class="write-btn" @click="handleWriteClick">글쓰기</button>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import BoardHeader from './BoardHeader.vue';
import BoardTable from './BoardTable.vue';
import BoardPagination from './BoardPagination.vue';
import { useRouter } from 'vue-router';
import { boardAPI, BOARD_TYPES } from '@/api/board.js';

const router = useRouter();
const searchValue = ref('');
const currentPage = ref(1);
const itemsPerPage = 10; // 페이지당 게시글 수
const isLoading = ref(false);
const error = ref('');
const searchType = ref('title');
const searchResultMessage = ref('');

const groupPosts = ref([]);
const allPosts = ref([]); // 전체 게시글 저장

// 게시글 목록 로드
const loadPosts = async () => {
  isLoading.value = true;
  error.value = '';
  searchResultMessage.value = '';
  
  try {
    const result = await boardAPI.getPostsByCategory(BOARD_TYPES.GROUPVIEW);
    
    // 서버가 배열을 직접 반환하므로 result 자체가 배열
    if (Array.isArray(result)) {
      allPosts.value = result;
      updateDisplayedPosts();
    } else {
      error.value = '데이터 형식이 올바르지 않습니다.';
      console.error('게시글 목록 로드 실패: 잘못된 데이터 형식');
    }
  } catch (err) {
    error.value = '게시글 목록을 불러오는 중 오류가 발생했습니다.';
    console.error('게시글 목록 로드 중 오류:', err);
  } finally {
    isLoading.value = false;
  }
};

// 페이지네이션 계산
const totalPages = computed(() => {
  return Math.ceil(allPosts.value.length / itemsPerPage);
});

// 현재 페이지의 게시글만 표시하는 함수 (최신순 정렬)
const updateDisplayedPosts = () => {
  // 게시글을 최신순으로 정렬 (createdAt 기준 내림차순)
  const sortedPosts = [...allPosts.value].sort((a, b) => {
    const dateA = new Date(a.createdAt);
    const dateB = new Date(b.createdAt);
    return dateB - dateA; // 최신순 (내림차순)
  });
  
  const startIndex = (currentPage.value - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  groupPosts.value = sortedPosts.slice(startIndex, endIndex);
};

// 컴포넌트 마운트 시 게시글 목록 로드
onMounted(() => {
  loadPosts();
});

// 검색 타입 변경
const handleSearchTypeChange = (type) => {
  searchType.value = type;
  console.log('검색 타입 변경:', type);
};

// 검색 실행
const handleSearch = async (searchTypeParam = null) => {
  if (!searchValue.value.trim()) {
    alert('검색어를 입력해주세요.');
    return;
  }

  isLoading.value = true;
  error.value = '';
  
  try {
    const type = searchTypeParam || searchType.value;
    const result = await boardAPI.searchPosts(BOARD_TYPES.GROUPVIEW, type, searchValue.value.trim());
    
    // 서버가 배열을 직접 반환하므로 result 자체가 배열
    if (Array.isArray(result)) {
      allPosts.value = result;
      currentPage.value = 1; // 검색 시 첫 페이지로 이동
      updateDisplayedPosts();
      searchResultMessage.value = `검색 결과: ${result.length}건`;
      console.log('검색 성공:', result);
    } else {
      error.value = '데이터 형식이 올바르지 않습니다.';
      searchResultMessage.value = '';
      console.error('검색 실패: 잘못된 데이터 형식');
    }
  } catch (err) {
    error.value = '검색 중 오류가 발생했습니다.';
    searchResultMessage.value = '';
    console.error('검색 중 오류:', err);
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
  updateDisplayedPosts();
  console.log('페이지 변경:', page);
};

const handleWriteClick = () => {
  // TODO: 로그인 여부 확인 로직 추가
  // const isLoggedIn = useAuthStore().isLoggedIn;
  // if (!isLoggedIn) {
  //   alert('로그인이 필요합니다.');
  //   router.push('/login');
  //   return;
  // }
  
  // 단체 관련 게시판 타입을 쿼리 파라미터로 전달하여 글쓰기 페이지로 이동
  router.push({
    name: 'BulletinCreate',
    query: { type: 'group' }
  });
};
</script>

<style scoped>
/* 로딩 스타일 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #6b7280;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f4f6;
  border-top: 4px solid #e11d48;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 에러 스타일 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #dc2626;
}

.error-message {
  margin-bottom: 16px;
  text-align: center;
}

.retry-btn {
  background: #e11d48;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.retry-btn:hover {
  background: #be123c;
}

/* 검색 결과 메시지 */
.search-result-message {
  text-align: center;
  padding: 12px;
  margin: 16px 32px;
  background: #f0f9ff;
  border: 1px solid #0ea5e9;
  border-radius: 6px;
  color: #0369a1;
  font-size: 14px;
}

.write-btn {
  float: right;
  margin: 24px 16px 0 0;
  background: #888;
  color: #fff;
  font-size: 15px;
  border: none;
  border-radius: 6px;
  padding: 8px 18px;
  cursor: pointer;
  transition: background 0.2s;
}

.write-btn:hover {
  background: #e11d48;
}
</style> 