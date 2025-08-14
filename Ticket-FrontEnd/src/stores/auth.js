import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { API_CONFIG } from '@/config/api.config';
import http from '@/utils/http';

export const useAuthStore = defineStore('auth', () => {
  const router = useRouter();
  const token = ref(localStorage.getItem('accessToken') || null);
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'));
  const userRole = ref(localStorage.getItem('userRole') || null);
  const redirectPath = ref(localStorage.getItem('redirectPath') || null);

  const isTokenExpired = (tokenString) => {
    if (!tokenString) return true;
    try {
      const payload = JSON.parse(atob(tokenString.split('.')[1]));
      const now = Math.floor(Date.now() / 1000);
      return payload.exp < now;
    } catch (e) {
      console.error('Token decoding failed:', e);
      // Malformed token is considered expired/invalid
      return true;
    }
  };

  const isAuthenticated = computed(() => {
    const t = token.value;
    return !!t && !isTokenExpired(t);
  });

  // ADMIN 권한 확인
  const isAdmin = computed(() => {
    return userRole.value === 'ADMIN';
  });

  // 사용자 권한 확인 (ADMIN이거나 일반 사용자)
  const hasPermission = computed(() => {
    return isAuthenticated.value && (isAdmin.value || userRole.value === 'USER');
  });

  const checkTokenValidity = () => {
    if (token.value && isTokenExpired(token.value)) {
      console.log('Session expired, logging out.');
      logout();
    }
  };

  function setToken(newToken) {
    token.value = newToken;
    localStorage.setItem('accessToken', newToken);
  }

  function setUser(userData) {
    user.value = userData;
    localStorage.removeItem('user')
    localStorage.setItem('user', JSON.stringify(userData));
  }

  function setUserRole(role) {
    userRole.value = role;
    if (role) {
      localStorage.setItem('userRole', role);
    } else {
      localStorage.removeItem('userRole');
    }
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

  async function getUserInfo() {
    try {
      const response = await http.get(API_CONFIG.USER.PROFILE);
      
      // 기존 사용자 정보에서 userId 보존
      const currentUser = user.value;
      const serverUserData = response.data;
      
      // 서버 응답에 userId가 없고 기존에 userId가 있었다면 보존
      if (!serverUserData.userId && currentUser && currentUser.userId) {
        const mergedUserData = {
          ...serverUserData,
          userId: currentUser.userId
        };
        setUser(mergedUserData);
      } else {
        setUser(serverUserData);
      }
      
    } catch (error) {
      console.error('Failed to fetch user info:', error);
    }
  }

  async function logout() {
    try {
      // First make the API call to invalidate the session
      await http.post(API_CONFIG.AUTH.LOGOUT, { withCredentials: true });
    } catch (error) {
      console.error('Logout error:', error);
      // Even if the API call fails, we still want to clear the local state
    } finally {
      // First, clear the local state
      token.value = null;
      user.value = null;
      userRole.value = null;
      localStorage.removeItem('selectedTeam');
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      localStorage.removeItem('userRole');
      // Always redirect to home after logout
      router.push('/');
    }
  }

  // 토큰 검증 함수 (필요한 경우 API 호출로 검증 가능)
  async function verifyToken() {
    if (!token.value) return false;
    try {
      const response = await http.get(API_CONFIG.USER.PROFILE);
      setUser(response.data);
      console.log(user.value);
      return true;
    } catch (error) {
      console.error('Failed to fetch user info:', error);
      token.value = null;
      user.value = null;
      userRole.value = null;
      localStorage.removeItem('accessToken');
      localStorage.removeItem('user');
      localStorage.removeItem('userRole');
      localStorage.removeItem('selectedTeam');
    }
    // 여기서는 간단히 토큰 존재 여부만 확인
    // 실제로는 API 호출을 통해 토큰 검증이 필요할 수 있음
    return !!token.value;
  }

  return {
    token,
    user,
    userRole,
    isAuthenticated,
    isAdmin,
    hasPermission,
    redirectPath,
    setToken,
    setUser,
    setUserRole,
    setRedirectPath,
    getAndClearRedirectPath,
    logout,
    verifyToken,
    getUserInfo,
    checkTokenValidity,
  };
});
