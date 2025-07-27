import { createRouter, createWebHistory } from 'vue-router';

import HomeView from '../views/home/HomeView.vue';
import TicketTransferView from '../views/ticket/transfer/TicketTransferView.vue';
import TicketApplicationView from '../views/ticket/apply/TicketApplicationView.vue';
import BulletinView from '../views/board/BulletinView.vue';
import GuideView from '../views/user/GuideView.vue';

const routes = [
  { path: '/', name: 'Home', component: HomeView },
  { path: '/transfer', name: 'TicketTransfer', component: TicketTransferView },
  { path: '/application', name: 'TicketApplication', component: TicketApplicationView },
  { path: '/bulletin', name: 'Bulletin', component: BulletinView },
  { path: '/guide', name: 'Guide', component: GuideView },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
