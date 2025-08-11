<template>
  <div class="bulletin-root">
    <!-- 게시판 탭 -->
    <div class="board-tabs">
      <button class="tab" :class="{active: selectedTab==='notice'}" @click="selectedTab='notice'">공지사항</button>
      <button class="tab" :class="{active: selectedTab==='free'}" @click="selectedTab='free'">자유게시판</button>
      <button class="tab" :class="{active: selectedTab==='group'}" @click="selectedTab='group'">단체 관람 게시판</button>
    </div>

    <main class="main-content">
      <NoticeBoard v-if="selectedTab==='notice'" />
      <FreeBoard v-else-if="selectedTab==='free'" />
      <GroupBoard v-else />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import NoticeBoard from '@/components/board/NoticeBoard.vue';
import FreeBoard from '@/components/board/FreeBoard.vue';
import GroupBoard from '@/components/board/GroupBoard.vue';
import { boardAPI } from '@/api/board.js';
import { useTeamThemeStore } from '@/stores/teamTheme.js'

// 1. 로컬 스토리지에서 사용자 정보를 가져옵니다.
const user = JSON.parse(localStorage.getItem('user'))

// 2. 팀 테마 스토어를 가져옵니다.
const themeStore = useTeamThemeStore

// 3. 사용자 정보에 myTeam 값이 있으면 해당 팀으로 테마를 설정합니다.
// 이 코드는 컴포넌트가 생성될 때마다 실행되어 현재 사용자의 팀 테마를 적용합니다.
if (user?.myTeam) {
  themeStore.setSelectedTeam(user.myTeam)
}

const route = useRoute();
const selectedTab = ref('notice');

onMounted(() => {
  // 쿼리 파라미터에서 tab 값을 확인하여 탭 설정
  const tabParam = route.query.tab;
  if (tabParam && ['notice', 'free', 'group'].includes(tabParam)) {
    selectedTab.value = tabParam;
  }
});
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
  background: var(--theme-primary, #ff6b35);
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

@media (max-width: 1024px) {
  .board-tabs {
    padding: 24px 16px 0 16px;
    gap: 6px;
  }
  
  .tab {
    padding: 10px 20px;
    font-size: 14px;
  }
  
  .main-content {
    width: 98%;
    padding: 24px 0 32px 0;
  }
}

@media (max-width: 768px) {
  .board-tabs {
    flex-direction: row;
    align-items: center;
    gap: 4px;
    padding: 20px 12px 0 12px;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .tab {
    flex: 1;
    min-width: 100px;
    text-align: center;
    padding: 10px 8px;
    font-size: 12px;
    border-radius: 6px;
    white-space: nowrap;
  }
  
  .main-content {
    width: 100%;
    padding: 16px 0 24px 0;
    border-radius: 0;
    box-shadow: none;
  }
}

@media (max-width: 480px) {
  .board-tabs {
    padding: 16px 8px 0 8px;
    gap: 3px;
    flex-wrap: nowrap;
  }
  
  .tab {
    flex: 1;
    min-width: 80px;
    padding: 8px 4px;
    font-size: 11px;
    border-radius: 4px;
    white-space: nowrap;
  }
  
  .main-content {
    padding: 12px 0 20px 0;
  }
}
</style>
