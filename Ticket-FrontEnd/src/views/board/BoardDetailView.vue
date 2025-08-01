<template>
  <div class="board-detail-root">
    <div class="container">
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>게시글을 불러오는 중...</p>
      </div>
      
      <!-- 에러 상태 -->
      <div v-else-if="error" class="error-container">
        <p class="error-message">{{ error }}</p>
        <button @click="loadPostDetail" class="retry-btn">다시 시도</button>
      </div>
      
      <!-- 게시글 상세 내용 -->
      <div v-else>
        <!-- 상단 네비게이션 -->
        <div class="top-navigation">
          <div class="nav-links">
            <button class="nav-btn" @click="goToPrevious">이전글</button>
            <button class="nav-btn" @click="goToNext">다음글</button>
            <button class="nav-btn list-btn" @click="goToList">목록</button>
          </div>
        </div>

        <!-- 게시글 헤더 -->
        <div class="post-header">
          <div class="breadcrumb">
            <span class="board-category" @click="goToBoardList">{{ boardTypeName }} 게시판 ></span>
          </div>
          
          <h1 class="post-title">{{ post.title }}</h1>
          
          <div class="post-meta">
            <div class="author-info">
              <div class="profile-section">
                <div class="profile-icon">👤</div>
                <div class="author-details">
                  <span class="author-name">{{ post.author?.nickname || '알 수 없음' }}</span>
                  <div class="post-stats">
                    <span class="date">{{ formatDate(post.createdAt) }}</span>
                    <span class="views">조회 {{ post.views?.toLocaleString() || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="post-actions">
              <span class="comments-count">댓글 {{ post.comments?.length || 0 }}</span>
              <button class="action-btn more-btn">⋮</button>
            </div>
          </div>
        </div>

        <!-- 게시글 내용 -->
        <div class="post-content">
          <div class="content-text" v-html="post.content"></div>
        </div>

        <!-- 작성자 섹션 -->
        <div class="author-section">
          <div class="profile-icon">👤</div>
          <span class="author-more">{{ post.author?.nickname || '알 수 없음' }}님의 게시글 더보기 ></span>
        </div>

        <!-- 참여 섹션 -->
        <div class="engagement-section">
                              <div class="engagement-left">
                      <button 
                        class="like-btn" 
                        :class="{ 'liked': isLiked, 'liking': isLiking }"
                        @click="toggleLike"
                        :disabled="isLiking"
                      >
                        <span class="like-icon" :class="{ 'liked': isLiked }">
                          {{ isLiked ? '❤️' : '🤍' }}
                        </span>
                        <span class="like-count">
                          좋아요 {{ post.likes?.toLocaleString() || 0 }}
                        </span>
                        <span v-if="isLiking" class="like-loading">...</span>
                      </button>
                      <span class="comments-count">💬 댓글 {{ post.comments?.length || 0 }}</span>
                    </div>
          
          <div class="engagement-right">
            <button class="action-btn">신고</button>
          </div>
        </div>

        <!-- 댓글 섹션 -->
                            <CommentSection 
                      :comments="post.comments || []"
                      :postId="route.params.id"
                      @commentSubmit="handleCommentSubmit"
                      @commentEdit="handleCommentEdit"
                      @commentDelete="handleCommentDelete"
                    />
        
        <!-- 작성자 전용 버튼 -->
        <div v-if="isAuthor" class="author-actions-section">
          <button class="edit-btn" @click="editPost">글 수정</button>
          <button class="delete-btn" @click="deletePost">글 삭제</button>
        </div>
      </div>
    </div>
  </div>
  
  <!-- 삭제 확인 모달 -->
  <DeleteConfirmModal
    :isVisible="isDeleteModalVisible"
    :isDeleting="isDeleting"
    @confirm="handleDeleteConfirm"
    @cancel="handleDeleteCancel"
  />
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import CommentSection from '@/components/board/CommentSection.vue';
import DeleteConfirmModal from '@/components/board/DeleteConfirmModal.vue';
import { boardAPI, BOARD_TYPES, BOARD_TYPE_NAMES } from '@/api/board.js';

const route = useRoute();
const router = useRouter();

// 상태 관리
const isLoading = ref(false);
const error = ref('');
const post = ref({});
const isAuthor = ref(false);
const isDeleteModalVisible = ref(false);
const isDeleting = ref(false);
const isLiking = ref(false);
const isLiked = ref(false);

// 게시판 타입 이름
const boardTypeName = computed(() => {
  return BOARD_TYPE_NAMES[post.value.boardType] || '일반';
});

// 게시글 상세 조회
const loadPostDetail = async () => {
  const postId = route.params.id;
  if (!postId) {
    error.value = '게시글 ID가 없습니다.';
    return;
  }

  isLoading.value = true;
  error.value = '';
  
  try {
    const result = await boardAPI.getPostDetail(postId);
    
    if (result.success) {
      post.value = result.data;
      console.log('게시글 상세 조회 성공:', result.data);
      
      // 작성자 판별 (임시로 하드코딩, 실제로는 로그인된 사용자와 비교)
      // TODO: 실제 로그인된 사용자 정보와 비교
      isAuthor.value = post.value.author?.nickname === '닉네임';
      
      // 좋아요 상태 초기화 (임시로 랜덤하게 설정, 실제로는 API에서 받아와야 함)
      // TODO: 실제 좋아요 상태를 API에서 받아와서 설정
      // 현재는 테스트를 위해 좋아요 수가 0보다 크면 좋아요를 눌렀다고 가정
      isLiked.value = (post.value.likes || 0) > 0;
    } else {
      error.value = result.message;
      console.error('게시글 상세 조회 실패:', result.message);
    }
  } catch (err) {
    error.value = '게시글을 불러오는 중 오류가 발생했습니다.';
    console.error('게시글 상세 조회 중 오류:', err);
  } finally {
    isLoading.value = false;
  }
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 컴포넌트 마운트 시 게시글 조회
onMounted(() => {
  loadPostDetail();
});

// 네비게이션 함수들
const goToPrevious = () => {
  console.log('이전글');
  // TODO: 이전글 로직
};

const goToNext = () => {
  console.log('다음글');
  // TODO: 다음글 로직
};

const goToBoardList = () => {
  // 게시판 타입에 따라 올바른 탭으로 이동
  let tabType = 'free'; // 기본값
  
  switch (post.value.boardType) {
    case BOARD_TYPES.FREE:
      tabType = 'free';
      break;
    case BOARD_TYPES.GROUP:
      tabType = 'group';
      break;
    case BOARD_TYPES.NOTICE:
      tabType = 'notice';
      break;
    default:
      tabType = 'free';
  }
  
  router.push({
    path: '/bulletin',
    query: { tab: tabType }
  });
};

const toggleLike = async () => {
  if (isLiking.value) return; // 중복 클릭 방지
  
  isLiking.value = true;
  
  try {
    const result = await boardAPI.togglePostLike(route.params.id);
    
    if (result.success) {
      // 좋아요 수 업데이트
      post.value.likes = result.data.likeCount;
      
      // 좋아요 상태 토글 (현재 좋아요 수가 이전보다 많으면 좋아요 추가, 적으면 좋아요 취소)
      const previousLikes = (post.value.likes || 0) - (isLiked.value ? 1 : 0);
      isLiked.value = result.data.likeCount > previousLikes;
      
      console.log('좋아요 토글 성공:', result.data);
    } else {
      console.error('좋아요 토글 실패:', result.message);
      // 에러 시 사용자에게 알림 (선택사항)
      // alert(result.message);
    }
  } catch (error) {
    console.error('좋아요 토글 중 오류:', error);
    // 에러 시 사용자에게 알림 (선택사항)
    // alert('좋아요 처리 중 오류가 발생했습니다.');
  } finally {
    isLiking.value = false;
  }
};

const editPost = () => {
  console.log('글 수정');
  // 게시글 수정 페이지로 이동
  router.push(`/bulletin/edit/${route.params.id}`);
};

const deletePost = () => {
  isDeleteModalVisible.value = true;
};

const handleDeleteConfirm = async () => {
  isDeleting.value = true;
  
  try {
    const result = await boardAPI.deletePost(route.params.id);
    
    if (result.success) {
      alert(result.message || '게시글이 성공적으로 삭제되었습니다.');
      // 성공 시 해당 게시판 목록으로 이동
      goToList();
    } else {
      alert(result.message || '게시글 삭제에 실패했습니다.');
    }
  } catch (error) {
    console.error('게시글 삭제 실패:', error);
    alert('게시글 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
  } finally {
    isDeleting.value = false;
    isDeleteModalVisible.value = false;
  }
};

const handleDeleteCancel = () => {
  isDeleteModalVisible.value = false;
};

const handleCommentSubmit = async (commentText) => {
  // 댓글 작성 완료 후 게시글을 다시 로드하여 최신 댓글 목록을 가져옴
  console.log('댓글 작성 완료:', commentText);
  
  // 게시글 상세 정보를 다시 로드
  await loadPostDetail();
};

const handleCommentEdit = async (editData) => {
  // 댓글 수정 완료 후 게시글을 다시 로드하여 최신 댓글 목록을 가져옴
  console.log('댓글 수정 완료:', editData);
  
  // 게시글 상세 정보를 다시 로드
  await loadPostDetail();
};

const handleCommentDelete = async (deleteData) => {
  // 댓글 삭제 완료 후 게시글을 다시 로드하여 최신 댓글 목록을 가져옴
  console.log('댓글 삭제 완료:', deleteData);
  
  // 게시글 상세 정보를 다시 로드
  await loadPostDetail();
};
</script>

<style scoped>
/* 로딩 스타일 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
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
  padding: 100px 20px;
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

.board-detail-root {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 상단 네비게이션 */
.top-navigation {
  padding: 20px 30px;
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
}

.nav-links {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.nav-btn {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.nav-btn:hover {
  background: #f3f4f6;
}

.list-btn {
  background: #e11d48;
  color: white;
  border-color: #e11d48;
}

.list-btn:hover {
  background: #be185d;
}

/* 게시글 헤더 */
.post-header {
  padding: 30px;
  border-bottom: 1px solid #e5e7eb;
}

.breadcrumb {
  margin-bottom: 16px;
}

.board-category {
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
}

.board-category:hover {
  color: #e11d48;
  text-decoration: underline;
}

.post-title {
  font-size: 24px;
  font-weight: bold;
  color: #1f2937;
  margin-bottom: 20px;
  line-height: 1.4;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.author-info {
  flex: 1;
}

.profile-section {
  display: flex;
  align-items: center;
  gap: 12px;
}

.profile-icon {
  width: 40px;
  height: 40px;
  background: #f3f4f6;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.author-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  font-weight: 600;
  color: #1f2937;
  font-size: 16px;
}

.post-stats {
  display: flex;
  gap: 12px;
  font-size: 14px;
  color: #6b7280;
}

.post-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-btn {
  padding: 6px 12px;
  border: none;
  background: none;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}

.action-btn:hover {
  background: #f3f4f6;
}

.more-btn {
  font-size: 18px;
  padding: 4px 8px;
}

/* 게시글 내용 */
.post-content {
  padding: 30px;
  border-bottom: 1px solid #e5e7eb;
}

.content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #374151;
  white-space: pre-line;
}

/* 작성자 섹션 */
.author-section {
  padding: 20px 30px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.author-more {
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
}

.author-more:hover {
  color: #e11d48;
}

/* 참여 섹션 */
.engagement-section {
  padding: 20px 30px;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.engagement-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.like-btn {
  background: none;
  border: none;
  color: #6b7280;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 20px;
  transition: all 0.3s ease;
  font-size: 14px;
  position: relative;
  min-width: 80px;
}

.like-btn:hover:not(:disabled) {
  background: #fef2f2;
  color: #dc2626;
  transform: translateY(-1px);
}

.like-btn.liked {
  color: #dc2626;
  background: #fef2f2;
}

.like-btn.liked:hover:not(:disabled) {
  background: #fee2e2;
  transform: translateY(-1px);
}

.like-btn:disabled {
  cursor: not-allowed;
  opacity: 0.7;
}

.like-btn.liking {
  pointer-events: none;
}

.like-icon {
  font-size: 16px;
  transition: all 0.3s ease;
}

.like-icon.liked {
  animation: heartBeat 0.6s ease-in-out;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  25% { transform: scale(1.2); }
  50% { transform: scale(0.9); }
  75% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.like-loading {
  position: absolute;
  right: 8px;
  font-size: 12px;
  color: #9ca3af;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.like-count {
  font-size: 14px;
  color: #374151;
}

.comments-count {
  font-size: 14px;
  color: #6b7280;
}

.engagement-right {
  display: flex;
  gap: 12px;
}

/* 작성자 전용 버튼 */
.author-actions-section {
  padding: 20px 30px;
  border-top: 1px solid #e5e7eb;
  background: #f9fafb;
  display: flex;
  justify-content: center;
  gap: 12px;
}

.edit-btn, .delete-btn {
  padding: 10px 20px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 100px;
}

.edit-btn {
  background: #e11d48;
  color: white;
  border-color: #e11d48;
}

.edit-btn:hover {
  background: #be185d;
}

.delete-btn {
  background: white;
  color: #dc2626;
  border-color: #dc2626;
}

.delete-btn:hover {
  background: #fef2f2;
}

/* 반응형 */
@media (max-width: 768px) {
  .container {
    margin: 0 10px;
    border-radius: 0;
  }
  
  .post-header,
  .post-content {
    padding: 20px;
  }
  
  .post-title {
    font-size: 20px;
  }
  
  .post-meta {
    flex-direction: column;
    gap: 16px;
  }
  
  .engagement-section {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  /* 모바일에서 작성자 버튼 섹션 */
  .author-actions-section {
    padding: 15px 20px;
  }
  
  .edit-btn, .delete-btn {
    min-width: 80px;
    padding: 8px 16px;
  }
}
</style> 