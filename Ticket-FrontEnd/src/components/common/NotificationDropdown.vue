<template>
  <!-- 알림 아이콘 -->
  <div class="notification-container">
    <button class="notification-btn" @click="toggleNotificationModal" :class="{ 'active': isNotificationModalOpen }">
      <span class="notification-icon">🔔</span>
      <span v-if="notificationCount > 0" class="notification-badge">{{ notificationCount }}</span>
    </button>
    
    <!-- 알림 모달 -->
    <div v-if="isNotificationModalOpen" class="notification-modal">
      <div class="notification-header">
        <h3>알림</h3>
        <button class="close-notification-btn" @click="closeNotificationModal">×</button>
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useNotificationStore } from '@/stores/notification'
import { storeToRefs } from 'pinia'

// notification store 사용
const notificationStore = useNotificationStore()
const { notifications, unreadCount, sortedNotifications } = storeToRefs(notificationStore)

// 로컬 상태
const isNotificationModalOpen = ref(false)
const isActionSheetOpen = ref(false)
const selectedNotificationIndex = ref(null)

// 계산된 속성들
const notificationCount = computed(() => unreadCount.value)

// 메서드들
const toggleNotificationModal = () => {
  isNotificationModalOpen.value = !isNotificationModalOpen.value
}

const closeNotificationModal = () => {
  isNotificationModalOpen.value = false
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

// 외부에서 접근 가능한 메서드들 노출
defineExpose({
  toggleNotificationModal,
  closeNotificationModal
})
</script>

<style scoped>
/* 알림 컨테이너 */
.notification-container {
  position: relative;
  display: flex;
  align-items: center;
}

.notification-btn {
  background: none;
  border: none;
  outline: none;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  position: relative;
  transition: background-color 0.2s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.notification-btn:hover {
  background-color: #f5f5f5;
}

.notification-btn.active {
  background-color: #e0e0e0;
}

.notification-icon {
  font-size: 1.2rem;
  color: #666;
}

.notification-badge {
  position: absolute;
  top: 4px;
  right: 4px;
  background-color: #e11d48;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

/* 알림 모달 */
.notification-modal {
  position: absolute;
  top: 100%;
  right: 0;
  width: 380px;
  max-height: 500px;
  background: #f5f5f5;
  border: none;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  z-index: 1000;
  margin-top: 12px;
  overflow: hidden;
}

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

.notification-content {
  max-height: 400px;
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

.notification-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 네이버 스타일 알림 카드 */
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

/* 읽은 알림 스타일 */
.notification-card:not(.unread) {
  background: #f8f9fa;
  opacity: 0.8;
}

.notification-card:not(.unread):hover {
  background: #e9ecef;
  opacity: 0.9;
}

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

.notification-date {
  font-size: 0.75rem;
  color: #999;
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

/* 액션 시트 스타일 */
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

/* 모바일 대응 */
@media (max-width: 768px) {
  .notification-modal {
    position: fixed;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 90%;
    max-width: 380px;
    max-height: 70vh;
    margin-top: 0;
    border-radius: 16px;
  }
  
  .notification-header {
    padding: 16px 20px;
  }
  
  .notification-card {
    padding: 14px 16px;
  }
  
  .notification-content {
    padding: 10px;
  }
  
  .no-notifications {
    padding: 40px 20px;
  }
}
</style>
