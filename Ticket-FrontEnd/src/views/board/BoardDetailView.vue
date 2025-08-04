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
                  <span class="author-name">{{ post.postUser?.nickname || '알 수 없음' }}</span>
                  <div class="post-stats">
                    <span class="date">{{ formatDate(post.createdAt) }}</span>
                    <span class="views">조회 {{ post.viewCount?.toLocaleString() || 0 }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="post-actions">
              <span class="comments-count">댓글 {{ post.commentList?.length || 0 }}</span>
              <button class="action-btn more-btn">⋮</button>
            </div>
          </div>
        </div>

        <!-- 게시글 이미지 섹션 -->
        <div v-if="post.postImageUrl" class="post-image-section">
          <div class="image-container">
            <img 
              :src="post.postImageUrl" 
              :alt="post.title"
              class="post-image"
              @error="handleImageError"
              @load="handleImageLoad"
            />
          </div>
        </div>

        <!-- 게시글 내용 -->
        <div class="post-content">
          <div class="content-text" v-html="post.content"></div>
        </div>

        <!-- 작성자 섹션 -->
        <div class="author-section">
          <div class="profile-icon">👤</div>
          <span class="author-more">{{ post.postUser?.nickname || '알 수 없음' }}님의 게시글 더보기 ></span>
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
                          좋아요 {{ post.likeCount?.toLocaleString() || 0 }}
                        </span>
                        <span v-if="isLiking" class="like-loading">...</span>
                      </button>
                      <span class="comments-count">💬 댓글 {{ post.commentList?.length || 0 }}</span>
                    </div>
          
          <div class="engagement-right">
            <button class="action-btn">신고</button>
          </div>
        </div>

        <!-- 댓글 섹션 -->
        <CommentSection 
          :comments="post.commentList || []"
          :postId="route.params.id"
          @commentSubmit="handleCommentSubmit"
          @commentEdit="handleCommentEdit"
          @commentDelete="handleCommentDelete"
        />
        
        <!-- 작성자 전용 버튼 -->
        <div v-if="isAuthor" class="author-actions-section">
          <div class="action-buttons">
            <button class="edit-btn" @click="editPost">
              <span class="btn-icon">✏️</span>
              <span class="btn-text">글 수정</span>
            </button>
            <button class="delete-btn" @click="deletePost">
              <span class="btn-icon">🗑️</span>
              <span class="btn-text">글 삭제</span>
            </button>
          </div>
          <div class="action-info">
            <span class="info-text">작성자만 볼 수 있는 메뉴입니다</span>
          </div>
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
import { useAuthStore } from '@/stores/auth.js';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

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
  // API 응답의 boardId를 BOARD_TYPES 상수로 매핑
  let boardType = post.value.boardType;
  
  if (post.value.boardId === 'FREE') {
    boardType = BOARD_TYPES.FREE;
  } else if (post.value.boardId === 'GROUP') {
    boardType = BOARD_TYPES.GROUP;
  } else if (post.value.boardId === 'NOTICE') {
    boardType = BOARD_TYPES.NOTICE;
  }
  
  return BOARD_TYPE_NAMES[boardType] || '일반';
});

// 게시글 상세 조회 (로딩 상태 포함)
const loadPostDetail = async (showLoading = true) => {
  const postId = route.params.id;
  if (!postId) {
    error.value = '게시글 ID가 없습니다.';
    return;
  }

  if (showLoading) {
    isLoading.value = true;
  }
  error.value = '';
  
  try {
    const result = await boardAPI.getPostDetail(postId);
    
    console.log('게시글 상세 조회 결과:', result);
    
    // 백엔드에서 직접 PostDetailResponse 객체를 반환하므로 result 자체가 데이터
    if (result) {
      post.value = result;
      console.log('게시글 상세 조회 성공:', result);
      console.log('이미지 URL 상세 분석:', {
        postImageUrl: result.postImageUrl,
        imageUrl: result.imageUrl,
        hasPostImage: !!result.postImageUrl,
        hasUserImage: !!result.imageUrl,
        postImageUrlType: typeof result.postImageUrl,
        imageUrlType: typeof result.imageUrl
      });
      
      // 작성자 판별 (로그인된 사용자와 비교)
      if (authStore.isAuthenticated && authStore.user) {
        // userId 또는 nickName으로 작성자 판별
        const isUserIdMatch = post.value.postUser?.userId === authStore.user.userId;
        const isNicknameMatch = post.value.postUser?.nickname === authStore.user.nickName;
        
        isAuthor.value = isUserIdMatch || isNicknameMatch;
        
        console.log('작성자 판별:', {
          postAuthor: post.value.postUser,
          currentUser: authStore.user,
          isUserIdMatch,
          isNicknameMatch,
          isAuthor: isAuthor.value
        });
      }
      
      // 좋아요 상태는 서버에서 받아와야 하지만, 현재는 기본값으로 설정
      // TODO: 서버에서 사용자의 좋아요 상태를 함께 반환하도록 수정 필요
      isLiked.value = false; // 기본값
    } else {
      if (showLoading) {
        error.value = '게시글을 불러올 수 없습니다.';
      }
      console.error('게시글 상세 조회 실패: 데이터가 없습니다.');
    }
  } catch (err) {
    if (showLoading) {
      error.value = '게시글을 불러오는 중 오류가 발생했습니다.';
    }
    console.error('게시글 상세 조회 중 오류:', err);
  } finally {
    if (showLoading) {
      isLoading.value = false;
    }
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

// 이미지 로드 성공 핸들러
const handleImageLoad = (event) => {
  console.log('이미지 로드 성공:', {
    src: event.target.src,
    alt: event.target.alt,
    naturalWidth: event.target.naturalWidth,
    naturalHeight: event.target.naturalHeight,
    currentSrc: event.target.currentSrc
  });
};

// 이미지 에러 핸들러
const handleImageError = (event) => {
  console.error('이미지 로드 실패:', {
    src: event.target.src,
    alt: event.target.alt,
    naturalWidth: event.target.naturalWidth,
    naturalHeight: event.target.naturalHeight,
    currentSrc: event.target.currentSrc
  });
  event.target.style.display = 'none';
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

const goToList = () => {
  // 게시판 타입에 따라 올바른 탭으로 이동
  let tabType = 'free'; // 기본값
  
  // API 응답의 boardId를 기반으로 탭 결정
  if (post.value.boardId === 'FREE') {
    tabType = 'free';
  } else if (post.value.boardId === 'GROUP') {
    tabType = 'group';
  } else if (post.value.boardId === 'NOTICE') {
    tabType = 'notice';
  }
  
  router.push({
    path: '/bulletin',
    query: { tab: tabType }
  });
};

const toggleLike = async () => {
  if (isLiking.value) return; // 중복 클릭 방지
  
  // 로그인 확인
  if (!authStore.isAuthenticated) {
    alert('로그인이 필요한 서비스입니다.');
    return;
  }
  
  isLiking.value = true;
  
  try {
    const result = await boardAPI.togglePostLike(route.params.id);
    
    console.log('좋아요 토글 결과:', result);
    
    // 백엔드에서 PostLikeResponse 객체를 직접 반환
    if (result) {
      // 좋아요 수 업데이트
      post.value.likeCount = result.likeCount;
      
      // 좋아요 상태 토글 (서버에서 현재 사용자의 좋아요 상태를 반환해야 함)
      // 현재는 간단히 토글
      isLiked.value = !isLiked.value;
      
      console.log('좋아요 토글 성공:', result);
    } else {
      console.error('좋아요 토글 실패: 응답이 없습니다.');
    }
  } catch (error) {
    console.error('좋아요 토글 중 오류:', error);
    alert('좋아요 처리 중 오류가 발생했습니다.');
  } finally {
    isLiking.value = false;
  }
};

const editPost = () => {
  console.log('글 수정');
  // 게시글 수정 페이지로 이동 (게시판 타입은 변경하지 않으므로 전달하지 않음)
  router.push(`/bulletin/edit/${route.params.id}`);
};

const deletePost = () => {
  isDeleteModalVisible.value = true;
};

const handleDeleteConfirm = async () => {
  isDeleting.value = true;
  
  try {
    const result = await boardAPI.deletePost(route.params.id);
    
    console.log('게시글 삭제 결과:', result);
    
    // 백엔드에서 PostResponse 객체를 직접 반환
    if (result && result.success) {
      alert(result.message || '게시글이 성공적으로 삭제되었습니다.');
      // 성공 시 해당 게시판 목록으로 이동
      goToList();
    } else {
      alert(result?.message || '게시글 삭제에 실패했습니다.');
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

const handleCommentSubmit = async (newCommentData) => {
  // 댓글 작성 완료 후 즉시 화면에 추가
  console.log('댓글 작성 완료:', newCommentData);
  
  // 새 댓글을 목록 맨 위에 추가
  if (post.value.commentList) {
    post.value.commentList.unshift(newCommentData);
  } else {
    post.value.commentList = [newCommentData];
  }
  
  // 댓글 수 업데이트
  const commentCount = post.value.commentList.length;
  
  // 애니메이션 완료 후 isNew 플래그 제거
  setTimeout(() => {
    if (post.value.commentList && post.value.commentList.length > 0) {
      const firstComment = post.value.commentList[0];
      if (firstComment.isNew) {
        delete firstComment.isNew;
      }
    }
  }, 2000); // 2초 후 애니메이션 효과 제거
  
  // 백그라운드에서 서버 데이터와 동기화 (실제 댓글 ID 획득)
  try {
    await loadPostDetail(false); // 로딩 상태 없이 백그라운드에서만 실행
    console.log('댓글 작성 후 서버 데이터 동기화 완료');
  } catch (error) {
    console.error('댓글 작성 후 서버 동기화 실패:', error);
  }
};

const handleCommentEdit = async (editData) => {
  // 댓글 수정 완료 후 즉시 화면에 반영
  console.log('댓글 수정 완료:', editData);
  
  // 해당 댓글 찾아서 내용 업데이트
  if (post.value.commentList) {
    const commentIndex = post.value.commentList.findIndex(
      comment => comment.id === editData.commentId || comment.commentId === editData.commentId
    );
    
    if (commentIndex !== -1) {
      post.value.commentList[commentIndex].content = editData.content;
      post.value.commentList[commentIndex].updatedAt = new Date().toISOString();
    }
  }
  
  // 백그라운드에서 서버 데이터와 동기화
  try {
    await loadPostDetail(false); // 로딩 상태 없이 백그라운드에서만 실행
    console.log('댓글 수정 후 서버 데이터 동기화 완료');
  } catch (error) {
    console.error('댓글 수정 후 서버 동기화 실패:', error);
  }
};

const handleCommentDelete = async (deleteData) => {
  // 댓글 삭제 완료 후 즉시 화면에서 제거
  console.log('댓글 삭제 완료:', deleteData);
  
  // 해당 댓글을 목록에서 제거
  if (post.value.commentList) {
    const commentIndex = post.value.commentList.findIndex(
      comment => comment.id === deleteData.commentId || comment.commentId === deleteData.commentId
    );
    
    if (commentIndex !== -1) {
      post.value.commentList.splice(commentIndex, 1);
    }
  }
  
  // 백그라운드에서 서버 데이터와 동기화
  try {
    await loadPostDetail(false); // 로딩 상태 없이 백그라운드에서만 실행
    console.log('댓글 삭제 후 서버 데이터 동기화 완료');
  } catch (error) {
    console.error('댓글 삭제 후 서버 동기화 실패:', error);
  }
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

/* 게시글 이미지 섹션 */
.post-image-section {
  padding: 0 30px;
}

.image-container {
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
  margin: 20px 0;
}

.post-image {
  max-width: 400px;
  max-height: 280px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  object-fit: cover;
  transition: transform 0.3s ease;
}

.post-image:hover {
  transform: scale(1.05);
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
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.edit-btn, .delete-btn {
  padding: 12px 24px;
  border: 2px solid;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  min-width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.edit-btn {
  background: linear-gradient(135deg, #e11d48 0%, #f97316 100%);
  color: white;
  border-color: #e11d48;
  box-shadow: 0 2px 8px rgba(225, 29, 72, 0.2);
}

.edit-btn:hover {
  background: linear-gradient(135deg, #be185d 0%, #ea580c 100%);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(225, 29, 72, 0.3);
}

.edit-btn:active {
  transform: translateY(0);
}

.delete-btn {
  background: white;
  color: #dc2626;
  border-color: #dc2626;
  box-shadow: 0 2px 8px rgba(220, 38, 38, 0.1);
}

.delete-btn:hover {
  background: #fef2f2;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.2);
  border-color: #b91c1c;
}

.delete-btn:active {
  transform: translateY(0);
}

.btn-icon {
  font-size: 16px;
  transition: transform 0.2s ease;
}

.edit-btn:hover .btn-icon {
  transform: rotate(-10deg);
}

.delete-btn:hover .btn-icon {
  transform: scale(1.1);
}

.btn-text {
  font-weight: 600;
  letter-spacing: 0.5px;
}

.action-info {
  margin-top: 8px;
  text-align: center;
  color: #6b7280;
  font-size: 12px;
  opacity: 0.8;
}

.info-text {
  padding: 4px 8px;
  background: rgba(107, 114, 128, 0.1);
  border-radius: 4px;
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
  
  .post-image-section {
    padding: 0 20px;
  }
  
  .post-image {
    max-width: 320px;
    max-height: 200px;
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
  
  .action-buttons {
    flex-direction: column;
    width: 100%;
    gap: 8px;
  }
  
  .edit-btn, .delete-btn {
    min-width: 100%;
    padding: 12px 16px;
    font-size: 16px;
  }
  
  .btn-icon {
    font-size: 18px;
  }
  
  .action-info {
    margin-top: 12px;
  }
}
</style> 