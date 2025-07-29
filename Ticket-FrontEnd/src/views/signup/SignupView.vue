<template>
  <div class="signup-container">
    <div class="signup-header">
      <h1>회원가입</h1>
      <p>티켓 예매 서비스를 이용하시려면<br>회원가입이 필요해요</p>
    </div>

    <form @submit.prevent="handleSubmit" class="signup-form">
      <div class="form-group">
        <label for="name">이름</label>
        <input
          type="text"
          id="name"
          v-model="formData.userName"
          placeholder="이름을 입력해주세요"
          required
        >
      </div>

      <div class="form-group">
        <label for="email">이메일</label>
        <input
          type="email"
          id="email"
          v-model="formData.email"
          placeholder="이메일을 입력해주세요"
          required
        >
      </div>

      <div class="form-group">
        <label for="phone">전화번호</label>
        <input
          type="tel"
          id="phone"
          v-model="formData.phone"
          placeholder="전화번호를 입력해주세요 (예: 010-1234-5678)"
          required
        >
      </div>

      <div class="form-group">
        <label for="birthdate">생년월일</label>
        <input
          type="date"
          id="birthdate"
          v-model="formData.birthdate"
          required
        >
      </div>

      <div class="form-group">
        <label for="nickname">닉네임</label>
        <input
          type="text"
          id="nickname"
          v-model="formData.nickName"
          placeholder="닉네임을 입력해주세요"
          required
        >
      </div>

      <div class="form-group">
        <label for="favoriteTeam">선호구단</label>
        <select id="favoriteTeam" v-model="formData.favoriteTeam" required>
          <option value="" disabled selected>선호하는 구단을 선택해주세요</option>
          <option value="LG_TWINS">LG 트윈스</option>
          <option value="KT_WIZ">KT 위즈</option>
          <option value="SSG_LANDERS">SSG 랜더스</option>
          <option value="HANHWA_EAGLES">NC 다이노스</option>
          <option value="DOOSAN_BEARERS">두산 베어스</option>
          <option value="KIA_TIGERS">KIA 타이거즈</option>
          <option value="LOTTE_GIANTS">롯데 자이언츠</option>
          <option value="SAMSUNG_LIONS">삼성 라이온즈</option>
          <option value="HANHWA_EAGLES">한화 이글스</option>
          <option value="KIWOOM_HEROES">키움 히어로즈</option>
        </select>
      </div>

      <div class="form-footer">
        <button type="submit" class="submit-btn">가입하기</button>
        <p class="login-link">이미 계정이 있으신가요? <router-link to="/login">로그인</router-link></p>
      </div>
    </form>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { API_CONFIG } from "@/config/api.config.js";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const isSocialSignup = reactive(false);
const socialProvider = reactive('');

const formData = reactive({
  email: "",
  nickName: "",
  socialProvider: "",
  profilePhotoUrl: "",
  userName: "",
  gender: "",
  birthdate: "",
});

const handleSubmit = async () => {
  try {
    const userData = {
      email: formData.email,
      userName: formData.userName,
      nickName: formData.nickName,
      birthDate: formData.birthdate, // YYYY-MM-DD 형식이어야 함
      phoneNumber: formData.phone,
      myTeam: formData.favoriteTeam,
      gender: formData.gender || "MALE", // 임시값 또는 선택 옵션으로 구현 필요
      SocialProvider: isSocialSignup.value ? socialProvider.value : 'kakao',
    };
    console.log(userData);
    // 회원가입 API 호출
    const response = await fetch(`${API_CONFIG.AUTH.SIGNUP}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(userData),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || '회원가입에 실패했습니다.');
    }

    const data = await response.json();
    
    // 회원가입 성공 시 토큰 저장 및 메인 페이지로 이동
    if (data.accessToken) {
      authStore.setToken(data.accessToken);
      
      // 사용자 정보 저장 (있는 경우)
      if (data.user) {
        authStore.setUser(data.user);
      }
      
      // 리다이렉트 처리
      const redirectPath = 'oauth/callback?registered=true&redirect=/';
      await router.push(redirectPath);
    }
  } catch (error) {
    console.error('회원가입 오류:', error);
    alert(error.message || '회원가입 중 오류가 발생했습니다.');
  }
};

// 사용자 데이터 가져오기
const fetchUserData = async (userId) => {
  try {
    const response = await axios.get(
      `${API_CONFIG.AUTH.TEMP_USER}`,
      {
        params: { tempUserId: userId },
      }
    );
    if (response.data) {
      const userData = response.data;
      formData.userName = userData.userName || "";
      formData.email = userData.email || "";
      formData.phone = userData.phone || "";
      formData.birthdate = userData.birthYear + "-" + userData.birthDay || "";
      formData.nickName = userData.nickName || "";
      formData.favoriteTeam = userData.favoriteTeam || "";
    }
  } catch (error) {
    console.error('사용자 데이터를 불러오는 중 오류가 발생했습니다:', error);
  }
};

// 컴포넌트 마운트 시 쿼리 파라미터 확인 및 소셜 데이터로 폼 채우기
onMounted(() => {
  // URL에서 user_id 파라미터 확인
  const userId = route.query.tempUserId;
  if (userId) {
    fetchUserData(userId);
  }
});
</script>

<style scoped>
.signup-container {
  max-width: 400px;
  margin: 0 auto;
  padding: 40px 20px;
  font-family: 'Pretendard', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.signup-header {
  text-align: center;
  margin-bottom: 40px;
}

.signup-header h1 {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.signup-header p {
  font-size: 16px;
  color: #666;
  line-height: 1.5;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

input[type="text"],
input[type="email"],
input[type="tel"],
input[type="date"],
select {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: border-color 0.2s;
}

input[type="text"]:focus,
input[type="email"]:focus,
input[type="tel"]:focus,
input[type="date"]:focus,
select:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

select {
  appearance: none;
  background-image: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23999' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpath d='M6 9l6 6 6-6'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px;
  padding-right: 40px;
}

.submit-btn {
  width: 100%;
  padding: 14px;
  background-color: #3b82f6;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.submit-btn:hover {
  background-color: #2563eb;
}

.form-footer {
  margin-top: 32px;
  text-align: center;
}

.login-link {
  margin-top: 16px;
  font-size: 14px;
  color: #666;
}

.login-link a {
  color: #3b82f6;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>