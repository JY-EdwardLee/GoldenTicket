<template>
  <div v-if="isVisible" class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <!-- 모달 헤더 -->
      <div class="modal-header">
        <h3 class="modal-title">회원 탈퇴</h3>
        <button class="modal-close-btn" @click="closeModal">✕</button>
      </div>

      <!-- 모달 바디 -->
      <div class="modal-body">
        <!-- 경고 메시지 -->
        <div class="warning-section">
          <div class="warning-icon">⚠️</div>
          <div class="warning-text">
            <h4>정말로 탈퇴하시겠습니까?</h4>
            <p>회원 탈퇴 시 다음과 같은 정보가 삭제됩니다:</p>
            <ul class="warning-list">
              <li>개인정보 및 계정 정보</li>
              <li>티켓 구매 내역 및 응모 내역</li>
              <li>작성한 게시글 및 댓글</li>
              <li>적립된 포인트 및 쿠폰</li>
            </ul>
            <p class="warning-note">⚠️ 탈퇴 후에는 <strong>복구가 불가능</strong>합니다.</p>
          </div>
        </div>

        <!-- 탈퇴 확인 입력 -->
        <div class="confirmation-section">
          <label for="confirmText" class="confirmation-label">
            회원 탈퇴를 진행하시려면 아래 문구를 정확히 입력해주세요:
          </label>
          <div class="target-text">"회원탈퇴를 진행합니다"</div>
          <input
            id="confirmText"
            v-model="confirmationText"
            type="text"
            class="confirmation-input"
            placeholder="위 문구를 정확히 입력해주세요"
            @keyup.enter="attemptWithdraw"
          />
          <div v-if="errorMessage" class="error-message">
            {{ errorMessage }}
          </div>
        </div>

        <!-- 추가 확인 체크박스 -->
        <div class="checkbox-section">
          <label class="checkbox-label">
            <input
              v-model="finalConfirmation"
              type="checkbox"
              class="confirmation-checkbox"
            />
            <span class="checkbox-text">
              위 내용을 모두 확인했으며, 회원 탈퇴에 동의합니다.
            </span>
          </label>
        </div>
      </div>

      <!-- 모달 푸터 -->
      <div class="modal-footer">
        <button class="cancel-btn" @click="closeModal">
          취소
        </button>
        <button 
          class="withdraw-btn" 
          :disabled="!canWithdraw"
          @click="attemptWithdraw"
        >
          회원 탈퇴
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['close', 'withdraw'])

// 상태 관리
const confirmationText = ref('')
const finalConfirmation = ref(false)
const errorMessage = ref('')
const isProcessing = ref(false)

// 정확한 확인 문구
const TARGET_TEXT = '회원탈퇴를 진행합니다'

// 탈퇴 가능 여부 계산
const canWithdraw = computed(() => {
  return confirmationText.value.trim() === TARGET_TEXT && 
         finalConfirmation.value && 
         !isProcessing.value
})

// 모달이 열릴 때마다 초기화
watch(() => props.isVisible, (newValue) => {
  if (newValue) {
    resetForm()
  }
})

// 폼 초기화
const resetForm = () => {
  confirmationText.value = ''
  finalConfirmation.value = false
  errorMessage.value = ''
  isProcessing.value = false
}

// 모달 닫기
const closeModal = () => {
  if (!isProcessing.value) {
    resetForm()
    emit('close')
  }
}

// 탈퇴 시도
const attemptWithdraw = () => {
  if (isProcessing.value) return

  // 확인 문구 검증
  if (confirmationText.value.trim() !== TARGET_TEXT) {
    errorMessage.value = '확인 문구가 일치하지 않습니다. 정확히 입력해주세요.'
    return
  }

  // 최종 확인 체크박스 검증
  if (!finalConfirmation.value) {
    errorMessage.value = '탈퇴 동의에 체크해주세요.'
    return
  }

  // 에러 메시지 초기화
  errorMessage.value = ''
  
  // 탈퇴 처리 시작
  isProcessing.value = true
  
  // 실제로는 API 호출
  setTimeout(() => {
    alert('회원 탈퇴가 완료되었습니다. 그동안 이용해주셔서 감사합니다.')
    isProcessing.value = false
    emit('withdraw')
    closeModal()
    
    // 실제로는 로그아웃 후 홈페이지로 리다이렉트
    // router.push('/')
  }, 2000)
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(3px);
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  animation: modalAppear 0.3s ease-out;
}

@keyframes modalAppear {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 30px;
  border-bottom: 1px solid #eee;
}

.modal-title {
  margin: 0;
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.modal-close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.modal-close-btn:hover {
  background: #f5f5f5;
  color: #333;
}

.modal-body {
  padding: 30px;
}

.warning-section {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  padding: 20px;
  background: #fff8f0;
  border: 1px solid #ffc107;
  border-radius: 12px;
}

.warning-icon {
  font-size: 32px;
  flex-shrink: 0;
}

.warning-text h4 {
  margin: 0 0 15px 0;
  color: #d63384;
  font-size: 18px;
}

.warning-text p {
  margin: 0 0 10px 0;
  color: #333;
  line-height: 1.5;
}

.warning-list {
  margin: 15px 0;
  padding-left: 20px;
  color: #555;
}

.warning-list li {
  margin-bottom: 8px;
  line-height: 1.4;
}

.warning-note {
  margin-top: 15px !important;
  padding: 10px;
  background: #f8d7da;
  border: 1px solid #f5c6cb;
  border-radius: 6px;
  color: #721c24 !important;
  font-weight: 500;
}

.confirmation-section {
  margin-bottom: 25px;
}

.confirmation-label {
  display: block;
  margin-bottom: 15px;
  font-weight: 600;
  color: #333;
  font-size: 16px;
}

.target-text {
  background: #f8f9fa;
  border: 2px solid #dee2e6;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  font-family: 'Courier New', monospace;
  font-size: 16px;
  font-weight: bold;
  color: #495057;
  text-align: center;
}

.confirmation-input {
  width: 100%;
  padding: 15px;
  border: 2px solid #dee2e6;
  color: black;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s ease;
}

.confirmation-input:focus {
  outline: none;
  border-color: var(--theme-primary, #ff6b35);
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.error-message {
  margin-top: 10px;
  color: #dc3545;
  font-size: 14px;
  font-weight: 500;
}

.checkbox-section {
  margin-bottom: 20px;
}

.checkbox-label {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  cursor: pointer;
  line-height: 1.5;
}

.confirmation-checkbox {
  width: 20px;
  height: 20px;
  margin-top: 2px;
  /* background-color: white !important; */
  border: 2px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  appearance: none;
  -webkit-appearance: none;
  -moz-appearance: none;
}

.confirmation-checkbox:checked {
  appearance: checkbox;
  -webkit-appearance: checkbox;
  -moz-appearance: checkbox;
  background-color: initial;
  border: initial;
  border-radius: initial;
}

.checkbox-text {
  color: #333;
  font-size: 15px;
}

.modal-footer {
  display: flex;
  gap: 15px;
  justify-content: flex-end;
  padding: 20px 30px;
  border-top: 1px solid #eee;
  background: #f8f9fa;
  border-radius: 0 0 16px 16px;
}

.cancel-btn {
  padding: 12px 24px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: #5a6268;
  transform: translateY(-1px);
}

.withdraw-btn {
  padding: 12px 24px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.withdraw-btn:hover:not(:disabled) {
  background: #c82333;
  transform: translateY(-1px);
}

.withdraw-btn:disabled {
  background: #e9ecef;
  color: #6c757d;
  cursor: not-allowed;
  transform: none;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .modal-header,
  .modal-body,
  .modal-footer {
    padding: 20px;
  }
  
  .warning-section {
    flex-direction: column;
    text-align: center;
  }
  
  .modal-footer {
    flex-direction: column;
  }
  
  .cancel-btn,
  .withdraw-btn {
    width: 100%;
  }
}
</style> 