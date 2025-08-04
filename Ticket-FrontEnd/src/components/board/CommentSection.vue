<template>
  <div class="comments-section">
    <div class="comments-header">
      <div class="sort-options">
        <button class="sort-btn active">등록순</button>
        <button class="sort-btn">최신순</button>
        <button class="refresh-btn">🔄</button>
      </div>
      
      <div class="comment-settings">
        <div class="notification-toggle">
          <span>관심글 댓글 알림</span>
          <label class="toggle-switch">
            <input type="checkbox" v-model="notificationEnabled">
            <span class="toggle-slider"></span>
          </label>
        </div>
      </div>
    </div>

    <!-- 댓글 목록 -->
    <div class="comments-list">
      <div 
        v-for="comment in comments" 
        :key="comment.id || comment.commentId" 
        class="comment-item"
        :class="{ 'new-comment': comment.isNew }"
      >
        <div class="comment-header">
          <div class="profile-icon">👤</div>
          <div class="comment-info">
            <span class="commenter-name" :class="{ 'author': comment.isAuthor }">
              {{ comment.author }}
              <span v-if="comment.isAuthor" class="author-badge">- 작성자</span>
            </span>
            <span class="comment-date">{{ comment.date }}</span>
          </div>
        </div>
        
        <div class="comment-content">
          {{ comment.content }}
        </div>
        
        <div class="comment-actions">
          <button class="edit-btn" @click="openEditModal(comment)">수정</button>
          <button class="delete-btn" @click="openDeleteModal(comment)">삭제</button>
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
              <div class="profile-icon small">👤</div>
              <div class="reply-info">
                <span class="replyer-name" :class="{ 'author': reply.isAuthor }">
                  {{ reply.author }}
                  <span v-if="reply.isAuthor" class="author-badge">- 작성자</span>
                </span>
                <span class="reply-date">{{ reply.date }}</span>
              </div>
            </div>
            
            <div class="reply-content">
              {{ reply.content }}
            </div>
            
            <div class="reply-actions">
              <button class="like-btn small">
                <span class="like-icon">❤️</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 댓글 작성 -->
    <div class="comment-write">
      <div class="comment-input-section">
        <div class="profile-icon">👤</div>
        <div class="input-container">
          <div class="user-label">엄용</div>
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
              <button class="icon-btn" @click="attachFile">
                📷
              </button>
              <button class="icon-btn" @click="insertEmoji">
                😊
              </button>
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
import { ref } from 'vue';
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

const emit = defineEmits(['commentSubmit']);

const authStore = useAuthStore();
const newComment = ref('');
const notificationEnabled = ref(false);
const isInputActive = ref(false);
const isSubmitting = ref(false);
const isEditModalVisible = ref(false);
const selectedComment = ref(null);
const isDeleteModalVisible = ref(false);
const isDeleting = ref(false);

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

const attachFile = () => {
  console.log('파일 첨부');
  // TODO: 파일 첨부 로직
};

const insertEmoji = () => {
  // 텍스트 영역에 포커스
  const textarea = document.querySelector('.comment-input');
  if (!textarea) return;
  
  textarea.focus();
  
  // Windows 이모지 선택기 열기
  if (navigator.userAgent.includes('Windows')) {
    try {
      // 방법 1: 직접 Win + . 단축키 시뮬레이션
      const winKeyDown = new KeyboardEvent('keydown', {
        key: 'Meta',
        code: 'MetaLeft',
        keyCode: 91,
        which: 91,
        ctrlKey: false,
        altKey: false,
        shiftKey: false,
        metaKey: true,
        bubbles: true,
        cancelable: true
      });
      
      const periodKeyDown = new KeyboardEvent('keydown', {
        key: '.',
        code: 'Period',
        keyCode: 190,
        which: 190,
        ctrlKey: false,
        altKey: false,
        shiftKey: false,
        metaKey: true,
        bubbles: true,
        cancelable: true
      });
      
      // 이벤트 발생
      document.dispatchEvent(winKeyDown);
      document.dispatchEvent(periodKeyDown);
      
      // 키 업 이벤트
      setTimeout(() => {
        const winKeyUp = new KeyboardEvent('keyup', {
          key: 'Meta',
          code: 'MetaLeft',
          keyCode: 91,
          which: 91,
          ctrlKey: false,
          altKey: false,
          shiftKey: false,
          metaKey: false,
          bubbles: true,
          cancelable: true
        });
        
        const periodKeyUp = new KeyboardEvent('keyup', {
          key: '.',
          code: 'Period',
          keyCode: 190,
          which: 190,
          ctrlKey: false,
          altKey: false,
          shiftKey: false,
          metaKey: false,
          bubbles: true,
          cancelable: true
        });
        
        document.dispatchEvent(periodKeyUp);
        document.dispatchEvent(winKeyUp);
      }, 100);
      
      // 방법 2: 대안으로 커스텀 팔레트도 함께 표시 (Windows 이모지 선택기가 작동하지 않을 경우)
      setTimeout(() => {
        // Windows 이모지 선택기가 열리지 않았을 경우를 대비해 커스텀 팔레트도 표시
        const hasEmojiPanel = document.querySelector('[data-testid="emoji-panel"]') || 
                             document.querySelector('.emoji-panel') ||
                             document.querySelector('[role="dialog"]');
        
        if (!hasEmojiPanel) {
          console.log('Windows 이모지 선택기가 열리지 않았습니다. 커스텀 팔레트를 표시합니다.');
          showEmojiPalette();
        }
      }, 500);
      
    } catch (error) {
      console.log('Windows 이모지 선택기 열기 실패:', error);
      showEmojiPalette();
    }
  } else {
    // 다른 OS에서는 커스텀 이모지 팔레트 표시
    showEmojiPalette();
  }
};

const showEmojiPalette = () => {
  // 간단한 이모지 팔레트 (임시 구현)
  const emojis = ['😊', '😂', '❤️', '👍', '🎉', '🔥', '😍', '🤔', '😭', '😡', '🥳', '🤗'];
  
  // 이모지 선택 다이얼로그 생성
  const dialog = document.createElement('div');
  dialog.className = 'emoji-palette';
  dialog.innerHTML = `
    <div class="emoji-palette-content">
      <div class="emoji-palette-header">
        <span>이모지 선택</span>
        <button class="close-btn" onclick="this.parentElement.parentElement.parentElement.remove()">×</button>
      </div>
      <div class="emoji-grid">
        ${emojis.map(emoji => `<button class="emoji-btn" onclick="insertEmojiToTextarea('${emoji}')">${emoji}</button>`).join('')}
      </div>
    </div>
  `;
  
  // 스타일 추가
  dialog.style.cssText = `
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
  `;
  
  // 전역 함수로 이모지 삽입 함수 등록
  window.insertEmojiToTextarea = (emoji) => {
    const textarea = document.querySelector('.comment-input');
    if (textarea) {
      const start = textarea.selectionStart;
      const end = textarea.selectionEnd;
      const text = newComment.value;
      const newText = text.substring(0, start) + emoji + text.substring(end);
      newComment.value = newText;
      
      // 커서 위치 조정
      textarea.focus();
      textarea.setSelectionRange(start + emoji.length, start + emoji.length);
    }
    dialog.remove();
  };
  
  document.body.appendChild(dialog);
  
  // 배경 클릭 시 닫기
  dialog.addEventListener('click', (e) => {
    if (e.target === dialog) {
      dialog.remove();
    }
  });
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
        author: authStore.user?.nickname || '사용자',
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
    
    console.log('댓글 좋아요 토글 결과:', result);
    
    if (result) {
      // 좋아요 상태와 개수 업데이트
      comment.isLiked = result.isLiked;
      comment.likeCount = result.likeCount;
      
      console.log('댓글 좋아요 토글 성공:', result);
    } else {
      console.error('댓글 좋아요 토글 실패: 응답이 없습니다.');
    }
  } catch (error) {
    console.error('댓글 좋아요 토글 중 오류:', error);
    alert('좋아요 처리 중 오류가 발생했습니다.');
  }
};
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
  border: none;
  background: transparent;
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
  box-shadow: 0 0 0 2px rgba(225, 29, 72, 0.1);
}

.comment-input.active {
  background: white;
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

/* 이모지 팔레트 스타일 */
.emoji-palette-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
  padding: 20px;
  max-width: 400px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.emoji-palette-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e5e7eb;
}

.emoji-palette-header span {
  font-weight: 600;
  color: #1f2937;
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.emoji-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}

.emoji-btn {
  background: none;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 12px;
  font-size: 20px;
  cursor: pointer;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.emoji-btn:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
  transform: scale(1.05);
}

.emoji-btn:active {
  transform: scale(0.95);
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
}
</style> 