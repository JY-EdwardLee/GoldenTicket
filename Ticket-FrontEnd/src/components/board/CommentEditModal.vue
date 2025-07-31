<template>
  <div v-if="isVisible" class="modal-overlay" @click="handleOverlayClick">
    <div class="modal-container" @click.stop>
      <div class="modal-header">
        <h3 class="modal-title">댓글 수정</h3>
        <button class="close-btn" @click="handleCancel">×</button>
      </div>
      
      <div class="modal-body">
        <div class="comment-info">
          <div class="profile-icon">👤</div>
          <div class="comment-meta">
            <span class="commenter-name">{{ comment?.author || '사용자' }}</span>
            <span class="comment-date">{{ formatDate(comment?.createdAt) }}</span>
          </div>
        </div>
        
        <div class="edit-form">
          <label class="form-label">댓글 내용</label>
          <textarea
            v-model="editContent"
            class="comment-textarea"
            placeholder="댓글 내용을 입력하세요"
            rows="4"
            maxlength="500"
          ></textarea>
          <div class="char-count">{{ editContent.length }}/500</div>
        </div>
      </div>
      
      <div class="modal-footer">
        <button 
          class="btn btn-secondary" 
          @click="handleCancel"
          :disabled="isSubmitting"
        >
          취소
        </button>
        <button 
          class="btn btn-primary" 
          @click="handleSubmit"
          :disabled="!isFormValid || isSubmitting"
        >
          <span v-if="isSubmitting">수정 중...</span>
          <span v-else>수정 완료</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue';
import { boardAPI, validateCommentData } from '@/api/board.js';

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  },
  comment: {
    type: Object,
    default: null
  }
});

const emit = defineEmits(['confirm', 'cancel']);

const editContent = ref('');
const isSubmitting = ref(false);

// 폼 유효성 검사
const isFormValid = computed(() => {
  return editContent.value.trim().length > 0 && 
         editContent.value.trim() !== props.comment?.content;
});

// 모달이 열릴 때 기존 댓글 내용으로 초기화
watch(() => props.isVisible, (newValue) => {
  if (newValue && props.comment) {
    editContent.value = props.comment.content || '';
  }
});

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

const handleSubmit = async () => {
  if (!isFormValid.value) return;

  // 클라이언트 측 유효성 검사
  const validation = validateCommentData({
    postId: props.comment?.postId || 0,
    content: editContent.value
  });
  
  if (!validation.isValid) {
    alert(validation.errors.join('\n'));
    return;
  }

  isSubmitting.value = true;
  
  try {
    const result = await boardAPI.updateComment(props.comment.id, {
      content: editContent.value.trim()
    });
    
    if (result.success) {
      emit('confirm', {
        commentId: props.comment.id,
        content: editContent.value.trim()
      });
      console.log('댓글 수정 성공:', result.message);
    } else {
      alert(result.message || '댓글 수정에 실패했습니다.');
    }
  } catch (error) {
    console.error('댓글 수정 실패:', error);
    alert('댓글 수정 중 오류가 발생했습니다. 다시 시도해주세요.');
  } finally {
    isSubmitting.value = false;
  }
};

const handleCancel = () => {
  emit('cancel');
};

const handleOverlayClick = () => {
  if (!isSubmitting.value) {
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
  max-width: 500px;
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
}

.comment-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
}

.profile-icon {
  font-size: 20px;
}

.comment-meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.commenter-name {
  font-weight: 600;
  color: #374151;
  font-size: 14px;
}

.comment-date {
  font-size: 12px;
  color: #6b7280;
}

.edit-form {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.comment-textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.2s;
  background: white !important;
  color: #1f2937 !important;
}

.comment-textarea:focus {
  outline: none;
  border-color: #e11d48;
  box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.1);
}

.comment-textarea::placeholder {
  color: #9ca3af !important;
  opacity: 1;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
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

.btn-primary {
  background: linear-gradient(135deg, #e11d48 0%, #f97316 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(225, 29, 72, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(225, 29, 72, 0.4);
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