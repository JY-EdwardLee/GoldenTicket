import { createRouter, createWebHistory } from 'vue-router';

import HomeView from '../views/home/HomeView.vue';
import TicketTransferView from '../views/ticket/transfer/TicketTransferView.vue';
import TicketApplicationView from '../views/ticket/apply/TicketApplicationView.vue';
import BulletinView from '../views/board/BulletinView.vue';
import GuideView from '../views/user/GuideView.vue';
import MyPageView from '../views/user/MyPageView.vue';
import UserInfoComponent from '../components/user/UserInfoComponent.vue';
import MyApplicationsComponent from '../components/user/MyApplicationsComponent.vue';
import MyTicketsComponent from '../components/user/MyTicketsComponent.vue';
import MyPostsComponent from '../components/user/MyPostsComponent.vue';
import PurchaseHistoryComponent from '../components/user/PurchaseHistoryComponent.vue';
import PurchaseDetailComponent from '../components/user/PurchaseDetailComponent.vue';

const routes = [
  { path: '/', name: 'Home', component: HomeView },
  { path: '/transfer', name: 'TicketList', component: TicketTransferView }, // 티켓 리스트
  { path: '/transfer/detail/:id', name: 'TicketDetail', component: TicketTransferView }, // 티켓 상세
  { path: '/application', name: 'TicketApplication', component: TicketApplicationView },
  { path: '/bulletin', name: 'Bulletin', component: BulletinView },
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
  routes,
});

export default router;
