<template>
  <div class="mobile-notification-container">
    <button class="mobile-notification-btn" @click="handleMobileNotification">
      <span class="notification-icon">🔔</span>
      <span v-if="notificationCount > 0" class="notification-badge">{{ notificationCount }}</span>
    </button>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useNotificationStore } from '@/stores/notification'
import { storeToRefs } from 'pinia'

// notification store 사용
const notificationStore = useNotificationStore()
const { unreadCount } = storeToRefs(notificationStore)

// Emits
const emit = defineEmits(['notification-clicked'])

// 계산된 속성
const notificationCount = computed(() => unreadCount.value)

// 메서드
const handleMobileNotification = () => {
  emit('notification-clicked')
}
</script>

<style scoped>
/* 모바일 알림 스타일 */
.mobile-notification-container {
  width: 100%;
}

.mobile-notification-btn {
  width: 100%;
  background: none;
  border: none;
  padding: 12px 0;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #888;
  font-size: 0.95rem;
  transition: color 0.2s;
  position: relative;
}

.mobile-notification-btn:hover {
  color: #333;
}

.mobile-notification-btn .notification-icon {
  font-size: 1rem;
}

.mobile-notification-btn .notification-badge {
  position: static;
  margin-left: auto;
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


</style>
