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
      <tr v-for="post in posts" :key="post.id" @click="handleRowClick(post)">
        <td>{{ post.id }}</td>
        <td class="title-cell">{{ post.title }}</td>
        <td class="author-cell">{{ post.author }}</td>
        <td>{{ post.date }}</td>
        <td>{{ post.views?.toLocaleString() }}</td>
        <td class="like-cell">
          <span class="like-icon">❤️</span>
          {{ post.likes?.toLocaleString() || 0 }}
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
  }
});

defineEmits(['postClick']);

const handleRowClick = (post) => {
  // 상세 페이지로 이동 (게시판 타입 포함)
  router.push({
    name: 'BoardDetail',
    params: { id: post.id },
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