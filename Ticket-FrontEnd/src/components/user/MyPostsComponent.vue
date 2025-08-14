<template>
  <div class="my-posts-content">
    <div class="container">
      <div class="content-header">
        <h1>내가 쓴 게시글</h1>
        <div class="pagination-info">
          <span>총 {{ totalPosts }}개의 게시글</span>
        </div>
      </div>
      
      <br>
      <!-- 게시글 목록 테이블 -->
      <div class="posts-table-container">
        <table class="posts-table">
          <thead>
            <tr>
              <th class="checkbox-column">
                <input 
                  type="checkbox" 
                  :checked="allSelected" 
                  @change="toggleAllPosts"
                  class="select-all-checkbox"
                >
              </th>
              <th class="number-column">번호</th>
              <th class="title-column">제목</th>
              <th class="date-column">작성일</th>
              <th class="views-column">조회수</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="post in posts" :key="post.postId" class="post-row">
              <td class="checkbox-column">
                <input 
                  type="checkbox" 
                  :checked="selectedPosts.includes(post.postId)"
                  @change="togglePostSelection(post.postId)"
                  class="post-checkbox"
                >
              </td>
              <td class="number-column">{{ post.postId }}</td>
              <td class="title-column">
                <router-link :to="`/bulletin/detail/${post.postId}`" class="post-title">
                  {{ post.title }}
                </router-link>
              </td>
              <td class="date-column">{{ formatDate(post.createdAt) }}</td>
              <td class="views-column">{{ post.viewCount }}</td>
            </tr>
          </tbody>
        </table>

        <!-- 게시글이 없을 때 -->
        <div v-if="posts.length === 0" class="empty-state">
          <div class="empty-icon">📝</div>
          <h3>작성한 게시글이 없습니다</h3>
          <p>첫 번째 게시글을 작성해보세요!</p>
        </div>
      </div>

      <!-- 게시글 관리 버튼 -->
      <div class="table-actions">
        <div class="action-buttons">
          <button 
            class="delete-btn" 
            :disabled="selectedPosts.length === 0"
            @click="deletePosts"
          >
            삭제 ({{ selectedPosts.length }})
          </button>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="pagination-container">
        
        <div class="pagination-controls">
          <button 
            class="pagination-btn" 
            :disabled="currentPage === 1"
            @click="changePage(currentPage - 1)"
          >
            이전
          </button>
          <div class="page-numbers">
            <button 
              v-for="page in visiblePages" 
              :key="page"
              class="page-btn"
              :class="{ active: page === currentPage }"
              @click="changePage(page)"
            >
              {{ page }}
            </button>
          </div>
          <button 
            class="pagination-btn" 
            :disabled="currentPage === totalPages"
            @click="changePage(currentPage + 1)"
          >
            다음
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { API_CONFIG } from '@/config/api.config'
import { formatDate } from '@/utils/dateUtils'
import http from '@/utils/http'
import router from '@/router'

// 게시글 데이터 (실제로는 API에서 가져올 데이터)
const posts = ref([])

// 선택된 게시글 관리
const selectedPosts = ref([])

// 전체 선택 상태
const allSelected = computed(() => {
  return posts.value.length > 0 && selectedPosts.value.length === posts.value.length
})

// 게시글 선택 토글
const togglePostSelection = (postId) => {
  const index = selectedPosts.value.indexOf(postId)
  if (index > -1) {
    selectedPosts.value.splice(index, 1)
  } else {
    selectedPosts.value.push(postId)
  }
}

// 전체 게시글 선택/해제
const toggleAllPosts = () => {
  if (allSelected.value) {
    selectedPosts.value = []
  } else {
    selectedPosts.value = posts.value.map(post => post.postId)
  }
}

// 페이지네이션 관련
const currentPage = ref(1)
const postsPerPage = 10
const totalPosts = computed(() => posts.value.length)
const totalPages = computed(() => Math.ceil(totalPosts.value / postsPerPage))

// 보여줄 페이지 번호들
const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(1, currentPage.value - 2)
  const end = Math.min(totalPages.value, currentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  return pages
})

// 페이지 변경
const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    currentPage.value = page
  }
}

const fetchDeletePost = async (postId) => {
  try {
    await http.delete(API_CONFIG.BOARD.DELETE(postId));
    return { success: true };
  } catch (error) {
    console.error('게시글 삭제 중 오류 발생:', error);
    return { success: false, error };
  }
};

// 선택된 게시글 삭제
const deletePosts = async () => {
  // Proxy 객체에서 실제 배열을 추출
  const selectedPostIds = [...selectedPosts.value];
  
  if (selectedPostIds.length === 0) return;
  
  if (!confirm(`선택한 ${selectedPostIds.length}개의 게시글을 삭제하시겠습니까?`)) {
    return;
  }

  const results = [];
  
  // 순차적으로 삭제 요청 보내기
  for (const postId of selectedPostIds) {
    const result = await fetchDeletePost(postId);
    results.push({ postId, ...result });
  }

  // 성공/실패 결과 확인
  const successCount = results.filter(r => r.success).length;
  const failCount = results.length - successCount;
  
  // 목록 새로고침
  await fetchPosts();
  
  // 선택 목록 초기화
  selectedPosts.value = [];
  
  // 결과 알림
  if (failCount === 0) {
    alert(`${successCount}개의 게시글이 성공적으로 삭제되었습니다.`);
  } else if (successCount === 0) {
    alert('게시글 삭제에 실패했습니다. 다시 시도해주세요.');
  } else {
    alert(`${successCount}개 성공, ${failCount}개 실패했습니다.`);
  }
};

// 글쓰기 페이지로 이동
const goToWrite = () => {
  // 실제로는 글쓰기 페이지로 라우팅
  alert('글쓰기 페이지로 이동합니다.')
  // router.push('/bulletin/write') 등으로 구현
}

const isLoading = ref(false)

const fetchPosts = async () => {
  try {
    isLoading.value = true
    const response = await http.get(API_CONFIG.USER.POSTS)
    posts.value = response.data
  } catch (error) {
    console.error('게시글 조회 중 오류 발생:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchPosts()
})
</script>

<style scoped>
.my-posts-content {
  width: 100%;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-header h1 {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
}

.table-actions {
  display: flex;
  justify-content: flex-end;
  margin: 20px 0;
  padding: 0 10px;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.delete-btn {
  padding: 10px 20px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.delete-btn:hover:not(:disabled) {
  background: #c82333;
  transform: translateY(-1px);
}

.delete-btn:disabled {
  background: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
  transform: none;
}

.write-btn {
  padding: 10px 20px;
  background: var(--theme-primary, #ff6b35);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.write-btn:hover {
  background: var(--theme-accent, #e55a2e);
  transform: translateY(-1px);
}

.posts-table-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 30px;
}

.posts-table {
  width: 100%;
  border-collapse: collapse;
}

.posts-table th {
  background: #f8f9fa;
  padding: 15px 12px;
  text-align: center;
  font-weight: bold;
  color: #333;
  border-bottom: 1px solid #e9ecef;
  font-size: 14px;
}

.posts-table td {
  padding: 15px 12px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  text-align: center;
}

.posts-table tr:hover {
  background: #f8f9fa;
}

.posts-table tr:last-child td {
  border-bottom: none;
}

.checkbox-column {
  width: 50px;
  text-align: center;
}

.number-column {
  width: 80px;
  color: black;
  text-align: center;
}

.title-column {
  flex: 1;
  text-align: center;
}

.date-column {
  width: 120px;
  color: black;
  text-align: center;
}

.views-column {
  width: 80px;
  color: black;
  text-align: center;
}

.select-all-checkbox,
.post-checkbox {
  width: 16px;
  height: 16px;
  cursor: pointer;
  accent-color: var(--theme-primary, #ff6b35);
  background-color: white;
  border: 2px solid #ddd;
  border-radius: 3px;
}

.post-title {
  color: black;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.post-title:hover {
  color: var(--theme-primary, #ff6b35);
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
}

.pagination-info {
  color: #666;
  font-size: 14px;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination-btn {
  padding: 8px 16px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #f8f9fa;
  border-color: var(--theme-primary, #ff6b35);
}

.pagination-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-numbers {
  display: flex;
  gap: 5px;
}

.page-btn {
  padding: 8px 12px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  min-width: 40px;
  transition: all 0.3s ease;
}

.page-btn:hover {
  background: #f8f9fa;
  border-color: var(--theme-primary, #ff6b35);
}

.page-btn.active {
  background: var(--theme-primary, #ff6b35);
  color: white;
  border-color: var(--theme-primary, #ff6b35);
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 20px;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

@media (max-width: 768px) {
  .table-actions {
    justify-content: center;
    margin: 15px 0;
    padding: 0 5px;
  }
  
  .action-buttons {
    gap: 8px;
  }
  
  .delete-btn,
  .write-btn {
    padding: 8px 16px;
    font-size: 12px;
  }
  
  .posts-table {
    font-size: 12px;
  }
  
  .posts-table th,
  .posts-table td {
    padding: 10px 8px;
  }
  
  .checkbox-column {
    width: 40px;
  }
  
  .number-column {
    width: 60px;
  }
  
  .date-column {
    width: 90px;
  }
  
  .views-column {
    width: 60px;
  }
  
  .pagination-container {
    flex-direction: column;
    gap: 15px;
    align-items: center;
  }
  
  .page-numbers {
    gap: 3px;
  }
  
  .page-btn {
    padding: 6px 10px;
    min-width: 35px;
    font-size: 12px;
  }
}
</style> 