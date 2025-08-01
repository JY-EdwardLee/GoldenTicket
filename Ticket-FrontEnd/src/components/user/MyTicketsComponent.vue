<template>
  <div class="tickets-content">
    <div class="container">
      <div class="content-header">
        <h1>나의 티켓</h1>
      </div>

    <!-- 티켓 목록 -->
    <div class="tickets-list">
              <div
          v-for="ticket in tickets"
          :key="ticket.id"
          class="ticket-card"
          :style="{ backgroundImage: `url(${generateTicketBackground(ticket.id)})` }"
        >
        <div class="ticket-header">
          <div class="ticket-info">
            <div class="team-icon">⚾</div>
            <div class="game-details">
              <h3 class="game-title">{{ enumToTeamName[ticket.game.away] }} vs {{ enumToTeamName[ticket.game.home] }}</h3>
              <p class="stadium">{{ stadiumOfTeam[ticket.game.stadium] }}</p>
            </div>
          </div>
          <div class="ticket-status" :class="ticket.status">
            {{ getStatusText(ticket.status) }}
          </div>
        </div>
        
        <div class="ticket-body">
          <div class="ticket-details">
            <div class="detail-item">
              <span class="label">경기시간:</span>
              <span class="value">{{ ticket.game.time }}</span>
            </div>
            <div class="detail-item">
              <span class="label">경기일:</span>
              <span class="value">{{ ticket.game.date }}</span>
            </div>
            <div class="detail-item">
              <span class="label">좌석:</span>
              <span class="value">{{ ticket.seat }}</span>
            </div>
            <div class="detail-item">
              <span class="label">티켓 번호:</span>
              <span class="value">{{ ticket.ticketId }}</span>
            </div>
            <div class="detail-item">
              <span class="label">구매일:</span>
              <span class="value">{{ formatDate(ticket.transactionDate) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">구매 금액:</span>
              <span class="value price">{{ ticket.price }}원</span>
            </div>
          </div>
        </div>

        <div class="ticket-actions">
          <button 
            v-if="ticket.status === 'TRANSACTION_COMPLETE'" 
            class="qr-btn"
            @click="showQRCode(ticket)"
          >
            QR코드
          </button>
        </div>
      </div>

      <!-- 티켓이 없을 때 -->
      <div v-if="tickets.length === 0" class="empty-state">
        <div class="empty-icon">🎟️</div>
        <h3>보유한 티켓이 없습니다</h3>
        <p>아직 구매한 티켓이 없습니다. 티켓 응모를 시작해보세요!</p>
      </div>
    </div>
  </div>

    <!-- QR코드 모달 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>티켓 QR코드</h3>
          <button class="modal-close-btn" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <div class="qr-container">
            <canvas ref="qrCanvas" class="qr-code"></canvas>
          </div>
          <div class="ticket-info-modal">
            <h4>{{ enumToTeamName[selectedTicket?.game.away] }} vs {{ enumToTeamName[selectedTicket?.game.home] }}</h4>
            <p>{{ stadiumOfTeam[selectedTicket?.game.stadium] }}</p>
            <p>경기일: {{ selectedTicket?.game.date }}</p>
            <p>좌석: {{ selectedTicket?.seat }}</p>
            <p>티켓번호: {{ selectedTicket?.ticketId }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import QRCode from 'qrcode'
import { API_CONFIG } from '@/config/api.config'
import http from '@/utils/http'
import { formatDate } from '@/utils/dateUtils'
import { teamNameToEnum, enumToTeamName } from '@/utils/teamNameMap'

const isLoading = ref(false)

const ticketStatus = {
    BEFORE_ASSIGNMENT: '양도 전',
    BEING_ASSIGNMENT: '양도 중',
    BEING_PAYING: '결제 중',
    TRANSACTION_COMPLETE: '거래 완료'
}

const stadiumOfTeam = {
  'JAMSIL': '대구 삼성 라이온즈 파크',
  'GOCHUK': '고척 스카이돔',
  'JAMSIL': '서울종합운동장 야구장',
  'JAMSIL': '서울종합운동장 야구장',
  'SUWON': '수원 케이티 위즈 파크',
  'MUNHAK': '인천 SSG 랜더스필드',
  'CHANGWON': '창원NC파크',
  'GWANGJU': '광주 기아 챔피언스 필드',
  'SAJIK': '사직 야구장',
  'DAEJEON': '대전 한화생명 볼파크',
}

// 티켓 데이터 (실제로는 API에서 가져올 데이터)
const tickets = ref([
  {
    id: 1,
    game: {
      id: 1,
      away: 'SSG 랜더스',
      home: 'KIA 타이거즈',
      date: '2024.01.15',  
      stadium: '인천 문학경기장',
      },
    time: '18:30',
    seat: '1루석 A구역 15열 8번',
    price: '25,000',
    status: 'BEING_ASSIGNMENT'
  },
])

// 상태 텍스트 반환
const getStatusText = (status) => {
  const statusMap = ticketStatus
  return statusMap[status]
}

// QR코드 모달 관련 상태
const showModal = ref(false)
const selectedTicket = ref(null)
const qrCanvas = ref(null)

// 티켓 배경 이미지 생성 함수
const generateTicketBackground = (ticketId) => {
  const patterns = [
    'geometric', 'abstract', 'minimal', 'texture', 'gradient',
    'pattern', 'design', 'modern', 'clean', 'elegant'
  ]
  
  const patternIndex = ticketId % patterns.length
  const pattern = patterns[patternIndex]
  
  // 매우 흐린 배경 이미지로 텍스트 가독성 보장
  return `https://picsum.photos/seed/ticket-${ticketId}-${pattern}/400/200?blur=8`
}

// QR코드 표시 함수
const showQRCode = async (ticket) => {
  selectedTicket.value = ticket
  showModal.value = true
  
  // QR코드 생성
  await nextTick()
  if (qrCanvas.value) {
    try {
      // QR코드에 포함할 데이터 (티켓 정보)
      const qrData = JSON.stringify({
        ticketNumber: ticket.ticketNumber,
        gameTitle: ticket.gameTitle,
        gameDate: ticket.gameDate,
        seatInfo: ticket.seatInfo,
        stadium: ticket.stadium
      })
      
      await QRCode.toCanvas(qrCanvas.value, qrData, {
        width: 200,
        margin: 2,
        color: {
          dark: '#000000',
          light: '#FFFFFF'
        }
      })
    } catch (error) {
      console.error('QR코드 생성 실패:', error)
    }
  }
}

// 모달 닫기 함수
const closeModal = () => {
  showModal.value = false
  selectedTicket.value = null
}

const fetchTickets = async () => {
  try {
    isLoading.value = true
    const response = await http.get(API_CONFIG.USER.TICKETS)
    tickets.value = response.data
    console.log(tickets.value)
  } catch (error) {
    console.error('티켓 조회 중 오류 발생:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchTickets()
})

</script>

<style scoped>
.tickets-content {
  width: 100%;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-header h1 {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 30px;
}

.tickets-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ticket-card {
  background: white;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
  border-left: 4px solid var(--theme-primary, #ff6b35);
  position: relative;
  overflow: hidden;
}

.ticket-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.85);
  z-index: 1;
}

.ticket-card > * {
  position: relative;
  z-index: 2;
}

.ticket-card:hover {
  transform: translateY(-2px);
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.ticket-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.team-icon {
  width: 50px;
  height: 50px;
  background: var(--theme-primary, #ff6b35);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.game-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0 0 5px 0;
}

.stadium {
  color: #666;
  margin: 0;
  font-size: 14px;
}

.ticket-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  color: #333;
}

.ticket-status.active {
  background: #d4edda;
  color: #155724;
}

.ticket-status.used {
  background: #e2e3e5;
  color: #6c757d;
}

.ticket-status.cancelled {
  background: #f8d7da;
  color: #721c24;
}

.ticket-body {
  margin-bottom: 20px;
}

.ticket-details {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.label {
  font-weight: bold;
  color: #666;
  font-size: 14px;
}

.value {
  color: #333;
  font-size: 14px;
}

.value.price {
  color: var(--theme-primary, #ff6b35);
  font-weight: bold;
  font-size: 16px;
}

.ticket-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.qr-btn {
  padding: 10px 20px;
  background: #17a2b8;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.qr-btn:hover {
  background: #138496;
}

/* 모달 스타일 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.modal-close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.3s ease;
}

.modal-close-btn:hover {
  background: #f5f5f5;
}

.modal-body {
  padding: 25px;
}

.qr-container {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.qr-code {
  border: 2px solid #ddd;
  border-radius: 8px;
  background: white;
}

.ticket-info-modal {
  text-align: center;
}

.ticket-info-modal h4 {
  color: #333;
  margin: 0 0 10px 0;
  font-size: 18px;
}

.ticket-info-modal p {
  color: #666;
  margin: 5px 0;
  font-size: 14px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: 20px;
}

.empty-state h3 {
  color: #333;
  margin-bottom: 10px;
}

.empty-state p {
  color: #666;
  font-size: 14px;
}

@media (max-width: 768px) {
  .ticket-details {
    flex-direction: column;
  }
  
  .ticket-actions {
    flex-direction: column;
  }
  
  .modal-content {
    width: 95%;
    margin: 20px;
  }
  
  .qr-container {
    padding: 15px;
  }
  
  .qr-code {
    width: 150px;
    height: 150px;
  }
}
</style> 