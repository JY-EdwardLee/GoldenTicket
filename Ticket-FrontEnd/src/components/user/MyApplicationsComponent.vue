<template>
  <div class="applications-content">
    <div class="container">
      <div class="content-header">
        <h1>나의 응모</h1>
      </div>

    <!-- 탭 네비게이션 -->
    <div class="tab-navigation">
      <button 
        v-for="tab in tabs" 
        :key="tab.id"
        :class="['tab-button', { active: activeTab === tab.id }]"
        @click="activeTab = tab.id"
      >
        {{ tab.name }}
      </button>
    </div>

    <!-- 응모 내역 목록 -->
    <div class="applications-list">
      <div 
        v-for="application in filteredApplications" 
        :key="application.id"
        class="application-card"
      >
        <div class="card-header">
                  <div class="team-info">
          <div class="team-icon">⚾</div>
          <div class="game-details">
            <h3 class="game-title">{{ application.gameTitle }}</h3>
            <p class="stadium">{{ application.stadium }}</p>
          </div>
        </div>
          <div class="status-badge" :class="application.status">
            {{ getStatusText(application.status) }}
          </div>
        </div>
        
        <div class="card-body">
          <div class="game-info">
            <div class="info-item">
              <span class="label">경기시간 : </span>
              <span class="value">{{ application.gameTime }}</span>
            </div>
            <div class="info-item">
              <span class="label">응모일 : </span>
              <span class="value">{{ application.applicationDate }}</span>
            </div>
            <div v-if="application.paymentAmount" class="info-item">
              <span class="label">결제 금액 : </span>
              <span class="value">{{ application.paymentAmount }}원</span>
            </div>
            <div v-if="application.cancellationDate" class="info-item">
              <span class="label">취소일 : </span>
              <span class="value">{{ application.cancellationDate }}</span>
            </div>
          </div>
        </div>

        <div class="card-actions">
          <button 
            v-if="application.status === 'applying'" 
            class="cancel-btn"
            @click="cancelApplication(application.id)"
          >
            응모 취소
          </button>
          <button 
            v-if="application.status === 'payment-pending'" 
            class="cancel-btn"
            @click="cancelPayment(application.id)"
          >
            결제 취소
          </button>
          <button 
            v-if="application.status === 'payment-pending'" 
            class="payment-btn"
            @click="processPayment(application.id)"
          >
            결제하기
          </button>
        </div>
      </div>

      <!-- 응모 내역이 없을 때 -->
      <div v-if="filteredApplications.length === 0" class="empty-state">
        <div class="empty-icon">📋</div>
        <h3>응모 내역이 없습니다</h3>
        <p>아직 응모한 티켓이 없습니다. 티켓 응모를 시작해보세요!</p>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

// 탭 데이터
const tabs = [
  { id: 'applying', name: '응모 중' },
  { id: 'payment-pending', name: '결제 대기' },
  { id: 'cancelled', name: '응모 취소' }
]

// 현재 활성 탭
const activeTab = ref('applying')

// 응모 내역 데이터 (실제로는 API에서 가져올 데이터)
const applications = ref([
  {
    id: 1,
    gameTitle: 'SSG 랜더스 vs KIA 타이거즈',
    stadium: '인천 문학경기장',
    gameTime: '18:30',
    applicationDate: '2024.01.10',
    status: 'applying'
  },
  {
    id: 2,
    gameTitle: '두산 베어스 vs 한화 이글스',
    stadium: '잠실야구장',
    gameTime: '18:30',
    applicationDate: '2024.01.12',
    paymentAmount: '25,000',
    status: 'payment-pending'
  },
  {
    id: 3,
    gameTitle: 'LG 트윈스 vs KT 위즈',
    stadium: '잠실야구장',
    gameTime: '18:30',
    applicationDate: '2024.01.08',
    cancellationDate: '2024.01.09',
    status: 'cancelled'
  }
])

// 현재 탭에 따른 필터링된 응모 내역
const filteredApplications = computed(() => {
  return applications.value.filter(app => app.status === activeTab.value)
})

// 상태 텍스트 반환
const getStatusText = (status) => {
  const statusMap = {
    'applying': '응모 중',
    'payment-pending': '결제 대기',
    'cancelled': '응모 취소'
  }
  return statusMap[status] || status
}

// 응모 취소
const cancelApplication = (id) => {
  console.log('응모 취소:', id)
  // API 호출 로직
}

// 결제 취소
const cancelPayment = (id) => {
  console.log('결제 취소:', id)
  // API 호출 로직
}

// 결제 처리
const processPayment = (id) => {
  console.log('결제 처리:', id)
  // API 호출 로직
}
</script>

<style scoped>
.applications-content {
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

.tab-navigation {
  display: flex;
  gap: 10px;
  margin-bottom: 30px;
  background: white;
  padding: 10px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.tab-button {
  flex: 1;
  padding: 12px 20px;
  border: none;
  background: transparent;
  color: #666;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.tab-button:hover {
  background: #f5f5f5;
  color: var(--theme-primary, #ff6b35);
}

.tab-button.active {
  background: var(--theme-primary, #ff6b35);
  color: white;
}

.applications-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.application-card {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.application-card:hover {
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.team-info {
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

.status-badge {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
}

.status-badge.applying {
  background: #f0f8ff;
  color: var(--theme-primary, #ff6b35);
  border: 1px solid var(--theme-primary, #ff6b35);
}

.status-badge.payment-pending {
  background: #fff3e0;
  color: #f57c00;
}

.status-badge.cancelled {
  background: #ffebee;
  color: #d32f2f;
}

.card-body {
  margin-bottom: 20px;
}

.game-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
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

.card-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.cancel-btn {
  padding: 10px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.cancel-btn:hover {
  background: #5a6268;
}

.payment-btn {
  padding: 10px 20px;
  background: var(--theme-primary, #ff6b35);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.payment-btn:hover {
  background: var(--theme-accent, #e55a2e);
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
  .tab-navigation {
    flex-direction: column;
  }
  
  .card-actions {
    flex-direction: column;
  }
  
  .game-info {
    flex-direction: column;
  }
}
</style> 