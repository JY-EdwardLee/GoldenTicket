<template>
  <v-app>
    <v-main>
      <!-- 히어로 섹션 -->
      <v-container fluid class="hero-section pa-4" style="min-height: 25%; height: auto;">
        <v-container class="pa-0" :class="{ 'hovered': isTransferCardHovered || isEnterCardHovered }">
          <!-- 데스크톱 레이아웃 -->
          <v-row no-gutters class="fill-height d-none d-md-flex">
            <!-- 왼쪽 영역 - 두 개의 메인 카드 -->
            <v-col cols="12" md="12" class="d-flex pa-0">
              <v-row no-gutters class="flex-nowrap" style="width: 100%;">
                <!-- 응모 카드 -->
                <v-col 
                  cols="12"
                  :md="isEnterCardHovered ? 8 : (isTransferCardHovered ? 4 : 6)"
                  class="pa-4 card-col flex-column"
                  :class="{ 'card-col-hover': isEnterCardHovered }"
                >
                  <HeroCard
                    class="enter-card h-100"
                    title="응모하기"
                    :description="'원하는 경기를 응모하고 티켓을 양도받아 보세요'"
                    button-text="응모하기"
                    type="secondary"
                    @mouseover="isEnterCardHovered = true"
                    @mouseleave="isEnterCardHovered = false"
                    @click="goToApply"
                  />
                </v-col>
                <!-- 양도 카드 -->
                <v-col 
                  cols="12"
                  :md="isTransferCardHovered ? 8 : (isEnterCardHovered ? 4 : 6)" 
                  class="pa-4 card-col flex-column"
                  :class="{ 'card-col-hover': isTransferCardHovered }"
                >
                  <HeroCard
                    id="transfer-card"
                    class="transfer-card h-100"
                    title="양도하기"
                    :description="'티켓이 필요한사람에게 안전하게 양도하세요'"
                    button-text="양도하기"
                    type="primary"
                    @mouseover="isTransferCardHovered = true"
                    @mouseleave="isTransferCardHovered = false"
                    @click="goToTransfer"
                  />
                </v-col>
              </v-row>
            </v-col>
            
            <!-- 아래 영역 - 세장 -->
            <v-row cos="12" md="12" class="pl-2 d-flex card-col" style="min-height: 30vh; height: auto;">
              <!-- 첫 번째 작은 카드 -->
              <v-col md="3"> 
              <v-card
               class="card-col flex-grow-1 d-flex flex-column pt-4 pa-3" 
               :class="{ 'card-col-hover': isApplyCardHovered }"
               :elevation="0" 
               rounded="lg">
              <HeroCard
                :height="270"
                title="응모 내역"
                button-text="응모 내역 확인"
                type="sub"
                @mouseover="isApplyCardHovered = true"
                @mouseleave="isApplyCardHovered = false"
                @click="goToApplications"
              />
              </v-card>
              </v-col>
              <!-- 두 번째 작은 카드 -->
            <v-col md="3"> 
              <v-card
               class="card-col flex-grow-1 d-flex flex-column pt-4 pa-3" 
               :class="{ 'card-col-hover': isTicketCardHovered }"
               :elevation="0" 
               rounded="lg">
              <HeroCard
                :height="270"
                title="나의 티켓"
                button-text="내 티켓 확인"
                type="sub"
                @mouseover="isTicketCardHovered = true"
                @mouseleave="isTicketCardHovered = false"
                @click="goToTickets"
              />
              </v-card>
            </v-col>
            <!-- 세 번째 작은 카드 -->
            <v-col md="6"> 
              <v-card
              :height="270"
               class="card-col flex-grow-1 d-flex flex-column pt-4 pa-3" 
               :elevation="0" 
               rounded="lg">
              <v-card-text class="d-flex flex-column" style="height: 100%;">
                <div>
                  <div class="d-flex align-center">
                    <icon color="primary" style="font-size: 30px">🎫</icon>
                    <span class="font-weight-bold text-black ml-2" style="font-size: 1.3rem">다가오는 경기</span>
                  </div>
                  <div class="text-medium-emphasis mt-1 font-weight-bold">예정된 가장 가까운 직관 경기를 확인하세요</div>
                </div>
                <v-btn
                v-if="tickets.length > 0"
                  color="transparent"
                  variant="flat"
                  class="ticket-btn mt-auto h-50 glass-effect"
                  style="min-height: 120px; border-radius: 15px;"
                  @click="goToTicket(tickets[0]?.ticketId)"
                  @mouseover="hovered = true"
                  @mouseleave="hovered = false"
                  :class="{ 'ticket-btn-hover': hovered }"
                >
                <img :src="`/org/logo/${tickets[0]?.game?.home}`" alt="">
                  <div class="ticket-text d-flex flex-column align-center justify-center w-100 h-100">
                    <div class="font-weight-bold text-h6">{{ enumToTeamName[tickets[0]?.game?.away] }} vs {{ enumToTeamName[tickets[0]?.game?.home] }}</div>
                    <div class="mt-1 text-body-2">{{ formatDate(tickets[0]?.game?.date) }}</div>
                    <div class="mt-1 text-body-2">{{ stadiumOfTeam[tickets[0]?.game?.stadium] }}</div>
                  </div>
                </v-btn>
                <v-btn
                v-else
                class="mt-auto h-50 d-flex align-center justify-center"
                style="min-height: 100px; border-radius: 15px; border-color: var(--theme-primary); border-width: 1px;"
                >
                  <div class="text-medium-emphasis mt-1 font-weight-bold">보유한 티켓이 없습니다.</div>
                </v-btn>
              </v-card-text>
              </v-card>
            </v-col>
            </v-row>
          </v-row>
          
          <!-- 모바일 레이아웃 -->
          <div class="d-flex d-md-none flex-column">
            <!-- 양도 카드 -->
            <div class="pa-4 card-col mb-4">
              <HeroCard
                class="enter-card h-100"
                title="양도하기"
                :description="['티켓을 안전하게 양도하고', '필요한 사람에게 전달하세요']"
                button-text="양도하기"
                type="primary"
                @mouseover="isTransferCardHovered = true"
                @mouseleave="isTransferCardHovered = false"
                @click="goToTransfer"
              />
            </div>
            
            <!-- 응모 카드 -->
            <div class="pa-4 card-col mb-4">
              <HeroCard
                class="transfer-card h-100"
                title="응모하기"
                :description="['원하는 경기를 응모하고', '티켓을 양도받아 보세요.']"
                button-text="응모하기"
                type="secondary"
                @mouseover="isEnterCardHovered = true"
                @mouseleave="isEnterCardHovered = false"
                @click="goToApply"
              />
            </div>
            
            <!-- 모바일용 작은 카드들 -->
            <div class="d-flex flex-column">
              <!-- 첫 번째 작은 카드 -->
              <v-card class="mb-2 flex-grow-1 d-flex flex-column" :elevation="0" rounded="lg" style="background-color: #f8f9fa;">
                <v-card-text class="text-center pa-4 d-flex flex-column align-center justify-center">
                  <v-icon size="40" color="primary" class="mb-2">mdi-ticket-confirmation</v-icon>
                  <div class="text-subtitle-1 font-weight-medium">실시간 인기 경기</div>
                  <div class="text-caption text-medium-emphasis mt-1">지금 가장 인기있는 경기를 확인하세요</div>
                </v-card-text>
              </v-card>
              
              <!-- 두 번째 작은 카드 -->
              <v-card class="flex-grow-1 d-flex flex-column" :elevation="0" rounded="lg" style="background-color: #f8f9fa;">
                <v-card-text class="text-center pa-4 d-flex flex-column align-center justify-center">
                  <v-icon size="40" color="secondary" class="mb-2">mdi-star-shooting</v-icon>
                  <div class="text-subtitle-1 font-weight-medium">이벤트 안내</div>
                  <div class="text-caption text-medium-emphasis mt-1">특별한 혜택을 놓치지 마세요</div>
                </v-card-text>
              </v-card>
            </div>
          </div>
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
                title-icon="star"
                :items="userRankingData"
                type="user"
              />
            </v-col>
            
            <!-- 팀별 양도랭킹 -->
            <v-col cols="12" lg="6" class="pa-4">
              <RankingCard
                title="팀별 양도랭킹"
                title-icon="trophy"
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
          
          <!-- 자동 스크롤 캐러셀 컨테이너 -->
          <div class="auto-review-carousel-wrapper">
            <div class="auto-review-carousel-container">
              <div 
                class="auto-review-carousel-track"
                :style="{
                  transform: `translateX(${translateX}px)`,
                  transition: isTransitioning ? 'transform 0.5s ease-in-out' : 'none'
                }"
              >
                <!-- 원본 후기들 -->
                <div 
                  v-for="(review, index) in extendedReviews" 
                  :key="`review-${index}`"
                  class="auto-review-slide"
                >
                  <ReviewCard
                    :name="review.name"
                    :team="review.team"
                    :content="review.content"
                    :date="review.date"
                  />
                </div>
              </div>
            </div>
            

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
    
    <!-- Tutorial Modal -->
    <v-dialog v-model="showTutorialModal" max-width="500" persistent>
      <v-card class="tutorial-modal">
        <v-card-title class="text-h5 text-center pa-6">
          <div class="tutorial-modal-main-title">서비스 사용을 도와드릴까요?</div>
        </v-card-title>
        
        <v-card-text class="text-center pa-6">
          <p class="tutorial-modal-sub-title">
            쉽고 편리한 튜토리얼 기능을 이용해보세요!
          </p>
        </v-card-text>
        
        <v-card-actions class="justify-center pa-6">
          <v-btn 
            style="font-size: 15px;"
            color="primary" 
            variant="flat" 
            @click="() => startTutorial(authStore, { value: showLoginModal })"
          >
            네, 도와주세요!
          </v-btn>
          <v-btn 
            style="font-size: 15px;"
            color="grey" 
            variant="outlined" 
            @click="closeTutorialModal"
          >
            아뇨, 괜찮아요
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-app>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, computed, nextTick } from 'vue';
import { useRouter } from 'vue-router';

const isApplyCardHovered = ref(false);
const isTicketCardHovered = ref(false);
const isEnterCardHovered = ref(false);
const isTransferCardHovered = ref(false);
const hovered = ref(false);
import { useAuthStore } from '@/stores/auth';
import { useTutorial } from '@/views/tutorial/useTutorial';
import { driver } from 'driver.js';
import axios from 'axios';
import { API_CONFIG } from '@/config/api.config';
import { enumToTeamName } from '@/utils/teamNameMap';
import { stadiumOfTeam } from '@/utils/teamStadium';
import { formatDate } from '@/utils/dateUtils'
import 'driver.js/dist/driver.css';
// 공통 컴포넌트 import
import HeroCard from '../../components/ui/HeroCard.vue';
import RankingCard from '../../components/ui/RankingCard.vue';
import ReviewCard from '../../components/ui/ReviewCard.vue';
import ServiceCard from '../../components/ui/ServiceCard.vue';
import SectionHeader from '../../components/ui/SectionHeader.vue';
import LoginModal from '../../components/common/LoginModal.vue';
import { useTeamThemeStore } from '@/stores/teamTheme.js'
import http from '@/utils/http';

// 1. 로컬 스토리지에서 사용자 정보를 가져옵니다.
const user = JSON.parse(localStorage.getItem('user'))

// 2. 팀 테마 스토어를 가져옵니다.
const themeStore = useTeamThemeStore

// 3. 사용자 정보에 myTeam 값이 있으면 해당 팀으로 테마를 설정합니다.
// 이 코드는 컴포넌트가 생성될 때마다 실행되어 현재 사용자의 팀 테마를 적용합니다.
if (user?.myTeam) {
  themeStore.setSelectedTeam(user.myTeam)
}

// 라우터 설정
const router = useRouter();

// Auth store
const authStore = useAuthStore();
const showLoginModal = ref(false);

// 로그인 후 리다이렉트할 경로 저장 (localStorage 사용)
const pendingRedirect = ref(localStorage.getItem('pendingRedirect') || null);

// 튜토리얼 composable 사용
const { showTutorialModal, openTutorialModal, closeTutorialModal, startTutorial } = useTutorial();

// 로그인 성공 후 처리를 위한 플래그
const isProcessingLogin = ref(false);

// 티켓 데이터
const tickets = ref([]);

// 로그인 상태 변화 감지하여 리다이렉트 처리
watch(() => authStore.isAuthenticated, async (newValue, oldValue) => {
  console.log('인증 상태 변경 감지:', { 
    from: oldValue, 
    to: newValue, 
    hasPendingRedirect: !!pendingRedirect.value,
    pendingRedirect: pendingRedirect.value,
    isProcessingLogin: isProcessingLogin.value
  });
  
  // 로그인 성공 시에만 처리
  if (newValue && !oldValue && pendingRedirect.value && !isProcessingLogin.value) {
    isProcessingLogin.value = true;
    
    try {
      // DOM 업데이트 완료까지 대기
      await nextTick();
      
      // 로그인 모달이 열려있다면 닫기
      if (showLoginModal.value) {
        console.log('로그인 모달 닫기');
        showLoginModal.value = false;
        // 모달 닫힘 애니메이션 대기
        await new Promise(resolve => setTimeout(resolve, 300));
      }
      
      // 리다이렉트 경로 저장 후 초기화
      const redirectPath = pendingRedirect.value;
      pendingRedirect.value = null;
      localStorage.removeItem('pendingRedirect');
      
      console.log('리다이렉트 시도:', redirectPath);
      
      // 리다이렉트 실행
      await router.push(redirectPath);
      console.log('리다이렉트 성공');
      
    } catch (error) {
      console.error('리다이렉트 실패:', error);
      // 실패 시 메인 페이지로 이동
      await router.push('/');
      console.log('메인 페이지로 이동');
    } finally {
      isProcessingLogin.value = false;
    }
  }
});


// 로그인 모달 닫기
function closeLoginModal() {
  console.log('로그인 모달 닫기 요청');
  showLoginModal.value = false;
  
  // 모달이 수동으로 닫힌 경우 대기 중인 리다이렉트 초기화
  if (!authStore.isAuthenticated) {
    pendingRedirect.value = null;
    localStorage.removeItem('pendingRedirect');
    isProcessingLogin.value = false;
  }
}

// 로그인 성공 후 처리를 위한 메서드
const handleLoginSuccess = async () => {
  console.log('로그인 성공 처리 시작');
  
  if (pendingRedirect.value) {
    console.log('대기 중인 리다이렉트 있음:', pendingRedirect.value);
    
    // 잠시 대기 후 리다이렉트 처리는 watch에서 자동으로 처리됨
    // 여기서는 추가 로직이 필요한 경우에만 사용
  }
};

// 팝오버가 완전히 닫혔는지 확인하는 함수
const waitForPopoverClose = () => {
  return new Promise((resolve) => {
    const checkPopover = () => {
      const popover = document.querySelector('.driver-popover');
      if (!popover) {
        console.log('팝오버가 완전히 닫혔습니다');
        resolve();
      } else {
        console.log('팝오버가 아직 존재합니다, 다시 확인...');
        setTimeout(checkPopover, 10); // 10ms 후 다시 확인 (응답성 향상)
      }
    };
    checkPopover();
  });
};

// 응모 페이지로 이동
const goToApply = async () => {
  console.log('응모하기 클릭 - 현재 인증 상태:', authStore.isAuthenticated);
  const isInTutorial = !!document.querySelector('.driver-popover');
  console.log('튜토리얼 상태:', isInTutorial);
  
  // 응모 튜토리얼 플래그 확인
  const showApplyTutorial = localStorage.getItem('showApplyTutorial') === 'true';
  
  // 튜토리얼이 활성화된 상태면 먼저 닫기
  if (isInTutorial) {
    console.log('튜토리얼 오버레이 닫기');
    try {
      const driverObj = driver();
      driverObj.destroy();
      // 팝오버가 완전히 닫힐 때까지 대기
      await waitForPopoverClose();
    } catch (e) {
      console.warn('튜토리얼 닫기 실패:', e);
    }
  }
  
  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    // 로그인 후 이동할 경로 저장 (튜토리얼 플래그 유지)
    const redirectPath = isInTutorial || showApplyTutorial ? '/application?tutorial=true' : '/application';
    console.log('로그인 필요, 리다이렉트 경로 저장:', redirectPath);
    
    // 대기 중인 리다이렉트 설정 및 로그인 모달 표시
    pendingRedirect.value = redirectPath;
    localStorage.setItem('pendingRedirect', redirectPath);
    await nextTick(); // DOM 업데이트 대기
    showLoginModal.value = true;
    console.log('로그인 모달 표시');
    return;
  }
  
  // 이미 로그인된 상태면 바로 이동
  try {
    const path = isInTutorial || showApplyTutorial ? '/application?tutorial=true' : '/application';
    console.log('바로 이동:', path);
    
    // 응모 튜토리얼 플래그가 있으면 제거 (한 번만 보여주기 위함)
    if (showApplyTutorial) {
      localStorage.removeItem('showApplyTutorial');
    }
    
    await router.push(path);
    console.log('이동 완료');
  } catch (err) {
    console.error('이동 실패:', err);
  }
};

// 양도 페이지로 이동 (개선된 버전)
const goToTransfer = async () => {
  console.log('양도하기 클릭 - 현재 인증 상태:', authStore.isAuthenticated);
  const isInTutorial = !!document.querySelector('.driver-popover');
  console.log('튜토리얼 상태:', isInTutorial);
  
  // 양도 튜토리얼 플래그 확인
  const showTransferTutorial = localStorage.getItem('showTransferTutorial') === 'true';
  
  // 튜토리얼이 활성화된 상태면 먼저 닫기
  if (isInTutorial) {
    console.log('튜토리얼 오버레이 닫기');
    try {
      const driverObj = driver();
      driverObj.destroy();
      // 팝오버가 완전히 닫힐 때까지 대기
      await waitForPopoverClose();
    } catch (e) {
      console.warn('튜토리얼 닫기 실패:', e);
    }
  }
  
  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    // 로그인 후 이동할 경로 저장 (튜토리얼 플래그 유지)
    const redirectPath = isInTutorial || showTransferTutorial ? '/transfer?tutorial=true' : '/transfer';
    console.log('로그인 필요, 리다이렉트 경로 저장:', redirectPath);
    
    // 대기 중인 리다이렉트 설정 및 로그인 모달 표시
    pendingRedirect.value = redirectPath;
    localStorage.setItem('pendingRedirect', redirectPath);
    await nextTick(); // DOM 업데이트 대기
    showLoginModal.value = true;
    console.log('로그인 모달 표시');
    return;
  }
  
  // 이미 로그인된 상태면 바로 이동
  try {
    const path = isInTutorial || showTransferTutorial ? '/transfer?tutorial=true' : '/transfer';
    console.log('바로 이동:', path);
    
    // 양도 튜토리얼 플래그가 있으면 제거 (한 번만 보여주기 위함)
    if (showTransferTutorial) {
      localStorage.removeItem('showTransferTutorial');
    }
    
    await router.push(path);
    console.log('이동 완료');
  } catch (err) {
    console.error('이동 실패:', err);
  }
};

// 사용자 랭킹 데이터
const userRankingData = ref([]);

// 사용자 랭킹 데이터 가져오기
const fetchUserRanking = async () => {
  try {
    const response = await axios.get(API_CONFIG.MAIN_PAGE.USER);
    console.log(response);
    // API 응답 데이터를 RankingCard 컴포넌트에 맞는 형태로 변환
    userRankingData.value = response.data.map(item => ({
      name: item.userName,
      subtitle: `${item.rank}위`,
      score: item.transferAllCount,
      avatar: item.imageUrl?item.imageUrl:'avatar/pitcher2.png'
    }));
  } catch (error) {
    console.error('사용자 랭킹 데이터 가져오기 실패:', error);
    // 기본값 설정 - RankingCard 컴포넌트 형식에 맞게 수정
    userRankingData.value = [
      { name: '서버 연결 중...', subtitle: '1위', score: 0, avatar: '/default-avatar.png' },
      { name: '데이터 로딩 중...', subtitle: '2위', score: 0, avatar: '/default-avatar.png' },
      { name: '잠시만 기다려주세요', subtitle: '3위', score: 0, avatar: '/default-avatar.png' }
    ];
  }
};

// 티켓 이동
const goToTicket = (ticketId) => {
  router.push(`/mypage/tickets/${ticketId}`);
};

// 응모 이동
const goToApplications = () => {
  router.push('/mypage/applications');
};

const goToTickets = () => {
  router.push('/mypage/tickets');
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
      ...(item.growthRate !== null && { change: `+${item.growthRate}%` }),
      avatar: `/orglogo/${item.teamName}.svg`
    }));
  } catch (error) {
    console.error('팀 랭킹 데이터 가져오기 실패:', error);
    // 기본값 설정 - RankingCard 컴포넌트 형식에 맞게 수정
    teamRankingData.value = [
      { name: '서버 연결 중...', subtitle: '1위', score: 0, change: '+0%', avatar: '/default-team.png' },
      { name: '데이터 로딩 중...', subtitle: '2위', score: 0, change: '+0%', avatar: '/default-team.png' },
      { name: '잠시만 기다려주세요', subtitle: '3위', score: 0, change: '+0%', avatar: '/default-team.png' }
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

// 자동 스크롤 캐러셀 관련 변수
const originalReviews = computed(() => reviews.value);
const extendedReviews = computed(() => {
  // 무한 루프를 위해 앞뒤로 복사본 추가
  return [...originalReviews.value, ...originalReviews.value, ...originalReviews.value];
});

const translateX = ref(0);
const isTransitioning = ref(false);
const autoScrollTimer = ref(null);
const isPaused = ref(false);
const currentActiveIndex = ref(0);

// 슬라이드 설정 - 더 부드럽게 조정
const slideWidth = 364; // 각 슬라이드 너비 (340px 카드 + 24px 간격)
const autoScrollSpeed = 4000; // 4초마다 이동 (더 여유롭게)
const scrollPixelsPerMove = 0.2; // 한 번에 이동할 픽셀 (더 부드럽게)
const smoothScrollInterval = 5; // 부드러운 스크롤 간격 (더 빠른 주기로 부드럽게)

// 자동 스크롤 시작
const startAutoScroll = () => {
  if (autoScrollTimer.value) clearInterval(autoScrollTimer.value);
  
  autoScrollTimer.value = setInterval(() => {
    if (!isPaused.value) {
      translateX.value -= scrollPixelsPerMove;
      
      // 한 슬라이드만큼 이동했는지 확인
      const currentSlideIndex = Math.abs(translateX.value) / slideWidth;
      
      if (currentSlideIndex >= originalReviews.value.length) {
        // 첫 번째 복사본 세트를 다 지나면 원본 위치로 리셋
        translateX.value = 0;
        currentActiveIndex.value = 0;
      } else {
        // 현재 활성 인덱스 업데이트
        currentActiveIndex.value = Math.floor(currentSlideIndex) % originalReviews.value.length;
      }
    }
  }, smoothScrollInterval);
};

// 자동 스크롤 일시정지
const pauseAutoScroll = () => {
  isPaused.value = true;
};

// 자동 스크롤 재개
const resumeAutoScroll = () => {
  isPaused.value = false;
};


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



// 컴포넌트 마운트 시 초기화
onMounted(async () => {
  console.log('HomeView 마운트됨');

  // 로그인 후 리다이렉트 처리 (watch가 동작하지 않을 경우 대비)
  if (authStore.isAuthenticated && pendingRedirect.value) {
    console.log('마운트 시 로그인된 상태에서 대기 중인 리다이렉트 발견:', pendingRedirect.value);
    const redirectPath = pendingRedirect.value;
    pendingRedirect.value = null;
    localStorage.removeItem('pendingRedirect');
    
    try {
      console.log('마운트 시 리다이렉트 실행:', redirectPath);
      await router.push(redirectPath);
      console.log('마운트 시 리다이렉트 성공');
      return; // 리다이렉트 성공 시 나머지 초기화 건너뛰기
    } catch (error) {
      console.error('마운트 시 리다이렉트 실패:', error);
    }
  }
  
  fetchUserRanking();
  fetchTeamRanking();
  startAutoScroll();
  
  // 튜토리얼은 이제 navbar의 '튜토리얼' 버튼을 통해서만 실행됩니다.
  // 최초 회원가입 이후 첫 방문 시 자동 튜토리얼 실행
  try {
    const firstSignup = localStorage.getItem('firstSignup');
    if (firstSignup === '0') {
      if (authStore.isAuthenticated) {
        // 로그인된 상태면 즉시 튜토리얼 시작
        startTutorial(authStore, { value: showLoginModal });
      } else {
        // 로그인 전이면 로그인 완료를 기다렸다가 시작
        const unwatch = watch(
          () => authStore.isAuthenticated,
          (val) => {
            if (val && localStorage.getItem('firstSignup') === '0') {
              startTutorial(authStore, { value: showLoginModal });
              unwatch && unwatch();
            }
          }
        );
      }
    }
  } catch (e) {
    console.warn('firstSignup 확인 중 오류:', e);
  }
  try {
  const response = await http.get(API_CONFIG.USER.TICKETS);
  
  if (response.data) {
    const now = new Date(); // 오늘 날짜
    // 가장 가까운 날짜의 티켓 고르기
    const closestTicket = response.data
      .filter(ticket => ticket.game && ticket.game.date && new Date(ticket.game.date) >= now) // 미래 티켓만
      .sort((a, b) => new Date(a.game.date) - new Date(b.game.date))[0]; // 날짜순 정렬 후 가장 가까운 티켓 선택

    tickets.value = closestTicket ? [closestTicket] : [];
    console.log("tickets.value : ", tickets.value);
    }
  } catch (error) {
    console.error('티켓 정보를 불러오는 중 오류 발생:', error);
    tickets.value = []; // 오류 발생 시 빈 배열로 초기화
  }
});

// 컴포넌트 언마운트 시 타이머 정리
onUnmounted(() => {
  if (autoScrollTimer.value) {
    clearInterval(autoScrollTimer.value);
  }
});

</script>

<style scoped>

/* Vuetify 커스텀 스타일 */

.hero-section .v-container {
  padding: 0 !important;
  transition: height 0.3s ease;
  height: 100%;
}

/* .hero-section .v-container.hovered {
  height: 65vh;
} */

.hero-section .v-row {
  height: 100%;
  align-items: stretch;
}

.hero-section .v-col {
  height: 100%;
}

.hero-section .v-row.flex-nowrap {
  height: 100%;
}

.card-col {
  transition: all 0.4s ease-in-out;
  height: 100%;
  display: flex;
}

.card-col .hero-card {
  height: 100%;
  width: 100%;
}

.hero-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
/* Card hover effects */
.card-col {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  z-index: 1;
  border-radius: 16px;
  overflow: hidden;
}

.card-col::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: var(--theme-gradient);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: -1;
  opacity: 0.7;
  pointer-events: none;
}

.card-col-hover::before {
  width: 300%;
  height: 300%;
  opacity: 0.7;
  border-radius: 0;
}

.card-col-hover {
  z-index: 2;
}

.hero-card {
  position: relative;
  overflow: hidden;
}

.hero-card::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: var(--theme-gradient);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 0;
  opacity: 0.7;
  pointer-events: none;
}

.hero-card:hover::before {
  width: 300%;
  height: 300%;
  opacity: 0.7;
  border-radius: 0;
}

.ticket-btn {
  background: rgba(255, 255, 255, 0.1) !important;
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
}

.ticket-btn::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  background: var(--theme-gradient);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 0;
  opacity: 0.7;
  pointer-events: none;
}

.ticket-btn-hover::before {
  width: 300%;
  height: 300%;
  opacity: 0.7;
  border-radius: 0;
}
/* 
.ticket-btn-hover {
  transform: translateY(-4px) scale(1.01);
  box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.2);
  background: var(--theme-gradient) !important;
} */

.ticket-btn-hover .ticket-text {
  transform: translateY(-4px) scale(1.01);
  color: #ffffff;
  transition: all 0.3s ease;
}


.ticket-text {
  color: var(--theme-primary);
}

.glass-effect {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1), rgba(255, 255, 255, 0.05));
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.glass-effect:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(31, 38, 135, 0.2);
}

.sub-card {
  height: 45%;
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
    font-size: 2rem !important;
  }
  
  .text-h5 {
    font-size: 2rem !important;
  }
  
  .text-h6 {
    font-size: 1rem !important;
  }
}

/* 튜토리얼 모달 스타일 - 금색 테마 */
.tutorial-modal {
  border-radius: 16px !important;
  box-shadow: 0 8px 32px rgba(255, 178, 44, 0.2) !important;
  border: 2px solid #FFB22C;
}

.tutorial-modal .v-card-title {
  height: 100px !important;
  background: linear-gradient(135deg, #FFB22C 0%, #FF9A1A 100%);
  color: white;
  border-radius: 16px 16px 0 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.tutorial-modal .v-icon {
  color: white !important;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.2));
}

.tutorial-modal .v-btn {
  font-weight: 600;
}

.tutorial-modal .v-btn--variant-flat {
  background: linear-gradient(135deg, #FFB22C 0%, #FF9A1A 100%) !important;
  color: white !important;
  box-shadow: 0 4px 12px rgba(255, 178, 44, 0.3) !important;
}

.tutorial-modal .v-btn--variant-flat:hover {
  transform: translateY(-1px);
  box-shadow: 0 6px 16px rgba(255, 178, 44, 0.4) !important;
}

.tutorial-modal .v-btn--variant-outlined {
  border-color: #FFB22C !important;
  color: #FF9A1A !important;
}

.tutorial-modal .v-btn--variant-outlined:hover {
  background-color: rgba(255, 178, 44, 0.1) !important;
}

/* 자동 스크롤 캐러셀 스타일 */
.auto-review-carousel-wrapper {
  position: relative;
  width: 100%;
  overflow: hidden;
  /* min-height: 400px; */
  background: #f8f9fa;
  border-radius: 16px;
  padding: 24px 0;
}

.auto-review-carousel-container {
  width: 100%;
  overflow: hidden;
  height: 300px;
}

.auto-review-carousel-track {
  display: flex;
  width: fit-content;
  gap: 24px; /* 간격 약간 증가 */
  transition: transform 0.1s linear; /* 부드러운 전환 효과 */
  will-change: transform; /* GPU 가속 최적화 */
}

.auto-review-slide {
  width: 340px; /* 너비 약간 증가 */
  flex-shrink: 0;
  height: 100%;
}



/* 진행 상태 인디케이터 */
.auto-carousel-indicators {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  margin-top: 24px;
}

.auto-carousel-indicators .indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #e0e0e0;
  transition: all 0.3s ease;
  cursor: pointer;
}

.auto-carousel-indicators .indicator-dot.active {
  background-color: #1976d2;
  transform: scale(1.5);
  box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.2);
}

.auto-carousel-indicators .indicator-dot:hover:not(.active) {
  background-color: #bdbdbd;
  transform: scale(1.2);
}

/* 반응형 디자인 */
@media (max-width: 768px) {
  .hero-section {
    height: auto;
    min-height: 400px;
  }
  
  .hero-section .card-col {
    margin-bottom: 16px;
  }
  
  .hero-section .card-col:last-child {
    margin-bottom: 0;
  }
  
  .auto-review-slide {
    width: 280px;
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

/**
 * 튜토리얼 모달의 제목 폰트 크기
 */
.tutorial-modal-main-title {
  font-size: 30px;
}

.tutorial-modal-sub-title {
  font-size: 17px;
}

.text-center.pa-6{
  padding-bottom: 0px !important;
}

</style>
