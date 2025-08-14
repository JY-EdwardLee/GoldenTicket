<template>
  <div class="group-board-container">

    <!-- 메인 헤더 -->
    <div class="main-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="main-title">{{ selectedTeamTitle }}</h1>
        </div>
        <!-- 모바일 전용 햄버거 버튼 -->
        <button class="mobile-menu-btn" @click="toggleMobileMenu" aria-label="팀 메뉴 열기">☰</button>
      </div>
    </div>

    <!-- 모바일 팀 선택 드로어 -->
    <div v-if="isMobileMenuOpen" class="mobile-overlay" @click="closeMobileMenu">
      <div class="mobile-drawer" @click.stop>
        <div class="drawer-header">
          <span>팀 선택</span>
          <button class="drawer-close" @click="closeMobileMenu" aria-label="닫기">✕</button>
        </div>
        <div class="drawer-team-list">
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'SSG' }" @click="selectTeam('SSG')">SSG 랜더스</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'KIA' }" @click="selectTeam('KIA')">KIA 타이거즈</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'LG' }" @click="selectTeam('LG')">LG 트윈스</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'KT' }" @click="selectTeam('KT')">KT 위즈</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'KIWOOM' }" @click="selectTeam('KIWOOM')">키움 히어로즈</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'SAMSUNG' }" @click="selectTeam('SAMSUNG')">삼성 라이온즈</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'LOTTE' }" @click="selectTeam('LOTTE')">롯데 자이언츠</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'DOOSAN' }" @click="selectTeam('DOOSAN')">두산 베어스</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'HANHWA' }" @click="selectTeam('HANHWA')">한화 이글스</div>
          <div class="drawer-team-item" :class="{ active: selectedTeam === 'NC' }" @click="selectTeam('NC')">NC 다이노스</div>
        </div>
      </div>
    </div>

    <!-- 야구 팀 리스트 (데스크톱/태블릿 표시) -->
    <div class="team-list-container">
      <div class="team-list">
        <div class="team-item" :class="{ 'active': selectedTeam === 'SSG' }" @click="selectedTeam = 'SSG'">
          <span class="team-name">SSG 랜더스</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'KIA' }" @click="selectedTeam = 'KIA'">
          <span class="team-name">KIA 타이거즈</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'LG' }" @click="selectedTeam = 'LG'">
          <span class="team-name">LG 트윈스</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'KT' }" @click="selectedTeam = 'KT'">
          <span class="team-name">KT 위즈</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'KIWOOM' }" @click="selectedTeam = 'KIWOOM'">
          <span class="team-name">키움 히어로즈</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'SAMSUNG' }" @click="selectedTeam = 'SAMSUNG'">
          <span class="team-name">삼성 라이온즈</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'LOTTE' }" @click="selectedTeam = 'LOTTE'">
          <span class="team-name">롯데 자이언츠</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'DOOSAN' }" @click="selectedTeam = 'DOOSAN'">
          <span class="team-name">두산 베어스</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'HANHWA' }" @click="selectedTeam = 'HANHWA'">
          <span class="team-name">한화 이글스</span>
        </div>
        <div class="team-item" :class="{ 'active': selectedTeam === 'NC' }" @click="selectedTeam = 'NC'">
          <span class="team-name">NC 다이노스</span>
        </div>
      </div>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading" class="loading-container">
      <div class="loading-spinner"></div>
      <p>게시글을 불러오는 중...</p>
    </div>
    
    <!-- 에러 상태 -->
    <div v-else-if="error" class="error-container">
      <p class="error-message">{{ error }}</p>
      <button @click="loadPosts" class="retry-btn">다시 시도</button>
    </div>
    
    <!-- 게시글 목록 -->
    <div v-else class="posts-grid">
      <div 
        v-for="post in displayedPosts" 
        :key="post.postId" 
        :class="['post-card', { 'completed': post.status === 'completed' }]"
        @click="handlePostClick(post)"
      >
        <div class="post-main">
          <!-- 카드 이미지 영역 -->
          <div class="card-image-section">
          <img 
            :src="post.imageUrl || '@/assets/images/default-group.jpg'" 
            :alt="post.title"
            class="card-image"
            @error="handleImageError"
          />
          <!-- 상태 배지 -->
          <div class="status-badges">
            <span 
              v-if="getStatusBadge(post)" 
              :class="['status-badge', getStatusBadge(post).type]"
            >
              {{ getStatusBadge(post).text }}
            </span>
          </div>
          <!-- 참가 인원 -->
          <div class="participant-count">
            {{ post.currentParticipants || 0 }}/{{ post.maxParticipants || 20 }}명
          </div>
          </div>

          <!-- 카드 내용 영역 -->
          <div class="card-content">
          <div class="title-row">
            <h3 class="post-title">{{ post.title }}</h3>
          </div>
          
          <div class="post-info">
            <div class="info-item">
              <span class="info-icon"><i class="fa-regular fa-calendar-check fa-lg"></i></span>
              <span>{{ formatDate(post.gameDate) }} {{ post.gameTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-icon"><i class="fa-solid fa-baseball-bat-ball"></i></span>
              <span>{{ post.location }}</span>
            </div>
            <div class="info-item">
              <span class="info-icon"><i class="fa-solid fa-users"></i></span>
              <span>{{ post.currentParticipants || 0 }}/{{ post.maxParticipants || 20 }}명 참가</span>
            </div>
          </div>

          <p class="post-description">{{ post.description }}</p>

          </div>
        </div>
        <!-- 카드 하단 풋터: 상단 참여율, 하단 좌우 대칭(좌: 등록정보 / 우: 버튼) -->
        <div class="card-footer">
          <div class="participation-rate">
            <span class="rate-label">참가율</span>
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: getParticipationRate(post) + '%' }"></div>
            </div>
            <span class="rate-percentage">{{ getParticipationRate(post) }}%</span>
          </div>
          <div class="footer-row">
            <div class="registration-info">
              <span class="reg-date">{{ formatDate(post.createdAt) }} 등록</span>
              <span class="conditions">{{ post.conditions }}</span>
            </div>
            <div class="action-buttons">
              <button
                class="apply-btn"
                @click.stop="handleApplyClick('apply', post)"
              >
                <span class="apply-icon"><i class="fa-regular fa-user"></i></span>
                관람 신청
              </button>
              <button
                class="apply-btn"
                @click.stop="handleApplyClick('cancel', post)"
              >
                <span class="apply-icon">✖</span>
                신청 취소
              </button>
            </div>
          </div>
        </div>
         
      </div>
    </div>
    
    <!-- 페이지네이션 -->
    <div class="pagination-container">
      <button 
        :disabled="currentPage === 1" 
        @click="handlePageChange(currentPage - 1)"
        class="page-btn"
      >
        이전
      </button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button 
        :disabled="currentPage === totalPages" 
        @click="handlePageChange(currentPage + 1)"
        class="page-btn"
      >
        다음
      </button>
    </div>

    
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { boardAPI, transformGroupData } from '@/api/board';

const props = defineProps({
  initialTeam: { type: String, default: 'SSG' }
});

const router = useRouter();
const authStore = useAuthStore();
const currentPage = ref(1);
const itemsPerPage = 6; // 페이지당 카드 수
const isLoading = ref(false);
const error = ref('');
const selectedTeam = ref(props.initialTeam || 'SSG');

const isMobileMenuOpen = ref(false);
const toggleMobileMenu = () => { isMobileMenuOpen.value = !isMobileMenuOpen.value; };
const closeMobileMenu = () => { isMobileMenuOpen.value = false; };
const selectTeam = (teamKey) => { selectedTeam.value = teamKey; closeMobileMenu(); };

const allPosts = ref([]);
const displayedPosts = ref([]);

// 내 응모 상태를 로컬에 저장해 UI를 제어한다 (동일 날짜 제한 제거)
const APPLIED_STORAGE_KEY = 'my-group-applications-v2'; // [groupId,...]
const appliedGroupIds = ref(new Set());

const loadAppliedSet = () => {
  try {
    const raw = localStorage.getItem(APPLIED_STORAGE_KEY);
    const arr = raw ? JSON.parse(raw) : [];
    appliedGroupIds.value = new Set(Array.isArray(arr) ? arr : []);
  } catch (e) {
    appliedGroupIds.value = new Set();
  }
};

const persistAppliedSet = () => {
  try {
    localStorage.setItem(APPLIED_STORAGE_KEY, JSON.stringify(Array.from(appliedGroupIds.value)));
  } catch (e) {
    // ignore
  }
};

// 선택된 팀의 제목
const selectedTeamTitle = computed(() => {
  const teamNames = {
    'SSG': 'SSG 랜더스 단체 관람 모집',
    'KIA': 'KIA 타이거즈 단체 관람 모집',
    'LG': 'LG 트윈스 단체 관람 모집',
    'KT': 'KT 위즈 단체 관람 모집',
    'KIWOOM': '키움 히어로즈 단체 관람 모집',
    'SAMSUNG': '삼성 라이온즈 단체 관람 모집',
    'LOTTE': '롯데 자이언츠 단체 관람 모집',
    'DOOSAN': '두산 베어스 단체 관람 모집',
    'HANHWA': '한화 이글스 단체 관람 모집',
    'NC': 'NC 다이노스 단체 관람 모집'
  };
  return teamNames[selectedTeam.value] || '단체 관람 게시판';
});

// 총 페이지 수
const filteredPosts = computed(() => {
  let filtered = allPosts.value.filter(post => post.team === selectedTeam.value);
  const now = new Date();
  const getGameDateTime = (post) => {
    const datePart = post?.gameDate || '';
    const timePart = post?.gameTime || '00:00';
    const dtStr = datePart ? `${datePart}T${timePart}:00` : '';
    const dt = dtStr ? new Date(dtStr) : null;
    if (dt && !isNaN(dt.getTime())) return dt;
    const created = post?.createdAt ? new Date(post.createdAt) : null;
    return created && !isNaN(created.getTime()) ? created : new Date(0);
  };
  filtered.sort((a, b) => {
    const da = getGameDateTime(a);
    const db = getGameDateTime(b);
    const aFuture = da >= now;
    const bFuture = db >= now;
    if (aFuture !== bFuture) return aFuture ? -1 : 1; // 미래 일정 우선
    if (aFuture && bFuture) return da - db;            // 미래: 가까운 순 (오름차순)
    return db - da;                                    // 과거: 가까운 순 (내림차순)
  });
  return filtered;
});

const totalPages = computed(() => Math.ceil(filteredPosts.value.length / itemsPerPage));

// 내 응모 여부만 판단 (동일 날짜 제한 제거)
const isMyApplied = (post) => {
  if (!post) return false;
  return post.groupId != null && appliedGroupIds.value.has(post.groupId);
};

// 게시글 목록 로드 (API 사용)
const loadPosts = async () => {
  isLoading.value = true;
  error.value = '';
  try {
    const raw = await boardAPI.getGroupList(selectedTeam.value);
    const transformed = transformGroupData(raw);
    allPosts.value = transformed;
    updateDisplayedPosts();
  } catch (err) {
    console.error('단체관람 목록 로드 실패:', err);
    error.value = '데이터를 불러오는데 실패했습니다. 다시 시도해주세요.';
    allPosts.value = [];
    updateDisplayedPosts();
  } finally {
    isLoading.value = false;
  }
};

// 현재 페이지의 게시글만 표시
const updateDisplayedPosts = () => {
  const startIndex = (currentPage.value - 1) * itemsPerPage;
  const endIndex = startIndex + itemsPerPage;
  displayedPosts.value = filteredPosts.value.slice(startIndex, endIndex);
};

// 상태 배지 반환
const getStatusBadge = (post) => {
  switch (post.status) {
    case 'recruiting':
      return { type: 'recruiting', text: '모집 중' };
    case 'closing':
      return { type: 'closing', text: '마감 임박' };
    case 'completed':
      return { type: 'completed', text: '모집 완료' };
    default:
      return null;
  }
};

// 참가율 계산
const getParticipationRate = (post) => {
  const current = post.currentParticipants || 0;
  const max = post.maxParticipants || 20;
  return Math.round((current / max) * 100);
};

// 날짜 포맷팅
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  }).replace(/\./g, '.');
};

// 이미지 에러 처리
const handleImageError = (event) => {
  event.target.src = '@/assets/images/default-group.jpg';
};

// 이벤트 핸들러들
const handlePostClick = (post) => {
  router.push({
    name: 'BoardDetail',
    params: { postId: post.postId }
  });
};

const handleApplyClick = async (action, post) => {
  try {
    // 클릭 시점에 상태 검증 및 가드 처리
    if (action === 'apply') {
      if (isMyApplied(post)) {
        alert('이미 이 경기에 관람 신청하셨습니다. 관람 취소 버튼을 이용해주세요.');
        return;
      }
    }
    if (action === 'cancel') {
      if (!isMyApplied(post)) {
        alert('이 경기는 아직 관람 신청되지 않았습니다. 먼저 관람 신청을 진행해주세요.');
        return;
      }
    }

    if (action === 'cancel') {
      const res = await boardAPI.cancelGroup(post.groupId);
      alert( '단체 관람 취소에 성공하셨습니다.');
      appliedGroupIds.value.delete(post.groupId);
      persistAppliedSet();
    } else {
      const res = await boardAPI.applyGroup(post.groupId);
      alert( '단체 관람 신청에 성공하셨습니다.');
      appliedGroupIds.value.add(post.groupId);
      persistAppliedSet();
    }
    await loadPosts();
  } catch (err) {
    const msg = err?.message || '요청 처리에 실패했습니다.';
    alert(msg);
    console.error('단체관람 신청/취소 실패:', err);
  }
};

const handlePageChange = (page) => {
  currentPage.value = page;
  updateDisplayedPosts();
};

// 팀 선택 변경 감지 시 API 재호출
const onTeamChange = async () => {
  currentPage.value = 1;
  await loadPosts();
};

watch(selectedTeam, onTeamChange);

onMounted(async () => {
  loadAppliedSet();
  await loadPosts();
});
</script>

<style scoped>
.group-board-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: 100vh;
  font-size: 16px;
}

/* 탭 네비게이션 */
.tab-navigation {
  display: flex;
  background: white;
  border-radius: 12px;
  padding: 4px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tab-item {
  flex: 1;
  padding: 12px 16px;
  text-align: center;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s;
  font-weight: 500;
  color: #6b7280;
}

.tab-item:hover {
  background: #f3f4f6;
}

.tab-item.active {
  background: #e11d48;
  color: white;
}

/* 메인 헤더 */
.main-header {
  margin-bottom: 32px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.main-title {
  font-size: 36px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.favorite-team-btn {
  background: var(--theme-primary);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 20px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  transition: background 0.2s;
}

.favorite-team-btn:hover {
  background: #be123c;
}

.btn-icon {
  font-size: 16px;
}



/* 야구 팀 리스트 */
.team-list-container {
  margin-bottom: 32px;
}

.team-list {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 12px;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.team-item {
  padding: 12px 16px;
  border-radius: 20px;
  background: #f3f4f6;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s;
  border: 2px solid transparent;
  text-align: center;
  min-height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.team-item:hover {
  background: #e5e7eb;
  color: #374151;
  transform: translateY(-1px);
}

.team-item.active {
  background: var(--theme-primary);
  color: white;
  border-color: var(--theme-primary);
  box-shadow: 0 2px 8px rgba(var(--theme-primary), 0.3);
}

.team-name {
  font-size: 16px;
  font-weight: 500;
  white-space: nowrap;
}



/* 게시글 그리드 */
.posts-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 24px;
  margin-bottom: 32px;
}

.post-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  display: flex;
  flex-direction: column; /* 상단 본문 + 하단 풋터 수직 배치 */
  width: 100%;
  min-height: 300px;
}

/* 이미지 + 본문 가로 정렬 */
.post-main {
  display: flex;
}

.post-card.completed {
  opacity: 0.6;
  filter: grayscale(0.3);
}

.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

/* 카드 이미지 섹션 */
.card-image-section {
  position: relative;
  width: 280px;
  height: 280px;
  overflow: hidden;
  flex-shrink: 0;
}

.card-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.status-badges {
  position: absolute;
  top: 12px;
  left: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.status-badge {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: white;
}

.status-badge.recruiting {
  background: #16a34a;
}

.status-badge.closing {
  background: #ea580c;
}

.status-badge.completed {
  background: #6b7280;
}



.participant-count {
  position: absolute;
  bottom: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 600;
}

/* 카드 내용 */
.card-content {
  padding: 20px;
  padding-bottom: 0px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 280px;
  font-size: 17px; /* 기본 본문 글씨 크기 증가 */
}

.post-title {
  font-size: 26px; /* 제목 크기 증가 */
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

/* 상단 제목 우측 버튼 영역 제거됨 */

.post-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 17px; /* 정보 항목 크기 증가 */
  color: #6b7280;
}

.info-icon {
  font-size: 17px;
  padding: 5px;
}

.post-description {
  font-size: 17px; /* 본문 설명 크기 증가 */
  color: #374151;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  /* -webkit-line-clamp: 2; */
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.meeting-info {
  margin-bottom: 12px;
}

.meeting-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 4px;
}

.meeting-label {
  color: #6b7280;
  font-weight: 500;
}

/* 해시태그 관련 스타일 제거 */

/* 카드 하단 풋터 */
.card-footer {
  display: flex;
  flex-direction: column;
  gap: 14px;
  padding: 16px 20px;
  border-top: 1px solid #f1f5f9;
}
.footer-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

/* participation-rate 내부 공용 스타일 재사용 */
.participation-rate {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}

.rate-label {
  font-size: 17px; /* 라벨 크기 증가 */
  color: #6b7280;
  font-weight: 500;
}

.progress-bar {
  flex: 1;
  height: 12px; /* 바 높이 확장 */
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #e11d48;
  transition: width 0.3s;
}

.rate-percentage {
  font-size: 16px; /* 퍼센트 크기 증가 */
  font-weight: 600;
  color: #1a1a1a;
  min-width: 35px;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
}

.registration-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.reg-date {
  font-size: 14px; /* 등록일 크기 증가 */
  color: #6b7280;
}

.conditions {
  font-size: 14px; /* 조건 크기 증가 */
  color: #6b7280;
}

.action-buttons {
  display: flex;
  gap: 10px;
  align-items: center;
}

.interest-btn, .apply-btn {
  padding: 14px 22px; /* 버튼 크기 추가 확대 */
  border: none;
  border-radius: 10px;
  font-size: 20px; /* 글자 크기 추가 확대 */
  font-weight: 700;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.apply-icon {
  font-size: 20px;
}

.interest-btn {
  background: white;
  color: #6b7280;
  border: 1px solid #d1d5db;
}

.interest-btn:hover {
  background: #f3f4f6;
}

.apply-btn {
  background: var(--theme-primary);
  color: white;
}

.apply-btn:hover {
  background: var(--theme-gradient);
}

/* 취소 버튼은 동일한 스타일을 사용하도록 제거 (요청사항: 버튼 색상 유지) */

/* 비활성화 상태 명시 (모바일/데스크톱 공통) */
.apply-btn:disabled {
  background: #9ca3af;
  color: #ffffff;
  cursor: not-allowed;
  filter: none;
}


/* 페이지네이션 */
.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.page-btn {
  padding: 8px 16px;
  border: 1px solid #d1d5db;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  background: #f3f4f6;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 16px;
  color: #6b7280;
}



/* 로딩 및 에러 상태 */
.loading-container, .error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f4f6;
  border-top: 4px solid #e11d48;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  color: #dc2626;
  margin-bottom: 16px;
}

.retry-btn {
  background: var(--theme-primary, #ff6b35);
  color: white;
  border: 1px solid var(--theme-primary, #ff6b35);
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.retry-btn:hover {
  filter: brightness(90%);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .group-board-container {
    padding: 16px;
  }
  
  .main-title {
    font-size: 28px;
  }
  
  .team-list {
    grid-template-columns: repeat(2, 1fr); /* 모바일에선 2열씩 */
    gap: 8px;
    padding: 16px;
  }
  
  .team-item {
    padding: 8px 12px;
    min-height: 40px;
  }
  
  .team-name {
    font-size: 15px;
  }
  
  .posts-grid {
    grid-template-columns: 1fr;
  }
  
  .post-card {
    flex-direction: column;
  }

  /* 본문을 세로로 쌓기 */
  .post-main {
    flex-direction: column;
  }
  .card-image-section { width: 100%; height: 200px; }
  
  .card-content {
    min-height: auto;
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .title-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .team-filter-select {
    align-self: flex-end;
  }

  /* 모바일에선 하단 행도 세로 스택 */
  .footer-row {
    flex-direction: column;
    align-items: stretch;
    gap: 10px;
  }
  .action-buttons { flex-direction: column; }
  .apply-btn {
    width: 100%;
    min-height: 48px;
    font-size: 18px;
    line-height: 1.2;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
  .card-footer { padding: 12px 16px; }
  .card-footer .footer-bottom { flex-direction: column; align-items: stretch; gap: 10px; }
}

/* 모바일 햄버거 버튼 */
.mobile-menu-btn {
  display: none;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  border-radius: 6px;
  padding: 6px 10px;
  font-size: 18px;
}

/* 모바일 드로어/오버레이 */
.mobile-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: flex-end;
  z-index: 1000;
}
.mobile-drawer {
  width: 80%;
  max-width: 320px;
  background: #fff;
  height: 100%;
  padding: 16px;
  box-shadow: -2px 0 10px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
}
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: 600;
}
.drawer-close {
  border: none;
  background: transparent;
  font-size: 18px;
  cursor: pointer;
}
.drawer-team-list {
  display: grid;
  grid-template-columns: 1fr;
  gap: 8px;
}
.drawer-team-item {
  padding: 10px 12px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  background: #fff;
}
.drawer-team-item.active {
  background: #e11d48;
  color: #fff;
  border-color: #e11d48;
}

/* 기존 팀 리스트는 모바일에서 숨김 */
@media (max-width: 768px) {
  .mobile-menu-btn { display: inline-flex; align-items: center; }
  .team-list-container { display: none; }
}
</style> 