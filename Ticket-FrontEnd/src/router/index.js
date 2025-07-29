import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

// Lazy load views for better performance
const HomeView = () => import('@/views/home/HomeView.vue');
const TicketTransferView = () => import('@/views/ticket/transfer/TicketTransferView.vue');
const TicketApplicationView = () => import('@/views/ticket/apply/TicketApplicationView.vue');
const BulletinView = () => import('@/views/board/BulletinView.vue');
const GuideView = () => import('@/views/userguide/GuideView.vue');
const SignupView = () => import('@/views/signup/SignupView.vue');
const OAuthCallbackView = () => import('@/views/auth/OAuthCallbackView.vue');

const routes = [
  // 메인 페이지
  { path: '/', name: 'Home', component: HomeView },

  // 회원가입 페이지
  { path: '/signup', name: 'Signup', component: SignupView },

  // 티켓 이전 페이지
  { path: '/transfer', name: 'TicketTransfer', component: TicketTransferView },

  // 티켓 구매 페이지
  { path: '/application', name: 'TicketApplication', component: TicketApplicationView },

  // 게시판 페이지
  { path: '/bulletin', name: 'Bulletin', component: BulletinView },

  // 사용자 가이드 페이지
  { path: '/guide', name: 'Guide', component: GuideView },
];

const router = createRouter({
  history: createWebHistory(),
  routes: [
    ...routes,
    // OAuth 콜백 라우트
    {
      path: '/oauth/callback',
      name: 'OAuthCallback',
      component: OAuthCallbackView,
      meta: { requiresAuth: false }
    },
    // 기존 라우트 유지
    ...routes
  ],
  scrollBehavior(to, from, savedPosition) {
    // 페이지 전환 시 스크롤을 맨 위로 이동
    return savedPosition || { top: 0 };
  }
});

// 네비게이션 가드 설정
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore();
  const isAuthenticated = await authStore.verifyToken();

  // 인증이 필요한 라우트인지 확인
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      // 인증되지 않은 경우 로그인 페이지로 리다이렉트
      next({ name: 'OAuthCallback', query: { redirect: to.fullPath } });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
