<script setup>
import DefaultLayout from './layouts/DefaultLayout.vue';
import DeleteUserModal from './components/common/DeleteUserModal.vue';
import { useDeleteUserModal } from './composables/useDeleteUserModal.js';

// 전역 회원탈퇴 모달 관리
const { isDeleteUserModalVisible, closeDeleteUserModal, handleDeleteUser } = useDeleteUserModal();
</script>

<template>
  <!-- Deployed via GitLab CI/CD at 2025-07-30 -->
  <DefaultLayout>
    <router-view />
    
    <!-- 전역 회원탈퇴 모달 -->
    <DeleteUserModal 
      :isVisible="isDeleteUserModalVisible"
      @close="closeDeleteUserModal"
      @withdraw="handleDeleteUser"
    />
  </DefaultLayout>
</template>
