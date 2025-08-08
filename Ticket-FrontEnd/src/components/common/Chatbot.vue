<template>
  <div>
    <!-- 채팅 버튼 -->
    <v-btn
      dark
      fixed
      bottom
      right
      icon
      size="64"
      :color="themeColors.gradient"
      class="chat-button"
      @click="toggleChat"
    >
      <v-icon class="white--text" size="32" v-if="!isChatOpen">mdi-chat-processing</v-icon>
      <v-icon class="white--text" size="32" v-else>mdi-close</v-icon>
    </v-btn>

    <!-- 채팅창 -->
    <v-card v-if="isChatOpen" class="chat-window" width="350" height="600">
      <!-- 헤더 -->
      <v-toolbar color="white" light flat class="chat-header">
        <v-btn icon @click="toggleChat">
          <v-icon>mdi-arrow-left</v-icon>
        </v-btn>
        <v-avatar :color="themeColors.primary" size="36" class="mr-3">
          <v-icon dark>mdi-robot</v-icon>
        </v-avatar>
        <v-toolbar-title class="font-weight-bold">골티봇</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-btn icon>
          <v-icon>mdi-dots-vertical</v-icon>
        </v-btn>
      </v-toolbar>

      <!-- 메시지 영역 -->
      <v-card-text class="messages-container" ref="messagesContainer">
         <div class="text-center my-2">
          <span class="date-divider">{{ new Date().toLocaleDateString() }}</span>
        </div>
        <div v-for="(message, index) in messages" :key="index" :class="['message-row', message.sender]">
          <div v-if="message.sender === 'bot'" class="bot-avatar">
             <v-avatar :color="themeColors.primary" size="32">
                <v-icon small dark>mdi-robot</v-icon>
             </v-avatar>
          </div>
          <div :class="['message', message.sender]">
            <p class="message-text" v-html="message.text.replace(/\n/g, '<br>')"></p>
          </div>
           <span class="message-time">{{ message.time }}</span>
        </div>
        <div v-if="isTyping" class="message-row bot">
           <div class="bot-avatar">
             <v-avatar :color="themeColors.primary" size="32">
                <v-icon small dark>mdi-robot</v-icon>
             </v-avatar>
           </div>
           <div class="message bot typing-indicator">
              <div class="dot"></div><div class="dot"></div><div class="dot"></div>
           </div>
        </div>
      </v-card-text>

      <!-- 입력 영역 -->
      <v-card-actions class="input-area">
        <v-text-field
          v-model="newMessage"
          @keypress.enter.prevent="sendMessage"
          placeholder="AI 에이전트에게 질문해 주세요."
          filled
          rounded
          dense
          hide-details
          class="chat-input"
        >
          <template v-slot:append>
            <v-icon>mdi-emoticon-happy-outline</v-icon>
            <v-icon class="mx-1">mdi-paperclip</v-icon>
            <v-btn icon small :color="themeColors.primary" @click="sendMessage" :disabled="!newMessage.trim()">
              <v-icon>mdi-send</v-icon>
            </v-btn>
          </template>
        </v-text-field>
      </v-card-actions>
    </v-card>
  </div>
</template>

<script setup>
import { ref, nextTick, computed } from 'vue';
import {API_CONFIG} from '@/config/api.config.js'
import { useTeamThemeStore } from '@/stores/teamTheme';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';
import axios from 'axios';
import http from '@/utils/http';

const authStore = useAuthStore();
const themeStore = useTeamThemeStore;
const router = useRouter();
themeStore.initializeTheme()
const themeColors = computed(() => themeStore.currentTheme.value);

const isChatOpen = ref(false);
const newMessage = ref('');
const messages = ref([
  {
    id: 0,
    sender: 'bot',
    text: '안녕하세요! 무엇을 도와드릴까요?',
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
  }
]);
const isTyping = ref(false);
const messagesContainer = ref(null);

const toggleChat = () => {
  isChatOpen.value = !isChatOpen.value;
};

const sendMessage = async () => {
  if (!newMessage.value.trim()) return;

  // 사용자 메시지 추가
  messages.value.push({
    id: Date.now(),
    sender: 'user',
    text: newMessage.value,
    time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
  });
  newMessage.value = '';

  await nextTick();
  scrollToBottom();

  // 유저 정보 세팅
  const user = JSON.parse(localStorage.getItem('user'))

  // 백엔드에 메시지 전달
  isTyping.value = true;
  try {
    let headers = {
      'Content-Type': 'application/json',
    };
    if (authStore.token?.value) {
      const jwToken = authStore.token.value;
      headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${jwToken}`,
      };
    }
    const response = await axios.post(API_CONFIG.MAIN_PAGE.CHAT, {
      userId: user?.userId || null,
      question: newMessage.value,
      isLogin: authStore.isAuthenticated.value,
    }, {
      headers: headers,
    });
    console.log(response.data);
    
    // 봇 응답 추가
    messages.value.push({
      id: Date.now(),
      sender: 'bot',
      text: response.data.answer,
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    });
    
    // action이 있는 경우 처리
    if (response.data.action && response.data.action.type === 'navigate' && response.data.action.target === 'application') {
      await handleApplicationAction(response.data.action.params);
      router.push(`/mypage/applications`);
    }
    
    await nextTick();
    await nextTick();
    scrollToBottom();
  } catch (error) {
    messages.value.push({
      id: Date.now(),
      sender: 'bot',
      text: '죄송합니다. 다시 시도해주세요.',
      time: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
    });
    await nextTick();
    await nextTick();
    scrollToBottom();
    console.error('Error sending message:', error);
  }
  isTyping.value = false;
};

const scrollToBottom = async () => {
  await nextTick(); // DOM 업데이트 대기
  await new Promise(resolve => setTimeout(resolve, 50)); // 추가 대기
  
  if (messagesContainer.value) {
    messagesContainer.value.scrollTo({
      top: messagesContainer.value.scrollHeight,
      behavior: 'smooth' // 부드러운 스크롤
    });
  }
};
</script>

<style scoped>
.chat-button {
  position: fixed;
  background: linear-gradient(135deg, var(--theme-primary, #ff6b35) 0%, var(--theme-accent, #e55a2e) 100%);
  bottom: 24px;
  right: 24px;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.white--text {
  color: white;
}

.chat-window {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 1000;
  border-radius: 16px;
  box-shadow: 0 5px 25px rgba(0,0,0,0.2);
  display: flex;
  flex-direction: column;
  background-color: #F9F9F9;
}

.chat-header {
  border-bottom: 1px solid #eee;
}

.chat-header .v-toolbar-title {
  font-size: 1rem;
  color: #333;
}

.messages-container {
  flex-grow: 1;
  overflow-y: auto;
  padding: 8px 16px;
  height: 400px;
}

.date-divider {
    display: inline-block;
    background-color: #E0E0E0;
    color: white;
    font-size: 0.75rem;
    padding: 2px 10px;
    border-radius: 12px;
}

.message-row {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-end;
}

.message-row.user {
  justify-content: flex-end;
}

.bot-avatar {
  margin-right: 8px;
}

.message {
  padding: 10px 14px;
  border-radius: 18px;
  max-width: 100%;
  position: relative;
  line-height: 1.5;
}

.message.user {
  background-color: var(--theme-primary);
  color: #ffffff;
  border-bottom-right-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.message.bot {
  background-color: #f2f2f2;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.message-text {
  margin: 0;
  font-size: 0.9rem;
}

.message-time {
  font-size: 0.7rem;
  color: #999;
  margin: 0 5px;
  align-self: flex-end;
}

.input-area {
  padding: 8px 12px;
  background-color: white;
}

.chat-input.v-text-field--filled.v-input--dense.v-text-field--single-line .v-input__control {
    min-height: 48px;
}

.chat-input >>> .v-input__slot {
  padding: 0 12px !important;
  margin-bottom: 0;
}

.typing-indicator {
  display: flex;
  align-items: center;
  padding: 10px 15px;
}

.typing-indicator .dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #BDBDBD;
  margin: 0 2px;
  animation: typing 1.4s infinite;
}

.typing-indicator .dot:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator .dot:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 80%, 100% { transform: scale(0); }
  40% { transform: scale(1.0); }
}
</style>
