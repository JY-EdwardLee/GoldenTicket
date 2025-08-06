<template>
  <div class="pagination" :class="{ 'with-margin': !hasWriteButton }">
    <span class="page-arrow" @click="goToPrevPage">&lt;</span>
    <span 
      v-for="page in visiblePages" 
      :key="page"
      class="page" 
      :class="{ active: page === currentPage }"
      @click="$emit('pageChange', page)"
    >
      {{ page }}
    </span>
    <span class="page-arrow" @click="goToNextPage">&gt;</span>
  </div>
</template>

<script setup>
import { computed } from 'vue';
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

const props = defineProps({
  currentPage: {
    type: Number,
    default: 1
  },
  totalPages: {
    type: Number,
    default: 5
  },
  hasWriteButton: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['pageChange']);

const visiblePages = computed(() => {
  const pages = [];
  for (let i = 1; i <= props.totalPages; i++) {
    pages.push(i);
  }
  return pages;
});

const goToPrevPage = () => {
  if (props.currentPage > 1) {
    emit('pageChange', props.currentPage - 1);
  }
};

const goToNextPage = () => {
  if (props.currentPage < props.totalPages) {
    emit('pageChange', props.currentPage + 1);
  }
};
</script>

<style scoped>
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
}

.pagination.with-margin {
  margin-bottom: 0;
}

.page, 
.page-arrow {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.page:hover,
.page-arrow:hover {
  background-color: #f3f4f6;
}

.page.active {
  background: var(--theme-primary, #ff6b35);
  color: #fff;
}

.page.active:hover {
  background: var(--theme-primary, #ff6b35);
}

.page-arrow:hover {
  background-color: var(--theme-primary, #ff6b35);
}
</style> 