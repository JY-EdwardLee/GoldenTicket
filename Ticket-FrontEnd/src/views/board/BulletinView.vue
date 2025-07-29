<template>
  <div class="bulletin-root">
    <!-- 게시판 탭 -->
    <div class="board-tabs">
      <button class="tab" :class="{active: selectedTab==='notice'}" @click="selectedTab='notice'">공지사항</button>
      <button class="tab" :class="{active: selectedTab==='free'}" @click="selectedTab='free'">자유게시판</button>
      <button class="tab" :class="{active: selectedTab==='group'}" @click="selectedTab='group'">단체 관련 게시판</button>
    </div>

    <main class="main-content">
      <NoticeBoard v-if="selectedTab==='notice'" />
      <FreeBoard v-else-if="selectedTab==='free'" />
      <GroupBoard v-else />
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import NoticeBoard from '@/components/board/NoticeBoard.vue';
import FreeBoard from '@/components/board/FreeBoard.vue';
import GroupBoard from '@/components/board/GroupBoard.vue';

const selectedTab = ref('notice');
</script>

<style scoped>
.bulletin-root {
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
}

.board-tabs {
  display: flex;
  gap: 8px;
  padding: 32px 0 0 0;
  justify-content: center;
}

.tab {
  border: none;
  background: #f5f5f5;
  padding: 8px 24px;
  border-radius: 6px 6px 0 0;
  font-size: 15px;
  color: #333;
  cursor: pointer;
  transition: all 0.2s;
}

.tab:hover {
  background: #e5e7eb;
}

.tab.active {
  background: #e11d48;
  color: #fff;
}

.main-content {
  width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  padding: 32px 0 48px 0;
  flex: 1;
}

/* 반응형 스타일 */
@media (max-width: 1280px) {
  .main-content {
    width: 95%;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .board-tabs {
    flex-direction: column;
    align-items: center;
    gap: 4px;
  }
  
  .tab {
    width: 200px;
    text-align: center;
  }
  
  .main-content {
    padding: 16px 0 24px 0;
  }
}
</style>
