import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter();
  const token = ref(localStorage.getItem('accessToken') || null);
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'));
  const redirectPath = ref(localStorage.getItem('redirectPath') || null);

  const isAuthenticated = computed(() => !!token.value);

  function setToken(newToken) {
    token.value = newToken;
    localStorage.setItem('accessToken', newToken);
  }

  function setUser(userData) {
    user.value = userData;
    localStorage.setItem('user', JSON.stringify(userData));
  }

  function setRedirectPath(path) {
    redirectPath.value = path;
    if (path) {
      localStorage.setItem('redirectPath', path);
    } else {
      localStorage.removeItem('redirectPath');
    }
  }

  function getAndClearRedirectPath() {
    const path = redirectPath.value || '/';
    setRedirectPath(null);
    return path;
  }
// In auth.js
async function logout() {
  try {
    // First, clear the local state
    token.value = null;
    user.value = null;
    localStorage.removeItem('accessToken');
    localStorage.removeItem('user');
    
    // Then make the API call to invalidate the session
    await axios.get(API_CONFIG.AUTH.LOGOUT, { withCredentials: true });
  } catch (error) {
    console.error('Logout error:', error);
    // Even if the API call fails, we still want to clear the local state
  } finally {
    // Always redirect to home after logout
    router.push('/');
  }
}
  // 토큰 검증 함수 (필요한 경우 API 호출로 검증 가능)
  async function verifyToken() {
    if (!token.value) return false;
    
    // 여기서는 간단히 토큰 존재 여부만 확인
    // 실제로는 API 호출을 통해 토큰 검증이 필요할 수 있음
    return !!token.value;
  }

  return {
    token,
    user,
    isAuthenticated,
    redirectPath,
    setToken,
    setUser,
    setRedirectPath,
    getAndClearRedirectPath,
    logout,
    verifyToken
  };
});
