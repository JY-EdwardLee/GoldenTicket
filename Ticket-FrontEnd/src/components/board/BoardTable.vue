<template>
  <table class="board-table">
    <thead>
      <tr>
        <th>번호</th>
        <th>제목</th>
        <th>작성자</th>
        <th>등록일</th>
        <th>조회수</th>
        <th>좋아요</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="(post, index) in posts" :key="post.postId" @click="handleRowClick(post)">
        <td>{{ getDisplayNumber(index) }}</td>
        <td class="title-cell">{{ post.title }}</td>
        <td class="author-cell">{{ post.nickName || '알 수 없음' }}</td>
        <td>{{ formatDate(post.createdAt) }}</td>
        <td>{{ post.viewCount?.toLocaleString() || 0 }}</td>
        <td class="like-cell">
          <span class="like-icon">❤️</span>
          {{ post.likeCount?.toLocaleString() || 0 }}
        </td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const props = defineProps({
  posts: {
    type: Array,
    required: true
  },
  boardType: {
    type: String,
    default: 'free'
  },
  currentPage: {
    type: Number,
    default: 1
  },
  totalPosts: {
    type: Number,
    default: 0
  }
});

defineEmits(['postClick']);

// 표시할 번호 계산 (페이지네이션 고려, 최신순)
const getDisplayNumber = (index) => {
  // 최신 글이 1번이 되도록 순차적으로 계산
  const itemsPerPage = 10;
  const currentPosition = (props.currentPage - 1) * itemsPerPage + index;
  
  // 전체 게시글 수에서 현재 위치를 빼서 순차 번호 생성
  // 예: 전체 25개, 현재 위치가 0번째면 25번, 1번째면 24번...
  return props.totalPosts - currentPosition;
};

// 날짜 포맷팅 함수
const formatDate = (dateString) => {
  if (!dateString) return '';
  
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  return `${year}.${month}.${day}`;
};

const handleRowClick = (post) => {
  // 상세 페이지로 이동 (게시판 타입 포함)
  router.push({
    name: 'BoardDetail',
    params: { id: post.postId },
    query: { type: props.boardType }
  });
};
</script>

<style scoped>
.board-table {
  width: 100%;
  border-collapse: collapse;
  margin: 0 auto;
  background: #fff;
}

.board-table th, 
.board-table td {
  border-bottom: 1px solid #f0f0f0;
  padding: 14px 0;
  text-align: center;
  font-size: 15px;
}

.board-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #222;
}

.board-table tr:last-child td {
  border-bottom: none;
}

.board-table tbody tr {
  cursor: pointer;
  transition: background-color 0.2s;
}

.board-table tbody tr:hover {
  background-color: #f9fafb;
}

.title-cell {
  text-align: left;
  padding-left: 20px;
}

.author-cell {
  font-weight: 500;
  color: #374151;
}

.like-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  color: #e11d48;
  font-weight: 500;
}

.like-icon {
  font-size: 14px;
}
</style> 