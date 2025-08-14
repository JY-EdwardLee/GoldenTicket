<template>
  <div class="purchase-history-content">
    <div class="container">
      <div class="content-header">
        <h1>구매 내역</h1>
      </div>

      <!-- 구매 티켓 목록 -->
      <div class="purchase-list">
        <div 
          v-for="purchase in purchases" 
          :key="purchase.transactionId"
          class="purchase-card"
          @click="goToPurchaseDetail(purchase)"
        >
          <!-- 구매 완료 태그 -->
          <div class="purchase-status">구매 완료</div>
          
          
          <!-- 티켓 정보 -->
          <div class="ticket-info">
            <div class="game-details">
              <div class="detail-row">
                <span class="label">좌석</span>
                <span class="value">{{ purchase.ticket.seat }}</span>
              </div>
              <div class="detail-row">
                <span class="label">가격</span>
                <span class="value">{{ purchase.ticket.price }}</span>
              </div>
            </div>
            
            <!-- 구매 정보 -->
            <div class="purchase-info">
              <div class="purchase-date">구매일: {{ purchase.ticket.transactionDate?.toLocaleString() }}</div>
            </div>
          </div>
          
          <!-- 화살표 아이콘 -->
          <div class="arrow-icon">
            <span>›</span>
          </div>
        </div>

        <!-- 구매 내역이 없을 때 -->
        <div v-if="purchases.length === 0" class="empty-state">
          <div class="empty-icon">🛒</div>
          <h3>구매 내역이 없습니다</h3>
          <p>아직 구매한 티켓이 없습니다. 티켓 응모를 시작해보세요!</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { API_CONFIG } from '@/config/api.config'
import http from '@/utils/http'

const router = useRouter()

// 구매 내역 데이터 (실제로는 API에서 가져올 데이터)
const purchases = ref([])


// 상세 페이지로 이동 - sessionStorage로 전체 purchase 데이터 전달
const goToPurchaseDetail = (purchase) => {
  // sessionStorage에 전체 purchase 데이터 저장
  sessionStorage.setItem('selectedPurchase', JSON.stringify(purchase))
  
  router.push({
    path: `/mypage/purchase/${purchase.transactionId}`
  })
}

const isLoading = ref(false)

const fetchPurchases = async () => {
  try {
    isLoading.value = true
    const response = await http.get(API_CONFIG.USER.PAYMENTS)
    purchases.value = response.data
  } catch (error) {
    console.error('구매 내역 조회 중 오류 발생:', error)
  } finally {
    isLoading.value = false
  }
}

onMounted(() => {
  fetchPurchases()
})

</script>

<style scoped>
.purchase-history-content {
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

.purchase-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.purchase-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  display: flex;
  align-items: center;
  gap: 20px;
  border-left: 4px solid var(--theme-primary, #ff6b35);
}

.purchase-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.purchase-status {
  position: absolute;
  top: 15px;
  right: 15px;
  background: #28a745;
  color: white;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.ticket-image {
  flex-shrink: 0;
  width: 120px;
  /* height: 80px; */
  border-radius: 8px;
  overflow: hidden;
  background: #f8f9fa;
}

.ticket-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.ticket-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.game-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

.game-details {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.detail-row .icon {
  width: 16px;
  font-size: 14px;
}

.detail-row .label {
  color: #666;
  font-weight: 500;
}

.detail-row .value {
  color: #333;
  font-weight: bold;
}

.purchase-info {
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px solid #f0f0f0;
}

.purchase-date {
  font-size: 12px;
  color: #999;
}

.arrow-icon {
  flex-shrink: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #ccc;
  font-size: 24px;
  font-weight: bold;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #666;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.empty-state h3 {
  margin: 0 0 10px 0;
  color: #333;
  font-size: 20px;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

@media (max-width: 768px) {
  .purchase-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    padding: 15px;
  }
  
  .purchase-status {
    position: static;
    align-self: flex-end;
    margin-bottom: 10px;
  }
  
  .ticket-image {
    align-self: center;
    width: 100px;
    height: 67px;
  }
  
  .game-title {
    font-size: 16px;
  }
  
  .detail-row {
    font-size: 13px;
  }
  
  .arrow-icon {
    align-self: center;
    margin-top: 10px;
  }
}
</style> 