<script setup>
import DefaultLayout from './layouts/DefaultLayout.vue';
import DeleteUserModal from './components/common/DeleteUserModal.vue';
import { useDeleteUserModal } from './composables/useDeleteUserModal.js';

// 전역 회원탈퇴 모달 관리
const { isDeleteUserModalVisible, closeDeleteUserModal, handleDeleteUser } = useDeleteUserModal();
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
