<script setup>
import DefaultLayout from "./layouts/DefaultLayout.vue";
import DeleteUserModal from "./components/common/DeleteUserModal.vue";
import { useDeleteUserModal } from "./composables/useDeleteUserModal.js";

import { connectWebSocket, disconnectWebSocket } from "./utils/socket.js";
import { ref, onMounted, onBeforeUnmount, watch } from "vue";
import { useAuthStore } from "@/stores/auth"; // Pinia authStore 임포트

const authStore = useAuthStore();
// 토큰을 reactive하게 추적 (Pinia store의 토큰)
const token = ref(authStore.token);

// Pinia authStore의 토큰 변화를 감지해서 웹소켓 연결/해제 처리
watch(
  () => authStore.token,
  (newToken, oldToken) => {
    token.value = newToken; // token ref도 같이 업데이트
    if (oldToken) {
      disconnectWebSocket();
    }
    if (newToken) {
      connectWebSocket(newToken);
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
    <!-- 얍얍얍! -->
    <!-- 2025-08-04 중요한건 꺽이지 않는 마음음 -->
    <!-- Deployed via GitLab CI/CD at 2025-07-31 -->

    <router-view />

    <!-- 전역 회원탈퇴 모달 -->
    <DeleteUserModal
      :isVisible="isDeleteUserModalVisible"
      @close="closeDeleteUserModal"
      @withdraw="handleDeleteUser"
    />
  </DefaultLayout>
</template>
