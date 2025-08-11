<template>
  <div v-if="isVisible" class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">댓글 삭제</h3>
        <button class="close-btn" @click="handleCancel">×</button>
      </div>
      
      <div class="modal-body">
        <div class="warning-icon">⚠️</div>
        <p class="warning-text">
          정말로 이 댓글을 삭제하시겠습니까?
        </p>
        <p class="warning-detail">
          삭제된 댓글은 복구할 수 없습니다.
        </p>
        
        <!-- 삭제할 댓글 미리보기 -->
        <div class="comment-preview">
          <div class="comment-info">
            <div class="profile-icon">
              <img 
                v-if="comment?.commentUserUrl" 
                :src="comment.commentUserUrl" 
                :alt="comment?.author || '프로필'"
                class="profile-image"
                @error="handleProfileImageError"
              />
              <span v-else>👤</span>
            </div>
            <div class="comment-meta">
              <span class="commenter-name">{{ comment?.author || '사용자' }}</span>
              <span class="comment-date">{{ formatDate(comment?.createdAt) }}</span>
            </div>
          </div>
          <div class="comment-content">
            {{ comment?.content || '' }}
          </div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button 
          class="btn btn-secondary" 
          @click="handleCancel"
          :disabled="isDeleting"
        >
          취소
        </button>
        <button 
          class="btn btn-danger" 
          @click="handleConfirm"
          :disabled="isDeleting"
        >
          <span v-if="isDeleting">삭제 중...</span>
          <span v-else>삭제</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import { boardAPI } from '@/api/board.js';

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  comment: {
    type: Object,
    default: null
  },
  isDeleting: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['confirm', 'cancel']);

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

const handleConfirm = () => {
  emit('confirm');
};

const handleCancel = () => {
  emit('cancel');
};

const handleOverlayClick = () => {
  if (!props.isDeleting) {
    emit('cancel');
  }
};
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  max-width: 450px;
  width: 100%;
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px 0;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  transition: all 0.2s;
}

.close-btn:hover {
  background: #f3f4f6;
  color: #374151;
}

.modal-body {
  padding: 20px 24px;
  text-align: center;
}

.warning-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.warning-text {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
  line-height: 1.5;
}

.warning-detail {
  font-size: 14px;
  color: #6b7280;
  margin: 0 0 20px 0;
  line-height: 1.6;
}

.comment-preview {
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  margin-top: 16px;
  text-align: left;
}

.comment-info {
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
  font-size: 16px;
  overflow: hidden;
}

.profile-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.comment-meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.commenter-name {
  font-weight: 600;
  color: #374151;
  font-size: 13px;
}

.comment-date {
  font-size: 11px;
  color: #6b7280;
}

.comment-content {
  font-size: 14px;
  color: #374151;
  line-height: 1.5;
  word-break: break-word;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 0 24px 24px;
}

.btn {
  flex: 1;
  padding: 12px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  min-height: 44px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: 2px solid #e5e7eb;
}

.btn-secondary:hover:not(:disabled) {
  background: #e5e7eb;
}

.btn-danger {
  background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
}

.btn-danger:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(220, 38, 38, 0.4);
}

/* 반응형 */
@media (max-width: 480px) {
  .modal-overlay {
    padding: 10px;
  }
  
  .modal-container {
    max-width: none;
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding-left: 20px;
    padding-right: 20px;
  }
  
  .modal-footer {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style> 