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
            <tr v-for="post in posts" :key="post.id" class="post-row">
              <td class="checkbox-column">
                <input 
                  type="checkbox" 
                  :checked="selectedPosts.includes(post.id)"
                  @change="togglePostSelection(post.id)"
                  class="post-checkbox"
                >
              </td>
              <td class="number-column">{{ post.id }}</td>
              <td class="title-column">
                <router-link :to="`/bulletin/${post.id}`" class="post-title">
                  {{ post.title }}
                </router-link>
              </td>
              <td class="date-column">{{ post.createdDate }}</td>
              <td class="views-column">{{ post.views }}</td>
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
          <button class="write-btn" @click="goToWrite">
            글쓰기
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
import { ref, computed } from 'vue'

// 게시글 데이터 (실제로는 API에서 가져올 데이터)
const posts = ref([
  {
    id: 147,
    title: '야구 경기 관람 후기',
    createdDate: '2025.07.23',
    views: 20
  },
  {
    id: 146,
    title: 'SSG 랜더스 응원 후기',
    createdDate: '2025.07.23',
    views: 30
  },
  {
    id: 145,
    title: '문학경기장 방문 꿀팁',
    createdDate: '2025.07.22',
    views: 15
  },
  {
    id: 144,
    title: '야구 좌석 추천',
    createdDate: '2025.07.21',
    views: 8
  },
  {
    id: 143,
    title: '응모 당첨 후기',
    createdDate: '2025.07.20',
    views: 25
  }
])

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
    selectedPosts.value = posts.value.map(post => post.id)
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

// 선택된 게시글 삭제
const deletePosts = () => {
  if (selectedPosts.value.length === 0) return
  
  if (confirm(`선택한 ${selectedPosts.value.length}개의 게시글을 삭제하시겠습니까?`)) {
    // 선택된 게시글들을 posts 배열에서 제거
    posts.value = posts.value.filter(post => !selectedPosts.value.includes(post.id))
    // 선택 목록 초기화
    selectedPosts.value = []
    alert('게시글이 삭제되었습니다.')
  }
}

// 글쓰기 페이지로 이동
const goToWrite = () => {
  // 실제로는 글쓰기 페이지로 라우팅
  alert('글쓰기 페이지로 이동합니다.')
  // router.push('/bulletin/write') 등으로 구현
}
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