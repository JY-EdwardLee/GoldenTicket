<template>
  <div class="transfer-root">
    <!-- Header -->

    <!-- Main -->
    <main class="transfer-main">
      <div v-if="showCompletePage">
        <!-- 완료 페이지 UI -->
        <div class="complete-root">
          <div class="complete-card">
            <div class="match-info">
              <div class="match-title">
                <span :class="getTeamClass(selectedTicket)">{{ selectedTicket.homeKor || selectedTicket.game?.home }}</span> vs <span :class="getTeamClass({homeKor: selectedTicket.awayKor || selectedTicket.game?.away})">{{ selectedTicket.awayKor || selectedTicket.game?.away }}</span>
              </div>
              <div class="match-detail">
                <span class="series">{{ pageText.seriesTitle }}</span>
                <span class="time">{{ selectedTicket.date }}</span>
              </div>
            </div>
            <div class="complete-message">
              <div class="main-message">{{ pageText.completeMainMessage }}</div>
              <div class="sub-message">{{ pageText.completeSubMessage }}</div>
              <button class="confirm-btn" @click="handleCompleteConfirm">{{ pageText.confirmButtonText }}</button>
            </div>
          </div>
          <div class="process-info">
            <div class="process-title">{{ pageText.processTitle }}</div>
            <ul class="process-list">
              <li v-for="(msg, idx) in pageText.processList" :key="idx"><span class="num">{{ idx+1 }}</span> {{ msg }}</li>
            </ul>
          </div>
          <button class="back-btn" @click="handleCompleteBack">이전 페이지로</button>
        </div>
      </div>
      <div v-else-if="showDetailPage && selectedTicket" class="detail-page-wrap">
        <div class="detail-card">
          <div class="detail-header" :style="getTicketDetailStyle(selectedTicket)">
            <div class="detail-header-logo">{{ getTeamLogoName(selectedTicket.homeKor || selectedTicket.game?.home) }}</div>
            <div class="detail-header-ticketid">TICKET ID<br /><span class="ticketid-value">#{{ selectedTicket.ticketId }}</span></div>
            <div class="detail-match-title">
              <div class="main-title">
                <span class="main-title-home">{{ selectedTicket.homeKor || selectedTicket.game?.home }}</span><br />
                <span class="main-title-vs">VS</span><br />
                <span class="main-title-away">{{ selectedTicket.awayKor || selectedTicket.game?.away }}</span>
              </div>
            </div>
            <div class="detail-header-bg"><img :src="getTeamBackgroundImage(selectedTicket.homeKor || selectedTicket.game?.home)" :alt="selectedTicket.homeKor || selectedTicket.game?.home" /></div>
            <div class="detail-header-info-row">
              <div class="detail-header-info-line">
                <div class="detail-header-info-col"><span class="info-icon">📅</span>{{ selectedTicket.game?.date?.split('T')[0] || selectedTicket.date }}</div>
                <div class="detail-header-info-col"><span class="info-icon">⏰</span>{{ selectedTicket.game?.date?.split('T')[1]?.slice(0,5) || selectedTicket.time }}</div>
              </div>
              <div class="detail-header-info-line">
                <div class="detail-header-info-col"><span class="info-icon">📍</span>{{ truncateText(getStadiumName(selectedTicket.homeKor || selectedTicket.game?.home), 15) }}</div>
                <div class="detail-header-info-col"><span class="info-icon">🪑</span>{{ getSeatTypeOnly(selectedTicket.seat) }}</div>
              </div>
            </div>
          </div>
          <div class="detail-price-row">
            <span class="detail-price">₩{{ selectedTicket.price?.toLocaleString() }}</span>
          </div>

          <div class="detail-seat-row">
            <div class="detail-seat-block">
              <div class="seat-title">구역</div>
              <div class="seat-value">{{ truncateText(parseSeatInfo(selectedTicket.seat).section, 8) }}</div>
            </div>
            <div class="detail-seat-block">
              <div class="seat-title">열</div>
              <div class="seat-value">{{ truncateText(parseSeatInfo(selectedTicket.seat).row, 6) }}</div>
            </div>
            <div class="detail-seat-block">
              <div class="seat-title">번</div>
              <div class="seat-value">{{ truncateText(parseSeatInfo(selectedTicket.seat).seat, 6) }}</div>
            </div>
          </div>
          <div class="detail-apply-box">
            <div class="apply-title">{{ pageText.applyTitle }}</div>
            <div class="apply-btns">
              <button class="apply-yes" @click="handleApplyComplete">{{ pageText.applyYesText }}</button>
              <button class="apply-no" @click="handleBack">{{ pageText.applyNoText }}</button>
            </div>
          </div>
          <div class="detail-notice-box">
            <div class="notice-title">{{ pageText.noticeTitle }}</div>
            <ul class="notice-list">
              <li v-for="notice in selectedTicket.transferNotice" :key="notice">{{ notice }}</li>
            </ul>
          </div>
        </div>
      </div>
      <div v-else class="main-center-card">
        <div class="card-inner" v-if="!showTickets">
          <div class="card-title">{{ pageText.mainCardTitle }}</div>
          <div class="card-desc">{{ pageText.mainCardDesc }}</div>
          <div class="provider-row">
            <!-- NOL -->
            <div class="provider-col">
              <img src="/nol_logo.png" alt="NOL" class="provider-img nol-img" />
              <button class="provider-btn nol-btn" @click="fetchTickets('NOL')">
                NOL 바로가기
                <span class="btn-icon">↗</span>
              </button>
            </div>
            <!-- Ticketlink -->
            <div class="provider-col">
              <img src="/ticketlink_logo.png" alt="티켓링크" class="provider-img ticketlink-img" />
              <button class="provider-btn ticketlink-btn" @click="fetchTickets('TICKETLINK')">
                티켓링크 바로가기
                <span class="btn-icon">↗</span>
              </button>
            </div>
          </div>
        </div>
        <div v-else>
          <div class="ticket-tab-row">
            <button :class="['ticket-tab', activeTab === 'NOL' ? 'active' : '']" @click="switchTab('NOL')">NOL</button>
            <button :class="['ticket-tab', activeTab === '티켓링크' ? 'active' : '']" @click="switchTab('티켓링크')">티켓링크</button>
          </div>
          <div v-if="ticketLoading" class="ticket-list" style="justify-content:center;align-items:center;height:350px;">로딩중...</div>
          <div v-else-if="ticketError" class="ticket-list" style="justify-content:center;align-items:center;height:350px;">{{ ticketError }}</div>
          <div v-else-if="tickets.length === 0" class="ticket-list" style="justify-content:center;align-items:center;height:350px;">
            <div style="font-size:32px;font-weight:700;color:#969696;text-align:center;">현재 보유하고 있는 티켓이 없습니다.</div>
          </div>
          <div v-else class="ticket-list" style="max-height:600px;overflow-y:auto;">
            <div
              v-for="(ticket, idx) in tickets"
              :key="ticket.ticketId"
              :class="[
                'ticket-card',
                getTeamClass(ticket),
                { 'highlighted': hoveredTicket === ticket.ticketId }
              ]"
              :style="getTicketStyle(ticket)"
              @mouseenter="hoveredTicket = ticket.ticketId"
              @mouseleave="hoveredTicket = null"
            >
              <div class="ticket-header">
                <span class="ticket-title">{{ ticket.header }}</span>
                <span class="ticket-people">현재 응모 인원 : {{ ticket.waitNumber }}명</span>
              </div>
              <div class="ticket-info-row" style="justify-content:space-between;">
                <span>📅 {{ ticket.game.date.split('T')[0] }}</span>
                <span>🪑 {{ ticket.seat }}</span>
                <span>₩{{ ticket.price.toLocaleString() }}</span>
                <span style="margin-left:auto;font-weight:600;">{{ ticket.statusText }}</span>
              </div>
              <!-- 상세 정보: hover 시에만 표시 -->
              <transition name="fade">
                <div v-if="hoveredTicket === ticket.ticketId" class="ticket-detail ticket-detail-horizontal" :style="getTicketDetailStyle(ticket)">
                  <div class="ticket-detail-header-row">
                    <span class="ticket-detail-title">{{ ticket.header }}</span>
                  </div>
                  <div class="ticket-detail-info-row">
                    <span>📅 {{ ticket.game.date.split('T')[0] }}</span>
                    <span>⏰ {{ ticket.game.date.split('T')[1]?.slice(0,5) }}</span>
                    <span>현재 응모 인원 : {{ ticket.waitNumber }}명</span>
                  </div>
                  <div class="ticket-detail-horizontal-row-centered">
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title"><span class="block-icon">🏟️</span>경기장</div>
                      <div class="block-content">{{ ticket.game.home }} 홈</div>
                    </div>
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title"><span class="block-icon">🪑</span>좌석</div>
                      <div class="block-content">{{ ticket.seat }}</div>
                    </div>
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title"><span class="block-icon">💰</span>가격</div>
                      <div class="block-content">₩{{ ticket.price.toLocaleString() }}</div>
                    </div>
                  </div>
                  <div class="ticket-detail-transfer-btn-row">
                    <button 
                      class="ticket-detail-transfer-btn" 
                      :style="{ backgroundColor: getTeamColor(ticket.game.home) }"
                      @click="handleApply(ticket)"
                    >
                      양도하기
                    </button>
                  </div>
                </div>
              </transition>
            </div>
            <div v-if="tickets.length > 3" style="margin-top:10px;text-align:center;color:#888;font-size:14px;">스크롤하여 더 많은 티켓을 확인하세요</div>
          </div>
        </div>
      </div>
    </main>

    <!-- Footer -->
    <footer class="transfer-footer">
      <div class="footer-inner">
        <img src="/footer_logo.png" alt="logo" class="footer-logo-img" />
        <div class="footer-desc">Building amazing digital experiences<br />with modern design and technology.</div>
        <div class="footer-icons">
          <span class="footer-icon">⚫</span>
          <span class="footer-icon">⚫</span>
          <span class="footer-icon">⚫</span>
          <span class="footer-icon">⚫</span>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
// 화면 렌더에 필요한 기본 변수 선언 (없으면 추가)





// 모든 텍스트 변수 한 곳에서 관리
const selectedTicket = ref(null);
const showDetailPage = ref(false);
const showCompletePage = ref(false);
const pageText = {
  homeTeam: 'SSG 랜더스',
  awayTeam: '키움 히어로즈',
  matchTime: '18:30',
  detailHeaderLogo: 'SSG LANDERS',
  mainTitleLanders: 'LANDERS',
  mainTitleKiwoom: '키움 히어로즈',
  seriesTitle: '⚾ 오늘 한국시리즈',
  completeMainMessage: '양도 신청 되었습니다.',
  completeSubMessage: '양도가 완료되면 알려드리겠습니다.',
  confirmButtonText: '확인',
  processTitle: '양도 처리 안내',
  processList: [
    '양도 신청이 접수되었습니다',
    '매칭 및 결제 완료 시 양도가 완료됩니다',
    '완료 시 알림 메시지를 받으실 수 있습니다'
  ],
  applyTitle: '양도 신청하시겠습니까?',
  applyYesText: '네',
  applyNoText: '아니오',
  noticeTitle: '양도 신청 주의사항',
  mainCardTitle: '티켓을 불러와주세요',
  mainCardDesc: '아래 링크를 클릭하면 티켓을 가져옵니다.',
  currentPeopleText: (n) => `현재 응모 인원 : ${n}명`,
};

const showTickets = ref(false);
const activeTab = ref('NOL');
const hoveredTicket = ref(null);



import axios from 'axios';
import { API_CONFIG } from '@/config/api.config.js';
const { TICKET } = API_CONFIG;
import { teamColortoEnum } from '@/utils/teamColor.js';
import { getEnumTeamName, teamNameToEnum } from '@/utils/teamNameMap.js';
import { stadiumNameToEnum } from '@/utils/teamStadium.js';
import http from '@/utils/http'

const tickets = ref([]);
const ticketLoading = ref(false);
const ticketError = ref('');

// 팀 로고 매핑
const teamLogoMap = {
  'KIA타이거즈': 'kia.svg',
  '삼성라이온즈': 'samsung.svg',
  'LG트윈스': 'LG.svg',
  '두산베어스': 'DOOSAN.svg',
  'KT위즈': 'KT.svg',
  'SSG랜더스': 'SSG.svg',
  '롯데자이언츠': 'LOTTE.svg',
  '한화이글스': 'HanWha.svg',
  'NC다이노스': 'NC.svg',
  '키움히어로즈': 'KIWOOM.svg'
}

// 팀명 정규화 함수 (공백 제거)
const normalizeTeamName = (teamName) => {
  return teamName ? teamName.replace(/\s+/g, '') : ''
}

// 팀별 배경 이미지 매핑
const getTeamBackgroundImage = (teamName) => {
  const normalizedTeam = normalizeTeamName(teamName || '')
  const teamBgMap = {
    'kia타이거즈': '/kia_bg.png',
    '삼성라이온즈': '/samsung_bg.png',
    'lg트윈스': '/lg_bg.png',
    '두산베어스': '/doosan_bg.png',
    'kt위즈': '/kt_bg.png',
    'ssg랜더스': '/landers_bg.png',
    '롯데자이언츠': '/lotte_bg.png',
    '한화이글스': '/hanwha_bg.png',
    'nc다이노스': '/nc_bg.png',
    '키움히어로즈': '/kiwoom_bg.png'
  }
  return teamBgMap[normalizedTeam] || '/landers_bg.png'
}

// 팀별 로고명 가져오기
const getTeamLogoName = (teamName) => {
  const normalizedTeam = normalizeTeamName(teamName || '')
  const teamLogoNames = {
    'kia타이거즈': 'KIA TIGERS',
    '삼성라이온즈': 'SAMSUNG LIONS',
    'lg트윈스': 'LG TWINS',
    '두산베어스': 'DOOSAN BEARS',
    'kt위즈': 'KT WIZ',
    'ssg랜더스': 'SSG LANDERS',
    '롯데자이언츠': 'LOTTE GIANTS',
    '한화이글스': 'HANWHA EAGLES',
    'nc다이노스': 'NC DINOS',
    '키움히어로즈': 'KIWOOM HEROES'
  }
  return teamLogoNames[normalizedTeam] || teamName
}

// 좌석 정보 파싱 함수
const parseSeatInfo = (seatString) => {
  if (!seatString) return { section: '-', row: '-', seat: '-' }
  
  // "내야통로석 287구역 2열 10번" 형태를 파싱
  const sectionMatch = seatString.match(/(\d+)구역/)
  const rowMatch = seatString.match(/(\d+)열/)
  const seatMatch = seatString.match(/(\d+)번/)
  
  return {
    section: sectionMatch ? `${sectionMatch[1]}구역` : '-', // 숫자만 추출
    row: rowMatch ? `${rowMatch[1]}열` : '-',
    seat: seatMatch ? `${seatMatch[1]}번` : '-'
  }
}

// 텍스트 길이에 따라 동적으로 줄이는 함수
const truncateText = (text, maxLength = 8) => {
  if (!text) return '-'
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength - 1) + '…'
}

// 팀별 구단명 가져오기
const getStadiumName = (teamName) => {
  const normalizedTeam = normalizeTeamName(teamName || '')
  return stadiumNameToEnum[normalizedTeam] || '경기장'
}

// 좌석 정보에서 '석'까지만 추출
const getSeatTypeOnly = (seatString) => {
  if (!seatString) return '좌석 정보'
  
  // '내야통로석 287구역 27열 16번'에서 '내야통로석'만 추출
  const seatTypeMatch = seatString.match(/^(.+?석)/)
  return seatTypeMatch ? seatTypeMatch[1] : seatString.split(' ')[0]
}

// 팀별 CSS 클래스명 생성 함수
const getTeamClass = (ticket) => {
  const homeTeam = normalizeTeamName(ticket.homeKor || ticket.game?.home || '')
  return homeTeam ? `team-${homeTeam.toLowerCase()}` : 'team-default'
}

// 팀별 색상 매핑
const getTeamColor = (teamName) => {
  const normalizedTeam = normalizeTeamName(teamName || '')
  const teamColors = {
    'kia타이거즈': '#EA0029',
    '삼성라이온즈': '#074CA1',
    'lg트윈스': '#C30452',
    '두산베어스': '#1A1748',
    'kt위즈': '#000000',
    'ssg랜더스': '#CE0E2D',
    '롯데자이언츠': '#041E42',
    '한화이글스': '#FC4E00',
    'nc다이노스': '#315288',
    '키움히어로즈': '#570514'
  }
  return teamColors[normalizedTeam] || '#395b8c'
}

// 티켓 디테일 스타일 생성 함수
const getTicketDetailStyle = (ticket) => {
  const homeTeam = ticket.homeKor || ticket.game?.home || ''
  const teamColor = getTeamColor(homeTeam)
  const darkerColor = getTeamColor(homeTeam).replace('#', '').match(/.{2}/g).map(hex => Math.max(0, parseInt(hex, 16) - 30).toString(16).padStart(2, '0')).join('')
  
  return {
    '--team-color': teamColor,
    '--team-color-dark': `#${darkerColor}`,
    background: `linear-gradient(90deg, ${teamColor} 60%, #${darkerColor} 100%)`,
    color: '#fff'
  }
}

// 티켓 스타일 생성 함수 (기본 스타일만)
const getTicketStyle = (ticket) => {
  const logoPath = teamLogoMap[normalizeTeamName(ticket.homeKor || ticket.game?.home || '')]
  
  let style = {
    transition: 'all 0.3s ease',
    position: 'relative'
  }
  
  // 호버 상태일 때 로고 배경 추가
  if (hoveredTicket.value === ticket.ticketId && logoPath) {
    style.backgroundImage = `url('/src/assets/logo/${logoPath}')`
    style.backgroundSize = 'contain'
    style.backgroundPosition = 'center'
    style.backgroundRepeat = 'no-repeat'
    style.zIndex = 10
    style.height = 'auto'
    style.minHeight = '320px'
  }
  
  return style
}

async function fetchTickets(platform) {
  console.log(` NOL 버튼 클릭됨 - 플랫폼: ${platform}`);
  showTickets.value = true;
  activeTab.value = platform === 'NOL' ? 'NOL' : '티켓링크';
  ticketLoading.value = true;
  ticketError.value = '';
  try {
    console.log(` API 요청 시작: tickets/platform/${platform}`);
    const res = await http.get(`tickets/platform/${platform}`);
    console.log(` API 응답 받음:`, res.data);
    console.log(` 받은 티켓 개수: ${res.data.length}개`);
    
    tickets.value = res.data.map(ticket => {
      const homeKor = Object.keys(teamNameToEnum).find(
        key => teamNameToEnum[key] === ticket.game.home
      ) || ticket.game.home;
      const awayKor = Object.keys(teamNameToEnum).find(
        key => teamNameToEnum[key] === ticket.game.away
      ) || ticket.game.away;
      
      // 정규화된 팀명으로 색상 확인
      const normalizedHome = normalizeTeamName(homeKor);
      const teamColor = teamColortoEnum[normalizedHome];
      
      console.log(` 홈팀: ${homeKor} -> 정규화: ${normalizedHome} -> 색상: ${teamColor}`);
      console.log(` 어웨이팀: ${awayKor}`);
      
      const processedTicket = {
        ...ticket,
        homeKor,
        awayKor,
        color: teamColor ? `#${teamColor}` : '#395b8c',
        header: `${awayKor} vs ${homeKor}`,
        statusText: ticket.status === 'BEING_ASSIGNMENT' ? '응모 진행중' : ticket.status === 'TRANSACTION_COMPLETE' ? '응모 완료' : ticket.status === 'BEING_PAYING' ? '결제중' : '',
      };
      
      console.log(` 처리된 티켓:`, {
        id: processedTicket.ticketId,
        header: processedTicket.header,
        color: processedTicket.color,
        status: processedTicket.statusText
      });
      
      return processedTicket;
    });
    
    console.log(` 최종 티켓 배열:`, tickets.value);
  } catch (e) {
    console.error(` 티켓 불러오기 실패:`, e);
    ticketError.value = '티켓 불러오기에 실패했습니다.';
    tickets.value = [];
  } finally {
    ticketLoading.value = false;
    console.log(` 티켓 로딩 완료`);
  }
}

// 탭 전환 시 해당 플랫폼의 티켓을 가져오는 함수
async function switchTab(platform) {
  console.log(`탭 전환: ${platform}`);
  activeTab.value = platform;
  
  // 플랫폼에 맞는 API 엔드포인트 결정
  const apiPlatform = platform === 'NOL' ? 'NOL' : 'TICKETLINK';
  
  // 해당 플랫폼의 티켓 데이터 가져오기
  ticketLoading.value = true;
  ticketError.value = '';
  
  try {
    console.log(`탭 전환 API 요청: tickets/platform/${apiPlatform}`);
    const res = await http.get(`tickets/platform/${apiPlatform}`);
    console.log(`탭 전환 API 응답:`, res.data);
    
    tickets.value = res.data.map(ticket => {
      const homeKor = Object.keys(teamNameToEnum).find(
        key => teamNameToEnum[key] === ticket.game.home
      ) || ticket.game.home;
      const awayKor = Object.keys(teamNameToEnum).find(
        key => teamNameToEnum[key] === ticket.game.away
      ) || ticket.game.away;
      
      // 정규화된 팀명으로 색상 확인
      const normalizedHome = normalizeTeamName(homeKor);
      const teamColor = teamColortoEnum[normalizedHome];
      
      const processedTicket = {
        ...ticket,
        homeKor,
        awayKor,
        color: teamColor ? `#${teamColor}` : '#395b8c',
        header: `${awayKor} vs ${homeKor}`,
        statusText: ticket.status === 'BEING_ASSIGNMENT' ? '응모 진행중' : ticket.status === 'TRANSACTION_COMPLETE' ? '응모 완료' : ticket.status === 'BEING_PAYING' ? '결제중' : '',
      };
      
      return processedTicket;
    });
    
    console.log(`탭 전환 완료 - ${platform} 티켓 ${tickets.value.length}개 로드됨`);
  } catch (e) {
    console.error(`탭 전환 중 티켓 불러오기 실패:`, e);
    ticketError.value = '티켓 불러오기에 실패했습니다.';
    tickets.value = [];
  } finally {
    ticketLoading.value = false;
  }
}

const router = useRouter();

function handleApply(ticket) {
  selectedTicket.value = ticket;
  showDetailPage.value = true;
}
async function handleApplyComplete() {
  try {
    // 양도 API 호출
    const response = await http.get(`tickets/transfer/${selectedTicket.value.ticketId}`);
    
    console.log('양도 API 응답:', response.data);
    
    if (response.data.success) {
      // 성공 시 완료 페이지로 전환
      showCompletePage.value = true;
      console.log('양도 성공:', response.data.message);
    } else {
      // 실패 시 에러 메시지 표시
      alert('양도 신청에 실패했습니다.');
    }
  } catch (error) {
    console.error('양도 API 오류:', error);
    alert('양도 신청 중 오류가 발생했습니다.');
  }
}
function handleBack() {
  // 티켓 리스트(transfer-main)로 돌아가도록 상태만 복구
  showDetailPage.value = false;
  selectedTicket.value = null;
}
function handleCompleteBack() {
  // 완료 → 상세(양도 신청 여부)로 상태 복구
  showCompletePage.value = false;
  showDetailPage.value = true;
}
function handleCompleteConfirm() {
  // 완료 → 티켓 목록(카드 리스트) 상태로 이동
  showCompletePage.value = false;
  showDetailPage.value = false;
  showTickets.value = true;
  selectedTicket.value = null;
}

</script>

<style scoped>
.transfer-root {
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
}
.transfer-header {
  width: 100%;
  border-bottom: 1px solid #f1f1f1;
  background: #fff;
  height: 60px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 10;
}
.header-inner {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
  padding: 0 32px;
}
.header-logo {
  font-size: 26px;
  font-weight: 700;
  color: #111827;
  letter-spacing: -1px;
}
.header-nav {
  display: flex;
  gap: 36px;
}
.nav-item {
  font-size: 20px;
  font-weight: 700;
  color: #111827;
  text-decoration: none;
  transition: color 0.15s;
}
.nav-item:hover {
  color: #ce0e2d;
}
.header-login {
  font-size: 15px;
  color: #888;
}
.transfer-main {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}
.main-center-card {
  background: #fafbfc;
  border-radius: 18px;
  border: 1.5px solid #e5e7eb;
  width: 700px;
  max-width: 95vw;
  margin: 48px 0 64px 0;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 350px;
}
.card-inner {
  width: 1000px;
  height: 500px;
  padding: 48px 32px 48px 32px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.card-title {
  font-size: 40px;
  font-weight: 700;
  color: #969696;
  margin-bottom: 8px;
  text-align: center;
}
.card-desc {
  color: #969696;
  font-size: 20px;
  margin-top: 20px;
  margin-bottom: 32px;
  text-align: center;
}
.provider-row {
  display: flex;
  gap: 48px;
  justify-content: center;
  width: 100%;
  margin-top: 12px;
  align-items: flex-start;
}
.provider-col {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.provider-img {
  display: block;
  margin-bottom: 18px;
  background: #fff;
}
.nol-img,
.ticketlink-img {
  width: 250px;
  height: 100px;
  object-fit: contain;
  background: #fff;
}
.provider-btn {
  margin-top: 0;
  width: 250px;
  height: 54px;
  border: none;
  border-radius: 8px;
  font-size: 25px;
  font-weight: 700;
  color: #fff;
  background: #ce0e2d;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  transition: background 0.15s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.nol-btn {
  background: #ce0e2d;
}
.ticketlink-btn {
  background: linear-gradient(90deg, #ce0e2d 70%, #ffb22c 100%);
}
.btn-icon {
  font-size: 15px;
  margin-left: 4px;
}
.transfer-footer {
  width: 100%;
  background: #fafbfc;
  border-top: 1px solid #e5e7eb;
  padding: 32px 0 24px 0;
}
.footer-inner {
  max-width: 600px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.footer-logo-img {
  width: 70px;
  height: 45px;
  object-fit: contain;
  margin-bottom: 10px;
}
.footer-desc {
  color: #6b7280;
  font-size: 16px;
  text-align: center;
  margin-bottom: 18px;
  line-height: 1.5;
}
.footer-icons {
  display: flex;
  gap: 16px;
}
.footer-icon {
  width: 32px;
  height: 32px;
  background: #e5e7eb;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #6b7280;
}
.ticket-tab-row {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
  margin-bottom: 24px;
}
.ticket-tab {
  background: #f3f3f3;
  border: none;
  border-radius: 20px;
  padding: 8px 36px;
  font-size: 16px;
  font-weight: 700;
  color: #222;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.ticket-tab.active {
  background: #ce0e2d;
  color: #fff;
  box-shadow: 0 2px 8px 0 #ce0e2d22;
}
.ticket-list {
  display: flex;
  flex-direction: column;
  gap: 18px;
  align-items: center;
}
.ticket-card {
  width: 660px;
  max-width: 95vw;
  /* background 제거 - 인라인 스타일로 팀 색상 동적 적용 */
  border-radius: 8px;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  box-shadow: 0 2px 8px 0 #0001;
  padding: 24px 32px 18px 32px;
  transition: box-shadow 0.3s ease, background 0.3s ease, height 0.3s ease, min-height 0.3s ease;
  position: relative;
  cursor: pointer;
  overflow: visible;
  height: 130px;
  min-height: 130px;
  max-height: 400px;
}

/* 팀별 색상 클래스 */
.ticket-card.team-kia타이거즈 {
  background-color: #EA0029 !important;
}

.ticket-card.team-삼성라이온즈 {
  background-color: #074CA1 !important;
}

.ticket-card.team-lg트윈스 {
  background-color: #C30452 !important;
}

.ticket-card.team-두산베어스 {
  background-color: #1A1748 !important;
}

.ticket-card.team-kt위즈 {
  background-color: #000000 !important;
}

.ticket-card.team-ssg랜더스 {
  background-color: #CE0E2D !important;
}

.ticket-card.team-롯데자이언츠 {
  background-color: #041E42 !important;
}

.ticket-card.team-한화이글스 {
  background-color: #FC4E00 !important;
}

.ticket-card.team-nc다이노스 {
  background-color: #315288 !important;
}

.ticket-card.team-키움히어로즈 {
  background-color: #570514 !important;
}

.ticket-card.team-default {
  background-color: #395b8c !important;
}

/* hover 시 팀 색상 유지 및 그림자 효과 */
.ticket-card.highlighted.team-kia타이거즈 {
  box-shadow: 0 12px 48px 0 #EA002955 !important;
}

.ticket-card.highlighted.team-삼성라이온즈 {
  box-shadow: 0 12px 48px 0 #074CA155 !important;
}

.ticket-card.highlighted.team-lg트윈스 {
  box-shadow: 0 12px 48px 0 #C3045255 !important;
}

.ticket-card.highlighted.team-두산베어스 {
  box-shadow: 0 12px 48px 0 #1A174855 !important;
}

.ticket-card.highlighted.team-kt위즈 {
  box-shadow: 0 12px 48px 0 #00000055 !important;
}

.ticket-card.highlighted.team-ssg랜더스 {
  box-shadow: 0 12px 48px 0 #CE0E2D55 !important;
}

.ticket-card.highlighted.team-롯데자이언츠 {
  box-shadow: 0 12px 48px 0 #041E4255 !important;
}

.ticket-card.highlighted.team-한화이글스 {
  box-shadow: 0 12px 48px 0 #FC4E0055 !important;
}

.ticket-card.highlighted.team-nc다이노스 {
  box-shadow: 0 12px 48px 0 #31528855 !important;
}

.ticket-card.highlighted.team-키움히어로즈 {
  box-shadow: 0 12px 48px 0 #57051455 !important;
}

.ticket-card.highlighted.team-default {
  box-shadow: 0 12px 48px 0 #395b8c55 !important;
}

.ticket-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: inherit;
  background-size: contain;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 1;
}

.ticket-card:hover::before {
  opacity: 0.1;
}

.ticket-card > * {
  position: relative;
  z-index: 2;
}
.ticket-card.highlighted.landers-hover {
  /* 인라인 스타일로 동적 팀 색상 적용하므로 CSS 고정 색상 제거 */
  transition: all 0.3s ease;
}

.ticket-card .ticket-title {
  font-size: 22px;
  font-weight: 700;
}
.ticket-card .ticket-info-row {
  font-size: 14px;
  font-weight: 400;
  margin-top: 8px;
  display: flex;
  gap: 20px;
}
.ticket-card .landers {
  color: #fff;
  font-family: 'Pretendard', 'Montserrat', sans-serif;
  font-size: 28px;
  letter-spacing: 1px;
}
.ticket-card.highlighted {
  /* 인라인 스타일로 동적 팀 색상 적용하므로 CSS 고정 색상 제거 */
  transition: all 0.3s ease;
}
.ticket-card .ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.ticket-card .ticket-people {
  font-size: 15px;
  color: #fff;
  font-weight: 500;
  white-space: nowrap;
}
.ticket-card .ticket-detail {
  position: absolute;
  left: 0; right: 0; top: 0; bottom: 0;
  background: rgba(206,14,45,0.91);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  padding: 24px 32px 18px 32px;
  z-index: 10;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  animation: fadeIn 0.3s;
}
.ticket-detail-header-row {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 2px;
}
.ticket-detail-title {
  font-size: 28px;
  font-weight: 800;
  color: #fff;
  text-shadow: 0 2px 8px #b1002b55;
  letter-spacing: -1px;
  padding-left: 2px;
  margin-bottom: 0;
  margin-top: 4px;
}
.ticket-detail-info-row {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
  font-size: 16px;
  font-weight: 400;
  color: #fff;
  margin-bottom: 18px;
  padding-left: 2px;
  padding-right: 0;
}
.ticket-detail-horizontal-row-centered {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 18px;
  margin: 0 0 0 0;
  padding-right: 0;
}
.ticket-detail-block.block-horizontal {
  flex: 1 1 0;
  background: rgba(255,255,255,0.13);
  border-radius: 12px;
  padding: 16px 14px 12px 16px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;
  max-width: 400px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.ticket-detail-block.block-horizontal .block-title {
  font-size: 17px;
  font-weight: 700;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}
.ticket-detail-block.block-horizontal .block-content {
  font-size: 15px;
  font-weight: 400;
  color: #fff;
  margin-left: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.ticket-detail-block.block-horizontal .block-icon {
  font-size: 18px;
  margin-right: 4px;
}

.ticket-card .apply-btn {
  position: absolute;
  right: 32px;
  bottom: 26px;
  z-index: 12;
  background: #fff;
  color: #ce0e2d;
  border: none;
  border-radius: 8px;
  padding: 10px 28px;
  font-size: 17px;
  font-weight: 700;
  margin-top: 8px;
  box-shadow: 0 2px 8px #ce0e2d22;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
}
.ticket-card .apply-btn:hover {
  background: #ce0e2d;
  color: #fff;
}

.ticket-detail-bg {
  position: absolute;
  left: 0; right: 0; top: 0; bottom: 0;
  opacity: 0.18;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.ticket-detail-bg img {
  width: 80%;
  object-fit: contain;
  filter: brightness(1.2) drop-shadow(0 0 8px #fff8);
}
.ticket-detail-content {
  z-index: 2;
  position: relative;
  margin-bottom: 18px;
}
.detail-row {
  margin-bottom: 8px;
  font-size: 17px;
}
.apply-btn {
  z-index: 2;
  background: #fff;
  color: #ce0e2d;
  border: none;
  border-radius: 8px;
  padding: 10px 28px;
  font-size: 17px;
  font-weight: 700;
  margin-top: 8px;
  box-shadow: 0 2px 8px #ce0e2d22;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
}
.apply-btn:hover {
  background: #ce0e2d;
  color: #fff;
}
.ticket-detail-title {
  width: 100%;
  font-size: 32px;
  font-weight: 800;
  color: #fff;
  text-shadow: 0 2px 8px #b1002b55;
  margin-bottom: 10px;
  margin-top: 6px;
  z-index: 11;
  position: relative;
  letter-spacing: -1px;
  padding-left: 2px;
}
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.2s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
.detail-page-wrap {
  width: 100vw;
  min-height: 100vh;
  background: #f6f7fa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  padding: 36px 0 48px 0;
}
.detail-card {
  width: 600px;
  background: #fff;
  border-radius: 22px;
  box-shadow: 0 4px 32px 0 #0002;
  overflow: hidden;
  margin-top: 10px;
  margin-bottom: 40px;
}
.detail-header {
  color: #fff;
  padding: 32px 32px 54px 32px;
  position: relative;
  min-height: 320px;
} 
.detail-header-info-row {
  display: flex;
  flex-direction: column;
  margin-top: 8px;
  gap: 8px;
  position: absolute;
  left: 32px;
  bottom: 28px;
  z-index: 3;
  color: #fff;
  font-size: 18px;
  font-weight: 500;
}
.detail-header-info-line {
  display: flex;
  justify-content: center;
  gap: 100px;
  width: 500px;
}

.detail-header-info-col {
  font-size: 16px;
  display: flex;
  align-items: center;
  width: auto;
  min-width: 200px;
  gap: 6px;
  flex: 1;
}
.info-icon {
  font-size: 18px;
}

.detail-header-logo {
  font-size: 26px;
  font-weight: 900;
  letter-spacing: 1px;
  position: absolute;
  left: 32px;
  top: 22px;
}
.detail-header-ticketid {
  position: absolute;
  right: 32px;
  top: 22px;
  font-size: 13px;
  text-align: right;
  color: #fff9;
}
.ticketid-value {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
}
.detail-match-title {
  text-align: center;
  margin-top: 40px;
  margin-bottom: 0;
  z-index: 2;
  position: relative;
}
.main-title {
  font-size: 32px;
  font-weight: 900;
  line-height: 1.4;
  letter-spacing: 1px;
  color: #fff;
  text-shadow: 0 2px 16px #b1002b55;
}
.detail-header-bg {
  position: absolute;
  left: 0; right: 0; top: 0; bottom: 0;
  opacity: 0.13;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}
.detail-header-bg img {
  width: 90%;
  height: 230px;
  object-fit: contain;
  filter: brightness(1.2) drop-shadow(0 0 8px #fff8);
}
.detail-info-row {
  display: flex;
  gap: 18px;
  padding: 18px 32px 0 32px;
  color: #ce0e2d;
  font-size: 15px;
  font-weight: 600;
  align-items: center;
}
.detail-price-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 32px 0 32px;
  margin-top: 6px;
  margin-bottom: 6px;
}
.detail-price {
  margin-top: 10px;
  margin-bottom: 10px;
  font-size: 22px;
  font-weight: 800;
  color: #ce0e2d;
}
.detail-gate {
  font-size: 14px;
  color: #222;
  font-weight: 600;
}
.detail-transfer-btn-row {
  display: flex;
  justify-content: center;
  padding: 20px 32px;
  margin-bottom: 10px;
}
.transfer-btn {
  background: #ce0e2d;
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 14px 40px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(206, 14, 45, 0.3);
}
.transfer-btn:hover {
  background: #b1002b;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(206, 14, 45, 0.4);
}
.detail-seat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  padding: 0 32px 0 32px;
  margin-bottom: 18px;
}
.detail-seat-block {
  flex: 1 1 0;
  background: #f6f7fa;
  border-radius: 12px;
  padding: 14px 12px 10px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 0;
  max-width: 120px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.seat-title {
  font-size: 13px;
  font-weight: 700;
  color: #b1002b;
  margin-bottom: 4px;
}
.seat-value {
  font-size: 16px;
  font-weight: 700;
  color: #222;
}
.detail-apply-box {
  background: #fafbfc;
  border-radius: 14px;
  margin: 0 32px 18px 32px;
  padding: 22px 0 18px 0;
  text-align: center;
  box-shadow: 0 2px 8px #ce0e2d11;
}
.apply-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 16px;
}
.apply-btns {
  display: flex;
  justify-content: center;
  gap: 18px;
}
.apply-yes {
  background: #ce0e2d;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
}
.apply-no {
  background: #fff;
  color: #ce0e2d;
  border: 1.5px solid #ce0e2d;
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
}
.apply-yes:hover {
  background: #b1002b;
}
.apply-no:hover {
  background: #ce0e2d;
  color: #fff;
}
.detail-notice-box {
  background: #fff;
  border-radius: 14px;
  margin: 0 32px 24px 32px;
  padding: 18px 20px 18px 20px;
  box-shadow: 0 2px 8px #ce0e2d11;
}
.notice-title {
  font-size: 15px;
  font-weight: 700;
  color: #ce0e2d;
  margin-bottom: 10px;
}
.notice-list {
  padding-left: 18px;
  color: #b1002b;
  font-size: 13px;
  line-height: 1.7;
}
.notice-list li {
  margin-bottom: 2px;
}

/* --- 완료 페이지 스타일 병합 --- */
.complete-root {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
  height: 700px;
  background: #fafbfc;
}
.complete-card {
  width: 570px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px #0001;
  padding: 32px 24px 24px 24px;
  margin-bottom: 24px;
  text-align: center;
}
.match-info {
  background: #ce0e2d;
  color: #fff;
  border-radius: 12px;
  padding: 18px 0 12px 0;
  margin-bottom: 30px;
}
.match-title {
  font-size: 26px;
  font-weight: 700;
}
.ssg, .kiwoom {
  font-weight: 900;
}
.match-detail {
  font-size: 15px;
  margin-top: 6px;
  display: flex;
  justify-content: center;
  gap: 18px;
}
.complete-message {
  margin: 34px 0 14px 0;
}
.main-message {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 10px;
}
.sub-message {
  font-size: 1rem;
  color: #444;
  margin-bottom: 18px;
}
.confirm-btn {
  background: #ce0e2d;
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 30px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 10px;
}
.process-info {
  width: 570px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 24px #0001;
  padding: 24px 24px 18px 24px;
  margin-bottom: 20px;
}
.process-title {
  font-size: 1.1rem;
  font-weight: 700;
  margin-bottom: 10px;
}
.process-list {
  list-style: none;
  padding: 0;
  margin: 0;
}
.process-list li {
  display: flex;
  align-items: center;
  font-size: 1rem;
  margin-bottom: 7px;
}
.num {
  display: inline-block;
  width: 22px;
  height: 22px;
  background: #e5e5e5;
  color: #ce0e2d;
  font-weight: 700;
  border-radius: 50%;
  text-align: center;
  line-height: 22px;
  margin-right: 8px;
}
.back-btn {
  background: #fff;
  color: #ce0e2d;
  border: 1.5px solid #ce0e2d;
  border-radius: 8px;
  padding: 10px 34px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  margin-top: 4px;
}

/* Fade transition for ticket details */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from, .fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

.fade-enter-to, .fade-leave-from {
  opacity: 1;
  transform: translateY(0);
}

/* Ticket detail styling improvements */
.ticket-detail {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  animation: expandDetail 0.3s ease;
}

@keyframes expandDetail {
  from {
    opacity: 0;
    max-height: 0;
    padding-top: 0;
    margin-top: 0;
  }
  to {
    opacity: 1;
    max-height: 200px;
    padding-top: 16px;
    margin-top: 16px;
  }
}

.ticket-detail-horizontal {
  overflow: hidden;
}

/* 호버 시 나타나는 양도하기 버튼 스타일 */
.ticket-detail-transfer-btn-row {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  position: relative;
}

.ticket-detail-transfer-btn {
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 20px;
  font-weight: 600;
  width: 200px;
  height: 50px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  /* 배경색은 인라인 스타일로 동적 설정 */
  margin-left: auto;
  flex-shrink: 0;
}

.ticket-detail-transfer-btn:hover {
  filter: brightness(0.9);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.ticket-detail-transfer-btn:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  filter: brightness(0.8);
}
</style>

