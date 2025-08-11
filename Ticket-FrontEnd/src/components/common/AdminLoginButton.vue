<template>
  <div class="admin-login-container">
    <v-btn
      v-if="!isAdminAuthenticated"
      @click="handleAdminLogin"
      :loading="isLoading"
      color="warning"
      variant="flat"
      size="small"
      class="admin-login-btn"
    >
      <v-icon left>mdi-account-cog</v-icon>
      관리자 로그인 (테스트)
    </v-btn>
    
    <v-btn
      v-else
      @click="handleAdminLogout"
      :loading="isLoading"
      color="error"
      variant="outlined"
      size="small"
      class="admin-logout-btn"
    >
      <v-icon left>mdi-logout</v-icon>
      관리자 로그아웃
    </v-btn>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAdminAuthStore } from '@/stores/adminAuth';

const adminAuthStore = useAdminAuthStore();
const isLoading = ref(false);

const isAdminAuthenticated = computed(() => adminAuthStore.isAdminAuthenticated);

const handleAdminLogin = async () => {
  isLoading.value = true;
  try {
    const result = await adminAuthStore.adminLogin();
    if (result.success) {
      console.log('관리자 로그인 성공!');
      // 성공 메시지나 리다이렉트 로직 추가 가능
    } else {
      console.error('관리자 로그인 실패:', result.error);
    }
  } catch (error) {
    console.error('관리자 로그인 중 오류 발생:', error);
  } finally {
    isLoading.value = false;
  }
};

const handleAdminLogout = async () => {
  isLoading.value = true;
  try {
    await adminAuthStore.adminLogout();
    console.log('관리자 로그아웃 완료!');
  } catch (error) {
    console.error('관리자 로그아웃 중 오류 발생:', error);
  } finally {
    isLoading.value = false;
  }
};
</script>

<style scoped>
.admin-login-container {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 9999;
}

.admin-login-btn,
.admin-logout-btn {
  font-size: 12px;
  font-weight: 600;
  text-transform: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.admin-login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.admin-logout-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}
</style>

