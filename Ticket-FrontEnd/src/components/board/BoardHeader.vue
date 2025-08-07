<template>
  <div class="board-header">
    <div class="header-left">
      <span class="board-title">{{ title }}</span>
    </div>
    
    <!-- 웹용 검색 컨테이너 -->
    <div class="search-container desktop-search">
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
    
         <!-- 모바일용 검색 컨테이너 -->
     <div class="mobile-search-container" ref="mobileSearchContainer">
       <!-- 돋보기 버튼 (검색창이 숨겨져 있을 때) -->
       <div v-if="!isMobileSearchVisible" class="mobile-search-toggle" @click.stop="toggleMobileSearch">
         <span class="mobile-search-icon">🔍</span>
       </div>
       
       <!-- 모바일 검색 입력창 -->
       <div v-else class="mobile-search-input-container">
         <!-- 모바일 검색 타입 선택 -->
         <select 
           v-model="searchType" 
           class="mobile-search-type-select"
           @change="handleSearchTypeChange"
         >
           <option value="title">제목</option>
           <option value="content">내용</option>
           <option value="writer">작성자</option>
         </select>
         
         <input 
           type="text" 
           :placeholder="`${searchTypeName}을 입력하세요`"
           :value="searchValue"
           @input="handleSearchInput"
           @keyup.enter="handleSearch"
           class="mobile-search-input"
           ref="mobileSearchInput"
         />
         <button class="mobile-search-btn" @click="handleSearch">검색</button>
       </div>
     </div>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue';
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
const isMobileSearchVisible = ref(false);
const mobileSearchInput = ref(null);
const mobileSearchContainer = ref(null);

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

// 모바일 검색 토글
const toggleMobileSearch = async () => {
  isMobileSearchVisible.value = true;
  await nextTick();
  mobileSearchInput.value?.focus();
};

// document 클릭 이벤트 처리
const handleDocumentClick = (event) => {
  // 돋보기 버튼 클릭은 무시 (이미 toggleMobileSearch에서 처리됨)
  if (event.target.closest('.mobile-search-toggle')) {
    return;
  }
  
  if (isMobileSearchVisible.value && mobileSearchContainer.value) {
    // 검색 컨테이너 외부를 클릭한 경우에만 검색창 닫기
    if (!mobileSearchContainer.value.contains(event.target)) {
      isMobileSearchVisible.value = false;
    }
  }
};

// 컴포넌트 마운트 시 document 클릭 이벤트 리스너 추가
onMounted(() => {
  document.addEventListener('click', handleDocumentClick);
});

// 컴포넌트 언마운트 시 이벤트 리스너 제거
onUnmounted(() => {
  document.removeEventListener('click', handleDocumentClick);
});
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

/* 모바일 검색 컨테이너 */
.mobile-search-container {
  display: none;
}

.mobile-search-toggle {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.2s;
}

.mobile-search-toggle:hover {
  background: #e5e7eb;
}

.mobile-search-icon {
  font-size: 18px;
  color: #6b7280;
}

.mobile-search-input-container {
  display: flex;
  align-items: center;
  gap: 8px;
  animation: slideIn 0.3s ease-out;
  flex-wrap: wrap;
}

.mobile-search-type-select {
  padding: 6px 10px;
  border: 1px solid #d1d5db;
  border-radius: 16px;
  background: #ffffff;
  font-size: 12px;
  color: #333333;
  cursor: pointer;
  outline: none;
  transition: all 0.2s ease;
  min-width: 60px;
}

.mobile-search-type-select:focus {
  border-color: var(--theme-primary, #ff6b35);
}

.mobile-search-type-select option {
  background: #ffffff;
  color: #333333;
  font-size: 12px;
}

.mobile-search-input {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 20px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
  min-width: 140px;
  flex: 1;
}

.mobile-search-input:focus {
  border-color: var(--theme-primary, #ff6b35);
}

.mobile-search-btn {
  padding: 8px 16px;
  background: var(--theme-primary, #ff6b35);
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.mobile-search-btn:hover {
  filter: brightness(90%);
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .board-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
    padding: 12px 16px;
  }
  
  /* 웹용 검색 숨기기 */
  .desktop-search {
    display: none;
  }
  
  /* 모바일 검색 표시 */
  .mobile-search-container {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
  
  .mobile-search-toggle {
    width: 36px;
    height: 36px;
  }
  
  .mobile-search-icon {
    font-size: 16px;
  }
  
  .mobile-search-type-select {
    padding: 5px 8px;
    font-size: 11px;
    min-width: 50px;
  }
  
  .mobile-search-input {
    min-width: 120px;
    font-size: 13px;
  }
  
  .mobile-search-btn {
    padding: 6px 12px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .mobile-search-toggle {
    width: 32px;
    height: 32px;
  }
  
  .mobile-search-icon {
    font-size: 14px;
  }
  
  .mobile-search-type-select {
    padding: 4px 6px;
    font-size: 10px;
    min-width: 45px;
  }
  
  .mobile-search-input {
    min-width: 80px;
    font-size: 12px;
    padding: 6px 10px;
  }
  
  .mobile-search-btn {
    padding: 6px 10px;
    font-size: 12px;
  }
}

@media (max-width: 360px) {
  .mobile-search-toggle {
    width: 28px;
    height: 28px;
  }
  
  .mobile-search-icon {
    font-size: 12px;
  }
  
  .mobile-search-type-select {
    padding: 3px 5px;
    font-size: 9px;
    min-width: 40px;
  }
  
  .mobile-search-input {
    min-width: 60px;
    font-size: 11px;
    padding: 5px 8px;
  }
  
  .mobile-search-btn {
    padding: 5px 8px;
    font-size: 11px;
  }
}
</style> 