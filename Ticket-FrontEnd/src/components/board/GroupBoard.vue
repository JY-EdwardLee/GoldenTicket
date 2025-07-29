<template>
  <div>
    <BoardHeader 
      title="단체 관련 게시판"
      v-model:searchValue="searchValue"
      @search="handleSearch"
    />
    
    <BoardTable 
      :posts="groupPosts"
      @postClick="handlePostClick"
    />
    
    <BoardPagination 
      :currentPage="currentPage"
      :totalPages="totalPages"
      :hasWriteButton="true"
      @pageChange="handlePageChange"
    />
    
    <button class="write-btn" @click="handleWriteClick">글쓰기</button>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import BoardHeader from './BoardHeader.vue';
import BoardTable from './BoardTable.vue';
import BoardPagination from './BoardPagination.vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const searchValue = ref('');
const currentPage = ref(1);
const totalPages = ref(5);

const groupPosts = ref([
  {
    id: 4,
    title: '7/30일 SSG 게임 단체 관람 가실분~',
    date: '2024.01.15',
    views: 1245,
    comments: 23
  },
  {
    id: 3,
    title: '양도 받으신 분 후기 남겨주세요.',
    date: '2024.01.14',
    views: 892,
    comments: 15
  },
  {
    id: 2,
    title: '이번 시즌 응모 팁 알려주세요.',
    date: '2024.01.13',
    views: 756,
    comments: 8
  },
  {
    id: 1,
    title: '처음 가입했는데 궁금한 점이 있어요',
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

const handleWriteClick = () => {
  // TODO: 로그인 여부 확인 로직 추가
  // const isLoggedIn = useAuthStore().isLoggedIn;
  // if (!isLoggedIn) {
  //   alert('로그인이 필요합니다.');
  //   router.push('/login');
  //   return;
  // }
  
  // 단체 관련 게시판 타입을 쿼리 파라미터로 전달하여 글쓰기 페이지로 이동
  router.push({
    name: 'BulletinCreate',
    query: { type: 'group' }
  });
};
</script>

<style scoped>
.write-btn {
  float: right;
  margin: 24px 16px 0 0;
  background: #888;
  color: #fff;
  font-size: 15px;
  border: none;
  border-radius: 6px;
  padding: 8px 18px;
  cursor: pointer;
  transition: background 0.2s;
}

.write-btn:hover {
  background: #e11d48;
}
</style> 