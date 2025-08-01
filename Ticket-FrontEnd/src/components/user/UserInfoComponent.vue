<template>
  <div class="user-info-content">
    <div class="container">
      <div class="content-header">
        <h1>내 정보</h1>
      </div>

    <!-- 사용자 정보 입력 폼 -->
    <div class="info-form">
      <div class="section-title">
        <h2>개인정보</h2>
        <span class="section-description">회원님의 개인정보를 관리할 수 있습니다</span>
      </div>
      
      <div class="form-grid">
        <div class="form-group">
          <label>이름</label>
          <div class="input-wrapper">
            <div class="input-icon">👤</div>
            <input type="text" :value="user?.userName || ''" readonly>
            <button class="edit-btn">
              <span class="edit-icon">✏️</span>
            </button>
          </div>
        </div>

        <div class="form-group">
          <label>이메일</label>
          <div class="input-wrapper">
            <div class="input-icon">📧</div>
            <input type="email" :value="user?.email || ''" readonly>
            <button class="edit-btn">
              <span class="edit-icon">✏️</span>
            </button>
          </div>
        </div>

        <div class="form-group">
          <label>닉네임</label>
          <div class="input-wrapper">
            <div class="input-icon">🏷️</div>
            <input type="text" :value="user?.nickName || ''" readonly>
            <button class="edit-btn">
              <span class="edit-icon">✏️</span>
            </button>
          </div>
        </div>

        <div class="form-group">
          <label>전화번호</label>
          <div class="input-wrapper">
            <div class="input-icon">📱</div>
            <input type="tel" :value="user?.phoneNumber || ''" readonly>
            <button class="edit-btn">
              <span class="edit-icon">✏️</span>
            </button>
          </div>
        </div>

        <div class="form-group full-width">
          <label>생년월일</label>
          <div class="input-wrapper">
            <div class="input-icon">🎂</div>
            <input type="date" :value="user?.birthDate || ''">
            <button class="edit-btn">
              <span class="edit-icon">✏️</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 연결된 서비스 -->
    <div class="connected-services">
      <div class="section-title">
        <h2>연결된 서비스</h2>
        <span class="section-description">외부 서비스와의 연동 상태를 관리할 수 있습니다</span>
      </div>
      
      <div class="services-grid">
        <div class="service-card">
          <div class="service-header">
            <div class="service-icon">🏟️</div>
            <div class="service-info">
              <span class="service-name">SSG 랜더스 공식 앱</span>
              <span class="service-description">야구 경기 정보 및 티켓 연동</span>
            </div>
            <div class="service-status connected">
              <span class="status-dot"></span>
              연결됨
            </div>
          </div>
          <button class="disconnect-btn">연결 해제</button>
        </div>
        
        <div class="service-card">
          <div class="service-header">
            <div class="service-icon">🎫</div>
            <div class="service-info">
              <span class="service-name">인터파크 티켓</span>
              <span class="service-description">티켓 예매 및 결제 연동</span>
            </div>
            <div class="service-status connected">
              <span class="status-dot"></span>
              연결됨
            </div>
          </div>
          <button class="connect-btn">연결 관리</button>
        </div>
      </div>
    </div>

    <!-- 관심 야구팀 -->
    <div class="favorite-team">
      <div class="section-title">
        <h2>관심 야구팀</h2>
        <span class="section-description">응원하는 팀을 선택하여 맞춤형 테마를 적용하세요</span>
      </div>
      
      <div class="team-selection">
        <div class="current-team-card">
          <div class="team-badge">
            <div class="logo-container">
              <img 
                v-if="themeStore.currentTheme.value.logo"
                :src="themeStore.currentTheme.value.logo" 
                :alt="themeStore.selectedTeam.value || currentTeam.name"
                class="team-logo"
                @error="handleImageError"
                @load="handleImageLoad"
              >
              <div class="team-logo-placeholder">⚾</div>
            </div>
            <div class="team-details">
              <span class="team-name">{{ themeStore.selectedTeam.value || currentTeam.name }}</span>
              <span class="team-label">나의 관심팀</span>
            </div>
          </div>
          <div class="team-love">
            <span class="heart-icon">❤️</span>
            <span class="support-text">응원중</span>
          </div>
        </div>
        
        <div class="team-selector-card">
          <div class="selector-header">
            <span class="selector-icon">⚙️</span>
            <span class="selector-title">관심 팀 변경</span>
          </div>
          <select v-model="selectedTeam" @change="changeFavoriteTeam" class="team-select">
            <option value="">팀을 선택하세요</option>
            <option value="ssg">SSG 랜더스</option>
            <option value="kiwoom">키움 히어로즈</option>
            <option value="lg">LG 트윈스</option>
            <option value="kt">KT 위즈</option>
            <option value="kia">KIA 타이거즈</option>
            <option value="nc">NC 다이노스</option>
            <option value="samsung">삼성 라이온즈</option>
            <option value="lotte">롯데 자이언츠</option>
            <option value="doosan">두산 베어스</option>
            <option value="hanwha">한화 이글스</option>
          </select>
        </div>
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useTeamThemeStore } from '../../stores/teamTheme.js'
import { useAuthStore } from '../../stores/auth.js'

// 테마 스토어 사용
const themeStore = useTeamThemeStore
const authStore = useAuthStore()
const userInfo = localStorage.getItem('userInfo')
const user = ref(null)

// 팀 정보 데이터
const teams = {
  SSG_: {
    name: 'SSG_LANDERS',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/SSG_Landers_logo.svg/1200px-SSG_Landers_logo.svg.png',
    color: '#CE0E2D'
  },
  kiwoom: {
    name: 'KIWOOM_HEROES',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/Kiwoom_Heroes_logo.svg/1200px-Kiwoom_Heroes_logo.svg.png',
    color: '#820024'
  },
  lg: {
    name: 'LG_TWINS',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/2/2f/LG_Twins_logo.svg/1200px-LG_Twins_logo.svg.png',
    color: '#C41E3A'
  },
  kt: {
    name: 'KT_WIZ',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/KT_Wiz_logo.svg/1200px-KT_Wiz_logo.svg.png',
    color: '#000000'
  },
  kia: {
    name: 'KIA_TIGERS',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/KIA_Tigers_logo.svg/1200px-KIA_Tigers_logo.svg.png',
    color: '#DA291C'
  },
  nc: {
    name: 'NC_DINOS',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/NC_Dinos_logo.svg/1200px-NC_Dinos_logo.svg.png',
    color: '#315288'
  },
  samsung: {
    name: 'SAMSUNG_LIONS',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/Samsung_Lions_logo.svg/1200px-Samsung_Lions_logo.svg.png',
    color: '#074CA1'
  },
  lotte: {
    name: 'LOTTE_GIANTS',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/Lotte_Giants_logo.svg/1200px-Lotte_Giants_logo.svg.png',
    color: '#002E6D'
  },
  doosan: {
    name: 'DOOSAN_BEAR',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/Doosan_Bears_logo.svg/1200px-Doosan_Bears_logo.svg.png',
    color: '#131230'
  },
  hanwha: {
    name: 'HANHWA_EAGLES',
    logo: 'https://upload.wikimedia.org/wikipedia/ko/thumb/8/8f/Hanwha_Eagles_logo.svg/1200px-Hanwha_Eagles_logo.svg.png',
    color: '#FF6600'
  }
}

// 현재 선택된 팀 (기본값: SSG)
const selectedTeam = ref('SSG_')

// 현재 팀 정보
const currentTeam = computed(() => teams[selectedTeam.value] || teams.SSG_)

// 관심 팀 변경 함수
const changeFavoriteTeam = () => {
  if (selectedTeam.value) {
    const teamName = currentTeam.value.name
    console.log('관심 팀이 변경되었습니다:', teamName)
    
    // 테마 색상 변경
    themeStore.setSelectedTeam(teamName)
    
    // 여기에 API 호출 로직을 추가할 수 있습니다
  }
}

// 이미지 로드 성공 핸들러
const handleImageLoad = (event) => {
  // 이미지 로드 성공 시 placeholder 숨기기
  const placeholder = event.target.nextElementSibling
  if (placeholder) {
    placeholder.style.display = 'none'
  }
}

// 이미지 에러 핸들러
const handleImageError = (event) => {
  // 이미지 로드 실패 시 placeholder 보이기
  event.target.style.display = 'none'
  const placeholder = event.target.nextElementSibling
  if (placeholder) {
    placeholder.style.display = 'flex'
  }
}

// 컴포넌트 마운트 시 테마 초기화
onMounted(async () => {
  await authStore.getUserInfo()
  // 저장된 팀이 있으면 해당 팀으로 설정
  const userInfo = localStorage.getItem('user')
  console.log('userInfo : ', userInfo)
  const savedTeam = authStore.user.value?.myTeam
  console.log('savedTeam : ', savedTeam)
  if (userInfo) {
    user.value = JSON.parse(userInfo)
  if (savedTeam) {
    // 저장된 팀명으로 select 값 찾기, 지금 팀 key.name으로 되어 있음 바꿔야 함함
    const teamKey = Object.keys(teams).find(key => teams[key].name === savedTeam)
    if (teamKey) {
      selectedTeam.value = teamKey
    }
  }
  themeStore.initializeTheme()
}})
</script>

<style scoped>
.user-info-content {
  width: 100%;
}

.container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.content-header h1 {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a1a;
  margin-bottom: 40px;
  background: linear-gradient(135deg, var(--theme-primary, #ff6b35) 0%, var(--theme-accent, #e55a2e) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.section-title {
  margin-bottom: 24px;
}

.section-title h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
}

.section-description {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.4;
}

.info-form {
  background: white;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.04);
  margin-bottom: 32px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-group label {
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
  font-size: 14px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  transition: all 0.2s ease;
}

.input-wrapper:focus-within {
  border-color: var(--theme-primary, #ff6b35);
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
  background: white;
}

.input-icon {
  padding: 0 12px;
  font-size: 16px;
  color: #6b7280;
}

.input-wrapper input {
  flex: 1;
  padding: 14px 12px;
  border: none;
  background: transparent;
  color: #1a1a1a;
  font-size: 14px;
  font-weight: 500;
  outline: none;
}

/* 날짜 입력 필드의 달력 아이콘 스타일링 */
.input-wrapper input[type="date"]::-webkit-calendar-picker-indicator {
  filter: invert(0.4);
  cursor: pointer;
  opacity: 0.7;
  margin-right: 8px;
}

.input-wrapper input[type="date"]::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
}

.edit-btn {
  position: absolute;
  right: 8px;
  padding: 8px;
  background: var(--theme-primary, #ff6b35);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  height: 36px;
}

.edit-btn:hover {
  background: var(--theme-accent, #e55a2e);
  transform: translateY(-1px);
}

.edit-icon {
  font-size: 14px;
}

.connected-services, .favorite-team {
  background: white;
  padding: 32px;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.04);
  margin-bottom: 32px;
}

.services-grid {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.service-card {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.2s ease;
}

.service-card:hover {
  border-color: var(--theme-primary, #ff6b35);
  box-shadow: 0 4px 12px rgba(255, 107, 53, 0.1);
}

.service-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.service-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, var(--theme-primary, #ff6b35) 0%, var(--theme-accent, #e55a2e) 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
}

.service-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.service-name {
  font-weight: 600;
  color: #1a1a1a;
  font-size: 16px;
}

.service-description {
  font-size: 14px;
  color: #6b7280;
}

.service-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: 20px;
  background: #dcfce7;
  color: #166534;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #22c55e;
  border-radius: 50%;
}

.disconnect-btn, .connect-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.disconnect-btn {
  background: #fee2e2;
  color: #dc2626;
}

.disconnect-btn:hover {
  background: #fecaca;
  transform: translateY(-1px);
}

.connect-btn {
  background: var(--theme-primary, #ff6b35);
  color: white;
}

.connect-btn:hover {
  background: var(--theme-accent, #e55a2e);
  transform: translateY(-1px);
}

.team-selection {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.current-team-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px;
  background: linear-gradient(135deg, var(--theme-primary, #ff6b35) 0%, var(--theme-accent, #e55a2e) 100%);
  border-radius: 16px;
  color: white;
  box-shadow: 0 8px 24px rgba(255, 107, 53, 0.2);
}

.team-badge {
  display: flex;
  align-items: center;
  gap: 16px;
}

.logo-container {
  position: relative;
  width: 64px;
  height: 64px;
}

.team-logo {
  width: 64px;
  height: 64px;
  object-fit: contain;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  padding: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.team-logo-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 64px;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.team-details {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.team-name {
  font-weight: 700;
  font-size: 20px;
  color: white;
}

.team-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.team-love {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.heart-icon {
  font-size: 28px;
  animation: heartbeat 2s infinite ease-in-out;
}

.support-text {
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.9);
}

@keyframes heartbeat {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.team-selector-card {
  background: #f8fafc;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.2s ease;
}

.team-selector-card:hover {
  border-color: var(--theme-primary, #ff6b35);
}

.selector-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.selector-icon {
  font-size: 16px;
}

.selector-title {
  font-weight: 600;
  color: #374151;
  font-size: 16px;
}

.team-select {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  color: #1a1a1a;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.team-select:focus {
  outline: none;
  border-color: var(--theme-primary, #ff6b35);
  box-shadow: 0 0 0 3px rgba(255, 107, 53, 0.1);
}

.team-select:hover {
  border-color: var(--theme-primary, #ff6b35);
}

@media (max-width: 768px) {
  .container {
    padding: 0 16px;
  }
  
  .content-header h1 {
    font-size: 28px;
    margin-bottom: 32px;
  }
  
  .info-form, .connected-services, .favorite-team {
    padding: 24px;
    margin-bottom: 24px;
  }
  
  .form-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .form-group.full-width {
    grid-column: 1;
  }
  
  .services-grid {
    gap: 12px;
  }
  
  .service-card {
    padding: 16px;
  }
  
  .service-header {
    gap: 12px;
    margin-bottom: 12px;
  }
  
  .service-icon {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .current-team-card {
    flex-direction: column;
    gap: 16px;
    padding: 20px;
    text-align: center;
  }
  
  .team-badge {
    justify-content: center;
  }
  
  .logo-container, .team-logo, .team-logo-placeholder {
    width: 56px;
    height: 56px;
  }
  
  .team-name {
    font-size: 18px;
  }
  
  .heart-icon {
    font-size: 24px;
  }
  
  .team-selector-card {
    padding: 16px;
  }
}
</style> 