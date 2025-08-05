<template>
  <div class="purchase-detail-content">
    <div class="container">
      <!-- 뒤로 가기 -->
      <div class="back-navigation">
        <button @click="goBack" class="back-btn">
          <span class="back-icon">‹</span>
          구매 상세 정보
        </button>
        <div class="order-number">주문번호: {{ purchaseData.transactionId }}</div>
      </div>

      <!-- 티켓 배너 -->
      <div class="ticket-banner">
        <div class="banner-content">
          <div class="team-logo">
            <img src="" alt="ticketDetail.gameTitle">
          </div>
          <div class="game-info">
            <h2 class="game-title">{{ ticketDetail.away }} vs {{ ticketDetail.home }}</h2>
            <div class="game-subtitle">2025 KBO</div>
          </div>
          <div class="purchase-status-tag">구매 완료</div>
        </div>
      </div>

      <!-- 경기 정보 -->
      <div class="info-section">
        <h3 class="section-title">경기 정보</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">경기 시간</span>
            <span class="value">{{ ticketDetail.gameDateTime }}</span>
          </div>
          <div class="info-item">
            <span class="label">경기장</span>
            <span class="value">{{ ticketDetail.stadium }}</span>
          </div>
        </div>
      </div>

      <!-- 티켓 정보 -->
      <div class="info-section">
        <h3 class="section-title">티켓 정보</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">티켓 번호</span>
            <span class="value">{{ purchaseData.ticket.ticketNumber }}</span>
          </div>
          <div class="info-item">
            <span class="label">좌석</span>
            <span class="value">{{ purchaseData.ticket.seatDetail }}</span>
          </div>
          <div class="info-item">
            <span class="label">구역</span>
            <span class="value">{{ purchaseData.ticket.section }}</span>
          </div>
          <div class="info-item">
            <span class="label">좌석 번호</span>
            <span class="value">{{ purchaseData.ticket.seatNumber }}</span>
          </div>
        </div>
      </div>

      <!-- 결제 정보 -->
      <div class="info-section">
        <h3 class="section-title">결제 정보</h3>
        <div class="payment-summary">
          <div class="payment-row">
            <span class="label">티켓 가격</span>
            <span class="value">{{ purchaseData.ticket.price }}</span>
          </div>
          <div class="payment-row total">
            <span class="label">총 결제 금액</span>
            <span class="value">{{ purchaseData.ticket.price }}</span>
          </div>
          <div class="payment-details">
            <div class="payment-method">
              <span class="label">결제 방법</span>
              <span class="value">{{ purchaseData.paymentMethod }}</span>
            </div>
            <div class="payment-date">
              <span class="label">결제 일시</span>
              <span class="value">{{ purchaseData.transactionDate }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 액션 버튼 -->
      <div class="action-buttons">
        <button class="cancel-btn" @click="requestCancel">
          응모 취소
        </button>
        <button class="refund-btn" @click="requestRefund">
          환불 요청
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '@/utils/http'
import { API_CONFIG } from '@/config/api.config'

const route = useRoute()
const router = useRouter()

// sessionStorage에서 전체 purchase 데이터 가져오기
const transactionId = route.params.id
let purchaseData = ref(null)

// sessionStorage에서 데이터 로드
try {
  const storedPurchase = sessionStorage.getItem('selectedPurchase')
  if (storedPurchase) {
    purchaseData.value = JSON.parse(storedPurchase)
    console.log('세션스토리지:', purchaseData.value)
    // 사용 후 sessionStorage 정리
    sessionStorage.removeItem('selectedPurchase')
  }
} catch (error) {
  purchaseData.value = null
  console.log('세션스토리지 없음:', error)
}

const ticketId = purchaseData.value?.ticket?.ticketId

const orderNumber = ref(transactionId)

// 티켓 상세 정보 - 라우트 state에서 가져오거나 API로 조회
const ticketDetail = ref(purchaseData.value?.ticket)

// 뒤로 가기
const goBack = () => {
  router.go(-1)
}

// 응모 취소
const requestCancel = () => {
  if (confirm('응모를 취소하시겠습니까?')) {
    alert('응모 취소 요청이 접수되었습니다.')
  }
}

// 환불 요청
const requestRefund = () => {
  if (confirm('환불을 요청하시겠습니까?')) {
    alert('환불 요청이 접수되었습니다.')
  }
}

const fetchTicketDetail = async (ticketId) => {
  try {
    const response = await http.get(API_CONFIG.TICKET.DETAIL(ticketId))
    ticketDetail.value = response.data
  } catch (error) {
    console.error('Failed to fetch ticket detail:', error)
  }
}

const fetchPurchaseDetail = async (transactionId) => {
  try {
    const response = await http.get(API_CONFIG.USER.PAYMENT.DETAIL(transactionId))
    purchaseDetail.value = response.data
  } catch (error) {
    console.error('Failed to fetch purchase detail:', error)
  }
}

onMounted(() => {
  // 라우트 state에 데이터가 있으면 사용하고, 없으면 API로 조회
  if (purchaseData.value) {
    // 필요시 추가 데이터 처리
    if (purchaseData.value.ticket) {
      fetchTicketDetail(purchaseData.value.ticket.ticketId)
      console.log("ticketDetail.value",ticketDetail.value)
    }
  }
})
</script>

<style scoped>
.purchase-detail-content {
  width: 100%;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.back-navigation {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: none;
  border: none;
  color: #333;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  padding: 8px 0;
}

.back-icon {
  font-size: 20px;
  font-weight: bold;
}

.order-number {
  font-size: 14px;
  color: #666;
}

.ticket-banner {
  background: linear-gradient(135deg, #8B1538 0%, #B91D47 100%);
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  color: white;
  position: relative;
  overflow: hidden;
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 2;
}

.team-logo {
  width: 80px;
  height: 50px;
  border-radius: 8px;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.1);
}

.team-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.game-info {
  flex: 1;
}

.game-title {
  font-size: 24px;
  font-weight: bold;
  margin: 0 0 5px 0;
  color: white;
}

.game-subtitle {
  font-size: 14px;
  opacity: 0.9;
}

.purchase-status-tag {
  background: #28a745;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
}

.info-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin: 0 0 20px 0;
  border-bottom: 2px solid var(--theme-primary, #ff6b35);
  padding-bottom: 10px;
}

.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item .label {
  font-weight: 500;
  color: #666;
  font-size: 14px;
}

.info-item .value {
  font-weight: bold;
  color: #333;
  font-size: 14px;
}

.info-item .value.price {
  color: var(--theme-primary, #ff6b35);
  font-size: 16px;
}

.payment-summary {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.payment-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.payment-row.total {
  border-bottom: 2px solid var(--theme-primary, #ff6b35);
  font-size: 18px;
  font-weight: bold;
  color: var(--theme-primary, #ff6b35);
}

.payment-details {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 10px;
}

.payment-method,
.payment-date {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-buttons {
  display: flex;
  gap: 15px;
  justify-content: center;
  margin: 30px 0;
}

.cancel-btn,
.refund-btn {
  padding: 12px 30px;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #6c757d;
  color: white;
}

.cancel-btn:hover {
  background: #5a6268;
}

.refund-btn {
  background: var(--theme-primary, #ff6b35);
  color: white;
}

.refund-btn:hover {
  background: var(--theme-accent, #e55a2e);
}

@media (max-width: 768px) {
  .back-navigation {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .banner-content {
    flex-direction: column;
    text-align: center;
    gap: 15px;
  }
  
  .game-title {
    font-size: 20px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .payment-details {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .action-buttons {
    flex-direction: column;
    gap: 10px;
  }
  
  .cancel-btn,
  .refund-btn {
    width: 100%;
    padding: 15px;
  }
}
</style> 