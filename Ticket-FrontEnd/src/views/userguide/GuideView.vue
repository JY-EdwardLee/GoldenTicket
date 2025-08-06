<template>
  <v-container fluid class="pa-0">
    <div class="guide-wrapper">
      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="loading-container">
        <v-progress-circular indeterminate color="primary" size="64"></v-progress-circular>
        <p class="mt-4 text-h6">이용가이드를 불러오는 중...</p>
      </div>
      
      <!-- 메인 가이드 콘텐츠 -->
      <div v-else class="guide-content">
        <!-- 헤더 -->
        <div class="guide-header">
          <v-container>
            <div class="text-center py-8">
              <h1 class="text-h3 font-weight-bold mb-4">이용가이드</h1>
              <p class="text-h6 text-grey-darken-1">티켓 응모부터 양도까지, 모든 기능을 쉽게 이용할 수 있습니다.</p>
              

            </div>
          </v-container>
        </div>
        
        <!-- 빠른 시작 가이드 -->
        <v-container class="py-8">
          <h2 class="text-h4 font-weight-bold mb-8 text-center">빠른 시작 가이드</h2>
          <v-row>
            <v-col cols="12" md="6" lg="4" v-for="guide in quickGuides" :key="guide.id">
              <v-card class="guide-card h-100" elevation="3">
                <v-card-text class="pa-6">
                  <div class="text-center mb-4">
                    <v-avatar size="80" :color="guide.color" class="mb-3">
                      <v-icon size="40" color="white">{{ guide.icon }}</v-icon>
                    </v-avatar>
                    <h3 class="text-h6 font-weight-bold">{{ guide.title }}</h3>
                  </div>
                  <div class="guide-steps">
                    <div v-for="(step, index) in guide.steps" :key="index" class="step-item mb-3">
                      <div class="d-flex align-center">
                        <v-avatar size="24" color="grey-lighten-2" class="mr-3">
                          <span class="text-caption font-weight-bold">{{ index + 1 }}</span>
                        </v-avatar>
                        <span class="text-body-2">{{ step }}</span>
                      </div>
                    </div>
                  </div>
                </v-card-text>
              </v-card>
            </v-col>
          </v-row>
        </v-container>
        
        <!-- 상세 가이드 섹션 -->
        <div class="detailed-guide-section">
          <v-container class="py-8">
            <h2 class="text-h4 font-weight-bold mb-8 text-center">상세 이용 가이드</h2>
            <v-row>
              <v-col cols="12" md="6" v-for="section in detailedSections" :key="section.id">
                <v-card class="detail-card mb-4" elevation="2">
                  <v-card-title class="bg-primary text-white">
                    <v-icon class="mr-2">{{ section.icon }}</v-icon>
                    {{ section.title }}
                  </v-card-title>
                  <v-card-text class="pa-4">
                    <div v-for="(item, index) in section.content" :key="index" class="mb-3">
                      <h4 class="text-subtitle-1 font-weight-bold mb-2">{{ item.subtitle }}</h4>
                      <p class="text-body-2 text-grey-darken-1">
                        <template v-if="item.subtitle === '구매 확인'">
                          구매 내역은 마이페이지 - 구매내역에서 확인 가능합니다.<br>
                          바로가고 싶다면 <span @click="goToPurchaseHistory" class="text-primary font-weight-bold cursor-pointer">이 링크</span>를 클릭하세요.
                        </template>
                        <template v-else-if="item.subtitle === '티켓 확인'">
                          결제 완료된 티켓은 마이페이지 - 나의 티켓에서 확인 가능합니다.<br>
                          바로가고 싶다면 <span @click="goToMyTickets" class="text-primary font-weight-bold cursor-pointer">이 링크</span>를 클릭하세요.
                        </template>
                        <template v-else>
                          <span v-html="item.description.replace(/\n/g, '<br>')"></span>
                        </template>
                      </p>
                    </div>
                  </v-card-text>
                </v-card>
              </v-col>
            </v-row>
          </v-container>
        </div>
        
        <!-- FAQ 섹션 -->
        <div class="faq-section bg-grey-lighten-5">
          <v-container class="py-8">
            <h2 class="text-h4 font-weight-bold mb-8 text-center">자주 묻는 질문 (FAQ)</h2>
            <v-expansion-panels variant="accordion">
              <v-expansion-panel v-for="faq in faqs" :key="faq.id" class="mb-2">
                <v-expansion-panel-title class="text-h6 font-weight-medium">
                  <v-icon class="mr-3" color="primary">mdi-help-circle</v-icon>
                  {{ faq.question }}
                </v-expansion-panel-title>
                <v-expansion-panel-text>
                  <p class="text-body-1 pa-4">{{ faq.answer }}</p>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-container>
        </div>
        
        <!-- 문의 섹션 -->
        <div class="contact-section">
          <v-container class="py-8">
            <v-card class="text-center pa-8" color="primary" dark>
              <v-icon size="64" class="mb-4">mdi-headset</v-icon>
              <h2 class="text-h5 mb-4">추가 도움이 필요하신가요?</h2>
              <p class="text-body-1 mb-6">기타 문의사항이 있으시면 고객센터로 연락주세요.</p>
              <v-btn 
                color="white"
                size="large"
                prepend-icon="mdi-phone"
              >
                고객센터 연락하기
              </v-btn>
            </v-card>
          </v-container>
        </div>
      </div>
    </div>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const isLoading = ref(true);

const quickGuides = [
  {
    id: 1,
    title: '티켓 응모하기',
    icon: 'mdi-ticket',
    color: 'primary',
    steps: [
      '원하는 팀 선택',
      '원하는 날짜와 경기 선택',
      '응모하기 버튼 클릭',
    ]
  },
  {
    id: 2,
    title: '티켓 양도하기',
    icon: 'mdi-swap-horizontal',
    color: 'success',
    steps: [
      '보유 티켓 목록 확인',
      '양도할 티켓 선택',
      '양도하기 버튼 클릭'
    ]
  },
  {
    id: 3,
    title: '게시판 이용하기',
    icon: 'mdi-forum',
    color: 'warning',
    steps: [
      '자유 게시판에서 글 작성',
      '다른 사용자 글에 댓글 달기',
      '단체관람 신청',
    ]
  }
];

const detailedSections = [
  {
    id: 1,
    title: '티켓 응모 상세 가이드',
    icon: 'mdi-ticket-confirmation',
    content: [
      {
        subtitle: '원하는 팀 선택',
        description: '관람하고 싶은 팀을 선택하세요.'
      },
      {
        subtitle: '원하는 날짜와 경기 선택',
        description: '관람하고 싶은 날짜를 선택하세요.\n해당 날짜의 경기가 자동으로 표시됩니다.'
      },
      {
        subtitle: '응모하기 버튼 클릭',
        description: '응모 버튼을 클릭하세요. 응모 신청이 완료됩니다.'
      },
      {
        subtitle: '당첨!',
        description: '30분 내로 결제해주세요. 결제 후 티켓을 받을 수 있습니다.'
      }
    ]
  },
  {
    id: 2,
    title: '티켓 양도 상세 가이드',
    icon: 'mdi-swap-horizontal-circle',
    content: [
      {
        subtitle: '보유 티켓 목록 확인',
        description: '티켓 플랫폼을 선택해주세요.\n보유하고 있는 티켓 목록이 자동으로 표시됩니다.'
      },
      {
        subtitle: '양도할 티켓 선택',
        description: '양도할 티켓을 선택해주세요.'
      },
      {
        subtitle: '양도하기 버튼 클릭',
        description: '양도 버튼을 클릭하세요. 양도 신청이 완료됩니다.'
      },
      {
        subtitle: '매칭!',
        description: '매칭이 완료되었습니다. 결제 완료 후 금액을 받을 수 있습니다.'
      }
    ]
  },
  {
    id: 3,
    title: '결제 안내',
    icon: 'mdi-credit-card',
    content: [
      {
        subtitle: '결제 방법',
        description: '당첨 확정 후 30분 이내에 결제를 완료해야 합니다.\n간편결제, 무통장입금을 지원합니다.'
      },
      {
        subtitle: '구매 확인',
        description: '구매 내역은 마이페이지 - 구매내역에서 확인 가능합니다.\n바로가고 싶다면 이 링크를 클릭하세요.'
      },
      {
        subtitle: '티켓 확인',
        description: '결제 완료된 티켓은 마이페이지 - 나의 티켓에서 확인 가능합니다.\n바로가고 싶다면 이 링크를 클릭하세요.'
      },
    ]
  },
  {
    id: 4,
    title: '게시판 이용 안내',
    icon: 'mdi-forum-outline',
    content: [
      {
        subtitle: '자유 게시판',
        description: '자유롭게 글을 작성하고 다른 사용자들과 소통할 수 있습니다.\n댓글을 달아 소통할 수 있습니다.'
      },
      {
        subtitle: '단체관람 게시판',
        description: '단체관람을 원하는 경우 신청할 수 있습니다.\n다른 사람들과 함께 야구 관람을 즐길 수 있습니다.'
      },
      {
        subtitle: '커뮤니티 규칙',
        description: '건전한 커뮤니티 문화를 위해 예의를 지켜주세요.\n비방이나 욕설은 제재될 수 있습니다.'
      }
    ]
  }
];

const faqs = [
  {
    id: 1,
    question: '티켓 응모는 언제까지 가능한가요?',
    answer: '경기 시작 2시간 전까지 응모가 가능합니다. 응모 마감 시간은 각 구단별로 다를 수 있으니 상세 페이지에서 확인해주세요.'
  },
  {
    id: 2,
    question: '당첨된 티켓을 양도할 수 있나요?',
    answer: '아뇨, 당첨된 티켓은 양도할 수 없습니다. 양도 문화 발전을 위해 양해 부탁드립니다.'
  },
  {
    id: 3,
    question: '결제는 어떻게 진행되나요?',
    answer: '당첨 확정 후 30분 이내에 결제를 완료해야 합니다. 간편결제, 무통장입금을 지원합니다.'
  },
  {
    id: 4,
    question: '결제했는데 어떻게 입장할 수 있나요?',
    answer: '티켓은 QR코드를 통해 확인할 수 있습니다. 티켓 확인에 문제가 있다면 고객센터로 연락주시기 바랍니다.'
  },
  {
    id: 5,
    question: '한 사람이 여러 경기에 동시 응모할 수 있나요?',
    answer: '네, 여러 경기에 동시 응모가 가능합니다. 단, 같은 경기에 대해서는 중복 응모가 불가능합니다.'
  }
];

// Router 인스턴스
const router = useRouter();

// 페이지 이동 함수들
const goToPurchaseHistory = () => {
  router.push('/mypage/purchase');
};

const goToMyTickets = () => {
  router.push('/mypage/tickets');
};

// 로딩 완료 처리
onMounted(() => {
  // 1초 후 로딩 완료
  setTimeout(() => {
    isLoading.value = false;
  }, 1000);
});
</script>

<style scoped>
.guide-wrapper {
  min-height: 600px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 400px;
}

.fallback-container {
  max-width: 1200px;
  margin: 0 auto;
}

.gitbook-frame {
  border: none;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.guide-card {
  transition: transform 0.2s ease-in-out;
  border-radius: 12px;
}

.guide-card:hover {
  transform: translateY(-2px);
}

.step-item {
  padding: 4px 0;
}

.d-flex.align-center{
  margin-top: 10px;
  margin-left: 10px;
}

.text-h6.font-weight-bold{
  font-size: 25px !important;
}

.text-subtitle-1 {
  font-size: 25px !important;
}

.pa-6{
  padding: 20px !important;
}

.text-h4.font-weight-bold.mb-8.text-center{
  margin-bottom: 40px !important;
}

v-card-title.bg-primary.text-white{
  font-size: 25px !important;
}

.text-body-2 {
  font-size: 20px !important;
}

.text-body-2.text-grey-darken-1 {
  white-space: pre-line;
  margin-bottom: 40px !important;
}

.cursor-pointer {
  cursor: pointer;
}

.cursor-pointer:hover {
  text-decoration: underline;
}
</style>