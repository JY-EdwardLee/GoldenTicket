<template>
  <div class="board-header">
    <div class="header-left">
      <span class="board-title">{{ title }}</span>
    </div>
    <div class="search-container">
      <!-- 검색 타입 선택 -->
      <div class="search-type-wrapper">
        <select 
          v-model="searchType" 
          class="search-type-select"
          @change="handleSearchTypeChange"
        >
          <option value="title">제목</option>
          <option value="content">내용</option>
          <option value="writer">작성자</option>
        </select>
      </div>
      
      <!-- 검색 입력창 -->
      <div class="search-box">
        <input 
          type="text" 
          :placeholder="`${searchTypeName}을 입력하세요`"
          :value="searchValue"
          @input="handleSearchInput"
          @keyup.enter="handleSearch"
          class="search-input"
        />
        <button class="search-button" @click="handleSearch">
          <span class="search-icon">🔍</span>
        </button>
      </div>
      
      <!-- 검색 초기화 버튼 -->
      <button 
        v-if="searchValue" 
        class="clear-button" 
        @click="handleClearSearch"
        title="검색 초기화"
      >
        ✕
      </button>
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

const emit = defineEmits(['update:searchValue', 'search', 'searchTypeChange', 'clearSearch']);

const searchType = ref('title');

// 검색 타입 한글명
const searchTypeName = computed(() => {
  return SEARCH_TYPE_NAMES[searchType.value] || '검색어';
});

// 검색 입력 처리
const handleSearchInput = (event) => {
  const value = event.target.value;
  emit('update:searchValue', value);
  
  // 검색어가 비어있을 때만 전체 목록 표시
  if (value.trim() === '') {
    emit('clearSearch');
  }
};

// 검색 실행 (엔터키 또는 검색 버튼 클릭 시)
const handleSearch = () => {
  if (!props.searchValue.trim()) {
    emit('clearSearch');
    return;
  }
  
  // 한글, 영어 모두 2자 이상인지 확인
  if (props.searchValue.trim().length < 2) {
    return; // 2자 미만이면 검색하지 않음
  }
  
  emit('search', searchType.value, props.searchValue.trim());
};

// 검색 타입 변경 시 이벤트 발생
const handleSearchTypeChange = () => {
  emit('searchTypeChange', searchType.value);
};

// 검색 초기화
const handleClearSearch = () => {
  emit('update:searchValue', '');
  emit('clearSearch');
};
</script>

<style scoped>
.board-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding: 16px 24px;
  background: #ffffff;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-left {
  display: flex;
  align-items: center;
}

.board-title {
  font-size: 18px;
  font-weight: 600;
  color: #333333;
}

.search-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.search-type-wrapper {
  position: relative;
}

.search-type-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #ffffff;
  font-size: 14px;
  color: #333333;
  cursor: pointer;
  outline: none;
  transition: all 0.2s ease;
  min-width: 80px;
}

.search-type-select:focus {
  border-color: #007acc;
  box-shadow: 0 0 0 2px rgba(0, 122, 204, 0.2);
}

.search-type-select option {
  background: #ffffff;
  color: #333333;
}

.search-box {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #ffffff;
  overflow: hidden;
  width: 280px;
  transition: all 0.2s ease;
}

.search-box:focus-within {
  border-color: #007acc;
  box-shadow: 0 0 0 2px rgba(0, 122, 204, 0.2);
}

.search-input {
  border: none;
  background: transparent;
  outline: none;
  padding: 10px 12px;
  font-size: 14px;
  width: 100%;
  color: #333333;
}

.search-input::placeholder {
  color: #999;
}

.search-button {
  background: #007acc;
  border: none;
  padding: 10px 12px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.search-button:hover {
  background: #005a9e;
}

.search-icon {
  font-size: 16px;
  color: #ffffff;
}

.clear-button {
  background: #f5f5f5;
  border: 1px solid #ddd;
  border-radius: 6px;
  color: #666;
  padding: 8px 10px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 36px;
}

.clear-button:hover {
  background: #e0e0e0;
  border-color: #ccc;
  color: #333;
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .board-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 12px 16px;
  }
  
  .search-container {
    flex-direction: column;
    gap: 12px;
  }
  
  .search-box {
    width: 100%;
  }
  
  .search-type-wrapper {
    width: 100%;
  }
  
  .search-type-select {
    width: 100%;
  }
}
</style> 