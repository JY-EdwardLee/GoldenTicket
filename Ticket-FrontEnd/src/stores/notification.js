// 실시간 알림 추가
import { defineStore } from "pinia";

export const useNotificationStore = defineStore("notification", {
  state: () => ({
    notifications: [], // 알림 목록
    unreadCount: 0, // 읽지 않은 알림 수
  }),
  actions: {
    // 알림 추가
    addNotification(payload) {
      this.notifications.unshift(payload); // 새로운 알림을 배열 앞에 추가
      this.unreadCount++; // 읽지 않은 알림 수 증가
    },
    // 모든 알림 읽음 처리
    markAllAsRead() {
      this.unreadCount = 0; // 모든 알림을 읽음 처리
    },
    // 특정 알림 읽음 처리
    markAsRead(index) {
      this.notifications[index].read = true; // 해당 알림을 읽음으로 표시
      this.unreadCount--; // 읽지 않은 알림 수 감소
    },
  },
});
