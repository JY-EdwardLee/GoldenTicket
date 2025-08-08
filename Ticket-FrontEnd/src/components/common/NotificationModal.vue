<template>
  <!-- 모바일 알림 모달 -->
  <div v-if="isVisible" class="notification-modal-overlay" @click="closeModal">
    <div class="notification-modal" @click.stop>
      <div class="notification-header">
        <h3>알림</h3>
        <button class="close-notification-btn" @click="closeModal">×</button>
      </div>
      <div class="notification-content">
        <div v-if="notifications.length === 0" class="no-notifications">
          새로운 알림이 없습니다.
        </div>
        <div v-else class="notification-list">
          <div 
            v-for="(notification, index) in sortedNotifications" 
            :key="index" 
            class="notification-card"
            :class="{ 'unread': !notification.read }"
            @click="markAsRead(index)"
          >
            <div class="notification-card-header">
              <div class="notification-icon-container">
                <div class="notification-app-icon" :class="{ 'unread-icon': !notification.read }">⚾</div>
                <div class="notification-app-info">
                  <span class="app-name">골든 티켓</span>
                </div>
              </div>
              <div class="notification-options" @click.stop="openActionSheet(index)">
                <span class="options-dots">⋯</span>
              </div>
            </div>
            <div class="notification-card-content">
              <div class="notification-message" v-html="notification.message"></div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 액션 시트 모달 -->
      <div v-if="isActionSheetOpen" class="action-sheet-overlay" @click="closeActionSheet">
        <div class="action-sheet" @click.stop>
          <div class="action-sheet-item" @click="deleteNotification">
            <span class="action-icon">🗑️</span>
            <span class="action-text">삭제하기</span>
          </div>
          <div class="action-sheet-cancel" @click="closeActionSheet">
            취소
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useNotificationStore } from '@/stores/notification'
import { storeToRefs } from 'pinia'

// Props
const props = defineProps({
  isVisible: {
    type: Boolean,
    default: false
  }
})

// Emits
const emit = defineEmits(['close'])

// notification store 사용
const notificationStore = useNotificationStore()
const { notifications, sortedNotifications } = storeToRefs(notificationStore)

// 로컬 상태
const isActionSheetOpen = ref(false)
const selectedNotificationIndex = ref(null)

// 메서드들
const closeModal = () => {
  emit('close')
}

const openActionSheet = (index) => {
  selectedNotificationIndex.value = index
  isActionSheetOpen.value = true
}

const closeActionSheet = () => {
  isActionSheetOpen.value = false
  selectedNotificationIndex.value = null
}

const deleteNotification = () => {
  if (selectedNotificationIndex.value !== null) {
    notificationStore.deleteNotification(selectedNotificationIndex.value)
  }
  closeActionSheet()
}

const markAsRead = (index) => {
  notificationStore.markAsRead(index)
}
</script>

<style scoped>
/* 모달 오버레이 */
.notification-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 2000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

/* 모달 컨테이너 */
.notification-modal {
  background: #f5f5f5;
  border-radius: 16px;
  width: 100%;
  max-width: 400px;
  max-height: 80vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 헤더 */
.notification-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: white;
  border-bottom: 1px solid #e5e5e5;
}

.notification-header h3 {
  margin: 0;
  font-size: 1rem;
  font-weight: 600;
  color: #333;
}

.close-notification-btn {
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.2s ease;
}

.close-notification-btn:hover {
  background: #f0f0f0;
}

/* 컨텐츠 */
.notification-content {
  flex: 1;
  overflow-y: auto;
  background: #f5f5f5;
  padding: 12px;
}

.notification-content::-webkit-scrollbar {
  width: 4px;
}

.notification-content::-webkit-scrollbar-track {
  background: transparent;
}

.notification-content::-webkit-scrollbar-thumb {
  background: #ddd;
  border-radius: 2px;
}

.notification-content::-webkit-scrollbar-thumb:hover {
  background: #ccc;
}

/* 빈 알림 */
.no-notifications {
  padding: 48px 24px;
  text-align: center;
  color: #8b8b8b;
  font-size: 0.95rem;
  background: white;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.no-notifications::before {
  content: "🔔";
  font-size: 2rem;
  opacity: 0.5;
}

/* 알림 리스트 */
.notification-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 알림 카드 */
.notification-card {
  background: white;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.notification-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.notification-card.unread {
  border-left: 3px solid #1e40af;
  background: #f0f4ff;
}

.notification-card.unread:hover {
  background: #e0e7ff;
}

.notification-card:not(.unread) {
  background: #f8f9fa;
  opacity: 0.8;
}

.notification-card:not(.unread):hover {
  background: #e9ecef;
  opacity: 0.9;
}

/* 카드 헤더 */
.notification-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.notification-icon-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.notification-app-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: white;
  background: #6b7280;
  transition: background-color 0.2s ease;
}

.notification-app-icon.unread-icon {
  background: #1e40af;
}

.notification-app-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.app-name {
  font-size: 0.9rem;
  font-weight: 500;
  color: #333;
}

.notification-options {
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.notification-options:hover {
  background-color: #f0f0f0;
}

.options-dots {
  font-size: 1.2rem;
  color: #999;
  font-weight: bold;
}

/* 카드 컨텐츠 */
.notification-card-content {
  display: flex;
  align-items: flex-start;
}

.notification-message {
  font-size: 0.9rem;
  color: #333;
  line-height: 1.4;
  font-weight: 400;
  white-space: pre-line;
}

/* 액션 시트 */
.action-sheet-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 3000;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-sheet {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 400px;
  overflow: hidden;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.action-sheet-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.action-sheet-item:hover {
  background-color: #f8f9fa;
}

.action-sheet-item:active {
  background-color: #e9ecef;
}

.action-icon {
  font-size: 1.2rem;
  margin-right: 12px;
  width: 24px;
  text-align: center;
}

.action-text {
  font-size: 1rem;
  color: #333;
  font-weight: 500;
}

.action-sheet-cancel {
  padding: 16px 20px;
  text-align: center;
  font-size: 1rem;
  color: #333;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
  border-top: 8px solid #f0f0f0;
}

.action-sheet-cancel:hover {
  background-color: #f8f9fa;
}

.action-sheet-cancel:active {
  background-color: #e9ecef;
}

/* 반응형 */
@media (max-width: 480px) {
  .notification-modal-overlay {
    padding: 10px;
  }
  
  .notification-modal {
    max-height: 90vh;
  }
  
  .notification-header {
    padding: 14px 16px;
  }
  
  .notification-card {
    padding: 14px;
  }
  
  .notification-content {
    padding: 10px;
  }
}
</style>
