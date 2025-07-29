import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth';

import HomeView from '../views/home/HomeView.vue';
import TicketTransferView from '../views/ticket/transfer/TicketTransferView.vue';
import TicketApplicationView from '../views/ticket/apply/TicketApplicationView.vue';
import BulletinView from '../views/board/BulletinView.vue';
import BulletinCreateView from '../views/board/BulletinCreateView.vue';
import GuideView from '../views/user/GuideView.vue';
import MyPageView from '../views/user/MyPageView.vue';
import UserInfoComponent from '../components/user/UserInfoComponent.vue';
import MyApplicationsComponent from '../components/user/MyApplicationsComponent.vue';
import MyTicketsComponent from '../components/user/MyTicketsComponent.vue';
import MyPostsComponent from '../components/user/MyPostsComponent.vue';
import PurchaseHistoryComponent from '../components/user/PurchaseHistoryComponent.vue';
import PurchaseDetailComponent from '../components/user/PurchaseDetailComponent.vue';
// Lazy load views for better performance
const SignupView = () => import('@/views/signup/SignupView.vue');
const OAuthCallbackView = () => import('@/views/auth/OAuthCallbackView.vue');

const routes = [
  // 메인 페이지
  { path: '/', name: 'Home', component: HomeView },
  { path: '/transfer', name: 'TicketList', component: TicketTransferView }, // 티켓 리스트
  { path: '/transfer/detail/:id', name: 'TicketDetail', component: TicketTransferView }, // 티켓 상세

  // 회원가입 페이지
  { path: '/signup', name: 'Signup', component: SignupView },

  // 티켓 이전 페이지
  { path: '/transfer', name: 'TicketTransfer', component: TicketTransferView },

  // 티켓 구매 페이지
  { path: '/application', name: 'TicketApplication', component: TicketApplicationView },

  // 게시판 페이지
  { path: '/bulletin', name: 'Bulletin', component: BulletinView },
  { path: '/bulletin/create', name: 'BulletinCreate', component: BulletinCreateView },
  { path: '/guide', name: 'Guide', component: GuideView },
  { 
    path: '/mypage', 
    name: 'MyPage', 
    component: MyPageView,
    children: [
      { path: '', name: 'UserInfo', component: UserInfoComponent },
      { path: 'applications', name: 'MyApplications', component: MyApplicationsComponent },
      { path: 'tickets', name: 'MyTickets', component: MyTicketsComponent },
      { path: 'posts', name: 'MyPosts', component: MyPostsComponent },
      { path: 'purchase', name: 'PurchaseHistory', component: PurchaseHistoryComponent },
      { path: 'purchase/:id', name: 'PurchaseDetail', component: PurchaseDetailComponent }
    ]
  }
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
      next({ name: 'Login', query: { redirect: to.fullPath } });
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
