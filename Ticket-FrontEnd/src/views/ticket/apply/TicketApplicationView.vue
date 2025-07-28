<template>
  <div class="apply-wrapper">
    <div class="stepper">
  <div class="step" :class="{active: step === 1}"><span>1</span><div>{{ pageText.stepper[0] }}</div></div>
  <div class="bar"></div>
  <div class="step" :class="{active: step === 2}"><span>2</span><div>{{ pageText.stepper[1] }}</div></div>
  <div class="bar"></div>
  <div class="step"><span>3</span><div>{{ pageText.stepper[2] }}</div></div>
  <div class="bar"></div>
  <div class="step"><span>4</span><div>{{ pageText.stepper[3] }}</div></div>
</div>
    <div v-if="step === 1" class="main-area">
      <h2 class="title">{{ pageText.selectTeamTitle }}</h2>
      <div class="team-bg-container">
        <div class="team-bg-image"
          :class="{ 'active': selectedTeam === 'SSG랜더스' || bgHover }"
          @mouseenter="bgHover = true"
          @mouseleave="bgHover = false"
          @click="selectTeam('SSG랜더스')"
          style="cursor:pointer;"
        ></div>
      </div>
      <div class="team-list team-list-row">
        <div class="team-row">
          <div v-for="team in teamRows[0]" :key="team" class="team-card"
            :class="{ selected: selectedTeam === team }"
            @click="selectTeam(team)"
          >
            {{ team }}
          </div>
        </div>
        <div class="team-row">
          <div v-for="team in teamRows[1]" :key="team" class="team-card"
            :class="{ selected: selectedTeam === team }"
            @click="selectTeam(team)"
          >
            {{ team }}
          </div>
        </div>
      </div>
      <button class="next-btn" :disabled="!selectedTeam" @click="step = 2">{{ pageText.nextBtn }}</button>
    </div>
    <div v-else-if="step === 2" class="game-select-main">
      <div class="calendar-area">
        <div class="calendar-title">{{ pageText.calendarTitle }}</div>
        <div class="calendar-grid">
          <div class="calendar-header" v-for="d in days" :key="d">{{ d }}</div>
          <div
            v-for="date in calendarDates"
            :key="date"
            class="calendar-cell"
            :class="{ selected: selectedDate === date }"
            @click="selectDate(date)"
          >
            {{ date }}
          </div>
        </div>
        <div class="calendar-logo">
  <img src="/landers_logo.png" alt="landers logo" />
</div>
      </div>
      <div class="info-area">
        <div class="selected-date-box">
          <b>{{ pageText.selectedDateTitle }}</b>
          <div v-if="selectedDate" style="margin-top: 20px;">
            {{ getFormattedDate() }}<span v-if="selectedTime"> {{ selectedTime }}</span>
          </div>
          <div v-else style="margin-top: 20px; ">{{ pageText.selectDateGuide }}</div>
          <div class="selected-team" style="margin-top: 20px;">{{ pageText.selectedTeam }} <span class="selected-team-name">{{ selectedTeam }}</span></div>
        </div>
        <div class="game-list-box">
          <b style="font-size: 30px;">{{ pageText.gameListTitle }}</b>
          <div v-if="selectedDate">
            <div class="game-card">
              <div class="game-title">{{ pageText.gameTitle }}</div>
              <div class="game-desc">{{ pageText.gameDesc }}</div>
              <button class="apply-btn" @click="step = 3">{{ pageText.applyBtn }}</button>
            </div>
          </div>
          <div v-else class="no-game" style="font-size: 20px;">{{ pageText.noGame }}</div>
        </div>
      </div>
    </div>

    <div v-if="step === 3" class="apply-complete-overlay">
      <div class="apply-complete-card">
        <div class="apply-complete-stepper">
          <div class="step active"><span>1</span><div>팀 선택</div></div>
          <div class="bar"></div>
          <div class="step active"><span>2</span><div>경기 선택</div></div>
          <div class="bar"></div>
          <div class="step active"><span>3</span><div style="color:#e57373;">응모하기</div></div>
          <div class="bar"></div>
          <div class="step"><span>4</span><div>결제</div></div>
        </div>
        <div class="apply-icon-box">
          <div class="apply-icon-circle"><span>🔨</span></div>
        </div>
        <div class="apply-title">응모 완료!</div>
        <div class="apply-desc">경기 응모가 성공적으로 완료되었습니다.</div>
        <div class="apply-info-card">
          <div class="apply-info-title">응모한 경기</div>
          <div class="apply-info-row"><span>경기:</span> <span class="right">SSG 랜더스 vs 키움 히어로즈</span></div>
          <div class="apply-info-row"><span>일시:</span> <span class="right">{{ getFormattedDate() }}<span v-if="selectedTime"> {{ selectedTime }}</span></span></div>
          <div class="apply-info-row"><span>장소:</span> <span class="right">인천 SSG 랜더스필드</span></div>
        </div>
        <button class="apply-confirm-btn" @click="step = 1">확인</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
const teamRows = [
  ['SSG랜더스', '키움히어로즈', 'LG트윈스', 'KT위즈', 'NC다이노스'],
  ['두산베어스', 'KIA타이거즈', '롯데자이언츠', '한화이글스', '삼성라이온즈']
];
const selectedTeam = ref('');
const bgHover = ref(false);
const step = ref(1);
const days = ['일', '월', '화', '수', '목', '금', '토'];
const calendarDates = Array.from({ length: 31 }, (_, i) => i + 1);
const selectedDate = ref(null);
const selectedTime = ref(null);
const currentYear = 2025; // 시스템 기준 연도
const selectedMonth = ref(7); // 기본 7월, 필요시 동적 할당 가능
function getFormattedDate() {
  if (!selectedDate.value) return '';
  return `${currentYear}년 ${String(selectedMonth.value).padStart(2, '0')}월 ${String(selectedDate.value).padStart(2, '0')}일`;
}

const pageText = {
  stepper: ['팀 선택', '경기 선택', '응모하기', '결제'],
  selectTeamTitle: '팀을 선택하세요',
  nextBtn: '다음',
  calendarTitle: '달력',
  selectedDateTitle: '선택된 날짜',
  selectedDatePrefix: '',
  selectedDateSuffix: '에 경기 있음',
  selectDateGuide: '날짜를 선택하세요',
  selectedTeam: '선택된 팀:',
  gameListTitle: '경기 목록',
  gameTitle: 'SSG 랜더스 vs 키움 히어로즈',
  gameDesc: '인천 SSG 랜더스필드',
  applyBtn: '응모하기',
  noGame: '선택된 날짜에 경기가 없습니다',
};

function selectTeam(team) {
  selectedTeam.value = team;
}
function selectDate(date) {
  selectedDate.value = date;
}
</script>

<style scoped>
*{
  font-size: 20px;
}
.apply-wrapper {
  min-height: 100vh;
  background: #fff;
  display: flex;
  flex-direction: column;
}
.stepper {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 40px 0 24px;
  gap: 0 14px;
}
.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #bdbdbd;
  font-size: 14px;
  font-weight: 500;
}
.step.active {
  color: #e57373;
}
.step span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #f8bbd0;
  color: #fff;
  font-weight: bold;
  margin-bottom: 6px;
  font-size: 18px;
}
.step.active span {
  background: #e57373;
}
.bar {
  width: 60px;
  height: 4px;
  background: #f8bbd0;
  border-radius: 2px;
}
.main-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}
.title {
  font-size: 28px;
  font-weight: 700;
  margin: 20px 0 20px 0;
  color: #222;
  letter-spacing: -1.5px;
  text-align: left;
  width: 100%;
  max-width: 700px;
}
.team-bg-container {
  width: 600px;
  height: 100px;
  margin: 0 auto 28px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
.team-bg-image {
  width: 800px;
  height: 120px;
  border-radius: 16px;
  background: url('/landers_bg2.png') center/cover no-repeat;
  opacity: 0.25;
  transition: opacity 0.3s;
}
.team-bg-image.active {
  opacity: 1;
}
.team-bg-image:hover {
  opacity: 1;
}
.team-list.team-list-row {
  display: flex;
  flex-direction: column;
  gap: 18px;
  justify-content: center;
  align-items: center;
  margin-bottom: 18px;
}
.team-row {
  display: flex;
  flex-direction: row;
  gap: 28px;
}
.team-card {
  width: 200px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  font-size: 20px;
  font-weight: 500;
  color: #333;
  background: #fff;
  cursor: pointer;
  transition: border 0.2s, box-shadow 0.2s;
  position: relative;
  z-index: 1;
}
.team-card.selected,
.team-card:hover {
  border: 2px solid #e57373;
  box-shadow: 0 2px 8px 0 #f8bbd04d;
}
.team-card.selected:hover ~ .team-bg-image,
.team-card:hover ~ .team-bg-image {
  opacity: 1 !important;
}
.next-btn {
  padding: 10px 36px;
  background: #bdbdbd;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.next-btn:disabled {
  background: #e0e0e0;
  cursor: not-allowed;
}
.footer {
  margin-top: auto;
  background: #fafafa;
  padding: 36px 0 24px 0;
  text-align: center;
}
.footer-logo {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 8px;
}
.footer-desc {
  font-size: 14px;
  color: #888;
  margin-bottom: 14px;
}
.footer-icons span {
  margin: 0 6px;
  font-size: 18px;
  color: #bbb;
}
.game-select-main {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  justify-content: center;
  gap: 48px;
  margin-bottom: 48px;
}
.calendar-area {
  width: 500px;
  height: 500px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 16px 0 #f8bbd04d;
  padding: 24px 18px 24px 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.calendar-title {
  font-size: 40px;
  font-weight: 700;
  color: #e57373;
  margin-bottom: 16px;
}
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 20px;
  margin-bottom: 18px;
}
.calendar-header {
  font-size: 23px;
  font-weight: 600;
  color: #bdbdbd;
  text-align: center;
  padding: 2px 0;
}
.calendar-cell {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: transparent;
  color: #444;
  font-size: 23px;
  font-weight: 500;
  text-align: center;
  line-height: 36px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
  user-select: none;
  position: relative;
}
.calendar-cell.selected {
  background: #e57373;
  color: #fff;
}
.calendar-cell:hover {
  background: #f8bbd0;
  color: #fff;
}
.calendar-logo {
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
}
.calendar-logo img {
  height: 36px;
  width: auto;
  margin-left: 40px;
  display: block;
}
.info-area {
  width: 340px;
  display: flex;
  flex-direction: column;
  gap: 28px;
}
.selected-date-box {
  background: #fff;
  border-radius: 14px;
  width: 400px;
  height: 200px;
  box-shadow: 0 2px 16px 0 #f8bbd04d;
  padding: 20px 20px 16px 20px;
  margin-bottom: 20px;
}
.selected-date-box b {
  color: #e57373;
  font-size: 30px;
}
.selected-team {
  margin-top: 20px;
  font-size: 15px;
  color: #888;
}
.selected-team-name {
  color: #e57373;
  font-weight: bold;
}
.game-list-box {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 16px 0 #f8bbd04d;
  padding: 20px 20px 20px 20px;
  width: 400px;
  height: 260px;
}
.game-list-box b {
  color: #e57373;
  font-size: 16px;
}
.game-card {
  margin-bottom: 16px;
  padding: 14px 18px;
  background: #fff3e0;
  border-radius: 10px;
  box-shadow: 0 2px 8px 0 #f8bbd04d;
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.game-title {
  font-size: 19px;
  font-weight: 700;
  color: #e57373;
}
.game-desc {
  font-size: 18px;
  margin-top: 10px;
  color: #888;
}
.apply-btn {
  margin-top: 30px;
  padding: 7px 20px;
  background: #e57373;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 20px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.apply-btn:hover {
  background: #b71c1c;
}
.no-game {
  color: #bbb;
  font-size: 20px;
  margin-top: 60px;
  text-align: center;
}
.apply-complete-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  z-index: 1000;
  background: rgba(0,0,0,0.10);
  display: flex;
  align-items: center;
  justify-content: center;
}
.apply-complete-card {
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 6px 32px 0 #e5737340;
  padding: 36px 32px 32px 32px;
  width: 1000px;
  height: auto;
  max-height: 90vh;
  overflow-y: auto;
  overflow-x: hidden;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.apply-complete-stepper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0 14px;
  margin-bottom: 18px;
}
.apply-complete-stepper .step {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #bdbdbd;
  font-size: 13px;
  font-weight: 500;
}
.apply-complete-stepper .step.active {
  color: #e57373;
}
.apply-complete-stepper .step span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #f8bbd0;
  color: #fff;
  font-weight: bold;
  margin-bottom: 4px;
  font-size: 15px;
}
.apply-complete-stepper .step.active span {
  background: #e57373;
}
.apply-complete-stepper .bar {
  width: 36px;
  height: 3px;
  background: #f8bbd0;
  border-radius: 2px;
}
.apply-icon-box {
  margin-bottom: 10px;
}
.apply-icon-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: #e57373;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
  margin: 0 auto;
}
.apply-title {
  font-size: 22px;
  font-weight: 700;
  color: #222;
  margin: 22px 0 4px 0;
  text-align: center;
}
.apply-desc {
  color: #888;
  font-size: 14px;
  margin-bottom: 22px;
  text-align: center;
}
.apply-info-card {
  background: #e53935;
  border-radius: 10px;
  padding: 18px 18px 10px 18px;
  margin-bottom: 18px;
  width: 80%;
  height: 200px;
}
.apply-info-title {
  color: #fff;
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 8px;
}
.apply-info-row {
  display: flex;
  justify-content: space-between;
  color: #fff;
  font-size: 20px;
  margin-bottom: 4px;
}
.apply-info-row .right {
  font-weight: 500;
}
.apply-wait-card {
  background: #f3f6fa;
  border-radius: 10px;
  padding: 16px 18px 10px 18px;
  margin-bottom: 24px;
  width: 100%;
  height: 200px;
}
.apply-wait-title {
  color: #222;
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 8px;
}
.apply-wait-row {
  display: flex;
  justify-content: space-between;
  color: #222;
  font-size: 20px;
  margin-bottom: 4px;
}
.apply-confirm-btn {
  width: 20%;
  padding: 11px 0;
  background: #e57373;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  margin-top: 8px;
  transition: background 0.2s;
}
.apply-confirm-btn:hover {
  background: #b71c1c;
}
</style>
