<template>
  <div>
    <BoardHeader 
      title="공지사항"
      v-model:searchValue="searchValue"
      @search="handleSearch"
    />
    
    <BoardTable 
      :posts="noticePosts"
      @postClick="handlePostClick"
    />
    
    <BoardPagination 
      :currentPage="currentPage"
      :totalPages="totalPages"
      :hasWriteButton="false"
      @pageChange="handlePageChange"
    />
  </div>
</template>

<script setup>
import { ref } from 'vue';
import BoardHeader from './BoardHeader.vue';
import BoardTable from './BoardTable.vue';
import BoardPagination from './BoardPagination.vue';

const searchValue = ref('');
const currentPage = ref(1);
const totalPages = ref(5);

const noticePosts = ref([
  {
    id: 4,
    title: '2024 시즌 티켓 교환 관련 공지사항',
    date: '2024.01.15',
    views: 1245,
    comments: 23
  },
  {
    id: 3,
    title: '양도 시 유의사항 안내',
    date: '2024.01.14',
    views: 892,
    comments: 15
  },
  {
    id: 2,
    title: '응모 시스템 업데이트 안내',
    date: '2024.01.13',
    views: 756,
    comments: 8
  },
  {
    id: 1,
    title: '신규 회원 가입 이벤트',
    date: '2024.01.12',
    views: 634,
    comments: 12
  }
]);

const handleSearch = () => {
  console.log('검색:', searchValue.value);
  // TODO: 실제 검색 로직 구현
};

const handlePostClick = (post) => {
  console.log('게시글 클릭:', post);
  // TODO: 게시글 상세 페이지로 이동
};

const handlePageChange = (page) => {
  currentPage.value = page;
  console.log('페이지 변경:', page);
  // TODO: 페이지 변경 로직 구현
};
</script> 