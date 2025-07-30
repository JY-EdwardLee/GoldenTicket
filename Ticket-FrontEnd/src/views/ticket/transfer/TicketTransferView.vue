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
                <span class="ssg">{{ pageText.homeTeam }}</span> vs <span class="kiwoom">{{ pageText.awayTeam }}</span>
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
          <div class="detail-header">
            <div class="detail-header-logo">{{ pageText.detailHeaderLogo }}</div>
            <div class="detail-header-ticketid">TICKET ID<br /><span class="ticketid-value">#{{ selectedTicket.ticketId }}</span></div>
            <div class="detail-match-title">
              <div class="main-title">
                <span class="main-title-landers">{{ pageText.mainTitleLanders }}</span><br />
                <span class="main-title-vs">VS</span><br />
                <span class="main-title-kiwoom">{{ pageText.mainTitleKiwoom }}</span>
              </div>
            </div>
            <div class="detail-header-bg"><img src="/landers_bg.png" alt="landers" /></div>
            <div class="detail-header-info-row">
              <div class="detail-header-info-line">
                <div class="detail-header-info-col"><span class="info-icon">📅</span>{{ selectedTicket.date }}</div>
                <div class="detail-header-info-col"><span class="info-icon">⏰</span>{{ selectedTicket.time }}</div>
              </div>
              <div class="detail-header-info-line">
                <div class="detail-header-info-col"><span class="info-icon">📍</span>{{ selectedTicket.stadium }}</div>
                <div class="detail-header-info-col"><span class="info-icon">🪑</span>{{ selectedTicket.provider }}</div>
              </div>
            </div>
          </div>
          <div class="detail-price-row">
            <span class="detail-price">₩{{ selectedTicket.price?.toLocaleString() }}</span>
            <span class="detail-gate">Gate <b>{{ selectedTicket.gate }}</b></span>
          </div>
          <div class="detail-seat-row">
            <div class="detail-seat-block">
              <div class="seat-title">Section</div>
              <div class="seat-value">{{ selectedTicket.section }}</div>
            </div>
            <div class="detail-seat-block">
              <div class="seat-title">Row</div>
              <div class="seat-value">{{ selectedTicket.row }}</div>
            </div>
            <div class="detail-seat-block">
              <div class="seat-title">Seat</div>
              <div class="seat-value">{{ selectedTicket.seat }}</div>
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
              <button class="provider-btn nol-btn" @click="showTickets = true">
                NOL 바로가기
                <span class="btn-icon">↗</span>
              </button>
            </div>
            <!-- Ticketlink -->
            <div class="provider-col">
              <img src="/ticketlink_logo.png" alt="티켓링크" class="provider-img ticketlink-img" />
              <button class="provider-btn ticketlink-btn">
                티켓링크 바로가기
                <span class="btn-icon">↗</span>
              </button>
            </div>
          </div>
        </div>
        <div v-else>
          <div class="ticket-tab-row">
            <button :class="['ticket-tab', activeTab === 'NOL' ? 'active' : '']" @click="activeTab = 'NOL'">NOL</button>
            <button :class="['ticket-tab', activeTab === '티켓링크' ? 'active' : '']" @click="activeTab = '티켓링크'">티켓링크</button>
          </div>
          <div class="ticket-list">
            <div
              v-for="(ticket, idx) in tickets"
              :key="ticket.id"
              class="ticket-card"
              :class="[{'highlighted': hoveredTicket === ticket.id}, { 'landers-hover': hoveredTicket === ticket.id && ticket.landers } ]"
              @mouseenter="hoveredTicket = ticket.id"
              @mouseleave="hoveredTicket = null"
            >
              <div class="ticket-header">
                <span class="ticket-title" :class="{ 'landers': ticket.landers }">{{ ticket.title }}</span>
                <span v-if="ticket.landers && hoveredTicket === ticket.id" class="ticket-people">현재 응모 인원 : 13명</span>
              </div>
              <div class="ticket-info-row">
                <span class="ticket-date">📅 {{ ticket.date }}</span>
                <span class="ticket-time">⏰ {{ ticket.time }}</span>
              </div>
              <!-- 상세 정보: hover 시에만 표시 -->
              <transition name="fade">
                <div v-if="hoveredTicket === ticket.id && ticket.landers" class="ticket-detail ticket-detail-horizontal">
                  <div class="ticket-detail-bg"><img src="/landers_bg.png" alt="landers" /></div>
                  <div class="ticket-detail-header-row">
                    <span class="ticket-detail-title">{{ ticket.title }}</span>
                  </div>
                  <div class="ticket-detail-info-row">
                    <span class="ticket-date">📅 {{ ticket.date }}</span>
                    <span class="ticket-time">⏰ {{ ticket.time }}</span>
                    <span class="ticket-people">현재 응모 인원 : 13명</span>
                  </div>
                  <div class="ticket-detail-horizontal-row-centered">
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title"><span class="block-icon">📍</span>경기장</div>
                      <div class="block-content">인천 SSG 랜더스 필드</div>
                    </div>
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title"><span class="block-icon">🪑</span>좌석 위치</div>
                      <div class="block-content">내야 지정석 A</div>
                    </div>
                    <div class="ticket-detail-block block-horizontal">
                      <div class="block-title"><span class="block-icon">🔒</span>티켓 가격</div>
                      <div class="block-content">13,000원</div>
                    </div>
                  </div>
                  <button class="apply-btn" @click="handleApply(ticket)">양도 신청하기 →</button>
                </div>
              </transition>
            </div>
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



const tickets = [
  {
    id: 1,
    title: 'NC 다이노스 vs 한화 이글스',
    date: '2025년 7월 23일',
    time: '오후 6시 30분',
    landers: false,
  },
  {
    id: 2,
    title: '롯데 자이언츠 vs KIA 타이거즈',
    date: '2025년 7월 23일',
    time: '오후 6시 30분',
    landers: false,
  },
  {
    id: 3,
    title: 'LANDERS vs 키움 히어로즈',
    date: '2025년 7월 23일',
    time: '오후 6시 30분',
    landers: true,
    price: 14000,
    section: '내야 1루석',
    row: 'A',
    seat: '15-16',
    gate: 'MAIN GATE',
    provider: '네이버 1루석',
    ticketId: 'T240724001',
    stadium: '인천 SSG 랜더스 필드',
    transferNotice: [
      '양도 신청 후 취소는 불가능합니다.',
      '매칭 완료 시 알림이 전송됩니다.',
      '양도료는 경기 완료 후 지급됩니다.'
    ]
  },
];

const router = useRouter();



function handleApply(ticket) {
  selectedTicket.value = ticket;
  showDetailPage.value = true;
}
function handleApplyComplete() {
  // 완료 페이지 상태로 전환
  showCompletePage.value = true;
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
  background: #395b8c;
  border-radius: 8px;
  color: #fff;
  font-size: 20px;
  font-weight: 700;
  box-shadow: 0 2px 8px 0 #0001;
  padding: 24px 32px 18px 32px;
  transition: transform 0.25s cubic-bezier(.4,1.5,.5,1), box-shadow 0.2s, background 0.2s, height 0.35s cubic-bezier(.4,1.5,.5,1);
  position: relative;
  cursor: pointer;
  overflow: hidden;
  height: 130px;
  min-height: 130px;
  max-height: 330px;
}
.ticket-card.highlighted.landers-hover {
  height: 330px !important;
  min-height: 330px !important;
  max-height: 330px !important;
  transform: scale(1.07) translateY(-12px);
  z-index: 2;
  box-shadow: 0 8px 32px 0 #ce0e2d33;
  background: #ce0e2d;
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
  transform: scale(1.07) translateY(-12px);
  z-index: 2;
  box-shadow: 0 8px 32px 0 #ce0e2d33;
  background: #ce0e2d;
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
  background: linear-gradient(90deg, #ce0e2d 60%, #b1002b 100%);
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
  gap: 100px;
  width: 100%;
}

.detail-header-info-col {
  font-size: 16px;
  display: flex;
  align-items: center;
  width: 220px;
  gap: 6px;
  flex: 1;
}
.detail-header-info-col {
  display: flex;
  align-items: center;
  gap: 6px;
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
  justify-content: space-between;
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
</style>

