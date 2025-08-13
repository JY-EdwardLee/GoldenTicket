<template>
  <div class="transfer-root">
    <!-- Header -->

    <!-- 응모 진행중 상태 표시 -->
    <div v-if="isApplying" class="applying-status">
      <div class="applying-message">양도 진행중입니다.</div>
    </div>

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
                <span class="time">{{ selectedTicket.date }}</span>
              </div>
            </div>
            <div class="complete-message">
              <div class="main-message">{{ pageText.completeMainMessage }}</div>
              <div class="sub-message">{{ pageText.completeSubMessage }}</div>
              <button class="confirm-btn" @click="handleCompleteConfirm">{{ pageText.confirmButtonText }}</button>
            </div>
            
            <!-- 주의사항 섹션을 complete-card 내부로 이동 -->
            <div class="detail-notice-box" style="margin-top: 30px; padding: 20px; background-color: #f8f9fa; border-radius: 8px; border: 1px solid #e9ecef;">
              <div class="notice-title" style="font-size: 18px; font-weight: 700; color: var(--theme-primary); margin-bottom: 15px;">양도 신청 주의사항</div>
              <div class="notice-content" style="font-size: 14px; color: #333; line-height: 1.8;">
                <div style="margin-bottom: 8px;">• 양도 신청 후 취소는 불가능합니다.</div>
                <div style="margin-bottom: 8px;">• 매칭 완료 시 알림이 전송됩니다.</div>
                <div style="margin-bottom: 8px;">• 양도료는 경기 시작 후 지급됩니다.</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else-if="showDetailPage && selectedTicket" class="detail-page-wrap">
        <div class="detail-card" style="position: relative;">
          <!-- 팀 로고 배경 비활성화 -->
          <!-- <div class="detail-team-logo" 
               :style="{ 
                 backgroundImage: selectedTicket ? (getTeamLogo(selectedTicket.homeKor || selectedTicket.game?.home) ? `url(${getTeamLogo(selectedTicket.homeKor || selectedTicket.game?.home)})` : 'none') : 'none',
                 opacity: 0.1,
                 position: 'absolute',
                 top: '0',
                 left: '0',
                 right: '0',
                 bottom: '0',
                 width: '100%',
                 height: '100%',
                 backgroundSize: 'contain',
                 backgroundRepeat: 'no-repeat',
                 backgroundPosition: 'center',
                 pointerEvents: 'none',
                 zIndex: 1
               }">
          </div> -->
          <div class="detail-header" :style="getTicketDetailStyle(selectedTicket)">
            <div class="detail-header-logo" style="font-size: 26px; font-weight: 900; letter-spacing: 1px;">{{ getTeamLogoName(selectedTicket.homeKor || selectedTicket.game?.home) }}</div>
            <div class="detail-header-ticketid">TICKET ID<br /><span class="ticketid-value">#{{ selectedTicket.ticketId }}</span></div>
            <div class="detail-match-title">
              <div class="main-title">
                <span class="main-title-home">{{ selectedTicket.homeKor || selectedTicket.game?.home }}</span><br />
                <span class="main-title-vs">VS</span><br />
                <span class="main-title-away">{{ selectedTicket.awayKor || selectedTicket.game?.away }}</span>
              </div>
            </div>
            <div class="detail-header-bg"><img :src="getTeamLogo(selectedTicket.homeKor || selectedTicket.game?.home)" :alt="selectedTicket.homeKor || selectedTicket.game?.home" /></div>
            <div class="detail-header-info-row">
              <div class="detail-header-info-line">
                <div class="detail-header-info-col">
                  <span class="info-icon">
                    <img src="/icon/calendar.svg" alt="날짜" class="info-icon-img" />
                  </span>
                  {{ selectedTicket.game?.date?.split('T')[0] || selectedTicket.date }}
                </div>
                <div class="detail-header-info-col">
                  <span class="info-icon">
                    <img src="/icon/clock.svg" alt="시간" class="info-icon-img" />
                  </span>
                  {{ selectedTicket.game?.date?.split('T')[1]?.slice(0,5) || selectedTicket.time }}
                </div>
              </div>
              <div class="detail-header-info-line">
                <div class="detail-header-info-col">
                  <span class="info-icon">
                    <img src="/icon/stadium.svg" alt="경기장" class="info-icon-img" />
                  </span>
                  {{ truncateText(getStadiumName(selectedTicket.homeKor || selectedTicket.game?.home), 15) }}
                </div>
                <div class="detail-header-info-col">
                  <span class="info-icon">
                    <img src="/icon/chair.svg" alt="좌석" class="info-icon-img" />
                  </span>
                  {{ getSeatTypeOnly(selectedTicket.seat) }}
                </div>
              </div>
            </div>
          </div>
          <div class="detail-price-row">
            <img src="/icon/credit_card.svg" alt="가격" class="price-icon" style="width: 25px; height: 25px;" />
            <span class="detail-price">{{ selectedTicket.price?.toLocaleString() }}</span>
          </div>

          <div class="detail-seat-row" style="position: relative; z-index: 3; background-color: rgba(255, 255, 255, 0.95); margin: 0px 20px; border-radius: 8px; padding: 10px;">
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
          <div class="detail-apply-box" style="position: relative; z-index: 3;">
            <div class="apply-title">{{ pageText.applyTitle }}</div>
            <div class="apply-btns">
              <button class="apply-yes" @click="handleApplyComplete">{{ pageText.applyYesText }}</button>
              <button class="apply-no" @click="handleBack">{{ pageText.applyNoText }}</button>
            </div>
          </div>
          <div class="detail-notice-box" style="position: relative; z-index: 3; margin-top: 20px; padding: 20px; background-color: #f8f9fa; border-radius: 8px; border: 1px solid #e9ecef;">
            <div class="notice-title" style="font-size: 18px; font-weight: 700; color: var(--theme-primary); margin-bottom: 15px;">양도 신청 주의사항</div>
            <div class="notice-content" style="font-size: 14px; color: #333; line-height: 1.8;">
              <div style="margin-bottom: 8px;">• 양도 신청 후 취소는 불가능합니다.</div>
              <div style="margin-bottom: 8px;">• 매칭 완료 시 알림이 전송됩니다.</div>
              <div style="margin-bottom: 8px;">• 양도료는 경기 시작 후 지급됩니다.</div>
            </div>
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
                NOL 불러오기
                <span class="btn-icon">↗</span>
              </button>
            </div>
            <!-- Ticketlink -->
            <div class="provider-col">
              <img src="/ticketlink_logo.png" alt="티켓링크" class="provider-img ticketlink-img" />
              <button class="provider-btn ticketlink-btn" @click="fetchTickets('TICKETLINK')">
                티켓링크 불러오기
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
          <div v-else class="ticket-list" style="max-height:600px;overflow-y:auto;padding:0 50px;">
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
                <span> <img src="/icon/calendar.svg" alt="날짜" style="width: 25px; height: 25px;" />{{ ticket.game.date.split('T')[0] }}</span>
                <span> <img src="/icon/chair.svg" alt="좌석" style="width: 25px; height: 25px;" />{{ ticket.seat }}</span>
                <span> <img src="/icon/credit_card.svg" alt="가격" style="width: 25px; height: 25px;" />{{ ticket.price.toLocaleString() }}</span>
                <span style="margin-left:auto;font-weight:600;">{{ ticket.statusText }}</span>
              </div>
              <!-- 상세 정보: hover 시에만 표시 -->
              <transition name="fade">
                <!-- 응모 진행중 상태일 때 -->
                <div v-if="hoveredTicket === ticket.ticketId && (ticket.status === 'BEING_ASSIGNMENT' || ticket.statusText === '응모 진행중')" 
                     class="ticket-detail ticket-detail-horizontal ticket-detail-being-assignment" 
                     :style="getTicketDetailStyle(ticket)">
                  <div class="ticket-detail-being-assignment-content">
                    <div class="being-assignment-icon">
                      <img src="/icon/hourglass.svg" alt="응모 진행중" style="width: 24px; height: 24px;" />
                    </div>
                    <div class="being-assignment-text">응모 진행중입니다</div>
                  </div>
                </div>
                <!-- 결제중 상태일 때 -->
                <div v-else-if="hoveredTicket === ticket.ticketId && (ticket.status === 'BEING_PAYING' || ticket.statusText === '결제중')" 
                     class="ticket-detail ticket-detail-horizontal ticket-detail-being-assignment" 
                     :style="getTicketDetailStyle(ticket)">
                  <div class="ticket-detail-being-assignment-content">
                    <div class="being-assignment-icon">
                      <img src="/icon/credit_card.svg" alt="결제중" style="width: 24px; height: 24px;" />
                    </div>
                    <div class="being-assignment-text">결제중입니다</div>
                  </div>
                </div>
                <!-- 거래 완료 상태일 때 -->
                <div v-else-if="hoveredTicket === ticket.ticketId && (ticket.status === 'TRANSACTION_COMPLETE' || ticket.statusText === '거래 완료')" 
                     class="ticket-detail ticket-detail-horizontal ticket-detail-being-assignment" 
                     :style="getTicketDetailStyle(ticket)">
                  <div class="ticket-detail-being-assignment-content">
                    <div class="being-assignment-icon">
                      <img src="/icon/check.svg" alt="거래 완료" style="width: 24px; height: 24px;" />
                    </div>
                    <div class="being-assignment-text">거래 완료</div>
                  </div>
                </div>
                <!-- 일반 상태일 때 -->
                <div v-else-if="hoveredTicket === ticket.ticketId" class="ticket-detail ticket-detail-horizontal" :style="getTicketDetailStyle(ticket)">
                  <!-- 팀 로고 배경 -->
                  <div class="ticket-detail-bg">
                    <img :src="getTeamLogo(ticket.homeKor || ticket.game?.home)" :alt="ticket.homeKor || ticket.game?.home" />
                  </div>
                  <div class="ticket-detail-header-row">
                    <span class="ticket-detail-title">{{ ticket.header }}</span>
                  </div>
                  <div class="ticket-detail-info-row">
                    <span style="display: flex; align-items: center; gap: 4px;">
                      <img src="/icon/calendar.svg" alt="날짜"/>
                      {{ ticket.game.date.split('T')[0] }}
                    </span>
                    <span style="display: flex; align-items: center; gap: 4px;">
                      <img src="/icon/clock.svg" alt="시간"/>
                      {{ ticket.game.date.split('T')[1]?.slice(0,5) }}
                    </span>
                    <span>현재 응모 인원 : {{ ticket.waitNumber }}명</span>
                  </div>
                  <div class="ticket-detail-horizontal-row-centered">
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title" style="display: flex; align-items: center; gap: 4px; font-size: 20px">
                        <img src="/icon/stadium.svg" alt="경기장" style="width: 20px; height: 20px" />
                        경기장
                      </div>
                      <div class="block-content">{{ getStadiumName(ticket.homeKor || ticket.game?.home) }}</div>
                    </div>
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title" style="display: flex; align-items: center; gap: 4px; font-size: 20px">
                        <img src="/icon/chair.svg" alt="좌석" style="width: 20px; height: 20px;" />
                        좌석
                      </div>
                      <div class="block-content">{{ ticket.seat }}</div>
                    </div>
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title" style="display: flex; align-items: center; gap: 4px; font-size: 20px">
                        <img src="/icon/credit_card.svg" alt="가격" style="width: 20px; height: 20px;" />
                        가격
                      </div>
                      <div class="block-content">{{ ticket.price.toLocaleString() }}</div>
                    </div>
                  </div>
                  <div class="ticket-detail-transfer-btn-row">
                    <button 
                      class="ticket-detail-transfer-btn" 
                      :style="{ 
                        backgroundColor: lightenHexColor(getTeamColor(ticket.homeKor || ticket.game?.home), 40),
                        border: `2px solid ${getTeamColor(ticket.homeKor || ticket.game?.home)}` 
                      }"
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

    <!-- 경고 모달(응모 인원 0명일 때) -->
    <div v-if="showWarningModal" class="warning-modal-overlay" @click="closeWarningModal">
      <div class="warning-modal-container" @click.stop>
        <div class="warning-modal-content">
          <div class="warning-modal-icon">
            <img src="/icon/warning.svg" alt="경고" style="width: 48px; height: 48px;">
          </div>
          <div class="warning-modal-title">양도 신청 불가</div>
          <div class="warning-modal-message">
            현재 응모 인원이 0명이라<br>
            양도 신청을 할 수 없습니다
          </div>
          <button class="warning-modal-confirm-btn" @click="closeWarningModal">
            확인
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { driver } from 'driver.js';
import { useTutorial } from '@/views/tutorial/useTutorial';
import { teamColortoEnum } from '@/utils/teamColor.js';
// 화면 렌더에 필요한 기본 변수 선언 (없으면 추가)


// 모든 텍스트 변수 한 곳에서 관리
const selectedTicket = ref(null);
const showDetailPage = ref(false);
const showCompletePage = ref(false);
const pageText = {
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
const isApplying = ref(false); // 응모 진행중 상태
const showWarningModal = ref(false); // 경고 모달 표시 상태

// 상태 변화 시 페이지 상단으로 부드럽게 스크롤 (튜토리얼 스크롤 고정과는 무관)
function scrollTopIfUnlocked() {
  try {
    const bodyFixed = getComputedStyle(document.body).position === 'fixed';
    if (!bodyFixed) window.scrollTo({ top: 0, behavior: 'smooth' });
  } catch (_) {
    window.scrollTo(0, 0);
  }
}

watch(showDetailPage, async (v) => { if (v) { await nextTick(); scrollTopIfUnlocked(); } });
watch(showCompletePage, async (v) => { if (v) { await nextTick(); scrollTopIfUnlocked(); } });
watch(showTickets, async (v) => { if (v) { await nextTick(); scrollTopIfUnlocked(); } });
const allowTransferTutorial = ref(false); // 메인 튜토리얼 플로우에서만 하위 튜토리얼 허용
const allowConfirmTutorial = ref(false); // 튜토리얼 진입 시에만 양도 확인 튜토리얼 허용



import axios from 'axios';
import { API_CONFIG } from '@/config/api.config.js';
const { TICKET } = API_CONFIG;
import { getEnumTeamName, teamNameToEnum } from '@/utils/teamNameMap.js';
import { stadiumNameToEnum } from '@/utils/teamStadium.js';
import http from '@/utils/http'
import { useTeamThemeStore } from '@/stores/teamTheme.js'

const user = JSON.parse(localStorage.getItem('user'))

// 팀 테마 스토어를 가져옵니다.
const themeStore = useTeamThemeStore

// 사용자 정보에 myTeam 값이 있으면 해당 팀으로 테마를 설정합니다.
// 이 코드는 컴포넌트가 생성될 때마다 실행되어 현재 사용자의 팀 테마를 적용합니다.
if (user?.myTeam) {
  themeStore.setSelectedTeam(user.myTeam)
}

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
  if (!teamName) return '';
  
  // 팀명을 소문자로 변환하고 공백 제거하여 정규화
  const normalizedTeam = teamName.toLowerCase().replace(/\s+/g, '');
  
  const teamLogoNames = {
    'kia타이거즈': 'KIA TIGERS',
    'kia': 'KIA TIGERS',
    '삼성라이온즈': 'SAMSUNG LIONS',
    '삼성': 'SAMSUNG LIONS',
    'lg트윈스': 'LG TWINS',
    'lg': 'LG TWINS',
    '두산베어스': 'DOOSAN BEARS',
    '두산': 'DOOSAN BEARS',
    'kt위즈': 'KT WIZ',
    'kt': 'KT WIZ',
    'ssg랜더스': 'SSG LANDERS',
    'ssg': 'SSG LANDERS',
    '롯데자이언츠': 'LOTTE GIANTS',
    '롯데': 'LOTTE GIANTS',
    '한화이글스': 'HANWHA EAGLES',
    '한화': 'HANWHA EAGLES',
    'nc다이노스': 'NC DINOS',
    'nc': 'NC DINOS',
    '키움히어로즈': 'KIWOOM HEROES',
    '키움': 'KIWOOM HEROES'
  };
  
  // 정규화된 팀명으로 먼저 찾기
  if (teamLogoNames[normalizedTeam]) {
    return teamLogoNames[normalizedTeam];
  }
  
  // 부분 매칭으로 찾기
  for (const [key, value] of Object.entries(teamLogoNames)) {
    if (normalizedTeam.includes(key.toLowerCase()) || key.toLowerCase().includes(normalizedTeam)) {
      return value;
    }
  }
  
  // 매칭되지 않으면 영어로 변환 시도
  if (normalizedTeam.includes('lg')) return 'LG TWINS';
  if (normalizedTeam.includes('삼성')) return 'SAMSUNG LIONS';
  if (normalizedTeam.includes('kia')) return 'KIA TIGERS';
  if (normalizedTeam.includes('두산')) return 'DOOSAN BEARS';
  if (normalizedTeam.includes('kt')) return 'KT WIZ';
  if (normalizedTeam.includes('ssg')) return 'SSG LANDERS';
  if (normalizedTeam.includes('롯데')) return 'LOTTE GIANTS';
  if (normalizedTeam.includes('한화')) return 'HANWHA EAGLES';
  if (normalizedTeam.includes('nc')) return 'NC DINOS';
  if (normalizedTeam.includes('키움')) return 'KIWOOM HEROES';
  
  return teamName; // 매칭되지 않으면 원본 반환
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

// 팀 로고 이미지 경로를 가져오는 함수 (getTeamBackgroundImage와 동일한 방식 사용)
const getTeamLogo = (teamName) => {
  const normalizedTeam = normalizeTeamName(teamName || '').toLowerCase()
  // 디버그: 팀명 로그 출력
  
  // 팀별 로고 매핑 - public 폴더의 로고 파일 사용 (소문자로 통일)
  const teamLogoMap = {
    'kia타이거즈': '/logo/kia.svg',
    '삼성라이온즈': '/logo/samsung.svg',
    'lg트윈스': '/logo/LG.svg',
    '두산베어스': '/logo/DOOSAN.svg',
    'kt위즈': '/logo/KT.svg',
    'ssg랜더스': '/logo/SSG.svg',
    '롯데자이언츠': '/logo/LOTTE.svg',
    '한화이글스': '/logo/HanWha.svg',
    'nc다이노스': '/logo/NC.svg',
    '키움히어로즈': '/logo/KIWOOM.svg'
  }
  
  const logoPath = teamLogoMap[normalizedTeam]
  console.log(logoPath);
  return logoPath || null
}

// 팀별 CSS 클래스명 생성 함수
const getTeamClass = (ticket) => {
  const homeTeam = normalizeTeamName(ticket.homeKor || ticket.game?.home || '')
  // teamColortoEnum의 키와 일치하는 팀명 사용
  return homeTeam ? `team-${homeTeam}` : 'team-default'
}

// 팀별 색상 매핑
const lightenHexColor = (hex, amount) => {
  hex = hex.replace('#', '');
  let r = parseInt(hex.substring(0, 2), 16);
  let g = parseInt(hex.substring(2, 4), 16);
  let b = parseInt(hex.substring(4, 6), 16);

  r = Math.min(255, r + amount);
  g = Math.min(255, g + amount);
  b = Math.min(255, b + amount);

  const toHex = c => ('0' + c.toString(16)).slice(-2);
  return `#${toHex(r)}${toHex(g)}${toHex(b)}`;
}

// 팀별 색상 매핑
const getTeamColor = (teamName) => {
  const normalizedTeam = normalizeTeamName(teamName || '')
  // teamColortoEnum의 색상 값에 # 추가
  const colorCode = teamColortoEnum[normalizedTeam]
  
  return colorCode ? `#${colorCode}` : '#395b8c'
}

// 티켓 디테일 스타일 생성 함수
const getTicketDetailStyle = (ticket) => {
  const homeTeam = ticket.homeKor || ticket.game?.home || ''
  const teamColor = getTeamColor(homeTeam)
  
  return {
    '--team-color': teamColor,
    backgroundColor: teamColor,
    color: '#fff'
  }
}

// 티켓 스타일 생성 함수 (기본 스타일만)
const getTicketStyle = (ticket) => {
  const logoPath = teamLogoMap[normalizeTeamName(ticket.homeKor || ticket.game?.home || '')]
  const teamColor = getTeamColor(ticket.homeKor || ticket.game?.home || '')
  
  let style = {
    transition: 'all 0.3s ease',
    position: 'relative',
    backgroundColor: teamColor // 모든 팀에 대해 배경색 적용
  }
  
  // 호버 상태일 때 로고 배경 추가
  if (hoveredTicket.value === ticket.ticketId && logoPath) {
    style.backgroundImage = `url('@/assets/logo/${logoPath}')`
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

  // 이전 튜토리얼 닫기
  const driverObj = driver();
  if(driverObj){
    driverObj.destroy();
  }

  showTickets.value = true;
  activeTab.value = platform === 'NOL' ? 'NOL' : '티켓링크';
  ticketLoading.value = true;
  ticketError.value = '';
  try {
    const res = await http.get(`tickets/platform/${platform}`);
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
    
    // 메인 튜토리얼에서 진입했을 때에만 티켓 선택 하위 튜토리얼 자동 시작
    if (allowTransferTutorial.value && tickets.value.length > 0) {
      setTimeout(() => {
        selectTicketTutorial();
      }, 500);
      // 중복 트리거 방지
      allowTransferTutorial.value = false;
    }
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
const route = useRoute();
const { startTransferTutorial, selectTicketTutorial, transferConfirmTutorial, closeTutorial } = useTutorial();

// 튜토리얼 닫기 함수는 이제 useTutorial에서 가져옵니다.

async function handleApply(ticket) {
  // 1. 진행 중인 튜토리얼이 있다면 닫기
  closeTutorial();
  
  // 2. 튜토리얼이 완전히 닫히도록 잠시 대기
  await new Promise(resolve => setTimeout(resolve, 100));
  
  // 3. 응모 인원이 0명인 경우
  if (ticket.waitNumber === 0) {
    showWarningModal.value = true;
    return;
  }
  
  selectedTicket.value = ticket;
  showDetailPage.value = true;
  
  // 4. 상세 페이지 표시 후 (튜토리얼 진입한 경우에만) 양도 확인 튜토리얼 시작
  if (allowConfirmTutorial.value) {
    setTimeout(() => {
      transferConfirmTutorial();
    }, 500); // DOM 렌더링 완료 대기
    // 중복 실행 방지
    allowConfirmTutorial.value = false;
  }
}

async function handleApplyComplete() {
  try {
    // 1. 진행 중인 튜토리얼이 있다면 닫기
    closeTutorial();
    
    // 2. 응모 진행중 상태로 변경
    isApplying.value = true;
    
    // 양도 API 호출
    const response = await http.get(`tickets/transfer/${selectedTicket.value.ticketId}`);
    
    console.log('양도 API 응답:', response.data);
    
    if (response.data.success) {
      // 성공 시 완료 페이지로 전환
      isApplying.value = false;
      showCompletePage.value = true;
      console.log('양도 성공:', response.data.message);
    } else {
      // 실패 시 에러 메시지 표시
      isApplying.value = false;
      alert('양도 신청에 실패했습니다.');
    }
  } catch (error) {
    console.error('양도 API 오류:', error);
    isApplying.value = false;
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
  // 완료 → NOL/티켓링크 바로가기 버튼이 있는 초기 화면으로 이동
  showCompletePage.value = false;
  showDetailPage.value = false;
  showTickets.value = false;
  selectedTicket.value = null;
  // 티켓 데이터 초기화
  tickets.value = [];
  activeTab.value = 'NOL';
}

// 경고 모달 닫기 함수(응모 인원 0명일 때)
function closeWarningModal() {
  showWarningModal.value = false;
  
  // 1. 티켓 선택 모달 다시 열기
  showTickets.value = true;
  
  // 2. 메인 튜토리얼 플로우에서 온 경우에만 하위 튜토리얼 재시작
  if (allowTransferTutorial.value) {
    nextTick(() => {
      setTimeout(() => {
        selectTicketTutorial(1);
      }, 100);
    });
  }
};

// 튜토리얼 관련 로직
onMounted(() => {
  // URL 파라미터에서 tutorial=true 확인
  const isTutorialMode = route.query.tutorial === 'true';
  
  // localStorage에서 양도 튜토리얼 플래그 확인
  const showTransferTutorialFlag = localStorage.getItem('showTransferTutorial') === 'true';
  
  // 메인 페이지 튜토리얼에서 명시적으로 진입한 경우에만 하위 튜토리얼 허용
  allowTransferTutorial.value = isTutorialMode || showTransferTutorialFlag;
  // 같은 조건으로 이번 방문에서만 확인 튜토리얼 허용
  allowConfirmTutorial.value = allowTransferTutorial.value;

  if (allowTransferTutorial.value) {
    console.log('양도 페이지 튜토리얼 조건 충족:', { isTutorialMode, showTransferTutorialFlag });
    // 양도 튜토리얼 플래그 제거 (한 번만 실행되도록)
    if (showTransferTutorialFlag) {
      localStorage.removeItem('showTransferTutorial');
    }
    // 상위(인트로) 단계 시작
    startTransferTutorial();
  }
});

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
  width: 1000px; /* 스크롤바까지 포함하여 너비 확장 */
  max-width: 95vw;
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
  width: 280px;
  height: 150px;
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
  background: var(--theme-gradient);
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
  background: var(--theme-gradient);
}
.ticketlink-btn {
  background: var(--theme-gradient);
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
  background: var(--theme-primary);
  color: #fff;
  box-shadow: 0 2px 8px 0 #ce0e2d22;
}
.ticket-tab:focus{
  outline: none;
  box-shadow: none;
}
.ticket-list {
  width: 1000px;
  display: flex;
  flex-direction: column;
  gap: 18px;
  align-items: center;
}
.ticket-card {
  width: 900px;
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
  overflow: hidden; /* ticket-detail이 카드 내부에 포함되도록 */
  height: 130px;
  min-height: 130px;
  max-height: 400px;
}

/* 팀별 색상 클래스 */
.ticket-card.team-KIA타이거즈 {
  background-color: #EA0029 !important;
}

.ticket-card.team-삼성라이온즈 {
  background-color: #074CA1 !important;
}

.ticket-card.team-LG트윈스 {
  background-color: #C30452 !important;
}

.ticket-card.team-두산베어스 {
  background-color: #1A1748 !important;
}

.ticket-card.team-KT위즈 {
  background-color: #000000 !important;
}

.ticket-card.team-SSG랜더스 {
  background-color: #CE0E2D !important;
}

.ticket-card.team-롯데자이언츠 {
  background-color: #041E42 !important;
}

.ticket-card.team-한화이글스 {
  background-color: #FC4E00 !important;
}

.ticket-card.team-NC다이노스 {
  background-color: #315288 !important;
}

.ticket-card.team-키움히어로즈 {
  background-color: #570514 !important;
}

.ticket-card.team-default {
  background-color: #395b8c !important;
}

/* hover 시 팀 색상 유지 및 그림자 효과 */
.ticket-card.highlighted.team-KIA타이거즈 {
  box-shadow: 0 12px 48px 0 #EA002955 !important;
}

.ticket-card.highlighted.team-삼성라이온즈 {
  box-shadow: 0 12px 48px 0 #074CA155 !important;
}

.ticket-card.highlighted.team-LG트윈스 {
  box-shadow: 0 12px 48px 0 #C3045255 !important;
}

.ticket-card.highlighted.team-두산베어스 {
  box-shadow: 0 12px 48px 0 #1A174855 !important;
}

.ticket-card.highlighted.team-KT위즈 {
  box-shadow: 0 12px 48px 0 #00000055 !important;
}

.ticket-card.highlighted.team-SSG랜더스 {
  box-shadow: 0 12px 48px 0 #CE0E2D55 !important;
}

.ticket-card.highlighted.team-롯데자이언츠 {
  box-shadow: 0 12px 48px 0 #041E4255 !important;
}

.ticket-card.highlighted.team-한화이글스 {
  box-shadow: 0 12px 48px 0 #FC4E0055 !important;
}

.ticket-card.highlighted.team-NC다이노스 {
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
  font-size: 25px;
  font-weight: 700;
}
.ticket-card .ticket-info-row {
  font-size: 20px;
  font-weight: 400;
  margin-top: 20px;
  display: flex;
  gap: 50px;
}
.ticket-card .landers {
  color: #fff;
  font-family: 'Pretendard', 'Montserrat', sans-serif;
  font-size: 28px;
  letter-spacing: 1px;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
  font-size: 15px;
  color: #333;
  line-height: 1.5;
}

.ticket-info-row span {
  display: inline-flex;
  align-items: flex-end; /* Changed from center to flex-end to align to bottom */
  gap: 4px;
  height: 100%;
}

.ticket-info-row img {
  width: 25px; /* Match the inline style width */
  height: 25px; /* Match the inline style height */
  margin-right: 4px;
  vertical-align: bottom; /* Align to bottom */
  position: relative;
  top: 0; /* Reset any vertical offset */
}

.ticket-card .ticket-people {
  font-size: 18px;
  color: #fff;
  font-weight: 500;
  white-space: nowrap;
  right: 0;
}
.ticket-card .ticket-detail {
  position: absolute;
  left: -5px; right: -5px; top: -5px; bottom: -5px; /* 카드를 완전히 덮도록 훨씬 확장 */
  background: rgba(206,14,45,0.91);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-start;
  padding: 24px 32px 18px 32px;
  z-index: 100;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  animation: fadeIn 0.3s;
  overflow: hidden;
  margin: 0;
  box-shadow: none; /* box-shadow 제거로 경계 명확히 */
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
  font-size: 20px;
  font-weight: 400;
  color: #fff !important;
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
  position: relative;
  z-index: 10;
}
.ticket-detail-block.block-horizontal .block-content {
  font-size: 18px;
  font-weight: 400;
  color: #fff;
  margin-left: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  position: relative;
  z-index: 10;
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

/* 응모 진행중 상태 스타일 */
.ticket-detail-being-assignment {
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  justify-content: center !important;
  min-height: 160px;
  padding: 20px;
}

.ticket-detail-being-assignment-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  gap: 16px;
}

.being-assignment-icon {
  font-size: 40px;
  opacity: 0.9;
  animation: pulse 2s infinite;
}

.being-assignment-text {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  text-shadow: 0 2px 6px rgba(0,0,0,0.4);
  letter-spacing: -0.5px;
  line-height: 1.2;
}

@keyframes pulse {
  0%, 100% { opacity: 0.9; }
  50% { opacity: 0.6; }
}

.ticket-detail-bg {
  position: absolute;
  left: 0; right: 0; top: 0; bottom: 0;
  opacity: 0.06;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none; /* 배경이 클릭을 방해하지 않도록 */
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
  box-shadow: 0 4px 32px 0 #0001;
  overflow: hidden;
  margin-top: 10px;
  margin-bottom: 40px;
}
.detail-header {
  color: #fff;
  padding: 32px 32px 32px 32px;
  position: relative;
  min-height: 320px;
}
.detail-header-info-row {
  display: flex;
  flex-direction: column;
  margin-top: 30px;
  margin-bottom: 0px;
  z-index: 3;
  color: #fff;
  font-weight: 500;
}
.detail-header-info-line {
  display: flex;
  justify-content: center;
  gap: 50px;
}

.detail-header-info-col {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  font-size: 20px;
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
  font-family: 'Allura', 'Alex Brush', 'Satisfy', cursive;
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
  line-height: 1.6;
  letter-spacing: 1px;
  color: #fff;
  text-shadow: 0 2px 16px #b1002b55;
}
.detail-header-bg {
  position: absolute;
  left: 0; right: 0; top: 0; bottom: 0;
  opacity: 0.1;
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
.detail-price-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 0 32px 0 32px;
  margin: 6px 0;
  width: auto;
  margin-left: auto;
  gap: 8px;
  z-index: 3;
}
.detail-price {
  margin-top: 10px;
  margin-bottom: 10px;
  font-size: 22px;
  font-weight: 800;
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
.ticket-card .ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 40px; /* Fixed height for consistent vertical centering */
  position: relative;
}

.ticket-card .ticket-title {
  font-size: 25px;
  font-weight: 700;
  display: flex;
  align-items: center;
  height: 100%;
  margin: 0;
  padding: 0;
}

.ticket-card .ticket-people {
  font-size: 18px;
  color: #fff;
  font-weight: 500;
  white-space: nowrap;
  position: absolute;
  right: 0;
  top: 50%;
  transform: translateY(-50%);
  margin: 0;
  padding: 0;
}
.detail-seat-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  /* gap: 12px; */
  /* margin-bottom: 18px; */
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
  background: var(--theme-primary);
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
  color: var(--theme-primary);
  border: 1.5px solid var(--theme-primary);
  border-radius: 8px;
  padding: 10px 32px;
  font-size: 17px;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.18s, color 0.18s;
}
.apply-yes:hover {
  background: var(--theme-primary);
}
.apply-no:hover {
  background: var(--theme-primary);
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
  color: var(--theme-primary);
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
  background: var(--theme-primary);
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
  background: var(--theme-primary);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 10px 30px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
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
  color: var(--theme-gradient);
  border: 1.5px solid var(--theme-gradient);
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

/* 경고 모달 스타일 */
.warning-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.warning-modal-container {
  background: #fff;
  border-radius: 12px;
  padding: 0;
  max-width: 400px;
  width: 90%;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.warning-modal-content {
  text-align: center;
  padding: 40px 30px 30px 30px;
}

/* .warning-modal-icon {
  font-size: 40px;
  margin-bottom: 10px;
} */

/* Icon styles */
.info-icon-img,
.ticket-info-row img,
.block-title img,
.being-assignment-icon img,
.detail-header-info-col img,
.detail-header-info-line .info-icon img,
.detail-header-info-line img[src*='.svg'],
.ticket-detail-info-row img {
  height: 20px;
  width: 20px;
  filter: brightness(0) invert(1);
  vertical-align: middle;
}

/* Ensure all icons in detail header are white */
.detail-header img[src*='.svg'],
.detail-header .info-icon img,
/* .ticket-detail-info-row img { 
  filter: brightness(0) invert(1) !important;
} */

.warning-modal-title {
  font-size: 30px;
  font-weight: 700;
  color: #ce0e2d;
  margin: 15px auto;
}

.warning-modal-message {
  font-size: 16px;
  color: #898888;
  line-height: 1.6;
  margin-bottom: 30px;
}

.warning-modal-confirm-btn {
  background: var(--theme-primary);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 12px 40px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.warning-modal-confirm-btn:hover {
  background-color: #b50c29;
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
  margin-top: 5px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  position: relative;
}

.ticket-detail-transfer-btn {
  border: 2px solid var(--theme-gradient);
  border-radius: 8px;
  font-size: 25px;
  font-weight: bold;
  width: 180px;
  height: 80px;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  /* 배경색은 인라인 스타일로 홈팀 색깔 동적 설정 */
  margin-left: auto;
  flex-shrink: 0;
  z-index: 200; /* 매우 높은 z-index로 설정하여 클릭 가능하도록 */
  position: relative;
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

/* 응모 진행중 상태 스타일 */
.applying-status {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 99999; /* 더 높은 z-index로 설정 */
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 15px 0;
  text-align: center;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  animation: slideDown 0.3s ease-out;
}

.applying-message {
  color: white;
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

@keyframes slideDown {
  from {
    transform: translateY(-100%);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* 응모 진행중일 때 메인 컨텐츠에 여백 추가 */
.transfer-root:has(.applying-status) .transfer-main {
  margin-top: 60px;
}

/* 주의사항 스타일 - 강력한 선택자 사용 */
.complete-root .detail-notice-box {
  margin-top: 20px !important;
  padding: 20px !important;
  background-color: #f8f9fa !important;
  border-radius: 8px !important;
  border: 1px solid #e9ecef !important;
  display: block !important;
  visibility: visible !important;
}

.complete-root .detail-notice-box .notice-title {
  font-size: 18px !important;
  font-weight: 700 !important;
  color: #ce0e2d !important;
  margin-bottom: 15px !important;
  display: block !important;
}

.complete-root .detail-notice-box .notice-list {
  list-style: none !important;
  padding: 0 !important;
  margin: 0 !important;
  display: block !important;
}

.complete-root .detail-notice-box .notice-list li {
  display: block !important;
  margin-bottom: 8px !important;
  font-size: 14px !important;
  color: #333 !important;
  line-height: 1.5 !important;
  padding-left: 15px !important;
  position: relative !important;
}

.complete-root .detail-notice-box .notice-list li:before {
  content: '•' !important;
  color: #ce0e2d !important;
  font-weight: bold !important;
  position: absolute !important;
  left: 0 !important;
  top: 0 !important;
}

/* Responsive design */
@media (max-width: 768px) {
  .transfer-root {
    padding: 0 16px;
  }
  
  .transfer-main {
    padding: 0 16px;
  }
  
  .main-center-card {
    width: 900px;
    margin: 24px 0 32px 0;
    min-height: 300px;
  }
  
  .card-inner {
    width: 100%;
    height: auto;
    min-height: 400px;
    padding: 32px 20px;
  }
  
  .card-title {
    font-size: 28px;
    margin-bottom: 6px;
  }
  
  .card-desc {
    font-size: 16px;
    margin-top: 16px;
    margin-bottom: 24px;
  }
  
  .provider-row {
    flex-direction: column;
    gap: 24px;
    width: 100%;
    margin-top: 8px;
  }
  
  .provider-col {
    width: 100%;
  }
  
  .provider-img {
    margin-bottom: 12px;
  }
  
  .nol-img,
  .ticketlink-img {
    width: 100%;
    max-width: 280px;
    height: 120px;
  }
  
  .provider-btn {
    width: 100%;
    max-width: 250px;
    height: 48px;
    font-size: 18px;
    gap: 6px;
  }
  
  .btn-icon {
    font-size: 14px;
  }
  
  /* Ticket list responsive */
  .ticket-tab-row {
    gap: 12px;
    margin-top: 16px;
    margin-bottom: 16px;
  }
  
  .ticket-tab {
    padding: 6px 24px;
    font-size: 14px;
  }
  
  .ticket-list {
    padding: 0 16px !important;
    max-height: 500px !important;
  }
  
  .ticket-card {
    width: 100%;
    max-width: 100%;
    padding: 20px 24px 16px 24px;
    height: auto;
    min-height: 120px;
    max-height: 350px;
  }
  
  .ticket-card .ticket-title {
    font-size: 20px;
  }
  
  .ticket-card .ticket-info-row {
    font-size: 16px;
    margin-top: 16px;
    gap: 12px;
    flex-wrap: wrap;
  }
  
  .ticket-card .ticket-people {
    font-size: 16px;
  }
  
  .ticket-card .ticket-detail {
    padding: 20px 24px 16px 24px;
    font-size: 16px;
  }
  
  .ticket-detail-title {
    font-size: 24px;
    margin-bottom: 8px;
    margin-top: 2px;
  }
  
  .ticket-detail-info-row {
    gap: 12px;
    font-size: 16px;
    margin-bottom: 16px;
    flex-wrap: wrap;
  }
  
  .ticket-detail-horizontal-row-centered {
    gap: 12px;
    flex-wrap: wrap;
  }
  
  .ticket-detail-block.block-horizontal {
    padding: 12px 10px 10px 12px;
    min-width: 120px;
  }
  
  .ticket-detail-block.block-horizontal .block-title {
    font-size: 15px;
    margin-bottom: 6px;
  }
  
  .ticket-detail-block.block-horizontal .block-content {
    font-size: 14px;
  }
  
  .ticket-detail-block.block-horizontal .block-icon {
    font-size: 16px;
  }
  
  .ticket-detail-transfer-btn {
    width: 100%;
    max-width: 180px;
    height: 60px;
    font-size: 18px;
  }
  
  /* Detail page responsive */
  .detail-page-wrap {
    padding: 20px 0 32px 0;
  }
  
  .detail-card {
    width: 100%;
    max-width: 400px;
    margin-top: 8px;
    margin-bottom: 24px;
  }
  
  .detail-header {
    padding: 24px 20px 40px 20px;
    min-height: 280px;
  }
  
  .detail-header-logo {
    font-size: 22px;
    left: 20px;
    top: 16px;
  }
  
  .detail-header-ticketid {
    right: 20px;
    top: 16px;
    font-size: 12px;
  }
  
  .ticketid-value {
    font-size: 13px;
  }
  
  .detail-match-title {
    margin-top: 32px;
  }
  
  .main-title {
    font-size: 26px;
  }
  
  .detail-header-info-row {
    left: 20px;
    bottom: 20px;
    font-size: 16px;
  }
  
  .detail-header-info-line {
    gap: 60px;
    width: 100%;
    max-width: 360px;
  }
  
  .detail-header-info-col {
    font-size: 14px;
    min-width: 150px;
    margin-left: 0;
  }
  
  .info-icon {
    font-size: 16px;
  }
  
  .detail-price-row {
    padding: 0 20px;
  }
  
  .detail-price {
    font-size: 20px;
  }
  
  .detail-seat-row {
    padding: 0 20px;
    gap: 8px;
    margin-bottom: 16px;
  }
  
  .detail-seat-block {
    padding: 12px 8px 8px 8px;
    max-width: 100px;
  }
  
  .seat-title {
    font-size: 12px;
    margin-bottom: 3px;
  }
  
  .seat-value {
    font-size: 14px;
  }
  
  .detail-apply-box {
    margin: 0 20px 16px 20px;
    padding: 18px 0 16px 0;
  }
  
  .apply-title {
    font-size: 16px;
    margin-bottom: 12px;
  }
  
  .apply-btns {
    gap: 12px;
  }
  
  .apply-yes,
  .apply-no {
    padding: 8px 24px;
    font-size: 15px;
  }
  
  .detail-notice-box {
    margin: 0 20px 20px 20px;
    padding: 16px;
  }
  
  .notice-title {
    font-size: 14px;
    margin-bottom: 12px;
  }
  
  .notice-content {
    font-size: 13px;
  }
  
  /* Complete page responsive */
  .complete-root {
    padding: 24px 0;
    height: auto;
    min-height: 600px;
  }
  
  .complete-card {
    width: 100%;
    max-width: 400px;
    padding: 24px 20px 20px 20px;
    margin-bottom: 16px;
  }
  
  .match-info {
    padding: 16px 0 10px 0;
    margin-bottom: 24px;
  }
  
  .match-title {
    font-size: 22px;
  }
  
  .match-detail {
    font-size: 14px;
    margin-top: 4px;
    gap: 12px;
  }
  
  .complete-message {
    margin: 28px 0 12px 0;
  }
  
  .main-message {
    font-size: 1.6rem;
    margin-bottom: 8px;
  }
  
  .sub-message {
    font-size: 0.9rem;
    margin-bottom: 16px;
  }
  
  .confirm-btn {
    padding: 8px 24px;
    font-size: 1rem;
    margin-top: 8px;
  }
  
  /* Warning modal responsive */
  .warning-modal-container {
    max-width: 320px;
    width: 95%;
  }
  
  .warning-modal-content {
    padding: 32px 24px 24px 24px;
  }
  
  .warning-modal-icon {
    font-size: 40px;
    margin-bottom: 16px;
  }
  
  .warning-modal-title {
    font-size: 18px;
    margin-bottom: 12px;
  }
  
  .warning-modal-message {
    font-size: 14px;
    margin-bottom: 24px;
  }
  
  .warning-modal-confirm-btn {
    padding: 10px 32px;
    font-size: 15px;
  }
  
  /* Applying status responsive */
  .applying-message {
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .transfer-root {
    padding: 0 12px;
  }
  
  .transfer-main {
    padding: 0 12px;
  }
  
  .card-inner {
    padding: 24px 16px;
    min-height: 350px;
  }
  
  .card-title {
    font-size: 24px;
  }
  
  .card-desc {
    font-size: 15px;
    margin-top: 12px;
    margin-bottom: 20px;
  }
  
  .provider-row {
    gap: 20px;
  }
  
  .nol-img,
  .ticketlink-img {
    height: 100px;
  }
  
  .provider-btn {
    height: 44px;
    font-size: 16px;
  }
  
  .ticket-tab {
    padding: 5px 20px;
    font-size: 13px;
  }
  
  .ticket-list {
    padding: 0 12px !important;
  }
  
  .ticket-card {
    padding: 16px 20px 14px 20px;
    min-height: 110px;
  }
  
  .ticket-card .ticket-title {
    font-size: 18px;
  }
  
  .ticket-card .ticket-info-row {
    font-size: 14px;
    margin-top: 12px;
    gap: 8px;
  }
  
  .ticket-card .ticket-people {
    font-size: 14px;
  }
  
  .ticket-card .ticket-detail {
    padding: 16px 20px 14px 20px;
    font-size: 14px;
  }
  
  .ticket-detail-title {
    font-size: 20px;
  }
  
  .ticket-detail-info-row {
    font-size: 14px;
    gap: 8px;
  }
  
  .ticket-detail-block.block-horizontal {
    padding: 10px 8px 8px 10px;
    min-width: 100px;
  }
  
  .ticket-detail-block.block-horizontal .block-title {
    font-size: 13px;
  }
  
  .ticket-detail-block.block-horizontal .block-content {
    font-size: 12px;
  }
  
  .ticket-detail-transfer-btn {
    max-width: 160px;
    height: 50px;
    font-size: 16px;
  }
  
  .detail-card {
    max-width: 320px;
  }
  
  .detail-header {
    padding: 20px 16px 32px 16px;
    min-height: 240px;
  }
  
  .detail-header-logo {
    font-size: 20px;
    left: 16px;
    top: 12px;
  }
  
  .detail-header-ticketid {
    right: 16px;
    top: 12px;
    font-size: 11px;
  }
  
  .main-title {
    font-size: 22px;
  }
  
  .detail-header-info-line {
    gap: 40px;
    max-width: 280px;
  }
  
  .detail-header-info-col {
    font-size: 13px;
    min-width: 120px;
  }
  
  .detail-seat-block {
    padding: 10px 6px 6px 6px;
    max-width: 80px;
  }
  
  .seat-title {
    font-size: 11px;
  }
  
  .seat-value {
    font-size: 12px;
  }
  
  .detail-apply-box {
    margin: 0 16px 12px 16px;
    padding: 16px 0 14px 0;
  }
  
  .apply-title {
    font-size: 15px;
  }
  
  .apply-yes,
  .apply-no {
    padding: 6px 20px;
    font-size: 14px;
  }
  
  .detail-notice-box {
    margin: 0 16px 16px 16px;
    padding: 12px;
  }
  
  .notice-title {
    font-size: 13px;
  }
  
  .notice-content {
    font-size: 12px;
  }
  
  .complete-card {
    max-width: 320px;
    padding: 20px 16px 16px 16px;
  }
  
  .match-title {
    font-size: 20px;
  }
  
  .match-detail {
    font-size: 13px;
  }
  
  .main-message {
    font-size: 1.4rem;
  }
  
  .sub-message {
    font-size: 0.85rem;
  }
  
  .warning-modal-container {
    max-width: 280px;
  }
  
  .warning-modal-content {
    padding: 24px 20px 20px 20px;
  }
  
  .warning-modal-icon {
    font-size: 36px;
  }
  
  .warning-modal-title {
    font-size: 16px;
  }
  
  .warning-modal-message {
    font-size: 13px;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .main-center-card {
    width: 90%;
    max-width: 700px;
  }
  
  .card-inner {
    width: 100%;
    height: auto;
    min-height: 450px;
    padding: 40px 28px;
  }
  
  .provider-row {
    gap: 36px;
  }
  
  .nol-img,
  .ticketlink-img {
    width: 240px;
    height: 130px;
  }
  
  .provider-btn {
    width: 220px;
    height: 50px;
    font-size: 22px;
  }
  
  .ticket-card {
    width: 580px;
    padding: 20px 28px 16px 28px;
  }
  
  .detail-card {
    width: 500px;
  }
  
  .complete-card {
    width: 500px;
  }

  :deep(button:focus) {
    outline: none !important;
    box-shadow: none !important;
  }
}
</style>
