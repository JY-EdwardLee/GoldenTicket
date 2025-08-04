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
        <div class="calendar-header-section">
          <button class="month-nav-btn" @click="previousMonth">‹</button>
          <div class="calendar-title">{{ currentYear }}년 {{ currentMonth }}월</div>
          <button class="month-nav-btn" @click="nextMonth">›</button>
        </div>
        <div class="calendar-grid">
          <div class="calendar-header" v-for="d in days" :key="d">{{ d }}</div>
          <!-- 이전 달의 빈 칸들 -->
          <div
            v-for="blank in firstDayOfMonth"
            :key="'blank-' + blank"
            class="calendar-cell blank"
          >
          </div>
          <!-- 현재 달의 날짜들 -->
          <div
            v-for="date in daysInCurrentMonth"
            :key="date"
            class="calendar-cell"
            :class="{ 
              selected: selectedDate === date,
              past: isPastDate(date),
              today: isToday(date)
            }"
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
            <template v-if="gamesOnDate.length > 0">
              <div v-for="game in filteredGamesOnDate" :key="game.gameId" class="game-card">
                <div class="game-title">{{ getTeamDisplayName(game.homeTeam) }} vs {{ getTeamDisplayName(game.awayTeam) }}</div>
                <div class="game-datetime" style="font-weight: normal; color: inherit;">
                  {{ formatGameDateTime(game.gameDateTime) }}
                </div>
                <button class="apply-btn" @click="() => handleApplyClick(game)">{{ pageText.applyBtn }}</button>
              </div>
            </template>
            <div v-else class="no-game-centered">해당 날짜에 경기가 없습니다.</div>
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
          <div class="apply-info-row"><span>경기:</span> <span class="right">{{ selectedGame ? getTeamDisplayName(selectedGame.homeTeam) : '' }} vs {{ selectedGame ? getTeamDisplayName(selectedGame.awayTeam) : '' }}</span></div>
          <div class="apply-info-row"><span>일시:</span> <span class="right">{{ selectedGame ? formatGameDateTime(selectedGame.gameDateTime) : '' }}</span></div>
          <div class="apply-info-row"><span>장소:</span> <span class="right">{{ stadiumNameToEnum[selectedTeam] }}</span></div>
        </div>
        <button class="apply-confirm-btn" @click="goToMainPage">확인</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';
import API_CONFIG from '@/config/api.config';
import { stadiumNameToEnum } from '@/utils/teamStadium';
import { teamNameToEnum, getEnumTeamName } from '@/utils/teamNameMap';
import http from '@/utils/http'
import { useRouter } from 'vue-router';

const router = useRouter();

const teamRows = [
  ['SSG랜더스', '키움히어로즈', 'LG트윈스', 'KT위즈', 'NC다이노스'],
  ['두산베어스', 'KIA타이거즈', '롯데자이언츠', '한화이글스', '삼성라이온즈']
];
const selectedTeam = ref('');
// 서버에서 받아올 경기 데이터 배열
const bgHover = ref(false);
const step = ref(1);
const days = ['일', '월', '화', '수', '목', '금', '토'];
const selectedDate = ref(null);
const selectedTime = ref(null);
const gamesOnDate = ref([]);
const selectedGame = ref(null);

// 달력 관련 변수들
const currentYear = 2025;
const currentMonth = ref(8); // 8월부터 시작
const today = new Date(2025, 7, 4); // 2025-08-04 (월은 0부터 시작)
import { computed, onMounted } from 'vue';

// 달력 computed 속성들
const daysInCurrentMonth = computed(() => {
  return new Date(currentYear, currentMonth.value, 0).getDate();
});

const firstDayOfMonth = computed(() => {
  return new Date(currentYear, currentMonth.value - 1, 1).getDay();
});

const filteredGamesOnDate = computed(() => {
  if (!selectedTeam.value) return [];
  const teamEnum = teamNameToEnum[selectedTeam.value];
  return gamesOnDate.value.filter(game => game.homeTeam === teamEnum || game.awayTeam === teamEnum);
});
// 기존 변수들을 제거하고 새로운 달력 로직 사용
function getFormattedDate() {
  if (!selectedDate.value) return '';
  return `${currentYear}년 ${String(currentMonth.value).padStart(2, '0')}월 ${String(selectedDate.value).padStart(2, '0')}일`;
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
  noGame: '날짜를 선택해주세요',
};

function selectTeam(team) {
  selectedTeam.value = team;
}

// 달력 관련 함수들
function isPastDate(date) {
  const checkDate = new Date(currentYear, currentMonth.value - 1, date);
  return checkDate < today;
}

function isToday(date) {
  const checkDate = new Date(currentYear, currentMonth.value - 1, date);
  return checkDate.getTime() === today.getTime();
}

function previousMonth() {
  if (currentMonth.value > 1) {
    currentMonth.value--;
    selectedDate.value = null; // 월 변경 시 선택된 날짜 초기화
    gamesOnDate.value = [];
  }
}

function nextMonth() {
  if (currentMonth.value < 12) {
    currentMonth.value++;
    selectedDate.value = null; // 월 변경 시 선택된 날짜 초기화
    gamesOnDate.value = [];
  }
}

// 컴포넌트 마운트 시 현재 날짜 자동 선택 및 경기 로드
onMounted(async () => {
  if (currentMonth.value === 8) { // 현재 월이 8월인 경우
    selectedDate.value = 4; // 현재 날짜 자동 선택
    
    // 선택된 팀이 있는 경우에만 경기 로드
    if (selectedTeam.value) {
      const formattedDate = `${currentYear}-${String(currentMonth.value).padStart(2, '0')}-${String(4).padStart(2, '0')}`;
      const teamEnum = teamNameToEnum[selectedTeam.value];
      
      try {
        const { data } = await axios.post(API_CONFIG.TICKET.GAMES, {
          date: formattedDate,
          team: teamEnum
        });
        gamesOnDate.value = data;
      } catch (error) {
        console.error('자동 경기 로드 에러:', error);
        gamesOnDate.value = [];
      }
    }
  }
});

async function selectDate(date) {
  // 과거 날짜는 선택할 수 없음
  if (isPastDate(date)) {
    return;
  }
  
  selectedDate.value = date;
  // 날짜 형식 YYYY-MM-DD
  const formattedDate = `${currentYear}-${String(currentMonth.value).padStart(2, '0')}-${String(date).padStart(2, '0')}`;
  // 팀 ENUM 변환
  const teamEnum = teamNameToEnum[selectedTeam.value];
  // POST: 응모하기
  try {
    const { data } = await axios.post(API_CONFIG.TICKET.GAMES, {
      date: formattedDate,
      team: teamEnum
    });
    gamesOnDate.value = data;
  } catch (error) {
    console.error('응모/경기조회 에러:', error);
    gamesOnDate.value = [];
  }
}
async function handleApplyClick(game) {
  selectedGame.value = game;
  console.log('응모 클릭됨')
  try {
    const { data } = await http.post(`/games/${game.gameId}/applications`);
    console.log('응모 결과:', data);
    // 성공 시에만 3단계로 이동
    step.value = 3;
  } catch (error) {
    console.error('응모 요청 실패:', error);
    // 에러 발생 시 중복 응모 경고창 표시
    alert('이미 진행한 응모입니다.');
  }
}

function getTeamDisplayName(enumName) {
  return enumName ? enumName.replace(/_/g, ' ') : '';
}

function goToMainPage() {
  // 메인 페이지로 이동
  router.push('/');
}

function formatGameDateTime(dateTimeStr) {
  // ISO 8601 문자열을 'YYYY-MM-DD HH:mm' 형식으로 변환
  const date = new Date(dateTimeStr);
  const yyyy = date.getFullYear();
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const min = String(date.getMinutes()).padStart(2, '0');
  return `${yyyy}-${mm}-${dd} ${hh}:${min}`;
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
  text-align: center;
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
  height: 550px;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 2px 16px 0 #f8bbd04d;
  padding: 24px 18px 24px 18px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.calendar-header-section {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-bottom: 20px;
}
.calendar-title {
  font-size: 30px;
  font-weight: 700;
  color: #e57373;
  text-align: center;
  min-width: 200px;
}
.month-nav-btn {
  background: #e57373;
  color: white;
  border: none;
  border-radius: 50%;
  width: 40px !important;
  height: 40px !important;
  min-width: 40px !important;
  min-height: 40px !important;
  max-width: 40px !important;
  max-height: 40px !important;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
  flex-shrink: 0 !important;
  flex-grow: 0 !important;
  flex-basis: 40px !important;
  aspect-ratio: 1 / 1;
  box-sizing: border-box;
  padding: 0 !important;
  margin: 0 !important;
  line-height: 1;
  text-align: center;
}
.month-nav-btn:hover {
  background: #d32f2f;
}
.month-nav-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
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
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}
.calendar-cell.selected {
  background: #d32f2f !important;
  color: #fff !important;
  border: none;
  box-sizing: border-box;
  transform: scale(1.05);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 2;
  position: relative;
  box-shadow: 0 2px 8px rgba(211, 47, 47, 0.3);
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
}
.calendar-cell:hover:not(.past):not(.blank) {
  background: #f8bbd0;
  color: #fff;
}
.calendar-cell.past {
  color: #ccc;
  cursor: not-allowed;
}
.calendar-cell.past:hover {
  background: transparent;
  color: #ccc;
}
.calendar-cell.today {
  background: #fff3e0;
  border: 2px solid #e57373;
  font-weight: bold;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
}
.calendar-cell.today.selected {
  background: #d32f2f !important;
  color: #fff !important;
  border: none;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: scale(1.05);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 2;
  position: relative;
  box-shadow: 0 2px 8px rgba(211, 47, 47, 0.3);
  font-weight: 600;
}
.calendar-cell.blank {
  cursor: default;
}
.calendar-cell.blank:hover {
  background: transparent;
}
.calendar-logo {
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  padding-bottom: 10px;
  padding-left: 20px;
}
.calendar-logo img {
  height: 36px;
  width: auto;
  margin-left: 0;
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
  margin: 16px 0;
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
.no-game-centered {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
  font-size: 22px;
  color: #555;
}
</style>
