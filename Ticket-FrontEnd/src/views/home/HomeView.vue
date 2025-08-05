<template>
  <v-app>
    <v-main>
      <!-- 히어로 섹션 -->
      <v-container fluid class="hero-section pa-8">
        <v-container>
          <v-row justify="center" align="center" class="fill-height">
            <!-- 응모 카드 -->
            <v-col cols="12" md="6" class="pa-4">
              <HeroCard
                title="응모"
                :description="['다양한 티켓 응모에 참여하고', '원하는 공연을 만나보세요']"
                button-text="응모하기"
                type="primary"
                @click="goToApply"
              />
            </v-col>
            
            <!-- 양도 카드 -->
            <v-col cols="12" md="6" class="pa-4">
              <HeroCard
                title="양도"
                :description="['티켓을 안전하게 양도하고', '필요한 사람에게 전달하세요']"
                button-text="양도하기"
                type="secondary"
                @click="goToTransfer"
              />
            </v-col>
          </v-row>
        </v-container>
      </v-container>

      <!-- 랭킹 섹션 -->
      <v-container fluid class="ranking-section py-12">
        <v-container>
          <v-row>
            <!-- 이달의 양도자 -->
            <v-col cols="12" lg="6" class="pa-4">
              <RankingCard
                title="이달의 양도자"
                title-icon="mdi-star"
                :items="userRankingData"
                type="user"
              />
            </v-col>
            
            <!-- 팀별 양도랭킹 -->
            <v-col cols="12" lg="6" class="pa-4">
              <RankingCard
                title="팀별 양도랭킹"
                title-icon="mdi-trophy"
                :items="teamRankingData"
                type="team"
              />
            </v-col>
          </v-row>
        </v-container>
      </v-container>

      <!-- 이용 후기 섹션 -->
      <v-container fluid class="review-section py-12 bg-grey-lighten-5">
        <v-container>
          <SectionHeader 
            title="이용 후기" 
            subtitle="실제 사용자들의 생생한 후기를 확인해보세요" 
          />
          
          <!-- 캐러셀 컨테이너 -->
          <div class="review-carousel-wrapper">
            <!-- 왼쪽 화살표 -->
            <v-btn
              icon
              class="carousel-arrow carousel-arrow-left"
              @click="previousReviews"
              :disabled="currentReviewIndex === 0"
              size="large"
              color="white"
              variant="elevated"
              elevation="3"
            >
              <v-icon color="primary">mdi-chevron-left</v-icon>
            </v-btn>
            
            <!-- 후기 카드들 -->
            <div class="review-carousel-container">
              <div class="review-carousel-content">
                <v-row class="review-row" :style="{ transform: `translateX(-${currentReviewIndex * (100 / reviewsPerView)}%)` }">
                  <v-col 
                    cols="12" 
                    md="4" 
                    class="pa-4 review-slide" 
                    v-for="(review, index) in reviews" 
                    :key="index"
                  >
                    <ReviewCard
                      :name="review.name"
                      :team="review.team"
                      :content="review.content"
                      :date="review.date"
                    />
                  </v-col>
                </v-row>
              </div>
            </div>
            
            <!-- 오른쪽 화살표 -->
            <v-btn
              icon
              class="carousel-arrow carousel-arrow-right"
              @click="nextReviews"
              :disabled="currentReviewIndex >= maxReviewIndex"
              size="large"
              color="white"
              variant="elevated"
              elevation="3"
            >
              <v-icon color="primary">mdi-chevron-right</v-icon>
            </v-btn>
          </div>
          
          <!-- 현대적인 인디케이터 -->
          <div class="review-indicators-modern">
            <div 
              v-for="(dot, index) in totalPages"
              :key="index"
              class="indicator-dot"
              :class="{ 'active': index === currentPage }"
              @click="goToPage(index)"
            ></div>
          </div>
        </v-container>
      </v-container>

      <!-- 더 많은 서비스 섹션 -->
      <v-container fluid class="services-section py-12">
        <v-container>
          <SectionHeader 
            title="더 많은 서비스" 
            subtitle="다양한 기능을 통해 더 나은 경험을 제공합니다" 
          />
          
          <v-row>
            <v-col cols="12" md="4" class="pa-4" v-for="(service, index) in services" :key="index">
              <ServiceCard
                :title="service.title"
                :description="service.description"
                :icon="service.icon"
                :icon-color="service.color"
              />
            </v-col>
          </v-row>
        </v-container>
      </v-container>
    </v-main>
    
    <!-- Login Modal -->
    <LoginModal :isVisible="showLoginModal" @close="closeLoginModal" />
  </v-app>
</template>

<script setup>
import { ref, watch, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import http from '@/utils/http';
import axios from 'axios';
import { API_CONFIG } from '@/config/api.config';

// 공통 컴포넌트 import
import HeroCard from '../../components/ui/HeroCard.vue';
import RankingCard from '../../components/ui/RankingCard.vue';
import ReviewCard from '../../components/ui/ReviewCard.vue';
import ServiceCard from '../../components/ui/ServiceCard.vue';
import SectionHeader from '../../components/ui/SectionHeader.vue';
import LoginModal from '../../components/common/LoginModal.vue';

// 라우터 설정
const router = useRouter();

// Auth store
const authStore = useAuthStore();
const showLoginModal = ref(false);
const pendingRedirect = ref(null); // 로그인 후 리다이렉트할 경로 저장

// 로그인 상태 변화 감지하여 리다이렉트 처리
watch(() => authStore.isAuthenticated, (newValue) => {
  if (newValue && pendingRedirect.value) {
    // 로그인 성공 시 저장된 경로로 리다이렉트
    router.push(pendingRedirect.value);
    pendingRedirect.value = null;
    showLoginModal.value = false;
  }
});

// 로그인 모달 닫기
function closeLoginModal() {
  showLoginModal.value = false;
  pendingRedirect.value = null; // 모달 닫을 때 대기 중인 리다이렉트 초기화
}

// 메인 페이지 로직
const goToApply = () => {
  console.log(authStore.isAuthenticated)
  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    pendingRedirect.value = '/application'; // 로그인 후 이동할 경로 저장
    showLoginModal.value = true;
    return;
  }
  // 응모 페이지로 이동
  router.push('/application');
};

const goToTransfer = () => {
  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    pendingRedirect.value = '/transfer'; // 로그인 후 이동할 경로 저장
    showLoginModal.value = true;
    return;
  }
  // 양도 페이지로 이동
  router.push('/transfer');
};

// 사용자 랭킹 데이터
const userRankingData = ref([]);

// 사용자 랭킹 데이터 가져오기
const fetchUserRanking = async () => {
  try {
    const response = await axios.get(API_CONFIG.MAIN_PAGE.USER);
    
    // API 응답 데이터를 RankingCard 컴포넌트에 맞는 형태로 변환
    userRankingData.value = response.data.map(item => ({
      name: item.userName,
      subtitle: `${item.rank}위`,
      score: item.transferAllCount,
      avatar: '/default-avatar.png'
    }));
  } catch (error) {
    console.error('사용자 랭킹 데이터 가져오기 실패:', error);
    // 기본값 설정
    userRankingData.value = [
      { name: '데이터 없음', subtitle: '0건', avatar: '/default-avatar.png' }
    ];
  }
};

// 팀 랭킹 데이터
const teamRankingData = ref([]);

// 팀명 변환 함수 (영문 → 한글)
const getTeamKoreanName = (teamName) => {
  const teamMap = {
    'SSG_LANDERS': 'SSG 랜더스',
    'KIA_TIGERS': 'KIA 타이거즈',
    'KIWOOM_HEROES': '키움 히어로즈',
    'LG_TWINS': 'LG 트윈스',
    'DOOSAN_BEARS': '두산 베어스',
    'KT_WIZ': 'KT 위즈',
    'LOTTE_GIANTS': '롯데 자이언츠',
    'HANHWA_EAGLES': '한화 이글스',
    'NC_DINOS': 'NC 다이노스',
    'SAMSUNG_LIONS': '삼성 라이온즈'
  };
  return teamMap[teamName] || teamName;
};

// 팀 랭킹 데이터 가져오기
const fetchTeamRanking = async () => {
  try {
    const response = await axios.get(API_CONFIG.MAIN_PAGE.TEAM);
    console.log(response.data);
    // API 응답 데이터를 RankingCard 컴포넌트에 맞는 형태로 변환
    teamRankingData.value = response.data.map(item => ({
      name: getTeamKoreanName(item.teamName),
      subtitle: `${item.rank}위`,
      score: item.transferAllCount,
      change: `+${item.growthRate}%`,
      avatar: `/team-logos/${item.teamName.toLowerCase()}.png`
    }));
  } catch (error) {
    console.error('팀 랭킹 데이터 가져오기 실패:', error);
    // 기본값 설정
    teamRankingData.value = [
      { name: '데이터 없음', subtitle: '0건', avatar: '/default-team.png' }
    ];
  }
};

// 후기 데이터
const reviews = ref([
  {
    name: '김민수',
    team: 'KIA 타이거즈',
    content: '광주 챔피언스필드에서 열린 경기 티켓을 양도받았는데, 절차가 정말 간단하고 안전했어요!',
    date: '2025.08.02'
  },
  {
    name: '박지영',
    team: 'LG 트윈스',
    content: '잠실야구장 LG vs 삼성 경기 티켓 응모에 당첨되어서 너무 기뻤습니다. 시스템이 투명하고 좋네요.',
    date: '2025.07.28'
  },
  {
    name: '이준호',
    team: '두산 베어스',
    content: '급하게 경기를 못 가게 되어서 티켓을 양도했는데, 빠르게 처리되어서 감사했어요.',
    date: '2025.07.25'
  },
  {
    name: '최수진',
    team: 'SSG 랜더스',
    content: '문학야구장 경기 티켓을 여기서 양도받았는데, 정말 믿을 수 있는 플랫폼이에요!',
    date: '2025.08.01'
  },
  {
    name: '정우성',
    team: '삼성 라이온즈',
    content: '대구 라이온즈파크 경기 보러 가고 싶었는데 응모로 티켓 받을 수 있어서 좋았습니다.',
    date: '2025.07.30'
  },
  {
    name: '한소희',
    team: '롯데 자이언츠',
    content: '사직야구장 경기 티켓 양도가 이렇게 쉬울 줄 몰랐어요. 다음에도 이용할게요!',
    date: '2025.07.22'
  },
  {
    name: '강동원',
    team: 'NC 다이노스',
    content: '창원NC파크에서 열린 경기 티켓을 안전하게 양도받았습니다. 추천해요!',
    date: '2025.08.03'
  },
  {
    name: '송혜교',
    team: '키움 히어로즈',
    content: '고척스카이돔 경기 응모했는데 당첨되어서 너무 행복했어요. 시스템이 공정해서 좋네요.',
    date: '2025.07.26'
  },
  {
    name: '박서준',
    team: 'KT 위즈',
    content: '수원KT위즈파크 경기 티켓을 급하게 양도해야 했는데, 빠르고 간편하게 처리됐어요.',
    date: '2025.07.31'
  },
  {
    name: '김태희',
    team: '한화 이글스',
    content: '대전한화생명이글스파크 경기 티켓 양도받았는데, 정말 안전하고 신뢰할 수 있는 서비스네요!',
    date: '2025.08.04'
  }
]);

// 후기 캐러셀 관련 데이터
const reviewsPerView = 3;
const currentReviewIndex = ref(0);
const maxReviewIndex = reviews.value.length - reviewsPerView;
const totalPages = Math.ceil(reviews.value.length / reviewsPerView);

// 현재 페이지 계산 (computed)
const currentPage = computed(() => {
  // 마지막 페이지 처리를 위한 올바른 계산
  if (currentReviewIndex.value >= maxReviewIndex) {
    return totalPages - 1; // 마지막 페이지 (인덱스 3)
  }
  return Math.floor(currentReviewIndex.value / reviewsPerView);
});

// 이전 후기 보기
function previousReviews() {
  const currentPageNum = currentPage.value;
  if (currentPageNum > 0) {
    const newPage = currentPageNum - 1;
    currentReviewIndex.value = newPage * reviewsPerView;
  }
}

// 다음 후기 보기
function nextReviews() {
  const currentPageNum = currentPage.value;
  if (currentPageNum < totalPages - 1) {
    const newPage = currentPageNum + 1;
    currentReviewIndex.value = newPage * reviewsPerView;
    // 마지막 페이지 처리
    if (currentReviewIndex.value > maxReviewIndex) {
      currentReviewIndex.value = maxReviewIndex;
    }
  }
}

// 특정 페이지로 이동
function goToPage(page) {
  currentReviewIndex.value = page * reviewsPerView;
  if (currentReviewIndex.value > maxReviewIndex) {
    currentReviewIndex.value = maxReviewIndex;
  }
}

// 서비스 데이터
const services = ref([
  {
    title: '모바일 최적화',
    description: '언제 어디서나 편리하게 이용할 수 있는 모바일 서비스',
    icon: 'mdi-cellphone',
    color: 'primary'
  },
  {
    title: '안전한 보안',
    description: '최고 수준의 보안 시스템으로 안전하게 보호되는 서비스',
    icon: 'mdi-shield-check',
    color: 'success'
  },
  {
    title: '24시간 지원',
    description: '언제든지 도움이 필요한 때 친절한 고객 지원 서비스',
    icon: 'mdi-headset',
    color: 'info'
  }
]);

// 컴포넌트 마운트 시 랭킹 데이터 가져오기
onMounted(() => {
  fetchUserRanking();
  fetchTeamRanking();
  // authStore.verifyToken()
});

</script>

<style scoped>
/* Vuetify 커스텀 스타일 */
.hero-section {
  min-height: 500px;
}

.enter-card {
  background: linear-gradient(135deg, #FFB22C 0%, #FF9A1A 100%) !important;
}

.transfer-card {
  background: linear-gradient(135deg, #854836 0%, #6B3A2E 100%) !important;
}

.hero-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.hero-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15) !important;
}

.ranking-section {
  background: #fafafa;
}

.review-section {
  background: #f5f5f5;
}

.services-section {
  background: white;
}

/* 반응형 텍스트 크기 조정 */
@media (max-width: 960px) {
  .text-h2 {
    font-size: 2.5rem !important;
  }
  
  .text-h3 {
    font-size: 2rem !important;
  }
}

@media (max-width: 600px) {
  .text-h2 {
    font-size: 2rem !important;
  }
  
  .text-h3 {
    font-size: 1.75rem !important;
  }
  
  .text-h5 {
    font-size: 1.25rem !important;
  }
  
  .text-h6 {
    font-size: 1rem !important;
  }
}

/* 후기 캐러셀 스타일 */
.review-carousel-container {
  position: relative;
  overflow: hidden;
}

/* 캐러셀 래퍼 - 화살표와 컨텐츠를 포함하는 컨테이너 */
.review-carousel-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 0 60px; /* 화살표 공간 확보 */
}

/* 실제 캐러셀 컨테이너 - 카드들만 포함 */
.review-carousel-container {
  flex: 1;
  overflow: hidden;
}

.review-carousel-content {
  transition: transform 0.5s ease;
}

.review-row {
  display: flex;
  flex-wrap: nowrap;
}

.review-slide {
  flex-shrink: 0;
  width: 100%;
}

/* 개선된 화살표 스타일 */
.carousel-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15) !important;
  transition: all 0.3s ease;
}

.carousel-arrow:hover {
  transform: translateY(-50%) scale(1.05);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2) !important;
}

.carousel-arrow:disabled {
  opacity: 0.3;
  transform: translateY(-50%);
}

.carousel-arrow-left {
  left: 0;
}

.carousel-arrow-right {
  right: 0;
}

/* 현대적인 인디케이터 스타일 */
.review-indicators-modern {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 12px;
  margin-top: 32px;
}

.indicator-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #e0e0e0;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.indicator-dot:hover {
  background-color: #bdbdbd;
  transform: scale(1.2);
}

.indicator-dot.active {
  background-color: #1976d2;
  transform: scale(1.3);
  box-shadow: 0 0 0 4px rgba(25, 118, 210, 0.2);
}

.indicator-dot.active::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 6px;
  height: 6px;
  background-color: white;
  border-radius: 50%;
  opacity: 0.8;
}

/* 모바일 반응형 */
@media (max-width: 768px) {
  .review-carousel-wrapper {
    gap: 16px;
    margin: 0 -16px;
  }
  
  .review-carousel-container {
    margin: 0 16px;
  }
  
  .carousel-arrow {
    display: none; /* 모바일에서는 화살표 숨김 */
  }
  
  .indicator-dot {
    width: 10px;
    height: 10px;
  }
  
  .indicator-dot.active::before {
    width: 4px;
    height: 4px;
  }
}

/* 랭킹 섹션 스타일 */
.ranking-section .v-col {
  display: flex;
}

.ranking-section .v-card {
  min-height: 320px;
  display: flex;
  flex-direction: column;
}

.ranking-section .v-card-text {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.ranking-section .v-list {
  flex: 1;
}
</style>
