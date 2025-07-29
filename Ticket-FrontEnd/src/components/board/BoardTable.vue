<template>
  <table class="board-table">
    <thead>
      <tr>
        <th>번호</th>
        <th>제목</th>
        <th>등록일</th>
        <th>조회수</th>
        <th>댓글</th>
      </tr>
    </thead>
    <tbody>
      <tr v-for="post in posts" :key="post.id" @click="$emit('postClick', post)">
        <td>{{ post.id }}</td>
        <td class="title-cell">{{ post.title }}</td>
        <td>{{ post.date }}</td>
        <td>{{ post.views?.toLocaleString() }}</td>
        <td>{{ post.comments }}</td>
      </tr>
    </tbody>
  </table>
</template>

<script setup>
defineProps({
  posts: {
    type: Array,
    required: true
  }
});

defineEmits(['postClick']);
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
</style> 