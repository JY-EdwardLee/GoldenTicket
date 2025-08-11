<template>
  <div class="comments-section">
    <div class="comments-header">
             <div class="sort-options">
         <button 
           class="sort-btn" 
           :class="{ active: sortType === 'oldest' }"
           @click="changeSortType('oldest')"
         >
           등록순
         </button>
         <button 
           class="sort-btn" 
           :class="{ active: sortType === 'newest' }"
           @click="changeSortType('newest')"
         >
           최신순
         </button>
         <button class="refresh-btn" @click="refreshComments">🔄</button>
       </div>
    </div>

         <!-- 댓글 목록 -->
     <div class="comments-list">
       <div 
         v-for="comment in sortedComments" 
         :key="comment.id || comment.commentId" 
         class="comment-item"
         :class="{ 'new-comment': comment.isNew }"
       >
        <div class="comment-header">
          <div class="profile-icon">
            <img 
              v-if="comment.commentUserUrl" 
              :src="comment.commentUserUrl" 
              :alt="comment.nickName || comment.author || '프로필'"
              class="profile-image"
              @error="handleProfileImageError"
            />
            <span v-else>👤</span>
          </div>
          <div class="comment-info">
            <span class="commenter-name" :class="{ 'author': isCommentAuthor(comment) }">
              {{ comment.nickName || comment.author || '알 수 없음' }}
              <span v-if="isCommentAuthor(comment)" class="author-badge">- 작성자</span>
            </span>
            <span class="comment-date">{{ formatCommentDate(comment.createdAt || comment.date) }}</span>
          </div>
        </div>
        
        <div class="comment-content">
          {{ comment.content }}
        </div>
        
        <div class="comment-actions">
          <button class="edit-btn" @click="openEditModal(comment)" v-if="isCommentAuthor(comment)">수정</button>
          <button class="delete-btn" @click="openDeleteModal(comment)" v-if="isCommentAuthor(comment)">삭제</button>
          <button class="like-btn" @click="toggleCommentLike(comment)">
            <span class="like-icon" :class="{ 'liked': comment.isLiked }">
              {{ comment.isLiked ? '❤️' : '🤍' }}
            </span>
            <span v-if="comment.likeCount > 0" class="like-count">{{ comment.likeCount }}</span>
          </button>
        </div>
        
        <!-- 답글 목록 -->
        <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
          <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
            <div class="reply-header">
              <div class="profile-icon small">
                <img 
                  v-if="reply.commentUserUrl" 
                  :src="reply.commentUserUrl" 
                  :alt="reply.nickName || reply.author || '프로필'"
                  class="profile-image"
                  @error="handleProfileImageError"
                />
                <span v-else>👤</span>
              </div>
              <div class="reply-info">
                <span class="replyer-name" :class="{ 'author': isCommentAuthor(reply) }">
                  {{ reply.nickName || reply.author || '알 수 없음' }}
                  <span v-if="isCommentAuthor(reply)" class="author-badge">- 작성자</span>
                </span>
                <span class="reply-date">{{ formatCommentDate(reply.createdAt || reply.date) }}</span>
              </div>
            </div>
            
            <div class="reply-content">
              {{ reply.content }}
            </div>
            
            <div class="reply-actions">
              <button class="edit-btn small" @click="openEditModal(reply)" v-if="isCommentAuthor(reply)">수정</button>
              <button class="delete-btn small" @click="openDeleteModal(reply)" v-if="isCommentAuthor(reply)">삭제</button>
              <button class="like-btn small" @click="toggleCommentLike(reply)">
                <span class="like-icon" :class="{ 'liked': reply.isLiked }">
                  {{ reply.isLiked ? '❤️' : '🤍' }}
                </span>
                <span v-if="reply.likeCount > 0" class="like-count">{{ reply.likeCount }}</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 댓글 작성 -->
    <div class="comment-write">
      <!-- 로그인한 사용자만 댓글 작성 가능 -->
      <div v-if="authStore.isAuthenticated" class="comment-input-section">
        <div class="input-container">
          <div class="user-label">{{ authStore.user?.nickName || authStore.user?.nickname || '사용자' }}</div>
          <textarea 
            v-model="newComment" 
            placeholder="댓글을 남겨보세요"
            class="comment-input"
            rows="3"
            @click="handleInputClick"
            @focus="handleInputFocus"
            @blur="handleInputBlur"
            :class="{ 'active': isInputActive }"
          ></textarea>
                     <div class="comment-actions-bottom">
             <div class="action-icons">
               <!-- 아이콘 버튼들 제거 -->
             </div>
            <button 
              class="submit-btn" 
              @click="submitComment"
              :disabled="!newComment.trim() || isSubmitting"
              :class="{ 'submitting': isSubmitting }"
            >
              <span v-if="isSubmitting" class="loading-text">
                <span class="loading-dots">등록 중</span>
              </span>
              <span v-else>등록</span>
            </button>
          </div>
        </div>
      </div>
      
      <!-- 비로그인 사용자 로그인 안내 -->
      <div v-else class="login-guide">
        <div class="login-guide-content">
          <div class="guide-icon">🔒</div>
          <div class="guide-text">
            <h3>로그인이 필요합니다</h3>
            <p>댓글을 작성하려면 로그인해주세요.</p>
          </div>
          <button class="login-btn" @click="goToLogin">로그인</button>
        </div>
      </div>
    </div>
    
    <!-- 댓글 수정 모달 -->
    <CommentEditModal
      :isVisible="isEditModalVisible"
      :comment="selectedComment"
      :postId="postId"
      @confirm="handleEditConfirm"
      @cancel="handleEditCancel"
    />
    
    <!-- 댓글 삭제 모달 -->
    <CommentDeleteModal
      :isVisible="isDeleteModalVisible"
      :comment="selectedComment"
      :isDeleting="isDeleting"
      @confirm="handleDeleteConfirm"
      @cancel="handleDeleteCancel"
    />
  </div>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { boardAPI, validateCommentData } from '@/api/board.js';
import { useAuthStore } from '@/stores/auth.js';
import CommentEditModal from './CommentEditModal.vue';
import CommentDeleteModal from './CommentDeleteModal.vue';

const props = defineProps({
  comments: {
    type: Array,
    required: true
  },
  postId: {
    type: [String, Number],
    required: true
  }
});

const emit = defineEmits(['commentSubmit', 'refresh']);

const router = useRouter();
const authStore = useAuthStore();
const newComment = ref('');
const isInputActive = ref(false);
const isSubmitting = ref(false);
const isEditModalVisible = ref(false);
const selectedComment = ref(null);
const isDeleteModalVisible = ref(false);
const isDeleting = ref(false);
const sortType = ref('oldest'); // 기본값: 등록순

// 정렬된 댓글 목록 계산
const sortedComments = computed(() => {
  if (!props.comments || !Array.isArray(props.comments)) {
    return [];
  }
  
  const comments = [...props.comments]; // 원본 배열 복사
  
  return comments.sort((a, b) => {
    const dateA = new Date(a.createdAt || a.date || 0);
    const dateB = new Date(b.createdAt || b.date || 0);
    
    if (sortType.value === 'oldest') {
      // 등록순: 오래된 것부터 (오름차순)
      return dateA - dateB;
    } else {
      // 최신순: 최신 것부터 (내림차순)
      return dateB - dateA;
    }
  });
});

// 정렬 타입 변경
const changeSortType = (type) => {
  sortType.value = type;
};

// 댓글 새로고침
const refreshComments = () => {
  // 부모 컴포넌트에 새로고침 이벤트 발생
  emit('refresh');
};

// 댓글 좋아요 상태 복원
const restoreCommentLikeStates = () => {
  if (!authStore.isAuthenticated) return;
  
  // 모든 댓글과 답글에 대해 좋아요 상태 복원
  const processComments = (comments) => {
    if (!comments || !Array.isArray(comments)) return;
    
    comments.forEach(comment => {
      const commentId = comment.id || comment.commentId;
      if (commentId) {
        const storageKey = `comment_like_${commentId}_${authStore.user?.userId || 'guest'}`;
        const storedLiked = localStorage.getItem(storageKey);
        
        if (storedLiked !== null) {
          comment.isLiked = storedLiked === 'true';
        }
      }
      
      // 답글도 처리
      if (comment.replies && Array.isArray(comment.replies)) {
        processComments(comment.replies);
      }
    });
  };
  
  processComments(props.comments);
};

// 댓글 작성자 권한 확인
const isCommentAuthor = (comment) => {
  // 로그인하지 않은 경우
  if (!authStore.isAuthenticated || !authStore.user) {
    console.log('로그인 상태 확인 실패:', {
      isAuthenticated: authStore.isAuthenticated,
      user: authStore.user
    });
    return false;
  }
  
  // 댓글의 userId와 현재 사용자의 userId 비교
  const commentUserId = comment.userId;
  const currentUserId = authStore.user.userId;
  
  return commentUserId === currentUserId;
};

const handleInputClick = () => {
  isInputActive.value = true;
};

const handleInputFocus = () => {
  isInputActive.value = true;
};

const handleInputBlur = () => {
  // 입력이 없으면 비활성화
  if (!newComment.value.trim()) {
    isInputActive.value = false;
  }
};

// 프로필 이미지 에러 핸들러
const handleProfileImageError = (event) => {
  console.error('프로필 이미지 로드 실패:', {
    src: event.target.src,
    alt: event.target.alt,
    naturalWidth: event.target.naturalWidth,
    naturalHeight: event.target.naturalHeight,
    currentSrc: event.target.currentSrc
  });
  event.target.style.display = 'none';
};



// 댓글 날짜 포맷팅 함수
const formatCommentDate = (dateString) => {
  if (!dateString) return '';
  
  try {
    const date = new Date(dateString);
    const now = new Date();
    const diffInMs = now - date;
    const diffInMinutes = Math.floor(diffInMs / (1000 * 60));
    const diffInHours = Math.floor(diffInMs / (1000 * 60 * 60));
    const diffInDays = Math.floor(diffInMs / (1000 * 60 * 60 * 24));
    
    // 1분 미만
    if (diffInMinutes < 1) {
      return '방금 전';
    }
    // 1시간 미만
    else if (diffInMinutes < 60) {
      return `${diffInMinutes}분 전`;
    }
    // 24시간 미만
    else if (diffInHours < 24) {
      return `${diffInHours}시간 전`;
    }
    // 7일 미만
    else if (diffInDays < 7) {
      return `${diffInDays}일 전`;
    }
    // 7일 이상 - 상세 날짜 표시
    else {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      
      // 올해가 아닌 경우 년도 포함
      if (year !== now.getFullYear()) {
        return `${year}.${month}.${day} ${hours}:${minutes}`;
      } else {
        return `${month}.${day} ${hours}:${minutes}`;
      }
    }
  } catch (error) {
    console.error('날짜 포맷팅 오류:', error);
    return dateString; // 원본 문자열 반환
  }
};



const submitComment = async () => {
  if (!newComment.value.trim()) {
    alert('댓글을 입력해주세요.');
    return;
  }

  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    alert('로그인이 필요한 서비스입니다. 로그인 후 다시 시도해주세요.');
    return;
  }

  // 클라이언트 측 유효성 검사
  const validation = validateCommentData({
    postId: props.postId,
    content: newComment.value
  });
  
  if (!validation.isValid) {
    alert(validation.errors.join('\n'));
    return;
  }

  isSubmitting.value = true;
  
  try {
    const result = await boardAPI.createComment(props.postId, {
      content: newComment.value.trim()
    });
    
    if (result.success) {
      // 성공 시 부모 컴포넌트에 댓글 작성 완료 알림 (새 댓글 정보 포함)
             const newCommentData = {
         commentId: Date.now(), // 임시 ID (서버에서 실제 ID를 반환하지 않으므로)
         content: newComment.value.trim(),
         createdAt: new Date().toISOString(),
         date: new Date().toISOString(), // 호환성을 위해 date 필드도 추가
         author: authStore.user?.nickName || authStore.user?.nickname || '사용자',
         nickName: authStore.user?.nickName || authStore.user?.nickname || '사용자',
         isAuthor: true,
         likeCount: 0,
         isLiked: false,
         isNew: true // 새 댓글 표시
       };
      
      emit('commentSubmit', newCommentData);
      newComment.value = '';
      isInputActive.value = false;
      
      // 성공 메시지 (선택사항)
      console.log('댓글 작성 성공:', result.message);
    } else {
      alert(result.message || '댓글 작성에 실패했습니다.');
    }
  } catch (error) {
    console.error('댓글 작성 실패:', error);
    
    // 권한 오류인 경우 로그인 안내
    if (error.response?.status === 403 || error.response?.status === 401) {
      alert('로그인이 필요한 서비스입니다. 로그인 후 다시 시도해주세요.');
    } else {
      alert('댓글 작성 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  } finally {
    isSubmitting.value = false;
  }
};

// 댓글 수정 모달 열기
const openEditModal = (comment) => {
  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    alert('로그인이 필요한 서비스입니다. 로그인 후 다시 시도해주세요.');
    return;
  }
  
  // 댓글 작성자 권한 확인
  if (!isCommentAuthor(comment)) {
    alert('본인이 작성한 댓글만 수정할 수 있습니다.');
    return;
  }
  
  selectedComment.value = comment;
  isEditModalVisible.value = true;
};

// 댓글 수정 완료
const handleEditConfirm = (editData) => {
  // 댓글 ID를 안전하게 가져오기
  const commentId = selectedComment.value.id || selectedComment.value.commentId;
  
  // 부모 컴포넌트에 댓글 수정 완료 알림
  emit('commentEdit', { ...editData, commentId: commentId });
  isEditModalVisible.value = false;
  selectedComment.value = null;
};

// 댓글 수정 취소
const handleEditCancel = () => {
  isEditModalVisible.value = false;
  selectedComment.value = null;
};

// 댓글 삭제 모달 열기
const openDeleteModal = (comment) => {
  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    alert('로그인이 필요한 서비스입니다. 로그인 후 다시 시도해주세요.');
    return;
  }
  
  // 댓글 작성자 권한 확인
  if (!isCommentAuthor(comment)) {
    alert('본인이 작성한 댓글만 삭제할 수 있습니다.');
    return;
  }
  
  selectedComment.value = comment;
  isDeleteModalVisible.value = true;
};

// 댓글 삭제 확인
const handleDeleteConfirm = async () => {
  if (!selectedComment.value) return;
  
  isDeleting.value = true;
  
  try {
    // 댓글 ID를 안전하게 가져오기
    const commentId = selectedComment.value.id || selectedComment.value.commentId;
    
    if (!commentId) {
      alert('댓글 ID를 찾을 수 없습니다.');
      return;
    }
    
    console.log('삭제할 댓글 ID:', commentId);
    const result = await boardAPI.deleteComment(commentId);
    
    if (result.success) {
      // 부모 컴포넌트에 댓글 삭제 완료 알림
      emit('commentDelete', { commentId: commentId });
      console.log('댓글 삭제 성공:', result.message);
    } else {
      alert(result.message || '댓글 삭제에 실패했습니다.');
    }
  } catch (error) {
    console.error('댓글 삭제 실패:', error);
    
    // 권한 오류인 경우 로그인 안내
    if (error.response?.status === 403 || error.response?.status === 401) {
      alert('로그인이 필요한 서비스입니다. 로그인 후 다시 시도해주세요.');
    } else {
      alert('댓글 삭제 중 오류가 발생했습니다. 다시 시도해주세요.');
    }
  } finally {
    isDeleting.value = false;
    isDeleteModalVisible.value = false;
    selectedComment.value = null;
  }
};

// 댓글 삭제 취소
const handleDeleteCancel = () => {
  isDeleteModalVisible.value = false;
  selectedComment.value = null;
};

// 댓글 좋아요 토글
const toggleCommentLike = async (comment) => {
  // 로그인 확인
  if (!authStore.isAuthenticated) {
    alert('로그인이 필요한 서비스입니다.');
    return;
  }
  
  try {
    // 댓글 ID를 안전하게 가져오기
    const commentId = comment.id || comment.commentId;
    
    if (!commentId) {
      console.error('댓글 ID를 찾을 수 없습니다.');
      return;
    }
    
    const result = await boardAPI.likeComment(commentId);
    
    if (result) {
      // 서버에서 반환하는 좋아요 상태 사용
      let newLikedState = false;
      
      if (result.isLiked !== undefined) {
        newLikedState = result.isLiked;
      } else if (result.liked !== undefined) {
        newLikedState = result.liked;
      } else if (result.userLiked !== undefined) {
        newLikedState = result.userLiked;
      } else {
        // 서버에서 좋아요 상태를 반환하지 않는 경우 토글
        newLikedState = !comment.isLiked;
      }
      
      // 상태 업데이트
      comment.isLiked = newLikedState;
      comment.likeCount = result.likeCount || 0;
      
      // localStorage에 저장
      const storageKey = `comment_like_${commentId}_${authStore.user?.userId || 'guest'}`;
      localStorage.setItem(storageKey, newLikedState.toString());
      
      console.log('댓글 좋아요 토글 성공:', {
        commentId: commentId,
        isLiked: newLikedState,
        likeCount: comment.likeCount,
        serverResponse: result,
        localStorageKey: storageKey
      });
    } else {
      console.error('댓글 좋아요 토글 실패: 응답이 없습니다.');
    }
  } catch (error) {
    console.error('댓글 좋아요 토글 중 오류:', error);
    alert('좋아요 처리 중 오류가 발생했습니다.');
  }
};

// 로그인 페이지로 이동
const goToLogin = () => {
  router.push('/');
};

// 댓글이 변경될 때마다 좋아요 상태 복원
watch(() => props.comments, () => {
  restoreCommentLikeStates();
}, { deep: true });

// authStore 상태 변화 감지
watch(() => authStore.user, (newUser) => {
  if (newUser && newUser.userId) {
    restoreCommentLikeStates();
  }
}, { deep: true });

// 컴포넌트 마운트 시 좋아요 상태 복원 및 authStore 상태 확인
onMounted(async () => {
  // authStore 상태 확인 및 필요시 사용자 정보 재조회
  if (authStore.isAuthenticated && (!authStore.user || !authStore.user.userId)) {
    try {
      await authStore.getUserInfo();
      console.log('사용자 정보 재조회 완료:', authStore.user);
    } catch (error) {
      console.error('사용자 정보 재조회 실패:', error);
    }
  }
  
  restoreCommentLikeStates();
});
</script>

<style scoped>
/* 댓글 섹션 */
.comments-section {
  padding: 30px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e5e7eb;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 12px;
}

.sort-btn {
  padding: 6px 12px;
  border: none;
  background: none;
  color: #6b7280;
  font-size: 14px;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.2s;
}

.sort-btn.active {
  color: #e11d48;
  font-weight: 600;
}

.sort-btn:hover {
  background: #f3f4f6;
}

.refresh-btn {
  padding: 6px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}

.refresh-btn:hover {
  background: #f3f4f6;
}

.comment-settings {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notification-toggle {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6b7280;
}

/* 토글 스위치 */
.toggle-switch {
  position: relative;
  display: inline-block;
  width: 40px;
  height: 20px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  transition: .4s;
  border-radius: 20px;
}

.toggle-slider:before {
  position: absolute;
  content: "";
  height: 16px;
  width: 16px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .toggle-slider {
  background-color: #e11d48;
}

input:checked + .toggle-slider:before {
  transform: translateX(20px);
}

/* 댓글 목록 */
.comments-list {
  margin-bottom: 30px;
}

.comment-item {
  padding: 20px 0;
  border-bottom: 1px solid #f3f4f6;
  transition: all 0.3s ease;
}

.comment-item.new-comment {
  animation: slideInFromTop 0.5s ease-out;
  background: linear-gradient(135deg, #fef7f0 0%, #fff5f5 100%);
  border-left: 4px solid #e11d48;
  margin-left: -4px;
  padding-left: 16px;
}

@keyframes slideInFromTop {
  0% {
    opacity: 0;
    transform: translateY(-20px);
    background: linear-gradient(135deg, #fef7f0 0%, #fff5f5 100%);
  }
  50% {
    opacity: 0.8;
    transform: translateY(-5px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
    background: linear-gradient(135deg, #fef7f0 0%, #fff5f5 100%);
  }
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
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
  overflow: hidden;
}

.profile-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.comment-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.commenter-name {
  font-weight: 600;
  color: #1f2937;
  font-size: 14px;
}

.commenter-name.author {
  color: #e11d48;
}

.author-badge {
  font-size: 12px;
  color: #e11d48;
  font-weight: normal;
}

.comment-date {
  font-size: 12px;
  color: #6b7280;
}

.comment-content {
  font-size: 14px;
  line-height: 1.6;
  color: #374151;
  margin-bottom: 12px;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.edit-btn, .delete-btn {
  padding: 4px 8px;
  border: none;
  background: none;
  color: #6b7280;
  font-size: 12px;
  cursor: pointer;
  border-radius: 4px;
  transition: background 0.2s;
}

.edit-btn.small, .delete-btn.small {
  padding: 2px 6px;
  font-size: 10px;
}

.edit-btn:hover {
  background: #f3f4f6;
}

.delete-btn:hover {
  background: #fef2f2;
  color: #dc2626;
}

.edit-btn {
  margin-right: 8px;
}

.delete-btn {
  margin-right: 8px;
}

.like-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid #e5e7eb;
}

.like-btn:hover {
  background: #fef2f2;
  border-color: #e11d48;
}

.like-btn .like-icon.liked {
  animation: heartBeat 0.6s ease-in-out;
}

@keyframes heartBeat {
  0% { transform: scale(1); }
  25% { transform: scale(1.2); }
  50% { transform: scale(0.9); }
  75% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.like-icon {
  font-size: 16px;
  transition: all 0.3s ease;
}

.like-count {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

/* 답글 스타일 */
.replies-list {
  margin-top: 16px;
  margin-left: 52px;
  border-left: 2px solid #e5e7eb;
  padding-left: 16px;
}

.reply-item {
  padding: 12px 0;
  border-bottom: 1px solid #f3f4f6;
}

.reply-item:last-child {
  border-bottom: none;
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.profile-icon.small {
  width: 24px;
  height: 24px;
  font-size: 12px;
  overflow: hidden;
}

.reply-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.replyer-name {
  font-weight: 600;
  color: #1f2937;
  font-size: 12px;
}

.replyer-name.author {
  color: #e11d48;
}

.reply-date {
  font-size: 10px;
  color: #6b7280;
}

.reply-content {
  font-size: 12px;
  line-height: 1.5;
  color: #374151;
  margin-bottom: 8px;
}

.reply-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.like-btn.small {
  padding: 4px 8px;
  font-size: 12px;
}

.like-btn.small .like-icon {
  font-size: 12px;
}

/* 댓글 작성 */
.comment-write {
  border-top: 2px solid #e5e7eb;
  padding-top: 20px;
}

.comment-input-section {
  display: flex;
  gap: 12px;
}

.input-container {
  flex: 1;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  border: 1px solid #e5e7eb;
  transition: all 0.2s;
}

.input-container:hover {
  border-color: #d1d5db;
}

.user-label {
  font-weight: bold;
  color: #1f2937;
  font-size: 14px;
  margin-bottom: 8px;
}

.comment-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 8px;
  font-size: 14px;
  resize: none;
  font-family: inherit;
  outline: none;
  transition: all 0.2s;
  min-height: 60px;
}

.comment-input:focus {
  background: white;
  border-color: #e11d48;
  box-shadow: 0 0 0 2px rgba(225, 29, 72, 0.1);
}

.comment-input.active {
  background: white;
  border-color: #e11d48;
}

.comment-input::placeholder {
  color: #9ca3af;
}

.comment-actions-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #e5e7eb;
}

.action-icons {
  display: flex;
  gap: 8px;
}

.icon-btn {
  padding: 6px;
  border: none;
  background: none;
  cursor: pointer;
  border-radius: 4px;
  font-size: 16px;
  transition: background 0.2s;
  color: #6b7280;
}

.icon-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.submit-btn {
  padding: 6px 16px;
  background: #e11d48;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  font-weight: 500;
}

.submit-btn:hover:not(:disabled) {
  background: #be185d;
}

.submit-btn:disabled {
  background: #d1d5db;
  color: #9ca3af;
  cursor: not-allowed;
}

.submit-btn.submitting {
  background: linear-gradient(135deg, #e11d48 0%, #f97316 100%);
  animation: pulse 1.5s infinite;
}

.loading-text {
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-dots {
  position: relative;
}

.loading-dots::after {
  content: '';
  animation: loadingDots 1.5s infinite;
}

@keyframes loadingDots {
  0%, 20% { content: ''; }
  40% { content: '.'; }
  60% { content: '..'; }
  80%, 100% { content: '...'; }
}



/* 로그인 안내 스타일 */
.login-guide {
  margin-top: 24px;
  padding: 0;
}

.login-guide-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  padding: 32px;
  background: #f9fafb;
  border: 2px dashed #d1d5db;
  border-radius: 12px;
  text-align: center;
}

.guide-icon {
  font-size: 24px;
  color: #6b7280;
}

.guide-text {
  flex: 1;
}

.guide-text h3 {
  margin: 0 0 8px 0;
  color: #374151;
  font-size: 18px;
  font-weight: 600;
}

.guide-text p {
  margin: 0;
  color: #6b7280;
  font-size: 14px;
}

.login-btn {
  padding: 12px 24px;
  background: #e11d48;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  min-width: 100px;
}

.login-btn:hover {
  background: #be185d;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(225, 29, 72, 0.3);
}

.login-btn:active {
  transform: translateY(0);
}

/* 반응형 */
@media (max-width: 768px) {
  .comments-section {
    padding: 20px;
  }
  
  .comments-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .login-guide-content {
    flex-direction: column;
    gap: 20px;
    padding: 24px;
  }
  
  .guide-text h3 {
    font-size: 16px;
  }
  
  .guide-text p {
    font-size: 13px;
  }
}
</style> 