<template>
  <div class="board-header">
    <span class="board-title">{{ title }}</span>
    <div class="search-box">
      <input 
        type="text" 
        placeholder="검색어를 입력하세요" 
        :value="searchValue"
        @input="$emit('update:searchValue', $event.target.value)"
      />
      <span class="search-icon" @click="$emit('search')">🔍</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  searchValue: {
    type: String,
    default: ''
  }
});

defineEmits(['update:searchValue', 'search']);
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
}
</style> 