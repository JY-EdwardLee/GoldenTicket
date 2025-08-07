<template>
  <div>
    <div v-if="ticketDetail" class="ticket-wrapper">
      <div class="ticket-container">
        <div class="ticket-header">
          <img :src="homeTeamLogoUrl" alt="Home Team Logo" class="team-logo" />
        </div>

        <div class="ticket-body">
          <div class="match-info">
            <div class="team">
              <img :src="homeTeamLogoUrl" alt="Home Team Logo" class="team-logo" />
              <span class="team-name">{{ enumToTeamName[ticketDetail.game.home] }}</span>
            </div>
            <span class="vs">VS</span>
            <div class="team">
              <img :src="awayTeamLogoUrl" alt="Away Team Logo" class="team-logo" />
              <span class="team-name">{{ enumToTeamName[ticketDetail.game.away] }}</span>
            </div>
          </div>

          <div class="detail-info">
            <div class="info-item">
              <span class="icon">&#x1F4C5;</span>
              <div class="info-text">
                <p>{{ formatDate(ticketDetail.game.date).split(' ')[0] }}</p>
                <p>{{ formatDate(ticketDetail.game.date).split(' ')[1] }} {{ formatDate(ticketDetail.game.date).split(' ')[2] }} 경기 시작</p>
              </div>
            </div>
            <div class="info-item">
              <span class="icon">&#x1F4CD;</span>
              <div class="info-text">
                <p>{{ stadiumOfTeam[ticketDetail.game.stadium] }}</p>
              </div>
            </div>
            <div class="info-item">
              <span class="icon">&#x1F4BA;</span>
              <div class="info-text">
                <p>{{ ticketDetail.seat?.split(' ')[0] }} {{ ticketDetail.seat?.split(' ')[1] }}</p>
                <p>{{ ticketDetail.seat?.split(' ').slice(2).join(' ') }}</p>
              </div>
            </div>
            <div class="info-item">
              <span class="icon">&#x1F6AA;</span>
              <div class="info-text">
                <p>메인 게이트</p>
                <p>18:00 입장 가능</p>
              </div>
            </div>
          </div>

          <div class="separator"></div>

          <div class="price-section">
            <div class="price">
              <p class="amount">₩{{ ticketDetail.price }}</p>
              <p class="type">일반 티켓</p>
            </div>
            <div class="ticket-id">
              <p>티켓 ID</p>
              <p>#T{{ ticketDetail.ticketId }}</p>
            </div>
          </div>

          <div class="qr-section" @click="showQrModal">
            <div class="qr-placeholder">
              <img :src="qrCodeImageSrc" alt="QR Code" class="qr-image">
            </div>
            <p class="qr-notice">입장 시 QR 코드 제시</p>
            <p class="mobile-ticket">모바일 티켓</p>
          </div>

          <div class="notice-section">
            <ul>
              <li>경기 시작 1시간 전 입장 가능</li>
              <li>재입장 불가</li>
              <li>우천 시 금액 자동 환불</li>
            </ul>
          </div>

          <div class="footer-logo-section">
            <img :src="homeTeamLogoUrl" alt="Footer Team Logo" class="footer-logo" />
            <p>NO LIMITS, AMAZING LANDERS</p>
          </div>
        </div>
      </div>
    </div>

    <!-- QR Code Modal -->
    <div v-if="isQrModalVisible" class="modal-overlay" @click="hideQrModal">
      <div class="modal-content" @click.stop>
        <img :src="qrCodeImageSrc" alt="Enlarged QR Code" class="enlarged-qr-image">
      </div>
    </div>

    <div v-else class="loading-container">
      <p>티켓 정보를 불러오는 중입니다...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import http from '@/utils/http';
import { API_CONFIG } from '@/config/api.config';
import { enumToTeamName, teamNameToLogo } from '@/utils/teamNameMap';
import {stadiumOfTeam} from '@/utils/teamStadium';
import { formatDate } from '@/utils/dateUtils';

const route = useRoute();
const isQrModalVisible = ref(false);

const ticketDetail = ref(null);
const qrCodeBase64 = ref(null);
let qrCodeInterval = null;

onMounted(() => { 
    fetchTicketDetail(route.params.id);
    fetchQRCode(route.params.id);
    qrCodeInterval = setInterval(() => {
        fetchQRCode(route.params.id);
    }, 10000);
});

onUnmounted(() => {
    if (qrCodeInterval) {
        clearInterval(qrCodeInterval);
    }
});

const fetchTicketDetail = async (ticketId) => {
  try {
    const response = await http.get(API_CONFIG.TICKET.DETAIL(ticketId));
    ticketDetail.value = response.data;
    // Assuming stadium address is part of the response, otherwise it needs to be fetched or mapped.
    // if (!ticketDetail.value.stadiumAddress) {
    //     ticketDetail.value.stadiumAddress = '인천광역시 미추홀구'; // Placeholder
    // }
    console.log("ticketDetail.value", ticketDetail.value)
  } catch (error) {
    console.error('Failed to fetch ticket details:', error);
  }
};

const fetchQRCode = async (ticketId) => {
  try {
    const response = await http.post(API_CONFIG.TICKET.QR(ticketId));
    qrCodeBase64.value = response.data;
    console.log("qrCodeBase64.value", qrCodeBase64.value)
  } catch (error) {
    console.error('Failed to fetch ticket details:', error);
  }
};

const getLogoUrl = (teamName) => {
    if (teamName) {
        const logoFileName = teamNameToLogo[teamName];
        // Correctly resolves path from assets
        return `/src/assets/logo/${logoFileName}.svg`;
    }
    return '';
};

const qrCodeImageSrc = computed(() => {
      return `data:image/png;base64,${qrCodeBase64.value}`
    })

const homeTeamLogoUrl = computed(() => getLogoUrl(ticketDetail.value?.game?.home));
const awayTeamLogoUrl = computed(() => getLogoUrl(ticketDetail.value?.game?.away));

const showQrModal = () => {
  isQrModalVisible.value = true;
};

const hideQrModal = () => {
  isQrModalVisible.value = false;
};

</script>

<style scoped>
.ticket-header {
  height: 200px; /* 원하는 높이로 조절하세요 */
  overflow: hidden;
  position: relative;
}

.ticket-header .team-logo {
  border-radius: 0;
  width: 100%;
  height: 100%;
  opacity: 0.5;
  object-fit: cover;
  object-position: center;
}

.ticket-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
  background-color: #f4f4f4;
}

.ticket-container {
  width: 400px;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  background-color: var(--theme-primary, #ff6b35); /* Main theme color */
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.ticket-header {
  height: 250px;
  background-size: cover;
  background-position: center;
  position: relative;
}

.ticket-body {
  background-color: white;
  border-radius: 16px 16px 0 0;
  padding: 20px;
  margin-top: -20px;
  position: relative;
}

.match-info {
  display: flex;
  justify-content: space-around;
  align-items: center;
  text-align: center;
  margin-bottom: 25px;
}

.team {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.team-logo {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  background: #f0f0f0;
  margin-bottom: 8px;
}

.team-name {
  font-weight: 600;
  font-size: 1rem;
}

.vs {
  font-size: 1.2rem;
  font-weight: 700;
  color: #555;
}

.detail-info {
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.icon {
  font-size: 1.2rem;
  color: #888;
  width: 30px;
  text-align: center;
}

.info-text p {
  margin: 0;
  line-height: 1.4;
}
.info-text p:first-child {
  font-weight: 600;
  color: #333;
}
.info-text p:last-child {
  font-size: 0.9rem;
  color: #666;
}

.separator {
  border-top: 1px dashed #ccc;
  margin: 25px 0;
}

.price-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.price .amount {
  font-size: 1.5rem;
  font-weight: 700;
  color: #CE0E2D;
  margin: 0;
}
.price .type {
  font-size: 0.9rem;
  color: #555;
  margin: 0;
}

.ticket-id {
  text-align: right;
}
.ticket-id p {
  margin: 0;
  font-size: 0.9rem;
  color: #666;
}
.ticket-id p:last-child {
  font-weight: 600;
}

.qr-section {
  background-color: #f9f9f9;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  margin-bottom: 25px;
}

.qr-placeholder {
  width: 80px;
  height: 80px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 10px;
}

.qr-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
}


.qr-notice {
  margin: 0 0 5px;
  font-weight: 500;
}

.mobile-ticket {
  margin: 0;
  color: #1a73e8;
  font-weight: 500;
  font-size: 0.9rem;
}

.notice-section ul {
  list-style: none;
  padding: 0;
  margin: 0 0 25px 0;
  font-size: 0.8rem;
  color: #777;
}

.notice-section li {
  margin-bottom: 5px;
  position: relative;
  padding-left: 15px;
}

.notice-section li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #ccc;
}

.footer-logo-section {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.footer-logo {
  height: 30px;
  opacity: 0.7;
  margin-bottom: 5px;
}

.footer-logo-section p {
  font-size: 0.8rem;
  color: #aaa;
  letter-spacing: 1px;
  margin: 0;
}

.loading-container {
  padding: 50px;
  text-align: center;
  color: #888;
}


.qr-section {
  cursor: pointer;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  padding: 20px;
  background: white;
  border-radius: 10px;
  display: inline-block;
}

.enlarged-qr-image {
  max-width: 80vw;
  max-height: 80vh;
  display: block;
}
</style>