<template>
  <div class="apply-wrapper">
    <div class="stepper">
  <div class="step" :class="{active: step === 1}" @click="step = 1"><span>1</span><div style="width: 80px; text-align: center;">{{ pageText.stepper[0] }}</div></div>
  <div class="bar"></div>
  <div class="step" :class="{active: step === 2}" @click="step = 2"><span>2</span><div style="width: 80px; text-align: center;">{{ pageText.stepper[1] }}</div></div>
  <div class="bar"></div>
  <div class="step" :class="{active: step === 3}"><span>3</span><div style="width: 80px; text-align: center;">{{ pageText.stepper[2] }}</div></div>
  <div class="bar"></div>
  <div class="step"><span>4</span><div style="width: 80px; text-align: center;">{{ pageText.stepper[3] }}</div></div>
</div>
    <div v-if="step === 1" class="main-area">
      <h2 class="title">{{ pageText.selectTeamTitle }}</h2>
      <div class="team-bg-container" :class="{ 'active': selectedTeam === enumToTeamName[user.myTeam] }">
        <div class="team-bg-image"
          :class="{ 'active': selectedTeam === enumToTeamName[user.myTeam] }"
          :style="{ background: `url('/catchphrase/${user.myTeam}_CP.svg') center no-repeat`, backgroundSize: 'cover' }"
          @mouseenter="bgHover = true"
          @mouseleave="bgHover = false"
          @click="selectTeam(enumToTeamName[user.myTeam])"
          style="cursor:pointer;"
        ></div>
      </div>
      <div class="team-list team-list-row">
        <div class="team-row">
          <div v-for="team in teamRows[0]" :key="team" class="team-card"
            :class="{ selected: selectedTeam === team }"
            @click="selectTeam(team)"
          >
            <div class="team-card-image">
              <img :src="`/orglogo/${getEnumTeamName(team)}.svg`" :alt="team" />
            </div>
            <div class="team-card-name">
              {{ team }}
            </div>
          </div>
        </div>
        <div class="team-row">
          <div v-for="team in teamRows[1]" :key="team" class="team-card"
            :class="{ selected: selectedTeam === team }"
            @click="selectTeam(team)"
          >
            <div class="team-card-image">
              <img :src="`/orglogo/${getEnumTeamName(team)}.svg`" :alt="team" />
            </div>
            <div class="team-card-name">
              {{ team }}
            </div>
          </div>
        </div>
      </div>
      <button class="next-btn" :disabled="!selectedTeam" @click="goToNextStep">{{ pageText.nextBtn }}</button>
    </div>
    <div v-else-if="step === 2" class="game-select-main">
      <div class="calendar-area">
        <div class="calendar-header-section">
          <button class="month-nav-btn" @click="previousMonth"><i class="fas fa-chevron-left"></i></button>
          <div class="calendar-title">{{ currentYear }}년 {{ currentMonth }}월</div>
          <button class="month-nav-btn" @click="nextMonth"><i class="fas fa-chevron-right"></i></button>
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
          <img :src="`/orglogo/${user.myTeam}.svg`" alt="landers logo" />
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
          <div class="step active"><span>3</span><div style="color:var(--theme-gradient, #e57373);">응모하기</div></div>
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
import { ref, onMounted, computed, onBeforeUnmount, nextTick } from 'vue';
import axios from 'axios';
import API_CONFIG from '@/config/api.config';
import { stadiumNameToEnum } from '@/utils/teamStadium';
import { teamNameToEnum, enumToTeamName } from '@/utils/teamNameMap';
import http from '@/utils/http'
import { useRouter } from 'vue-router';
import { driver } from 'driver.js';
import 'driver.js/dist/driver.css';
import { useTeamThemeStore } from '@/stores/teamTheme.js'
import { getEnumTeamName } from '@/utils/teamNameMap';

const user = JSON.parse(localStorage.getItem('user'))

// 팀 테마 스토어를 가져옵니다.
const themeStore = useTeamThemeStore

// 사용자 정보에 myTeam 값이 있으면 해당 팀으로 테마를 설정합니다.
// 이 코드는 컴포넌트가 생성될 때마다 실행되어 현재 사용자의 팀 테마를 적용합니다.
if (user?.myTeam) {
  themeStore.setSelectedTeam(user.myTeam)
}


const router = useRouter();

// 튜토리얼 관련 변수
const showTutorial = ref(false);

const teamRows = [
  ['SSG랜더스', '키움히어로즈', 'LG트윈스', 'KT위즈', 'NC다이노스'],
  ['두산베어스', 'KIA타이거즈', '롯데자이언츠', '한화이글스', '삼성라이온즈']
];
// 기본 팀을 SSG랜더스로 설정
const selectedTeam = ref('SSG랜더스');
const driverObj = ref(null); // 드라이버 인스턴스를 저장할 ref 추가
// 서버에서 받아올 경기 데이터 배열
const bgHover = ref(false);
const step = ref(1);
const days = ['일', '월', '화', '수', '목', '금', '토'];
const selectedDate = ref(null);
const selectedTime = ref(null);
const gamesOnDate = ref([]);
const selectedGame = ref(null);

// 달력 관련 변수들
const currentYear = new Date().getFullYear();
const currentMonth = ref(new Date().getMonth() + 1); // 현재 월
const today = new Date();
today.setDate(today.getDate()); // 오늘보다 하루 전 날짜 사용

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
  applyBtn: '응모하기',
  noGame: '날짜를 선택해주세요',
  prevBtn: '이전'
};

// 팀 이름을 ID로 변환하는 함수
function getTeamId(teamName) {
  const teamMap = {
    'SSG랜더스': 'SSG',
    '키움히어로즈': '키움',
    'LG트윈스': 'LG',
    'KT위즈': 'KT',
    'NC다이노스': 'NC',
    '두산베어스': '두산',
    'KIA타이거즈': 'KIA',
    '롯데자이언츠': '롯데',
    '한화이글스': '한화',
    '삼성라이온즈': '삼성'
  };
  return teamMap[teamName] || teamName;
}

function selectTeam(team) {
  // 이미 ENUM 값인 경우와 한글 팀명인 경우 모두 처리
  // const teamEnum = teamNameToEnum[team] || (Object.values(teamNameToEnum).includes(team) ? team : null);
  // selectedTeam.value = teamEnum ? enumToTeamName[teamEnum] : team;
  
  let teamEnum;
  if(teamNameToEnum[team]) {
    teamEnum = teamNameToEnum[team];
  } else if(Object.values(teamNameToEnum).includes(team)) {
    teamEnum = team;
  } else {
    teamEnum = null;
  }

  selectedTeam.value = teamEnum ? enumToTeamName[teamEnum] : team;

  if(teamEnum){
    localStorage.setItem('selectedTeam', teamEnum);
  }

  // 선택된 팀 강조 표시 업데이트
  document.querySelectorAll('.team-card').forEach(card => {
    if (card.textContent === enumToTeamName[teamEnum] || card.textContent === team) {
      card.classList.add('selected');
    } else {
      card.classList.remove('selected');
    }
  });
  
  // 팀 배경 이미지 업데이트 - 사용자의 관심팀일 때만 active 클래스 추가
  const teamBg = document.querySelector('.team-bg-image');
  if (teamBg) {
    if (teamEnum === user?.myTeam) {
      teamBg.classList.add('active');
    } else {
      teamBg.classList.remove('active');
    }
  }



  // 팀 선택 팝오버의 설명문 업데이트
  updateTeamSelectionPopup();
  
  console.log('팀 선택됨:', team); // 디버깅용 로그
}

// 팀 선택 팝오버 설명문
function updateTeamSelectionPopup() {
  // driver 인스턴스 없으면 함수 종료
  if (!driverObj.value) return;
  
  // 현재 활성화된 팝오버 요소 찾기
  const activePopup = document.querySelector('.driver-popover.driver-active-popup');
  if (!activePopup) return;

}




////////////////// 달력 관련 //////////////////

// 선택한 날짜가 오늘 이전인지 확인
function isPastDate(date) {
  const checkDate = new Date(currentYear, currentMonth.value - 1, date);
  const todayDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const compareDate = new Date(checkDate.getFullYear(), checkDate.getMonth(), checkDate.getDate());
  return compareDate < todayDate; // 시간 정보를 제거하고 날짜만 비교
}

// 선택한 날짜가 오늘인지 확인
function isToday(date) {
  const today = new Date();
  const checkDate = new Date(currentYear, currentMonth.value - 1, date);
  const todayDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const compareDate = new Date(checkDate.getFullYear(), checkDate.getMonth(), checkDate.getDate());
  return compareDate.getTime() === todayDate.getTime();
}

// 이전 달로 이동
function previousMonth() {
  if (currentMonth.value > 1) {
    currentMonth.value--;
    selectedDate.value = null; // 월 변경 시 선택된 날짜 초기화
    gamesOnDate.value = [];
  }
}

// 다음 달로 이동
function nextMonth() {
  if (currentMonth.value < 12) {
    currentMonth.value++;
    selectedDate.value = null; // 월 변경 시 선택된 날짜 초기화
    gamesOnDate.value = [];
  }
}


// 특정 날짜의 경기 목록을 로드하는 함수
async function loadGamesForDate(date) {
  try {
    // 실제 API 호출 대신 더미 데이터로 테스트
    // 실제 구현 시에는 여기서 API를 호출하여 경기 데이터를 가져와야 합니다
    const selectedTeamEnum = teamNameToEnum[selectedTeam.value];
    
    // 더미 경기 데이터 생성 (실제로는 API에서 가져와야 함)
    const dummyGames = [
      {
        gameId: 1,
        homeTeam: selectedTeamEnum,
        awayTeam: 'HANWHA_EAGLES',
        gameDateTime: `${currentYear}-${String(currentMonth.value).padStart(2, '0')}-${String(date).padStart(2, '0')}T18:30:00`
      }
    ];
    
    // gamesOnDate.value = dummyGames;
  } catch (error) {
    console.error('경기 목록 로드 실패:', error);
    gamesOnDate.value = [];
  }
}

// 페이지 진입 시 오늘 날짜 자동 선택 및 경기 목록 로드
function initializeTodaySelection() {
  const today = new Date();
  const todayDate = today.getDate();
  const todayMonth = today.getMonth() + 1;
  const todayYear = today.getFullYear();
  
  // 현재 달력이 오늘이 속한 달인지 확인
  if (currentYear === todayYear && currentMonth.value === todayMonth) {
    selectDate(todayDate);
  }
}




////////////////// 튜토리얼(팝오버) 관련 //////////////////

//// 초기 팀 선택 튜토리얼 시작 ////
const startTutorial = () => {
  // 초기화 (이미 실행 중인 튜토리얼 있다면 제거하고 새로 시작)
  if (driverObj.value) {
    driverObj.value.destroy();
  }

  // handleNextClick() : 다음 버튼 클릭 핸들러
  const handleNextClick = (fromTutorial = false) => {
    
    // 1. 팀이 선택되어 있는지 확인
    if (!selectedTeam.value) {
      selectedTeam.value = 'SSG랜더스'; // 기본값 설정
    }
    
    // 2. 현재 단계 확인
    const currentStep = step.value;
    
    // 3. 튜토리얼에서 호출된 경우
    if (fromTutorial) {
      // 튜토리얼 종료
      if (driverObj.value) {
        driverObj.value.destroy();
        driverObj.value = null;
      }
      
      // 다음 단계로 이동
      if (currentStep === 1) {
        step.value = 2;
        // 캘린더 튜토리얼 시작
        setTimeout(() => {
          startCalendarTutorial();
        }, 300);
      }
      return true;
    }
    
    // 일반 버튼 클릭에서 호출된 경우
    const nextButton = document.querySelector('.next-btn');
    if (nextButton && !nextButton.disabled) {
      // 현재 튜토리얼 종료
      if (driverObj.value) {
        driverObj.value.destroy();
        driverObj.value = null;
      }
      
      // 다음 버튼 클릭
      nextButton.click();
      
      // 단계가 1에서 2로 변경되었을 때 날짜 선택 튜토리얼 시작
      if (currentStep === 1) {
        setTimeout(() => {
          if (step.value === 2) {
            startCalendarTutorial();
          }
        }, 500);
      }
      
      return true;
    }
    return false;
  };

  // 드라이버 설정 -> driver.js 사용해 튜토리얼 구성
  driverObj.value = driver({
    showProgress: false,
    overlayColor: 'rgba(0, 0, 0, 0.7)',
    animate: 300,
    allowClose: true,
    doneBtnText: '완료',
    nextBtnText: '다음',
    // prevBtnText: '이전',
    showButtons: ['next', 'close'],
    
    // 글로벌 onNextClick 핸들러 제거 - 개별 step의 onNext 핸들러 사용
    
    // driver.js 튜토리얼 라이브러리에서 제공하는 콜백 함수
    // 각 단계에서 팝오버가 렌더링될 때마다 호출된다
    onPopoverRender: (popover, { config, state }) => {
      if (popover && typeof popover.querySelector === 'function') {
        const popoverElement = popover.nodeType === 1 ? popover : popover.querySelector('.driver-popover');
        if (popoverElement && popoverElement.classList) {
          // 1. 팝오버 스타일링 : tutorial-popover 클래스 -> 팝오버 스타일 커스터마이징
          popoverElement.classList.add('tutorial-popover');
          
          // 2. 단계별 특별 클래스 추가
          if (state.activeStep === 1) { // 다음 버튼 단계일 때
            popoverElement.classList.add('next-btn-popover');
          }
          
          // 3. 팀 선택 팝오버의 설명문을 실시간으로 업데이트
          if (state.activeStep === 0) { // 팀 선택 단계일 때만
            const descriptionEl = popoverElement.querySelector('.driver-popover-description');
            if (descriptionEl) {
              const defaultText = '원하는 팀을 선택해주세요.';
              const selectedTeamText = selectedTeam.value ? ` (현재 선택한 팀 : ${selectedTeam.value})` : '';
              descriptionEl.innerHTML = defaultText + selectedTeamText;
            }
          }
        }
        
        // 3. 다음 버튼 활성화
        const possibleSelectors = [
          '.driver-next-btn',
          '.driver-popover-next-btn', 
          '[data-driver-popover-next-btn]',
          'button:contains("다음")',
          '.driver-popover button:last-child'
        ];
        
        let nextButton = null;
        for (const selector of possibleSelectors) {
          nextButton = popover.querySelector(selector);
          if (nextButton) break;
        }
        
        if (nextButton) {
          nextButton.disabled = false;
          console.log('다음 버튼 찾음:', nextButton.className);
        } else {
          console.warn('다음 버튼을 찾을 수 없음');
        }
      }
    },
    
    // 튜토리얼 전체 흐름 정의
    steps: [
      {
        // 1. 팀 선택 단계
        element: '.team-list',
        popover: {
          title: '팀 선택',
          description: '원하는 팀을 선택해주세요.',
          side: 'bottom',
          align: 'center',
        }
      },
      {
        // 2. 팀 선택 후 다음 버튼 클릭 단계
        element: '.next-btn',
        popover: {
          title: '다음 단계',
          description: '다음 버튼을 눌러주세요.',
          side: 'top',
          align: 'center',
          onNext: () => {
            console.log('다음 버튼 단계 onNext 호출');
            // 튜토리얼 종료 후 다음 단계로 이동
            const result = goToNextStep();
            console.log('goToNextStep 결과:', result);
            return result;
          }
        }
      }
    ]
  });
  
  // 튜토리얼 시작
  driverObj.value.drive();
};


//// 날짜 선택 튜토리얼 시작 ////
const startCalendarTutorial = () => {
  
  // 1. 기존 driver 인스턴스(튜토리얼) 완전히 정리
  if (driverObj.value) {
    try {
      driverObj.value.destroy();
    } catch (e) {
      console.warn('startCalendarTutorial driver 종료 오류:', e);
    }
    driverObj.value = null;
  }
  
  // 팝오버 위치 미리 계산
  const calculatePopoverPosition = () => {
    const calendarArea = document.querySelector('.calendar-area');
    if (!calendarArea) return null;
    
    const rect = calendarArea.getBoundingClientRect();
    const rightPosition = rect.right + 20;
    const minTopPosition = 100;
    const maxTopPosition = window.innerHeight - 200;
    let topPosition = rect.top + (rect.height / 2) - 100;
    
    if (topPosition < minTopPosition) {
      topPosition = minTopPosition;
    } else if (topPosition > maxTopPosition) {
      topPosition = maxTopPosition;
    }
    
    return { rightPosition, topPosition };
  };
  
  // 팝오버 위치 고정 CSS 추가
  const addPopoverPositionCSS = (position) => {
    const existingStyle = document.getElementById('calendar-tutorial-style');
    if (existingStyle) {
      existingStyle.remove();
    }
    
    const style = document.createElement('style');
    style.id = 'calendar-tutorial-style';
    style.textContent = `
      .tutorial-calendar-modal {
        position: fixed !important;
        left: ${position.rightPosition}px !important;
        top: ${position.topPosition}px !important;
        transform: none !important;
        width: 400px !important;
        max-width: 500px !important;
        min-width: 300px !important;
        max-height: 250px !important;
        margin: 10px !important;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15) !important;
        z-index: 10001 !important;
        transition: none !important;
      }
    `;
    document.head.appendChild(style);
  };
  
  setTimeout(() => {
    // 타겟 요소 존재 여부 확인
    const calendarArea = document.querySelector('.calendar-area');
    console.log('캘린더 영역 찾기:', calendarArea);
    
    if (!calendarArea) {
      console.error('.calendar-area 요소를 찾을 수 없습니다!');
      return;
    }
    
    // 팝오버 위치 미리 계산 및 CSS 적용
    const position = calculatePopoverPosition();
    if (position) {
      addPopoverPositionCSS(position);
      console.log('팝오버 위치 미리 설정:', position);
    }
    
    console.log('driver.js 초기화 시작...');
    
    // 2. 새 튜토리얼 설정
    try {
      driverObj.value = driver({
        showProgress: true,
        overlayColor: 'rgba(0, 0, 0, 0.7)',
        animate: false, // 애니메이션 비활성화 -> 잔상 방지
        allowClose: true,
        doneBtnText: '완료',
        nextBtnText: '다음',
        prevBtnText: '이전',
        showButtons: ['close'],
        popoverClass: 'tutorial-calendar-modal',
        
        // 3. 튜토리얼 단계 정의
        steps: [
          {
            // 날짜 선택 안내
            element: '.calendar-area',
            popover: {
              title: '날짜 선택',
              description: '원하는 경기 날짜를 선택해주세요. 선택한 날짜의 경기 목록이 아래에 표시됩니다.',
              side: 'right',
              align: 'start',
              onNext: () => {
                // 날짜 클릭했을 때의 이벤트 핸들러
                setupDateClickListener();
                return true;
              }
            }
          }
        ],
        
        // 콜백 이벤트
        onHighlighted: (element, step, options) => {
          console.log('onHighlighted 호출됨 - 위치 고정됨:', element);
        },
        
        onDeselected: (element, step, options) => {
          console.log('onDeselected 호출됨:', element);
        },
        
        onDestroyed: () => {
          console.log('onDestroyed 호출됨');
          // 튜토리얼 종료 시 추가된 스타일 제거
          const style = document.getElementById('calendar-tutorial-style');
          if (style) {
            style.remove();
          }
        }
      });
      
      driverObj.value.drive();
      
    } catch (error) {
      console.error('driver.js 초기화 오류:', error);
    }
  }, 500);
};

// 사용자가 캘린더에서 날짜를 클릭했을 때 발생하는 이벤트 핸들러
// : 날짜 클릭하면, 경기 목록 튜토리얼 시작
const setupDateClickListener = () => {
  
  // 1. 기존 튜토리얼 종료 (중복 방지)
  if (driverObj.value) {
    driverObj.value.destroy();
    driverObj.value = null;
  }
  
  // 날짜 클릭 이벤트 리스너 추가
  const handleDateClick = (event) => {
    
    // 클릭된 요소가 날짜 셀인지 확인
    const dateCell = event.target.closest('.calendar-date');
    if (dateCell && !dateCell.classList.contains('disabled')) {
      
      // 이벤트 리스너 제거
      document.removeEventListener('click', handleDateClick);
      
      // 경기 목록이 로드될 때까지 잠시 대기 후 경기 목록 튜토리얼 시작
      setTimeout(() => {
        const gameList = document.querySelector('.game-list');
        if (gameList && gameList.children.length > 0) {
          startGameListTutorial();
        } else {
          showNoGameMessage();
        }
      }, 500);
    }
  };
  
  // 전역 클릭 이벤트 리스너 추가
  document.addEventListener('click', handleDateClick);
  
  // 30초 후 자동으로 리스너 제거 (타임아웃 방지)
  setTimeout(() => {
    document.removeEventListener('click', handleDateClick);
    console.log('날짜 클릭 리스너 타임아웃으로 제거됨');
  }, 30000);
};


// 팝오버의 스타일을 동적으로 조정하는 함수
// findAndStylePopover() : 캘린더 영역 계산 -> 팝오버 위치 조정
const applyPopoverStyles = () => {

  const findAndStylePopover = () => {
    
    const popoverElement = document.querySelector('.driver-popover');
    if (popoverElement) {
      // 캘린더 영역의 위치 정보 가져오기      
      const calendarArea = document.querySelector('.calendar-area');
      if (calendarArea) {
        const rect = calendarArea.getBoundingClientRect();
        // 오른쪽 여백 20px 추가
        const rightPosition = rect.right + 20;
        
        // 화면 경계를 고려한 위치 계산
        const minTopPosition = 100; // 최소 상단 여백
        const maxTopPosition = window.innerHeight - 200; // 최대 하단 여백
        let topPosition = rect.top + (rect.height / 2) - 100; // 캘린더 중앙 기준
        
        // 경계 값 보정
        if (topPosition < minTopPosition) {
          topPosition = minTopPosition;
        } else if (topPosition > maxTopPosition) {
          topPosition = maxTopPosition;
        }
        
        // 스타일 객체 정의 (강제)
        const styles = {
          'width': '400px',           
          'max-width': '500px',      
          'min-width': '300px',       
          'max-height': '250px',     
          'position': 'fixed',
          'left': `${rightPosition}px`,
          'top': `${topPosition}px`,
          'transform': 'none',
          'right': 'auto',
          'z-index': '10001',
          'margin': '10px',           // 여백 추가
          'box-shadow': '0 4px 12px rgba(0,0,0,0.15)' // 그림자 효과 추가
        };
        
        // 스타일 적용
        Object.entries(styles).forEach(([prop, value]) => {
          popoverElement.style.setProperty(prop, value, 'important');
        });
        
        return true;
      }
    }
    return false;
  };
  
  // 팝오버 요소를 찾아 스타일을 적용하기 위한 재시도 로직
  if (!findAndStylePopover()) {
    setTimeout(() => findAndStylePopover(), 10);
    setTimeout(() => findAndStylePopover(), 50);
    setTimeout(() => findAndStylePopover(), 100);
    setTimeout(() => findAndStylePopover(), 200);
    
    // 지속적인 감시
    const interval = setInterval(() => {
      if (findAndStylePopover()) {
        clearInterval(interval);
      }
    }, 100);
    
    setTimeout(() => clearInterval(interval), 3000);
  }
};





//// 경기 목록 튜토리얼 시작 ////
const startGameListTutorial = () => {
  
  // 1. 기존 driver 인스턴스 정리
  if (driverObj.value) {
    driverObj.value.destroy();
    driverObj.value = null;
  }
  
  // 2. 튜토리얼 설정 (300ms 지연 후 실행)
  setTimeout(() => {
    driverObj.value = driver({
      showProgress: false,
      overlayColor: 'rgba(0, 0, 0, 0.7)',
      animate: 300,
      allowClose: true,
      doneBtnText: '완료',
      nextBtnText: '완료',
      prevBtnText: '', // 이전 버튼 텍스트 비우기
      showButtons: ['close', 'next'], // next 버튼을 완료 버튼으로 사용
      popoverClass: 'tutorial-game-list-modal', // 경기 목록 모달 고유 클래스
      
      // 3. 튜토리얼 단계
      steps: [
        {
          element: '.game-list-box',
          popover: {
            title: '경기 선택',
            description: '선택한 날짜의 경기 목록입니다. 원하는 경기의 "응모하기" 버튼을 직접 클릭해주세요.',
            side: 'left',
            align: 'start'
          }
        }
      ],
      
      // 4. 이벤트 핸들러
      onHighlighted: (element, step, options) => {
        console.log('경기 목록 onHighlighted 호출됨:', element);
      },
    });
    
    // 5. 튜토리얼 시작
    driverObj.value.drive();
  }, 300);
};



// 다음 버튼 눌렀을 때 실행되는 함수
// 팀 선택 단계(step.value===1)이고, 팀이 선택된 경우(selectedTeam.value)에만 실행
// 이후 날짜 선택 튜토리얼 실행
const goToNextStep = () => {
  
  if (step.value === 1 && selectedTeam.value) {
    // 현재 튜토리얼이 있다면 종료
    if (driverObj.value) {
      driverObj.value.destroy();
      driverObj.value = null;
    }
    
    // 단계 변경 (날짜 선택 단계로 이동)
    step.value = 2;
    
    // DOM 업데이트 후 캘린더 튜토리얼 시작
    nextTick(() => {
      // URL에 튜토리얼 파라미터가 있으면 캘린더 튜토리얼 시작
      const urlParams = new URLSearchParams(window.location.search);
      const tutorialParam = urlParams.get('tutorial');
      
      if (tutorialParam === 'true') {
        startCalendarTutorial();
      }
    });

    setTimeout(() => {
      initializeTodaySelection();
    }, 100); // DOM 업데이트 후 실행
    
    return true;
  }
  return false;
};

// 이전 버튼 눌렀을 때 실행되는 함수
// 날짜 선택 단계(step.value===2)에만 실행
// 이후 팀 선택 튜토리얼로 되돌아감
const goToPreviousStep = () => {
  
  if (step.value === 2) {
    // 현재 튜토리얼이 있다면 종료
    if (driverObj.value) {
      driverObj.value.destroy();
      driverObj.value = null;
    }
    
    // 단계 변경 (팀 선택 단계로 이동)
    step.value = 1;
    
    return true;
  }
  return false;
};


// 컴포넌트 마운트 시 현재 날짜 자동 선택 및 경기 로드
onMounted(async () => {
  // 이전에 선택한 팀이 있으면 복원
  const savedTeam = localStorage.getItem('selectedTeam');
  if (savedTeam) {
    selectedTeam.value = savedTeam;
    // UI 업데이트
    document.querySelectorAll('.team-card').forEach(card => {
      if (card.textContent === savedTeam) {
        card.classList.add('selected');
      } else {
        card.classList.remove('selected');
      }
    });
  }

  // URL에서 튜토리얼 파라미터 확인
  // URL에 ?tutorial=true 가 있으면 튜토리얼을 자동으로 시작한다
  const urlParams = new URLSearchParams(window.location.search);
  const tutorialParam = urlParams.get('tutorial');
  
  if (tutorialParam === 'true') {
    // 약간의 지연 후 튜토리얼 시작 (컴포넌트 렌더링 완료 대기)
    setTimeout(() => {
      startTutorial();
    }, 500); // 로딩 시간을 좀 더 여유롭게 조정
  }
  
  // 현재 날짜 가져오기 (동적)
  const now = new Date();
  const currentDay = now.getDate();
  const todayMonth = now.getMonth() + 1; // 0부터 시작하므로 +1
  
  if (currentMonth.value === todayMonth) { // 현재 월인 경우
    selectedDate.value = currentDay; // 현재 날짜 자동 선택
    
    // 선택된 팀이 있는 경우에만 경기 로드
    if (selectedTeam.value) {
      const formattedDate = `${currentYear}-${String(currentMonth.value).padStart(2, '0')}-${String(currentDay).padStart(2, '0')}`;
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




//// 경기 없음 안내 모달 표시 함수 ////
function showNoGameModal(date) {
  if (driverObj.value) {
    driverObj.value.destroy();
  }
  
  driverObj.value = driver({
    showProgress: false,
    overlayColor: 'rgba(0, 0, 0, 0.7)',
    animate: 300,
    allowClose: true,
    doneBtnText: '확인',
    showButtons: ['close', 'done'],
    
    // 팝오버 스타일 설정
    onPopoverRender: (popover, { config, state }) => {
      if (popover && typeof popover.querySelector === 'function') {
        const popoverElement = popover.querySelector('.driver-popover');
        if (popoverElement) {
          popoverElement.classList.add('tutorial-popover', 'no-game-modal');
        }
      }
    },
    
    // 모달 내용
    steps: [
      {
        element: '.calendar-area',
        popover: {
          title: '날짜 선택',
          description: `<span style="color: red; font-weight: bold;">${date}일에는 경기가 없습니다.<br>다른 날짜를 선택해주세요.</span>`,
          side: 'right',
          align: 'start',
          onNext: () => {
            // 모달 닫기 후 날짜 선택 튜토리얼 재시작
            if (driverObj.value) {
              driverObj.value.destroy();
              driverObj.value = null;
            }
            setTimeout(() => {
              // 날짜 선택 튜토리얼 재시작
              startCalendarTutorial();
            }, 100);
            return false;
          }
        }
      }
    ]
  });
  
  // 모달 실행
  driverObj.value.drive();
}


// 날짜 관련
// 날짜 선택했을 때, 경기가 있으면 경기 목록 튜토리얼 시작, 없으면 경기 없음 안내 모달 표시
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
    console.log('gamesOnDate.value : ', gamesOnDate.value);
    // 날짜 선택 후 캘린더 튜토리얼이 활성화되어 있다면 종료하고 경기 목록 튜토리얼 시작
    if (driverObj.value) {
      driverObj.value.destroy();
      // 경기 데이터가 로드된 후 튜토리얼 시작
      setTimeout(() => {
        if (data && data.length > 0) {
          startGameListTutorial();
        } else {
          // 경기가 없는 경우 경고 모달 표시
          showNoGameModal(date);
        }
      }, 500);
    }
  } catch (error) {
    console.error('응모/경기조회 에러:', error);
    gamesOnDate.value = [];
  }

  loadGamesForDate(date);
}

// 응모하기 눌렀을 때, 튜토리얼 제거
async function handleApplyClick(game) {
  selectedGame.value = game;
  console.log('응모 클릭됨')
  
  // 모든 튜토리얼 종료 및 이벤트 리스너 제거
  if (driverObj.value) {
    driverObj.value.destroy();
    driverObj.value = null;
  }
  
  // 날짜 클릭 이벤트 리스너 제거 (튜토리얼 방지)
  const existingHandlers = document.querySelectorAll('*');
  existingHandlers.forEach(element => {
    element.removeEventListener('click', () => {});
  });
  
  try {
    console.log('응모 클릭됨', game)
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

// 팀 ENUM을 한글 팀 이름으로 변환
function getTeamDisplayName(enumName) {
  return enumToTeamName[enumName] || enumName;
}

// 메인 페이지로 이동
function goToMainPage() {
  // 메인 페이지로 이동
  router.push('/');
}

// 경기 날짜와 시간을 'YYYY-MM-DD HH:mm' 형식으로 변환
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

<style>
/* Global tutorial styles */
:global(.driver-popover.tutorial-popover) {
  border-radius: 10px;
  max-width: 220px;
  width: 220px;
  z-index: 10001;
}


:global(.driver-popover.tutorial-popover .driver-popover-title) {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}

:global(.driver-popover.tutorial-popover .driver-popover-description) {
  font-size: 15px;
  line-height: 1.5;
}

:global(.driver-popover.tutorial-popover .driver-popover-arrow) {
  display: none;
}

:global(.driver-popover.tutorial-popover .driver-popover-footer) {
  margin-top: 15px;
  text-align: right;
}

:global(.driver-popover.tutorial-popover .driver-popover-footer button) {
  background: #d32f2f;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

:global(.driver-popover.tutorial-popover .driver-popover-footer button:hover) {
  background: #b71c1c;
}

/* 다음 버튼 팝오버 스타일 - 우선순위 높임 */
:global(.driver-popover.tutorial-popover.next-btn-popover) {
  background: white !important;
  border-radius: 12px !important;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15) !important;
  border: none !important;
  max-width: 200px !important;
  width: 200px !important;
  padding: 16px !important;
  position: fixed !important;
}

:global(.driver-popover.tutorial-popover.next-btn-popover .driver-popover-title) {
  font-size: 16px !important;
  font-weight: 600 !important;
  color: #333 !important;
  margin-bottom: 8px !important;
  text-align: center !important;
}

:global(.driver-popover.tutorial-popover.next-btn-popover .driver-popover-description) {
  font-size: 14px !important;
  color: #666 !important;
  line-height: 1.4 !important;
  text-align: center !important;
  margin-bottom: 16px !important;
}

:global(.driver-popover.tutorial-popover.next-btn-popover .driver-popover-footer) {
  text-align: center !important;
  margin-top: 0 !important;
}

:global(.driver-popover.tutorial-popover.next-btn-popover .driver-popover-footer button) {
  background: #FF9500 !important;
  color: white !important;
  border: none !important;
  padding: 10px 20px !important;
  border-radius: 8px !important;
  cursor: pointer !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  min-width: 60px !important;
}

:global(.driver-popover.tutorial-popover.next-btn-popover .driver-popover-footer button:hover) {
  background: #E6850E !important;
}

:global(.driver-popover.tutorial-popover.next-btn-popover .driver-popover-close-btn) {
  position: absolute !important;
  top: 8px !important;
  right: 8px !important;
  background: transparent !important;
  border: none !important;
  font-size: 18px !important;
  color: #999 !important;
  cursor: pointer !important;
  width: 24px !important;
  height: 24px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

:global(.driver-popover.tutorial-popover.next-btn-popover .driver-popover-close-btn:hover) {
  color: #666 !important;
}

/* calendar-logo 영역을 시각적으로 제외하는 스타일 */
:global(.driver-highlighted[data-highlighted-element=".calendar-area"] .calendar-logo) {
  position: relative;
  z-index: 10002 !important;
  background: rgba(0, 0, 0, 0.7) !important;
  pointer-events: none;
}

/* driver.js 오버레이에서 calendar-logo 영역만 어둡게 처리 */
:global(.driver-active .calendar-logo::before) {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  z-index: 10002;
  pointer-events: none;
}

/* 각 튜토리얼 모달별 고유 스타일 */

/* 날짜 선택 모달 */
:global(.tutorial-calendar-modal) {
  border: 2px solid #2196F3;
  box-shadow: 0 4px 20px rgba(33, 150, 243, 0.3);
}

:global(.tutorial-calendar-modal .driver-popover-title) {
  color: #2196F3;
  font-weight: bold;
}

:global(.tutorial-calendar-modal .driver-popover-done-btn) {
  background: #2196F3;
  color: white;
}

:global(.tutorial-calendar-modal .driver-popover-done-btn:hover) {
  background: #1976D2;
}

/* 경기 목록 모달 */
:global(.tutorial-game-list-modal) {
  border: 2px solid #4CAF50;
  box-shadow: 0 4px 20px rgba(76, 175, 80, 0.3);
}

:global(.tutorial-game-list-modal .driver-popover-title) {
  color: #4CAF50;
  font-weight: bold;
}

:global(.tutorial-game-list-modal .driver-popover-done-btn),
:global(.tutorial-game-list-modal .driver-popover-prev-btn) {
  background: #4CAF50;
  color: white;
}

:global(.tutorial-game-list-modal .driver-popover-done-btn:hover),
:global(.tutorial-game-list-modal .driver-popover-prev-btn:hover) {
  background: #388E3C;
}

/* 경기 없음 모달 */
:global(.tutorial-no-game-modal) {
  border: 2px solid #f44336;
  box-shadow: 0 4px 20px rgba(244, 67, 54, 0.3);
}

:global(.tutorial-no-game-modal .driver-popover-title) {
  color: #f44336;
  font-weight: bold;
}

:global(.tutorial-no-game-modal .driver-popover-description) {
  color: #d32f2f;
}

:global(.tutorial-no-game-modal .driver-popover-done-btn) {
  background: #f44336;
  color: white;
}

:global(.tutorial-no-game-modal .driver-popover-done-btn:hover) {
  background: #d32f2f;
}

:global(.driver-highlighted-element) {
  border: 3px solid #d32f2f !important;
  border-radius: 8px;
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.5);
}

:global(.driver-active-element) {
  position: relative;
  z-index: 10000;
}
</style>

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
  margin: 20px 0 20px;
  gap: 0 5px;
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
  color: var(--theme-primary, #ff6b35);
}
.step span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--theme-primary, #ff6b35);
  opacity: 0.25;
  color: #fff;
  font-weight: bold;
  margin-bottom: 6px;
  font-size: 18px;
}
.step.active span {
  background: var(--theme-primary, #ff6b35);
  opacity: 1;
}
.bar {
  width: 60px;
  height: 4px;
  background: var(--theme-primary, #ff6b35);
  border-radius: 2px;
  margin-bottom: 35px;
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
  margin: 0px 0 30px 0;
  color: #222;
  letter-spacing: -1.5px;
  text-align: center;
  width: 100%;
  max-width: 700px;
}
.team-bg-container {
  width: 650px;
  height: 200px;
  margin: 0 auto 30px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.team-bg-container.active {
  border: 1px solid #d6623c !important;
  border-radius: 16px !important;
  box-shadow: 
    0 5px 10px rgba(0, 0, 0, 0.2), /* 깊이감 있는 그림자 */
    inset 0 1px 1px rgba(255, 255, 255, 0.4), /* 상단 하이라이트 */
    inset 0 -2px 1px rgba(0, 0, 0, 0.1); /* 하단 음영 */
  transform: translateY(-3px);
  transition: all 0.2s ease-out;
}

.team-bg-container.active:hover {
  transform: translateY(-1px);
  box-shadow: 
    0 2px 5px rgba(0, 0, 0, 0.2), 
    inset 0 1px 1px rgba(255, 255, 255, 0.4), 
    inset 0 -2px 1px rgba(0, 0, 0, 0.1);
}

.team-bg-image {
  width: 99%;
  height: 99%;
  border-radius: 16px;
  opacity: 0.25;
  transition: opacity 0.3s;
  background-position: center !important;
  background-repeat: no-repeat !important;
  background-size: 105% !important;
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
  height: 110px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  z-index: 1;
  padding: 15px;
  box-sizing: border-box;
  opacity: 0.5;
  /* Glass 3D Button Style */
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  transform: translateY(0);
  box-shadow:
    0 8px 15px rgba(0, 0, 0, 0.1),
    0 4px 6px rgba(0, 0, 0, 0.05),
    inset 0 1px 0 rgba(255, 255, 255, 0.4);
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.team-card-image {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}

.team-card-image img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.team-card-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  text-align: center;
  line-height: 1.2;
}
.team-card.selected,
.team-card:hover {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(229, 115, 115, 0.5);
  box-shadow:
    0 12px 20px rgba(0, 0, 0, 0.15),
    0 6px 8px rgba(0, 0, 0, 0.1);
  opacity: 1;
}

.team-card:active {
  transform: translateY(1px);
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  opacity: 1;
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
  height: fit-content;
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
  color: var(--theme-primary, #ff6b35);
  text-align: center;
  min-width: 200px;
}
.month-nav-btn {
  background: var(--theme-primary, #ff6b35);
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
  background: var(--theme-gradient, #ff6b35);
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
  font-family: "Poppins", sans-serif;
  font-weight: 500;
  font-style: normal;
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
  background: var(--theme-gradient, #ff6b35) !important;
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
  background: var(--theme-gradient, #ff6b35) !important;
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
  display: flex;
  margin-top: 10px;
  align-items: center;
  justify-content: flex-end;
  width: 100%;
  padding-bottom: 10px;
  padding-right: 20px;
}
.calendar-logo img {
  height: 36px;
  width: auto;
  margin-left: 0;
  display: block;
}
.info-area {
  width: 420px;
  display: flex;
  flex-direction: column;
  gap: 28px;
}
.selected-date-box {
  background: var(--theme-background, #fff);
  border-radius: 14px;
  width: 400px;
  height: 200px;
  box-shadow: 0 2px 16px 0 #f8bbd04d;
  padding: 20px 20px 16px 20px;
}
.selected-date-box b {
  color: var(--theme-primary, #e57373);
  font-size: 30px;
}
.selected-team {
  margin-top: 20px;
  font-size: 15px;
  color: #888;
}
.selected-team-name {
  color: var(--theme-primary, #e57373);
  font-weight: bold;
}
.game-list-box {
  background: var(--theme-background, #fff);
  border-radius: 14px;
  box-shadow: 0 2px 16px 0 #f8bbd04d;
  padding: 20px 20px 20px 20px;
  width: 400px;
  height: 260px;
}
.game-list-box b {
  color: var(--theme-primary, #e57373);
  font-size: 16px;
}
.game-card {
  margin: 16px 0;
  padding: 14px 18px;
  background: var(--theme-background, #fff3e0);
  border-radius: 10px;
  box-shadow: 0 2px 8px 0 #f8bbd04d;
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.game-title {
  font-size: 19px;
  font-weight: 700;
  color: var(--theme-primary, #e57373);
}
.game-datetime {
  font-size: 18px;
  margin-top: 10px;
  color: #888;
}
.game-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}
.apply-btn {
  padding: 7px 20px;
  background: var(--theme-gradient, #e57373);
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 20px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.apply-btn:hover {
  background: var(--theme-gradient, #e57373);
  box-shadow: 0 10px 20px 0 #f8bbd04d;
  transform: translateY(-2px);
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
  background: var(--theme-background, #fff);
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
  color: var(--theme-gradient, #e57373);
}
.apply-complete-stepper .step span {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: var(--theme-primary, #e57373);
  color: #fff;
  font-weight: bold;
  margin-bottom: 4px;
  font-size: 15px;
}
.apply-complete-stepper .step.active span {
  background: var(--theme-gradient, #e57373);
}
.apply-complete-stepper .bar {
  width: 36px;
  height: 3px;
  background: var(--theme-gradient, #e57373);
  border-radius: 2px;
}
.apply-icon-box {
  margin-bottom: 10px;
}
.apply-icon-circle {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--theme-gradient, #e57373);
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
  background: var(--theme-gradient, #e57373);
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
  background: var(--theme-gradient, #e57373);
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
  background: var(--theme-gradient, #e57373);
}
.no-game-centered {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 120px;
  font-size: 22px;
  color: #555;
}

/* Responsive design */
@media (max-width: 768px) {
  .apply-wrapper {
    padding: 0 16px;
  }
  
  .stepper {
    margin: 20px 0 16px;
    gap: 0 8px;
  }
  
  .step {
    font-size: 12px;
  }
  
  .step span {
    width: 28px;
    height: 28px;
    font-size: 16px;
  }
  
  .bar {
    width: 40px;
    height: 3px;
  }
  
  .title {
    font-size: 24px;
    margin: 0 0 20px 0;
    padding: 0 16px;
  }
  
  .team-bg-container {
    width: 100%;
    max-width: 400px;
    height: 80px;
    margin: 0 auto 20px auto;
  }
  
  .team-bg-image {
    width: 100%;
    height: 100px;
    background-position: center !important;
    background-repeat: no-repeat !important;
    background-size: contain !important;
  }
  
  .team-list.team-list-row {
    gap: 12px;
    margin-bottom: 16px;
    width: 100%;
    padding: 0 16px;
  }
  
  .team-row {
    flex-direction: column;
    gap: 12px;
    width: 100%;
  }
  
  .team-card {
    width: 100%;
    height: 80px;
    font-size: 18px;
  }
  
  .next-btn {
    padding: 12px 32px;
    font-size: 16px;
  }
  
  /* Game selection responsive */
  .game-select-main {
    flex-direction: column;
    height: auto;
    gap: 24px;
    margin-bottom: 24px;
    padding: 0 16px;
  }
  
  .calendar-area {
    width: 100%;
    max-width: 400px;
    height: auto;
    min-height: 450px;
    padding: 20px 16px;
  }
  
  .calendar-header-section {
    gap: 16px;
    margin-bottom: 16px;
  }
  
  .calendar-title {
    font-size: 24px;
    min-width: 160px;
  }
  
  .month-nav-btn {
    width: 36px !important;
    height: 36px !important;
    min-width: 36px !important;
    min-height: 36px !important;
    max-width: 36px !important;
    max-height: 36px !important;
    font-size: 18px;
    flex-basis: 36px !important;
  }
  
  .calendar-grid {
    gap: 12px;
    margin-bottom: 16px;
  }
  
  .calendar-header {
    font-size: 18px;
  }
  
  .calendar-cell {
    width: 32px;
    height: 32px;
    font-size: 18px;
  }
  
  .calendar-logo {
    padding-bottom: 8px;
    padding-left: 16px;
  }
  
  .calendar-logo img {
    height: 32px;
  }
  
  .info-area {
    width: 100%;
    max-width: 400px;
    gap: 20px;
  }
  
  .selected-date-box {
    width: 100%;
    height: auto;
    min-height: 160px;
    padding: 16px;
  }
  
  .selected-date-box b {
    font-size: 24px;
  }
  
  .selected-team {
    font-size: 14px;
  }
  
  .game-list-box {
    width: 100%;
    height: auto;
    min-height: 200px;
    padding: 16px;
  }
  
  .game-list-box b {
    font-size: 20px;
  }
  
  .game-card {
    margin: 12px 0;
    padding: 12px 16px;
    gap: 4px;
  }
  
  .game-title {
    font-size: 16px;
  }
  
  .game-desc {
    font-size: 16px;
    margin-top: 8px;
  }
  
  .apply-btn {
    margin-top: 20px;
    padding: 8px 16px;
    font-size: 16px;
  }
  
  .no-game {
    font-size: 18px;
    margin-top: 40px;
  }
  
  .no-game-centered {
    min-height: 100px;
    font-size: 18px;
  }
  
  /* Apply complete overlay responsive */
  .apply-complete-card {
    width: 90%;
    max-width: 400px;
    padding: 24px 20px 20px 20px;
    margin: 0 16px;
  }
  
  .apply-complete-stepper {
    gap: 0 8px;
    margin-bottom: 16px;
  }
  
  .apply-complete-stepper .step {
    font-size: 11px;
  }
  
  .apply-complete-stepper .step span {
    width: 24px;
    height: 24px;
    font-size: 14px;
  }
  
  .apply-complete-stepper .bar {
    width: 28px;
    height: 2px;
  }
  
  .apply-icon-circle {
    width: 40px;
    height: 40px;
    font-size: 24px;
  }
  
  .apply-title {
    font-size: 20px;
    margin: 18px 0 4px 0;
  }
  
  .apply-desc {
    font-size: 13px;
    margin-bottom: 18px;
  }
  
  .apply-info-card {
    padding: 16px;
    margin-bottom: 16px;
    width: 100%;
    height: auto;
    min-height: 160px;
  }
  
  .apply-info-title {
    font-size: 24px;
    margin-bottom: 6px;
  }
  
  .apply-info-row {
    font-size: 16px;
    margin-bottom: 3px;
  }
  
  .apply-confirm-btn {
    width: 100%;
    padding: 12px 0;
    font-size: 16px;
    margin-top: 6px;
  }
}

@media (max-width: 480px) {
  .apply-wrapper {
    padding: 0 12px;
  }
  
  .title {
    font-size: 22px;
    padding: 0 8px;
  }
  
  .team-bg-container {
    max-width: 320px;
    height: 70px;
  }
  
  .team-bg-image {
    height: 80px;
    background-position: center !important;
    background-repeat: no-repeat !important;
    background-size: contain !important;
  }
  
  .team-list.team-list-row {
    padding: 0 8px;
  }
  
  .team-card {
    height: 70px;
    font-size: 16px;
  }
  
  .game-select-main {
    padding: 0 8px;
  }
  
  .calendar-area {
    max-width: 320px;
    padding: 16px 12px;
  }
  
  .calendar-title {
    font-size: 20px;
    min-width: 140px;
  }
  
  .month-nav-btn {
    width: 32px !important;
    height: 32px !important;
    min-width: 32px !important;
    min-height: 32px !important;
    max-width: 32px !important;
    max-height: 32px !important;
    font-size: 16px;
    flex-basis: 32px !important;
  }
  
  .calendar-grid {
    gap: 8px;
  }
  
  .calendar-header {
    font-size: 16px;
  }
  
  .calendar-cell {
    width: 28px;
    height: 28px;
    font-size: 16px;
  }
  
  .info-area {
    max-width: 320px;
  }
  
  .selected-date-box {
    padding: 12px;
  }
  
  .selected-date-box b {
    font-size: 20px;
  }
  
  .game-list-box {
    padding: 12px;
  }
  
  .game-list-box b {
    font-size: 18px;
  }
  
  .game-card {
    padding: 10px 12px;
  }
  
  .game-title {
    font-size: 15px;
  }
  
  .game-desc {
    font-size: 14px;
  }
  
  .apply-btn {
    font-size: 15px;
  }
  
  .apply-complete-card {
    width: 95%;
    padding: 20px 16px 16px 16px;
    margin: 0 8px;
  }
  
  .apply-info-title {
    font-size: 20px;
  }
  
  .apply-info-row {
    font-size: 14px;
  }
}

@media (min-width: 769px) and (max-width: 1024px) {
  .team-list.team-list-row {
    gap: 16px;
  }
  
  .team-row {
    gap: 20px;
  }
  
  .team-card {
    width: 160px;
    height: 90px;
    font-size: 18px;
  }
  
  .game-select-main {
    gap: 32px;
  }
  
  .calendar-area {
    width: 400px;
    height: 500px;
  }
  
  .info-area {
    width: 300px;
  }
  
  .selected-date-box {
    width: 320px;
    height: 180px;
  }
  
  .game-list-box {
    width: 320px;
    height: 240px;
  }
}
</style>
