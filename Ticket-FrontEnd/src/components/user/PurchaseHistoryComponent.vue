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
          :key="purchase.id"
          class="purchase-card"
          @click="goToPurchaseDetail(purchase.id)"
        >
          <!-- 구매 완료 태그 -->
          <div class="purchase-status">구매 완료</div>
          
          <!-- 티켓 이미지 -->
          <div class="ticket-image">
            <img :src="generateRandomImage(purchase.id)" :alt="purchase.gameTitle" @error="handleImageError">
          </div>
          
          <!-- 티켓 정보 -->
          <div class="ticket-info">
            <h3 class="game-title">{{ purchase.gameTitle }}</h3>
            <div class="game-details">
              <div class="detail-row">
                <span class="icon">📅</span>
                <span class="label">{{ purchase.gameDate }}</span>
                <span class="value">{{ purchase.gameTime }}</span>
              </div>
              <div class="detail-row">
                <span class="icon">📍</span>
                <span class="label">{{ purchase.stadium }}</span>
              </div>
              <div class="detail-row">
                <span class="icon">🎟️</span>
                <span class="label">{{ purchase.seatInfo }}</span>
              </div>
              <div class="detail-row">
                <span class="icon">💰</span>
                <span class="label">{{ purchase.price }}</span>
              </div>
            </div>
            
            <!-- 구매 정보 -->
            <div class="purchase-info">
              <div class="purchase-date">구매일: {{ purchase.purchaseDate }}</div>
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 구매 내역 데이터 (실제로는 API에서 가져올 데이터)
const purchases = ref([
  {
    id: 1,
    gameTitle: 'SSG 랜더스 vs KIA 타이거즈',
    gameDate: '2025.07.30',
    gameTime: '18:30',
    stadium: '인천 문학경기장',
    seatInfo: '304구역 1열',
    price: '25,000원',
    purchaseDate: '2025.07.25 14:32',
    image: 'https://via.placeholder.com/120x80/ff6b35/ffffff?text=SSG+vs+KIA'
  },
  {
    id: 2,
    gameTitle: '두산 베어스 vs 한화 이글스',
    gameDate: '2025.08.01',
    gameTime: '19:00',
    stadium: '잠실야구장',
    seatInfo: '208구역 8열',
    price: '30,000원',
    purchaseDate: '2025.07.24 11:20',
    image: 'https://via.placeholder.com/120x80/002c5f/ffffff?text=두산+vs+한화'
  },
  {
    id: 3,
    gameTitle: 'LG 트윈스 vs KT 위즈',
    gameDate: '2025.08.01',
    gameTime: '14:00',
    stadium: '잠실야구장',
    seatInfo: '105구역 12열',
    price: '22,000원',
    purchaseDate: '2025.07.23 16:45',
    image: 'https://via.placeholder.com/120x80/c70025/ffffff?text=LG+vs+KT'
  }
])

// 랜덤 이미지 생성 함수
const generateRandomImage = (id) => {
  const colors = [
    'ff6b35', 'e55a2e', '3498db', '2ecc71', 'f39c12', 
    'e74c3c', '9b59b6', '1abc9c', '34495e', 'f1c40f'
  ]
  const themes = [
    'Baseball', 'Stadium', 'Ticket', 'Game', 'Sports',
    'Match', 'League', 'Team', 'Fan', 'Victory'
  ]
  
  const colorIndex = id % colors.length
  const themeIndex = id % themes.length
  const color = colors[colorIndex]
  const theme = themes[themeIndex]
  
  // Unsplash API를 사용한 랜덤 야구 관련 이미지
  return `https://picsum.photos/seed/${id}-${theme}/120/80?blur=1`
}

// 상세 페이지로 이동
const goToPurchaseDetail = (purchaseId) => {
  router.push(`/mypage/purchase/${purchaseId}`)
}

// 이미지 로드 실패 시 대체 이미지
const handleImageError = (event) => {
  const fallbackColors = ['ff6b35', '3498db', '2ecc71', 'f39c12', 'e74c3c']
  const randomColor = fallbackColors[Math.floor(Math.random() * fallbackColors.length)]
  event.target.src = `https://via.placeholder.com/120x80/${randomColor}/ffffff?text=⚾`
}
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
  height: 80px;
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