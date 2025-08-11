import { defineStore } from "pinia";

export const useNotificationStore = defineStore("notification", {
  state: () => ({
    notifications: [], // 알림 목록
    unreadCount: 0, // 읽지 않은 알림 수
  }),
  
  getters: {
    // 읽지 않은 알림만 필터링
    unreadNotifications: (state) => state.notifications.filter(n => !n.read),
    // 정렬 없이 추가된 순서 유지(unshift로 최신이 앞에 옴)
    sortedNotifications: (state) => [...state.notifications]
  },
  
  actions: {
    // 서버 응답 스냅샷을 그대로 반영 (권장 진입점)
    applyApiResponse(serverState) {
      if (!serverState || !Array.isArray(serverState.notifications)) {
        if (import.meta?.env?.DEV) {
          console.warn('[notification] 잘못된 서버 응답 형식', serverState);
        }
        return;
      }
      if (import.meta?.env?.DEV) {
        console.log('[notification] applyApiResponse()', serverState);
      }
      this.notifications = serverState.notifications.map((notification) => ({
        message: notification.message || '',
        read: Boolean(notification.read)
      }));
      if (typeof serverState.unreadCount === 'number') {
        this.unreadCount = serverState.unreadCount;
      } else {
        // safety fallback
        this.unreadCount = this.notifications.filter(n => !n.read).length;
      }
      if (import.meta?.env?.DEV) {
        console.log('[notification] state after applyApiResponse', {
          notifications: this.notifications,
          unreadCount: this.unreadCount,
        });
      }
    },

    // 알림 추가 (API 응답 형태로 처리)
    addNotification(payload) {
      const notification = {
        message: payload.message || '',
        read: Boolean(payload.read)
      };
      
      this.notifications.unshift(notification);
      if (import.meta?.env?.DEV) {
        console.log('[notification] addNotification()', notification);
      }
      if (!notification.read) {
        this.unreadCount++;
      }
      if (import.meta?.env?.DEV) {
        console.log('[notification] state after addNotification', {
          notifications: this.notifications,
          unreadCount: this.unreadCount,
        });
      }
    },
    
    // 여러 알림을 한번에 설정 (API 응답 처리용)
    setNotifications(payload) {
      // API 응답 형태: { notifications: [...], unreadCount: number }
      this.applyApiResponse(payload);
    },
    
    // 특정 알림 읽음 처리 (인덱스로 처리)
    markAsRead(index) {
      const target = this.notifications[index];
      if (target && !target.read) {
        target.read = true;
        this.unreadCount = Math.max(0, this.unreadCount - 1);
      }
      if (import.meta?.env?.DEV) {
        console.log('[notification] markAsRead()', { index, after: this.notifications[index] });
      }
    },
    
    // 모든 알림 읽음 처리
    markAllAsRead() {
      this.notifications.forEach(notification => {
        notification.read = true;
      });
      this.unreadCount = 0;
    },
    
    // 특정 알림 삭제 (인덱스로 처리)
    deleteNotification(index) {
      const notification = this.notifications[index];
      if (notification) {
        if (!notification.read) {
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        }
        this.notifications.splice(index, 1);
      }
      if (import.meta?.env?.DEV) {
        console.log('[notification] deleteNotification()', { index, unreadCount: this.unreadCount });
      }
    },
    
    // 모든 알림 삭제
    clearAllNotifications() {
      this.notifications = [];
      this.unreadCount = 0;
    }
  },
});
