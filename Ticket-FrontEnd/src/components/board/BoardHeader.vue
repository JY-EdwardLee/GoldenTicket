<template>
  <div class="board-header">
    <span class="board-title">{{ title }}</span>
    <div class="search-container">
      <!-- 검색 타입 선택 -->
      <select 
        v-model="searchType" 
        class="search-type-select"
        @change="handleSearchTypeChange"
      >
        <option value="title">제목</option>
        <option value="content">내용</option>
        <option value="writer">작성자</option>
      </select>
      
      <!-- 검색 입력창 -->
      <div class="search-box">
        <input 
          type="text" 
          :placeholder="`${searchTypeName}을 입력하세요`"
          :value="searchValue"
          @input="handleSearchInput"
          @keyup.enter="handleSearch"
        />
        <span class="search-icon" @click="handleSearch">🔍</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { SEARCH_TYPE_NAMES } from '@/api/board.js';

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  searchValue: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['update:searchValue', 'search', 'searchTypeChange']);

const searchType = ref('title');

// 검색 타입 한글명
const searchTypeName = computed(() => {
  return SEARCH_TYPE_NAMES[searchType.value] || '검색어';
});

// 검색 입력 처리
const handleSearchInput = (event) => {
  emit('update:searchValue', event.target.value);
};

// 검색 실행
const handleSearch = () => {
  if (!props.searchValue.trim()) {
    alert('검색어를 입력해주세요.');
    return;
  }
  emit('search', searchType.value, props.searchValue.trim());
};

// 검색 타입 변경 시 이벤트 발생
const handleSearchTypeChange = () => {
  emit('searchTypeChange', searchType.value);
};
</script>

<style scoped>
.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding: 0 32px;
}

.board-title {
  font-size: 20px;
  font-weight: bold;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-type-select {
  padding: 6px 8px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  background: white;
  font-size: 14px;
  color: #1f2937;
  cursor: pointer;
  outline: none;
  transition: border-color 0.2s;
}

.search-type-select:focus {
  border-color: #e11d48;
}

.search-box {
  display: flex;
  align-items: center;
  border: 1px solid #eee;
  border-radius: 6px;
  background: #fafafa;
  padding: 0 8px;
  width: 200px;
}

.search-box input {
  border: none;
  background: transparent;
  outline: none;
  padding: 6px 0;
  font-size: 14px;
  width: 100%;
  color: #1f2937; /* 검색 입력 텍스트 색상을 검정으로 설정 */
}

.search-box input::placeholder {
  color: #9ca3af; /* placeholder 색상 설정 */
}

.search-icon {
  margin-left: 4px;
  font-size: 18px;
  color: #888;
  cursor: pointer;
  transition: color 0.2s;
}

.search-icon:hover {
  color: #e11d48;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .board-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
  
  .search-container {
    flex-direction: column;
    gap: 8px;
  }
  
  .search-box {
    width: 100%;
  }
}
</style> 