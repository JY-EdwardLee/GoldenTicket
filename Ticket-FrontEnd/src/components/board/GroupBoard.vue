<template>
  <div class="group-board-container">

         <!-- 메인 헤더 -->
     <div class="main-header">
       <div class="header-content">
                   <div class="title-section">
            <h1 class="main-title">{{ selectedTeamTitle }}</h1>
            <button class="favorite-team-btn" @click="handleFavoriteTeamClick">
              <span class="btn-icon">🔍</span>
              내 관심팀
            </button>
          </div>
       </div>
     </div>

                     <!-- 야구 팀 리스트 -->
       <div class="team-list-container">
         <div class="team-list">
           <div class="team-item" :class="{ 'active': selectedTeam === 'all' }" @click="selectedTeam = 'all'">
             <span class="team-name">전체</span>
           </div>
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
           <div class="team-item" :class="{ 'active': selectedTeam === 'HANWHA' }" @click="selectedTeam = 'HANWHA'">
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
        <!-- 카드 이미지 영역 -->
        <div class="card-image-section">
          <img 
            :src="post.imageUrl || '/src/assets/images/default-group.jpg'" 
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
          <h3 class="post-title">{{ post.title }}</h3>
          
          <div class="post-info">
            <div class="info-item">
              <span class="info-icon">📅</span>
              <span>{{ formatDate(post.gameDate) }} {{ post.gameTime }}</span>
            </div>
            <div class="info-item">
              <span class="info-icon">📍</span>
              <span>{{ post.location }}</span>
            </div>
            <div class="info-item">
              <span class="info-icon">👤</span>
              <span>{{ post.organizer }}</span>
            </div>
            <div class="info-item">
              <span class="info-icon">👥</span>
              <span>{{ post.currentParticipants || 0 }}/{{ post.maxParticipants || 20 }}명 참가</span>
            </div>
          </div>

          <p class="post-description">{{ post.description }}</p>

          <div class="meeting-info">
            <div class="meeting-item">
              <span class="meeting-label">집합 장소:</span>
              <span>{{ post.meetingPlace }}</span>
            </div>
            <div class="meeting-item">
              <span class="meeting-label">집합 시간:</span>
              <span>{{ post.meetingTime }}</span>
            </div>
          </div>

          <div class="hashtags">
            <span 
              v-for="tag in post.hashtags" 
              :key="tag" 
              class="hashtag"
            >
              #{{ tag }}
            </span>
          </div>

          <div class="participation-rate">
            <span class="rate-label">참가율</span>
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: getParticipationRate(post) + '%' }"
              ></div>
            </div>
            <span class="rate-percentage">{{ getParticipationRate(post) }}%</span>
          </div>

          <div class="post-footer">
            <div class="registration-info">
              <span class="reg-date">{{ formatDate(post.createdAt) }} 등록</span>
              <span class="conditions">{{ post.conditions }}</span>
            </div>
            <div class="action-buttons">
              <button class="interest-btn" @click.stop="handleInterestClick(post)">
                <span class="heart-icon">❤️</span>
                관심
              </button>
              <button class="apply-btn" @click.stop="handleApplyClick(post)">
                <span class="apply-icon">👤</span>
                참가 신청
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

const router = useRouter();
const authStore = useAuthStore();
const currentPage = ref(1);
const itemsPerPage = 6; // 페이지당 카드 수
const isLoading = ref(false);
const error = ref('');
const selectedTeam = ref('all');

// 더미데이터
const dummyPosts = [
  // SSG 랜더스 경기들
  {
    postId: 1,
    title: "SSG 랜더스 vs KIA 타이거즈",
    gameDate: "2025-08-15",
    gameTime: "18:30",
    location: "인천 SSG 랜더스 필드",
    organizer: "SSG팬클럽회장",
    currentParticipants: 12,
    maxParticipants: 20,
    description: "시즌 마지막 KIA전! 함께 응원하며 즐거운 시간 보내요. 응원용품과 간식 준비해드립니다.",
    meetingPlace: "인천 SSG 랜더스 필드 정문",
    meetingTime: "17:00",
    hashtags: ["정기모임", "응원용품 제공", "사진촬영"],
    status: "recruiting",
    conditions: "매너 좋은 분들만, 끝까지 응원 가능한 분",
    createdAt: "2025-08-10",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "SSG"
  },
  {
    postId: 2,
    title: "SSG 랜더스 vs LG 트윈스",
    gameDate: "2025-08-25",
    gameTime: "14:00",
    location: "인천 SSG 랜더스 필드",
    organizer: "& NO_LIMITS_CREW",
    currentParticipants: 18,
    maxParticipants: 20,
    description: "오후 경기 단체 관람! 점심도 함께 하고 응원용품도 맞춰서 준비해요.",
    meetingPlace: "인천 SSG 랜더스 필드 메인 엔트런스",
    meetingTime: "13:00",
    hashtags: ["오후경기", "점심 포함", "단체 응원"],
    status: "closing",
    conditions: "정시 참석 필수, 단체 응원 참여 의무",
    createdAt: "2025-08-18",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "SSG"
  },
  {
    postId: 3,
    title: "SSG 랜더스 vs 한화 이글스",
    gameDate: "2025-09-01",
    gameTime: "18:30",
    location: "인천 SSG 랜더스 필드",
    organizer: "랜더스가족",
    currentParticipants: 20,
    maxParticipants: 20,
    description: "9월 첫 경기! 가족 단위 참가 환영합니다. 아이들을 위한 간식도 준비되어 있어요.",
    meetingPlace: "인천 SSG 랜더스 필드 정문",
    meetingTime: "17:30",
    hashtags: ["가족 환영", "아이 간식", "9월 개막"],
    status: "completed",
    conditions: "가족 단위 우선, 매너 필수",
    createdAt: "2025-08-22",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "SSG"
  },
  
  // KIA 타이거즈 경기들
  {
    postId: 4,
    title: "KIA 타이거즈 vs SSG 랜더스",
    gameDate: "2025-08-20",
    gameTime: "18:30",
    location: "광주 기아 챔피언스 필드",
    organizer: "KIA 팬클럽",
    currentParticipants: 15,
    maxParticipants: 25,
    description: "홈 경기 단체 관람! KIA 팬들과 함께하는 특별한 경기 관람.",
    meetingPlace: "광주 기아 챔피언스 필드 정문",
    meetingTime: "17:00",
    hashtags: ["홈경기", "KIA팬", "단체관람"],
    status: "recruiting",
    conditions: "KIA 팬 우선, 매너 필수",
    createdAt: "2025-08-12",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "KIA"
  },
  {
    postId: 5,
    title: "KIA 타이거즈 vs LG 트윈스",
    gameDate: "2025-08-28",
    gameTime: "14:00",
    location: "광주 기아 챔피언스 필드",
    organizer: "타이거즈 응원단",
    currentParticipants: 22,
    maxParticipants: 30,
    description: "오후 경기 단체 관람! 응원용품과 간식이 준비되어 있습니다.",
    meetingPlace: "광주 기아 챔피언스 필드 동쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["오후경기", "응원용품", "간식제공"],
    status: "closing",
    conditions: "정시 참석 필수",
    createdAt: "2025-08-20",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "KIA"
  },
  
  // LG 트윈스 경기들
  {
    postId: 6,
    title: "LG 트윈스 vs SSG 랜더스",
    gameDate: "2025-08-22",
    gameTime: "18:30",
    location: "잠실 야구장",
    organizer: "LG 팬클럽",
    currentParticipants: 18,
    maxParticipants: 25,
    description: "잠실 홈 경기! LG 팬들과 함께하는 즐거운 관람 시간.",
    meetingPlace: "잠실 야구장 정문",
    meetingTime: "17:30",
    hashtags: ["잠실홈", "LG팬", "즐거운시간"],
    status: "recruiting",
    conditions: "LG 팬 우선",
    createdAt: "2025-08-15",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "LG"
  },
  {
    postId: 7,
    title: "LG 트윈스 vs 두산 베어스",
    gameDate: "2025-09-05",
    gameTime: "14:00",
    location: "잠실 야구장",
    organizer: "트윈스 응원단",
    currentParticipants: 25,
    maxParticipants: 30,
    description: "두산과의 라이벌전! 특별 응원 이벤트와 함께하는 경기.",
    meetingPlace: "잠실 야구장 서쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["라이벌전", "특별이벤트", "응원단"],
    status: "completed",
    conditions: "응원단 활동 참여 가능한 분",
    createdAt: "2025-08-25",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "LG"
  },
  
  // KT 위즈 경기들
  {
    postId: 8,
    title: "KT 위즈 vs 삼성 라이온즈",
    gameDate: "2025-08-18",
    gameTime: "18:30",
    location: "수원 KT 위즈 파크",
    organizer: "KT 팬클럽",
    currentParticipants: 10,
    maxParticipants: 20,
    description: "수원 홈 경기! KT 팬들과 함께하는 단체 관람.",
    meetingPlace: "수원 KT 위즈 파크 정문",
    meetingTime: "17:00",
    hashtags: ["수원홈", "KT팬", "단체관람"],
    status: "recruiting",
    conditions: "KT 팬 우선",
    createdAt: "2025-08-10",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "KT"
  },
  {
    postId: 9,
    title: "KT 위즈 vs NC 다이노스",
    gameDate: "2025-08-30",
    gameTime: "14:00",
    location: "수원 KT 위즈 파크",
    organizer: "위즈 응원단",
    currentParticipants: 16,
    maxParticipants: 25,
    description: "오후 경기 단체 관람! 응원용품과 간식이 준비되어 있습니다.",
    meetingPlace: "수원 KT 위즈 파크 동쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["오후경기", "응원용품", "간식제공"],
    status: "closing",
    conditions: "정시 참석 필수",
    createdAt: "2025-08-22",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "KT"
  },
  
  // 키움 히어로즈 경기들
  {
    postId: 10,
    title: "키움 히어로즈 vs 한화 이글스",
    gameDate: "2025-08-16",
    gameTime: "18:30",
    location: "고척 스카이돔",
    organizer: "히어로즈 팬클럽",
    currentParticipants: 12,
    maxParticipants: 20,
    description: "고척 홈 경기! 히어로즈 팬들과 함께하는 즐거운 관람.",
    meetingPlace: "고척 스카이돔 정문",
    meetingTime: "17:00",
    hashtags: ["고척홈", "히어로즈팬", "즐거운시간"],
    status: "recruiting",
    conditions: "히어로즈 팬 우선",
    createdAt: "2025-08-08",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "KIWOOM"
  },
  {
    postId: 11,
    title: "키움 히어로즈 vs 롯데 자이언츠",
    gameDate: "2025-08-27",
    gameTime: "14:00",
    location: "고척 스카이돔",
    organizer: "히어로즈 응원단",
    currentParticipants: 20,
    maxParticipants: 25,
    description: "오후 경기 단체 관람! 특별 응원 이벤트가 준비되어 있습니다.",
    meetingPlace: "고척 스카이돔 서쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["오후경기", "특별이벤트", "응원단"],
    status: "completed",
    conditions: "응원단 활동 참여 가능한 분",
    createdAt: "2025-08-18",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "KIWOOM"
  },
  
  // 삼성 라이온즈 경기들
  {
    postId: 12,
    title: "삼성 라이온즈 vs 두산 베어스",
    gameDate: "2025-08-19",
    gameTime: "18:30",
    location: "대구 삼성 라이온즈 파크",
    organizer: "삼성 팬클럽",
    currentParticipants: 18,
    maxParticipants: 25,
    description: "대구 홈 경기! 삼성 팬들과 함께하는 단체 관람.",
    meetingPlace: "대구 삼성 라이온즈 파크 정문",
    meetingTime: "17:00",
    hashtags: ["대구홈", "삼성팬", "단체관람"],
    status: "recruiting",
    conditions: "삼성 팬 우선",
    createdAt: "2025-08-10",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "SAMSUNG"
  },
  {
    postId: 13,
    title: "삼성 라이온즈 vs NC 다이노스",
    gameDate: "2025-08-29",
    gameTime: "14:00",
    location: "대구 삼성 라이온즈 파크",
    organizer: "라이온즈 응원단",
    currentParticipants: 22,
    maxParticipants: 30,
    description: "오후 경기 단체 관람! 응원용품과 간식이 준비되어 있습니다.",
    meetingPlace: "대구 삼성 라이온즈 파크 동쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["오후경기", "응원용품", "간식제공"],
    status: "closing",
    conditions: "정시 참석 필수",
    createdAt: "2025-08-20",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "SAMSUNG"
  },
  
  // 롯데 자이언츠 경기들
  {
    postId: 14,
    title: "롯데 자이언츠 vs 한화 이글스",
    gameDate: "2025-08-17",
    gameTime: "18:30",
    location: "부산 사직 야구장",
    organizer: "롯데 팬클럽",
    currentParticipants: 15,
    maxParticipants: 25,
    description: "부산 홈 경기! 롯데 팬들과 함께하는 즐거운 관람.",
    meetingPlace: "부산 사직 야구장 정문",
    meetingTime: "17:00",
    hashtags: ["부산홈", "롯데팬", "즐거운시간"],
    status: "recruiting",
    conditions: "롯데 팬 우선",
    createdAt: "2025-08-09",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "LOTTE"
  },
  {
    postId: 15,
    title: "롯데 자이언츠 vs 두산 베어스",
    gameDate: "2025-08-26",
    gameTime: "14:00",
    location: "부산 사직 야구장",
    organizer: "자이언츠 응원단",
    currentParticipants: 20,
    maxParticipants: 30,
    description: "오후 경기 단체 관람! 특별 응원 이벤트가 준비되어 있습니다.",
    meetingPlace: "부산 사직 야구장 서쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["오후경기", "특별이벤트", "응원단"],
    status: "completed",
    conditions: "응원단 활동 참여 가능한 분",
    createdAt: "2025-08-18",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "LOTTE"
  },
  
  // 두산 베어스 경기들
  {
    postId: 16,
    title: "두산 베어스 vs SSG 랜더스",
    gameDate: "2025-08-21",
    gameTime: "18:30",
    location: "잠실 야구장",
    organizer: "두산 팬클럽",
    currentParticipants: 16,
    maxParticipants: 25,
    description: "잠실 홈 경기! 두산 팬들과 함께하는 단체 관람.",
    meetingPlace: "잠실 야구장 정문",
    meetingTime: "17:00",
    hashtags: ["잠실홈", "두산팬", "단체관람"],
    status: "recruiting",
    conditions: "두산 팬 우선",
    createdAt: "2025-08-12",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "DOOSAN"
  },
  {
    postId: 17,
    title: "두산 베어스 vs 한화 이글스",
    gameDate: "2025-08-31",
    gameTime: "14:00",
    location: "잠실 야구장",
    organizer: "베어스 응원단",
    currentParticipants: 18,
    maxParticipants: 25,
    description: "오후 경기 단체 관람! 응원용품과 간식이 준비되어 있습니다.",
    meetingPlace: "잠실 야구장 동쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["오후경기", "응원용품", "간식제공"],
    status: "closing",
    conditions: "정시 참석 필수",
    createdAt: "2025-08-22",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "DOOSAN"
  },
  
  // 한화 이글스 경기들
  {
    postId: 18,
    title: "한화 이글스 vs KIA 타이거즈",
    gameDate: "2025-08-23",
    gameTime: "18:30",
    location: "대전 한화생명 이글스파크",
    organizer: "한화 팬클럽",
    currentParticipants: 14,
    maxParticipants: 20,
    description: "대전 홈 경기! 한화 팬들과 함께하는 즐거운 관람.",
    meetingPlace: "대전 한화생명 이글스파크 정문",
    meetingTime: "17:00",
    hashtags: ["대전홈", "한화팬", "즐거운시간"],
    status: "recruiting",
    conditions: "한화 팬 우선",
    createdAt: "2025-08-14",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "HANWHA"
  },
  {
    postId: 19,
    title: "한화 이글스 vs NC 다이노스",
    gameDate: "2025-09-02",
    gameTime: "14:00",
    location: "대전 한화생명 이글스파크",
    organizer: "이글스 응원단",
    currentParticipants: 19,
    maxParticipants: 25,
    description: "오후 경기 단체 관람! 특별 응원 이벤트가 준비되어 있습니다.",
    meetingPlace: "대전 한화생명 이글스파크 서쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["오후경기", "특별이벤트", "응원단"],
    status: "completed",
    conditions: "응원단 활동 참여 가능한 분",
    createdAt: "2025-08-24",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "HANWHA"
  },
  
  // NC 다이노스 경기들
  {
    postId: 20,
    title: "NC 다이노스 vs KT 위즈",
    gameDate: "2025-08-24",
    gameTime: "18:30",
    location: "창원 NC 파크",
    organizer: "NC 팬클럽",
    currentParticipants: 17,
    maxParticipants: 25,
    description: "창원 홈 경기! NC 팬들과 함께하는 단체 관람.",
    meetingPlace: "창원 NC 파크 정문",
    meetingTime: "17:00",
    hashtags: ["창원홈", "NC팬", "단체관람"],
    status: "recruiting",
    conditions: "NC 팬 우선",
    createdAt: "2025-08-15",
    imageUrl: "https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop",
    team: "NC"
  },
  {
    postId: 21,
    title: "NC 다이노스 vs 삼성 라이온즈",
    gameDate: "2025-09-03",
    gameTime: "14:00",
    location: "창원 NC 파크",
    organizer: "다이노스 응원단",
    currentParticipants: 21,
    maxParticipants: 30,
    description: "오후 경기 단체 관람! 응원용품과 간식이 준비되어 있습니다.",
    meetingPlace: "창원 NC 파크 동쪽 게이트",
    meetingTime: "13:00",
    hashtags: ["오후경기", "응원용품", "간식제공"],
    status: "closing",
    conditions: "정시 참석 필수",
    createdAt: "2025-08-25",
    imageUrl: "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop",
    team: "NC"
  }
];

const allPosts = ref(dummyPosts);
const displayedPosts = ref([]);



// 선택된 팀의 제목
const selectedTeamTitle = computed(() => {
  const teamNames = {
    'all': '단체 관람 게시판',
    'SSG': 'SSG 랜더스 단체 관람 모집',
    'KIA': 'KIA 타이거즈 단체 관람 모집',
    'LG': 'LG 트윈스 단체 관람 모집',
    'KT': 'KT 위즈 단체 관람 모집',
    'KIWOOM': '키움 히어로즈 단체 관람 모집',
    'SAMSUNG': '삼성 라이온즈 단체 관람 모집',
    'LOTTE': '롯데 자이언츠 단체 관람 모집',
    'DOOSAN': '두산 베어스 단체 관람 모집',
    'HANWHA': '한화 이글스 단체 관람 모집',
    'NC': 'NC 다이노스 단체 관람 모집'
  };
  return teamNames[selectedTeam.value] || '단체 관람 게시판';
});

// 총 페이지 수
const totalPages = computed(() => Math.ceil(filteredPosts.value.length / itemsPerPage));

// 필터링된 게시글
const filteredPosts = computed(() => {
  let filtered = [...allPosts.value];
  
  // 팀별 필터링
  if (selectedTeam.value !== 'all') {
    filtered = filtered.filter(post => post.team === selectedTeam.value);
  }
  
  // 기본 정렬 (최신순)
  filtered.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
  
  return filtered;
});

// 게시글 목록 로드 (더미데이터 사용)
const loadPosts = () => {
  isLoading.value = true;
  error.value = '';
  
  // 더미데이터 사용
  setTimeout(() => {
    allPosts.value = dummyPosts;
      updateDisplayedPosts();
    isLoading.value = false;
  }, 500); // 로딩 효과를 위한 지연
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
  event.target.src = '/src/assets/images/default-group.jpg';
};

// 이벤트 핸들러들
const handleTabClick = (tab) => {
  console.log('탭 클릭:', tab);
  // TODO: 탭 변경 로직 구현
};

const handleFavoriteTeamClick = () => {
  console.log('내 관심팀 클릭');
  // TODO: 관심팀 설정 페이지로 이동
};

const handlePostClick = (post) => {
  console.log('게시글 클릭:', post);
  router.push({
    name: 'BoardDetail',
    params: { postId: post.postId }
  });
};

const handleInterestClick = (post) => {
  console.log('관심 클릭:', post);
  // TODO: 관심 등록/해제 로직
};

const handleApplyClick = (post) => {
  console.log('참가 신청 클릭:', post);
  // TODO: 참가 신청 로직
};

const handlePageChange = (page) => {
  currentPage.value = page;
  updateDisplayedPosts();
};



// 필터/정렬 변경 감지
const watchFilterAndSort = () => {
  currentPage.value = 1;
  updateDisplayedPosts();
};

// 컴포넌트 마운트
onMounted(() => {
  loadPosts();
});

// 팀 선택 변경 감지
watch(selectedTeam, watchFilterAndSort);
</script>

<style scoped>
.group-board-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: #f8f9fa;
  min-height: 100vh;
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
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.favorite-team-btn {
  background: #e11d48;
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
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
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
  background: #e11d48;
  color: white;
  border-color: #e11d48;
  box-shadow: 0 2px 8px rgba(225, 29, 72, 0.3);
}

.team-name {
  font-size: 14px;
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
  width: 100%;
  min-height: 300px;
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
  font-size: 12px;
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
  font-size: 14px;
  font-weight: 600;
}

/* 카드 내용 */
.card-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 280px;
}

.post-title {
  font-size: 18px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

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
  font-size: 14px;
  color: #6b7280;
}

.info-icon {
  font-size: 14px;
}

.post-description {
  font-size: 14px;
  color: #374151;
  line-height: 1.5;
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

.hashtags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 12px;
}

.hashtag {
  background: #f3f4f6;
  color: #6b7280;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.participation-rate {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.rate-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.progress-bar {
  flex: 1;
  height: 8px;
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
  font-size: 13px;
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
  font-size: 11px;
  color: #6b7280;
}

.conditions {
  font-size: 11px;
  color: #6b7280;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.interest-btn, .apply-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s;
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
  background: #e11d48;
  color: white;
}

.apply-btn:hover {
  background: #be123c;
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
  font-size: 14px;
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
    font-size: 24px;
  }
  
  .team-list {
    grid-template-columns: repeat(auto-fit, minmax(100px, 1fr));
    gap: 8px;
    padding: 16px;
  }
  
  .team-item {
    padding: 8px 12px;
    min-height: 40px;
  }
  
  .team-name {
    font-size: 13px;
  }
  
  .posts-grid {
    grid-template-columns: 1fr;
  }
  
  .post-card {
    flex-direction: column;
  }
  
  .card-image-section {
    width: 100%;
    height: 200px;
  }
  
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
}
</style> 