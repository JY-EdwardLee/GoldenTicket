<script setup>
import DefaultLayout from "./layouts/DefaultLayout.vue";
import DeleteUserModal from "./components/common/DeleteUserModal.vue";
import Chatbot from "./components/common/Chatbot.vue";
import { useDeleteUserModal } from "./composables/useDeleteUserModal.js";

import { connectWebSocket, disconnectWebSocket } from "./utils/socket.js";
import { ref, onMounted, onBeforeUnmount, watch } from "vue";
import { useAuthStore } from "@/stores/auth"; // Pinia authStore 임포트
import { useNotificationStore } from "@/stores/notification";

import { API_CONFIG } from "@/config/api.config.js";
import http from "@/utils/http";

const authStore = useAuthStore();
const notificationStore = useNotificationStore();
// 토큰을 reactive하게 추적 (Pinia store의 토큰)
const token = ref(authStore.token);

const chatHistory = ref([]);

onMounted(() => {
  authStore.checkTokenValidity();
  http.get(API_CONFIG.CHAT.HISTORY)
    .then((response) => {
      chatHistory.value = response.data;
      console.log('chatHistory.value', chatHistory.value);
    })
    .catch((error) => {
      console.error('Error fetching chat history:', error);
    });
});

// Pinia authStore의 토큰 변화를 감지해서 웹소켓 연결/해제 처리
watch(
  () => authStore.token,
  (newToken, oldToken) => {
    token.value = newToken; // token ref도 같이 업데이트
    if (oldToken) {
      disconnectWebSocket();
      if (import.meta?.env?.DEV) console.log('WebSocket disconnected due to token change');
    }
    if (newToken) {
      // 웹소켓 연결 (실시간 알림)
      if (import.meta?.env?.DEV) console.log('WebSocket connecting with new token');
      connectWebSocket(newToken);
    } else {
      // 로그아웃 등 토큰이 사라진 경우 알림 초기화
      notificationStore.clearAllNotifications();
      if (import.meta?.env?.DEV) console.log('Cleared notifications after logout/no token');
    }
  },
  { immediate: true }
);

onBeforeUnmount(() => {
  disconnectWebSocket();
});

// 전역 회원탈퇴 모달 관리
const { isDeleteUserModalVisible, closeDeleteUserModal, handleDeleteUser } =
  useDeleteUserModal();
</script>

<template>
  <DefaultLayout>


    <router-view />

    <!-- 전역 회원탈퇴 모달 -->
    <DeleteUserModal
      :isVisible="isDeleteUserModalVisible"
      @close="closeDeleteUserModal"
      @withdraw="handleDeleteUser"
    />
    
    <!-- 전역 챗봇 -->
    <Chatbot :history="chatHistory" />
  </DefaultLayout>
</template>
