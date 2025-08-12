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
      <GroupBoard v-else :initialTeam="initialTeam" />
    </main>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import NoticeBoard from '@/components/board/NoticeBoard.vue';
import FreeBoard from '@/components/board/FreeBoard.vue';
import GroupBoard from '@/components/board/GroupBoard.vue';
import { TEAM_MAPPING } from '@/api/board.js';
import { useTeamThemeStore } from '@/stores/teamTheme.js'

// 1. 로컬 스토리지에서 사용자 정보를 가져옵니다.
const user = JSON.parse(localStorage.getItem('user'))

// 2. 팀 테마 스토어를 가져옵니다.
const themeStore = useTeamThemeStore

// 3. 사용자 정보에 myTeam 값이 있으면 해당 팀으로 테마를 설정합니다.
if (user?.myTeam) {
  themeStore.setSelectedTeam(user.myTeam)
}

// myTeam을 UI 키로 변환
const KOREAN_TO_UI = {
  'SSG 랜더스': 'SSG',
  'KIA 타이거즈': 'KIA',
  'LG 트윈스': 'LG',
  'KT 위즈': 'KT',
  '키움 히어로즈': 'KIWOOM',
  '삼성 라이온즈': 'SAMSUNG',
  '롯데 자이언츠': 'LOTTE',
  '두산 베어스': 'DOOSAN',
  '한화 이글스': 'HANHWA',
  'NC 다이노스': 'NC'
};

const toUiTeam = (team) => {
  if (!team) return null;
  if (TEAM_MAPPING[team]) return team; // already UI key
  const entry = Object.entries(TEAM_MAPPING).find(([, api]) => api === team);
  if (entry) return entry[0];
  return KOREAN_TO_UI[team] || null;
};

const route = useRoute();
const router = useRouter();
const selectedTab = ref('notice');
const initialTeam = computed(() => toUiTeam(user?.myTeam) || 'all');

onMounted(() => {
  const tabParam = route.query.tab;
  if (tabParam && ['notice', 'free', 'group'].includes(tabParam)) {
    selectedTab.value = tabParam;
  }
});

// 탭 변경 시 URL 쿼리 동기화 (뒤로가기 시 정확한 탭으로 복귀)
watch(selectedTab, (tab) => {
  router.replace({ query: { ...route.query, tab } });
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
