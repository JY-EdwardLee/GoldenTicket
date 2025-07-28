import { ref } from 'vue'

// 전역 상태로 관리
const isDeleteUserModalVisible = ref(false)

export function useDeleteUserModal() {
  // 모달 열기
  const openDeleteUserModal = () => {
    isDeleteUserModalVisible.value = true
  }

  // 모달 닫기
  const closeDeleteUserModal = () => {
    isDeleteUserModalVisible.value = false
  }

  // 회원탈퇴 처리
  const handleDeleteUser = () => {
    // 실제로는 API 호출 후 로그아웃 처리
    console.log('회원탈퇴 완료')
    
    // 예시: 메인 페이지로 리다이렉트
    // window.location.href = '/'
  }

  return {
    isDeleteUserModalVisible,
    openDeleteUserModal,
    closeDeleteUserModal,
    handleDeleteUser
  }
} 