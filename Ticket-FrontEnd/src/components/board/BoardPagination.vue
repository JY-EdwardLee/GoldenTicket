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
  background: #e11d48;
  color: #fff;
}

.page.active:hover {
  background: #e11d48;
}

.page-arrow:hover {
  background-color: #e5e7eb;
}

/* Responsive design */
@media (max-width: 768px) {
  .pagination {
    gap: 6px;
    margin-top: 16px;
  }
  
  .page, 
  .page-arrow {
    width: 24px;
    height: 24px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .pagination {
    gap: 4px;
    margin-top: 12px;
  }
  
  .page, 
  .page-arrow {
    width: 20px;
    height: 20px;
    font-size: 12px;
  }
}

@media (max-width: 360px) {
  .pagination {
    gap: 3px;
  }
  
  .page, 
  .page-arrow {
    width: 18px;
    height: 18px;
    font-size: 11px;
  }
}
</style> 