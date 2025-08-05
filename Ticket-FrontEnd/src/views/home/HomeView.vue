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
          
          <v-row>
            <v-col cols="12" md="4" class="pa-4" v-for="(review, index) in reviews" :key="index">
              <ReviewCard
                :name="review.name"
                :team="review.team"
                :content="review.content"
                :date="review.date"
              />
            </v-col>
          </v-row>
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
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

// 공통 컴포넌트 import
import HeroCard from '../../components/ui/HeroCard.vue';
import RankingCard from '../../components/ui/RankingCard.vue';
import ReviewCard from '../../components/ui/ReviewCard.vue';
import ServiceCard from '../../components/ui/ServiceCard.vue';
import SectionHeader from '../../components/ui/SectionHeader.vue';

// 라우터 설정
const router = useRouter();

// Auth store
const authStore = useAuthStore();
const showLoginModal = ref(false);

// 로그인 모달 닫기
function closeLoginModal() {
  showLoginModal.value = false;
}

// 메인 페이지 로직
const goToApply = () => {
  console.log(authStore.isAuthenticated)
  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    showLoginModal.value = true;
    return;
  }
  // 응모 페이지로 이동
  router.push('/application');
};

const goToTransfer = () => {
  // 로그인 상태 확인
  if (!authStore.isAuthenticated) {
    showLoginModal.value = true;
    return;
  }
  // 양도 페이지로 이동
  router.push('/transfer');
};

// 사용자 랭킹 데이터
const userRankingData = ref([
  {
    name: '김스포츠',
    subtitle: '156회 양도',
    score: 156,
    type: 'user'
  },
  {
    name: '박축구',
    subtitle: '142회 양도',
    score: 142,
    type: 'user'
  },
  {
    name: '최야구',
    subtitle: '128회 양도',
    score: 128,
    type: 'user'
  }
]);

// 팀 랭킹 데이터
const teamRankingData = ref([
  {
    name: 'FC 서울',
    subtitle: '2456건',
    score: 2456,
    change: '+12%',
    type: 'team'
  },
  {
    name: '수원 삼성',
    subtitle: '2234건',
    score: 2234,
    change: '+8%',
    type: 'team'
  },
  {
    name: '인천 유나이티드',
    subtitle: '2156건',
    score: 2156,
    change: '+15%',
    type: 'team'
  }
]);

// 후기 데이터
const reviews = ref([
  {
    name: '김민수',
    team: '서울 FC',
    content: '티켓 응모가 정말 간편하고 빠르게 처리되어서 너무 만족합니다.',
    date: '2024.01.15'
  },
  {
    name: '박영희',
    team: '부산 아이파크',
    content: '안전하고 신뢰할 수 있는 플랫폼이에요. 처음 사용해봤는데 정말 좋네요.',
    date: '2024.01.12'
  },
  {
    name: '이철수',
    team: '인천 유나이티드',
    content: '티켓 양도가 이렇게 쉬울 줄 몰랐어요. 다음에도 꼭 이용할게요.',
    date: '2024.01.10'
  }
]);

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

// onMounted(() => {
//   const authStore = useAuthStore()
//   authStore.verifyToken()
// })

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
</style>
