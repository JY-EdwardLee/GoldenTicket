<template>
  <a 
    :href="authUrl" 
    class="social-login-button"
    :class="[provider]"
    @click="handleClick"
  >
    <div class="social-icon">
      <img 
        v-if="provider === 'kakao'" 
        src="@@/assets/images/kakao_login_large_narrow.png" 
        alt="카카오 로그인"
        class="social-image"
      >
      <img 
        v-else-if="provider === 'naver'" 
        src="@@/assets/images/btnG_완성형.png" 
        alt="네이버 로그인"
        class="social-image"
      >
      <span v-else class="social-label">{{ label }}</span>
    </div>
  </a>
</template>

<script setup>

const props = defineProps({
  provider: {
    type: String,
    required: true,
    validator: (value) => ['kakao', 'naver'].includes(value)
  },
  label: {
    type: String,
    default: ''
  },
  authUrl: {
    type: String,
    required: true
  }
});

const emit = defineEmits(['click']);

const handleClick = (e) => {
  emit('click', e);
  // 기본 동작을 막지 않아서 링크 이동이 정상적으로 동작합니다.
};
</script>

<style scoped>
.social-login-button {
  display: block;
  width: 100%;
  height: 56px;
  border-radius: 8px;
  text-decoration: none;
  margin-bottom: 12px;
  transition: opacity 0.2s ease;
  border: none;
  background: none;
  padding: 0;
  cursor: pointer;
  text-align: center;
}

.social-login-button:hover {
  opacity: 0.9;
}

.social-login-button:active {
  opacity: 0.8;
}

.social-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
}

.social-image {
  max-width: 100%;
  height: 100%;
  object-fit: contain;
  border-radius: 8px;
}

/* 카카오 버튼 스타일 */
.kakao {
  background-color: #FEE500;
}

/* 네이버 버튼 스타일 */
.naver {
  background-color: #03C75A;
}

.social-label {
  color: white;
  font-weight: 600;
  font-size: 16px;
}
</style>
