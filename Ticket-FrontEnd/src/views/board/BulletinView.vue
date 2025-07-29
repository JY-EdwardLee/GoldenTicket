<template>
  <div class="bulletin-root">
    <!-- 게시판 탭 -->
    <div class="board-tabs">
      <button class="tab" :class="{active: selectedTab==='notice'}" @click="selectedTab='notice'">공지사항</button>
      <button class="tab" :class="{active: selectedTab==='free'}" @click="selectedTab='free'">자유게시판</button>
      <button class="tab" :class="{active: selectedTab==='group'}" @click="selectedTab='group'">단체 관련 게시판</button>
    </div>

    <main class="main-content">
      <div v-if="selectedTab==='notice'">
        <div class="board-header">
          <span class="board-title">공지사항</span>
          <div class="search-box">
            <input type="text" placeholder="검색어를 입력하세요" />
            <span class="search-icon">🔍</span>
          </div>
        </div>
        <table class="notice-table">
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
            <tr>
              <td>4</td>
              <td>2024 시즌 티켓 교환 관련 공지사항</td>
              <td>2024.01.15</td>
              <td>1,245</td>
              <td>23</td>
            </tr>
            <tr>
              <td>3</td>
              <td>양도 시 유의사항 안내</td>
              <td>2024.01.14</td>
              <td>892</td>
              <td>15</td>
            </tr>
            <tr>
              <td>2</td>
              <td>응모 시스템 업데이트 안내</td>
              <td>2024.01.13</td>
              <td>756</td>
              <td>8</td>
            </tr>
            <tr>
              <td>1</td>
              <td>신규 회원 가입 이벤트</td>
              <td>2024.01.12</td>
              <td>634</td>
              <td>12</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination">
          <span class="page-arrow">&lt;</span>
          <span class="page active">1</span>
          <span class="page">2</span>
          <span class="page">3</span>
          <span class="page">4</span>
          <span class="page">5</span>
          <span class="page-arrow">&gt;</span>
        </div>
      </div>
      <div v-else-if="selectedTab==='free'">
        <div class="board-header">
          <span class="board-title">자유게시판</span>
          <div class="search-box">
            <input type="text" placeholder="검색어를 입력하세요" />
            <span class="search-icon">🔍</span>
          </div>
        </div>
        <table class="notice-table">
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
            <tr>
              <td>4</td>
              <td>오늘 경기 진짜 대박이었어요!</td>
              <td>2024.01.15</td>
              <td>1,245</td>
              <td>23</td>
            </tr>
            <tr>
              <td>3</td>
              <td>양도 받으신 분 후기 남겨주세요.</td>
              <td>2024.01.14</td>
              <td>892</td>
              <td>15</td>
            </tr>
            <tr>
              <td>2</td>
              <td>이번 시즌 응모 팁 알려주세요.</td>
              <td>2024.01.13</td>
              <td>756</td>
              <td>8</td>
            </tr>
            <tr>
              <td>1</td>
              <td>처음 가입했는데 궁금한 점이 있어요</td>
              <td>2024.01.12</td>
              <td>634</td>
              <td>12</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination free">
          <span class="page-arrow">&lt;</span>
          <span class="page active">1</span>
          <span class="page">2</span>
          <span class="page">3</span>
          <span class="page">4</span>
          <span class="page">5</span>
          <span class="page-arrow">&gt;</span>
        </div>
        <button class="write-btn">글쓰기</button>
      </div>
      <div v-else>
        <div class="board-header">
          <span class="board-title">단체 관련 게시판</span>
          <div class="search-box">
            <input type="text" placeholder="검색어를 입력하세요" />
            <span class="search-icon">🔍</span>
          </div>
        </div>
        <table class="notice-table">
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
            <tr>
              <td>4</td>
              <td>7/30일 SSG 게임 단체 관람 가실분~</td>
              <td>2024.01.15</td>
              <td>1,245</td>
              <td>23</td>
            </tr>
            <tr>
              <td>3</td>
              <td>양도 받으신 분 후기 남겨주세요.</td>
              <td>2024.01.14</td>
              <td>892</td>
              <td>15</td>
            </tr>
            <tr>
              <td>2</td>
              <td>이번 시즌 응모 팁 알려주세요.</td>
              <td>2024.01.13</td>
              <td>756</td>
              <td>8</td>
            </tr>
            <tr>
              <td>1</td>
              <td>처음 가입했는데 궁금한 점이 있어요</td>
              <td>2024.01.12</td>
              <td>634</td>
              <td>12</td>
            </tr>
          </tbody>
        </table>
        <div class="pagination free">
          <span class="page-arrow">&lt;</span>
          <span class="page active">1</span>
          <span class="page">2</span>
          <span class="page">3</span>
          <span class="page">4</span>
          <span class="page">5</span>
          <span class="page-arrow">&gt;</span>
        </div>
        <button class="write-btn">글쓰기</button>
      </div>
    </main>
  </div>
</template>


<script setup>
import { ref } from 'vue';
const selectedTab = ref('notice');
</script>

<style scoped>
.bulletin-root {
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
}
.header {
  width: 100%;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #eee;
  padding: 0 32px;
  font-size: 15px;
}
.logo {
  font-weight: bold;
  font-size: 20px;
}
.nav {
  display: flex;
  gap: 32px;
}
.nav .faq {
  font-weight: bold;
  text-decoration: underline;
}
.auth-btns {
  display: flex;
  gap: 8px;
}
.login-btn {
  background: none;
  border: none;
  color: #333;
  font-size: 14px;
  cursor: pointer;
}
.signup-btn {
  background: #2563eb;
  color: #fff;
  border: none;
  border-radius: 4px;
  padding: 4px 12px;
  font-size: 14px;
  cursor: pointer;
}
.board-tabs {
  display: flex;
  gap: 8px;
  padding: 32px 0 0 0;
  justify-content: center;
}
.tab {
  border: none;
  background: #f5f5f5;
  padding: 8px 24px;
  border-radius: 6px 6px 0 0;
  font-size: 15px;
  color: #333;
  cursor: pointer;
}
.tab.active {
  background: #e11d48;
  color: #fff;
}
.main-content {
  width: 1200px;
  margin: 0 auto;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
  padding: 32px 0 48px 0;
  flex: 1;
}
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
}
.search-icon {
  margin-left: 4px;
  font-size: 18px;
  color: #888;
  cursor: pointer;
}
.notice-table {
  width: 100%;
  border-collapse: collapse;
  margin: 0 auto;
  background: #fff;
}
.notice-table th, .notice-table td {
  border-bottom: 1px solid #f0f0f0;
  padding: 14px 0;
  text-align: center;
  font-size: 15px;
}
.notice-table th {
  background: #f9fafb;
  font-weight: 600;
  color: #222;
}
.notice-table tr:last-child td {
  border-bottom: none;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
}
.pagination.free {
  margin-bottom: 0;
}
.page, .page-arrow {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  font-size: 15px;
  cursor: pointer;
}
.page.active {
  background: #e11d48;
  color: #fff;
}
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

.sns-icon {
  font-family: 'FontAwesome', sans-serif;
  font-style: normal;
  font-size: 18px;
  color: #a8a8a8;
  background: #fff;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.sns-icon:hover {
  background: #e11d48;
  color: #fff;
}
</style>
