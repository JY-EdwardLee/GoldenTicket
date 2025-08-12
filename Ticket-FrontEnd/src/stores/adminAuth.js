import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { API_CONFIG } from '@/config/api.config';
import http from '@/utils/http';

export const useAdminAuthStore = defineStore('adminAuth', () => {
  const router = useRouter();
  const adminToken = ref(localStorage.getItem('adminAccessToken') || null);
  const adminRefreshToken = ref(localStorage.getItem('adminRefreshToken') || null);
  const adminUser = ref(JSON.parse(localStorage.getItem('adminUser') || 'null'));

  const isAdminTokenExpired = (tokenString) => {
    if (!tokenString) return true;
    try {
      const payload = JSON.parse(atob(tokenString.split('.')[1]));
      const now = Math.floor(Date.now() / 1000);
      return payload.exp < now;
    } catch (e) {
      console.error('Admin token decoding failed:', e);
      return true;
    }
  };

  const isAdminAuthenticated = computed(() => {
    const t = adminToken.value;
    return !!t && !isAdminTokenExpired(t);
  });

  function setAdminTokens(accessToken, refreshToken) {
    adminToken.value = accessToken;
    adminRefreshToken.value = refreshToken;
    localStorage.setItem('adminAccessToken', accessToken);
    localStorage.setItem('adminRefreshToken', refreshToken);
  }

  function setAdminUser(userData) {
    adminUser.value = userData;
    localStorage.setItem('adminUser', JSON.stringify(userData));
  }

  async function adminLogin() {
    try {
      console.log('관리자 로그인 시도...');
      const response = await http.post('/users/testlogin');
      
      const { accessToken, refreshToken } = response.data;
      
      // 토큰 저장
      setAdminTokens(accessToken, refreshToken);
      
      // 관리자 사용자 정보 설정 (테스트용)
      const adminUserData = {
        email: 'honggildong@example.com',
        userName: '홍길동',
        nickName: '길동이',
        userRole: 'ADMIN',
        userId: 5
      };
      setAdminUser(adminUserData);
      
      console.log('관리자 로그인 성공:', adminUserData);
      return { success: true, user: adminUserData };
      
    } catch (error) {
      console.error('관리자 로그인 실패:', error);
      return { success: false, error: error.message };
    }
  }

  async function adminLogout() {
    try {
      // 서버에 로그아웃 요청 (선택사항)
      if (adminToken.value) {
        await http.post(API_CONFIG.AUTH.LOGOUT, {}, {
          headers: {
            'Authorization': `Bearer ${adminToken.value}`
          }
        });
      }
    } catch (error) {
      console.error('관리자 로그아웃 API 호출 실패:', error);
    } finally {
      // 로컬 상태 정리
      adminToken.value = null;
      adminRefreshToken.value = null;
      adminUser.value = null;
      localStorage.removeItem('adminAccessToken');
      localStorage.removeItem('adminRefreshToken');
      localStorage.removeItem('adminUser');
      
      console.log('관리자 로그아웃 완료');
    }
  }

  async function refreshAdminToken() {
    if (!adminRefreshToken.value) {
      console.log('리프레시 토큰이 없습니다.');
      return false;
    }

    try {
      const response = await http.post('/users/auth/refresh', {}, {
        headers: {
          'Cookie': `refresh_token=${adminRefreshToken.value}`
        },
        withCredentials: true
      });

      const { accessToken, refreshToken } = response.data;
      setAdminTokens(accessToken, refreshToken);
      
      console.log('관리자 토큰 갱신 성공');
      return true;
    } catch (error) {
      console.error('관리자 토큰 갱신 실패:', error);
      adminLogout();
      return false;
    }
  }

  function getAdminAuthHeaders() {
    if (!adminToken.value) return {};
    
    return {
      'Authorization': `Bearer ${adminToken.value}`
    };
  }

  // 토큰 유효성 자동 체크
  function checkAdminTokenValidity() {
    if (adminToken.value && isAdminTokenExpired(adminToken.value)) {
      console.log('관리자 세션 만료, 토큰 갱신 시도...');
      refreshAdminToken();
    }
  }

  return {
    // State
    adminToken,
    adminRefreshToken,
    adminUser,
    
    // Computed
    isAdminAuthenticated,
    
    // Actions
    adminLogin,
    adminLogout,
    refreshAdminToken,
    setAdminTokens,
    setAdminUser,
    getAdminAuthHeaders,
    checkAdminTokenValidity
  };
});

