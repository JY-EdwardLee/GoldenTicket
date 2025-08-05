<template>
  <div class="ticket-detail">
    <div class="ticket-header">
      <h2>{{ ticketDetails.game?.home || '로딩 중...' }}</h2>
      <div class="ticket-status">{{ ticketDetails.status || '로딩 중...' }}</div>
    </div>
    
    <div class="player-info">
      <div class="player-image">
        <div class="player-avatar">14</div>
      </div>
      <div class="player-details">
        <h3>Choi Jeong (최정)</h3>
        <p>SSG Landers</p>
        <p>1993 AL MVP - Infielder</p>
        <p>No. 14 | 05</p>
      </div>
    </div>

    <div class="match-info">
      <div class="teams">
        <span class="home-team">{{ ticketDetails.game?.home ? enumToTeamName(ticketDetails.game.home) : '-' }}</span>
        <span class="vs">VS</span>
        <span class="away-team">{{ ticketDetails.game?.away ? enumToTeamName(ticketDetails.game.away) : '-' }}</span>
      </div>
      
      <div class="match-details">
        <div class="detail-row">
          <span class="label">경기일시</span>
          <span class="value">{{ ticketDetails.game?.date ? formatDate(ticketDetails.game.date) : '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">경기장소</span>
          <span class="value">{{ ticketDetails.game?.home ? stadiumOfTeam(ticketDetails.game.home) : '-' }}</span>
        </div>
        <div class="detail-row">
          <span class="label">좌석정보</span>
          <span class="value">{{ ticketDetails.seat }}</span>
        </div>
        <div class="detail-row">
          <span class="label">티켓가격</span>
          <span class="value">{{ ticketDetails.price }}</span>
        </div>
        <div class="detail-row">
          <span class="label">티켓번호</span>
          <span class="value">{{ ticketDetails.ticketId }}</span>
        </div>
      </div>
    </div>

    <div class="qr-section">
      <div class="qr-code">
        <div class="qr-placeholder">QR CODE</div>
        <p class="qr-notice">입장 시 QR 코드 제시</p>
      </div>
      <p class="mobile-ticket">모바일 티켓</p>
    </div>

    <div class="notice-section">
      <h3>유의사항</h3>
      <ul>
        <li>• 경기 시작 1시간 전 입장 가능</li>
        <li>• 재입장 불가</li>
        <li>• 우천 시 경기 일정 변경 가능</li>
      </ul>
    </div>

    <div class="team-logo">
      <img src="@/assets/logo/SSG.svg" alt="SSG Landers">
      <p>NO LIMITS, AMAZING LANDERS</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import http from '@/utils/http'
import { API_CONFIG } from '@/config/api.config'
import { formatDate } from '@/utils/dateUtils'
import { enumToTeamName } from '@/utils/teamNameMap'
import {stadiumOfTeam} from '@/utils/teamStadium'
const route = useRoute();
const ticketId = ref(route.params.id);

const ticketDetails = ref({});

const fetchTicketDetails = async () => {
  try {
    const response = await http.get(API_CONFIG.TICKET.DETAIL(ticketId.value))
    ticketDetails.value = response.data
    console.log(ticketDetails.value)
  } catch (error) {
    console.error('Error fetching ticket details:', error)
  }
}

onMounted(() => {
  ticketId.value = route.params.id
  console.log("티켓정보", ticketId.value)
  fetchTicketDetails(ticketId.value)
});
</script>

<style scoped>
.ticket-detail {
  max-width: 100%;
  margin: 0 auto;
  padding: 20px;
  font-family: 'Noto Sans KR', sans-serif;
  background-color: #f8f9fa;
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.ticket-header h2 {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
}

.ticket-status {
  background-color: #666;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.875rem;
}

.player-info {
  display: flex;
  align-items: center;
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.player-avatar {
  width: 80px;
  height: 80px;
  background-color: #002D62;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.5rem;
  font-weight: bold;
  margin-right: 20px;
}

.player-details h3 {
  margin: 0 0 8px 0;
  font-size: 1.25rem;
}

.player-details p {
  margin: 4px 0;
  color: #666;
  font-size: 0.9rem;
}

.match-info {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.teams {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
  font-size: 1.1rem;
  font-weight: 500;
}

.vs {
  margin: 0 15px;
  color: #999;
}

.detail-row {
  display: flex;
  margin-bottom: 12px;
  font-size: 0.95rem;
}

.label {
  width: 80px;
  color: #666;
  flex-shrink: 0;
}

.value {
  flex-grow: 1;
  font-weight: 500;
}

.qr-section {
  background-color: white;
  border-radius: 12px;
  padding: 30px 20px;
  margin-bottom: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.qr-placeholder {
  width: 200px;
  height: 200px;
  margin: 0 auto 15px;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 1rem;
}

.qr-notice {
  margin: 10px 0 0;
  font-weight: 500;
}

.mobile-ticket {
  margin-top: 15px;
  color: #1a73e8;
  font-weight: 500;
}

.notice-section {
  background-color: white;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.notice-section h3 {
  margin-top: 0;
  margin-bottom: 15px;
  font-size: 1.1rem;
  color: #333;
}

.notice-section ul {
  margin: 0;
  padding-left: 20px;
}

.notice-section li {
  margin-bottom: 8px;
  color: #666;
  font-size: 0.9rem;
}

.team-logo {
  text-align: center;
  margin-top: 30px;
}

.team-logo img {
  max-width: 120px;
  margin-bottom: 10px;
}

.team-logo p {
  color: #002D62;
  font-weight: 500;
  margin: 5px 0 0;
}
</style>