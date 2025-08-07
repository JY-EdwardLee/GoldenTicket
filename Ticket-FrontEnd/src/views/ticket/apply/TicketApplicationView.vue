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
          :class="{ 'active': selectedTeam === enumToTeamName[user.myTeam] || bgHover }"
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
      <button class="next-btn" :disabled="!selectedTeam" @click="goToNextStep">{{ pageText.nextBtn }}</button>
    </div>
    <div v-else-if="step === 2" class="game-select-main">
      <div class="calendar-area">
        <div class="calendar-header-section">
          <button class="month-nav-btn" @click="previousMonth"><</button>
          <div class="calendar-title">{{ currentYear }}년 {{ currentMonth }}월</div>
          <button class="month-nav-btn" @click="nextMonth">></button>
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
          <img :src="`@@/assets/logo/${user.myTeam}.png`" alt="landers logo" />
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
import { ref, onMounted, computed, onBeforeUnmount } from 'vue';
import axios from 'axios';
import API_CONFIG from '@/config/api.config';
import { stadiumNameToEnum } from '@/utils/teamStadium';
import { teamNameToEnum, enumToTeamName } from '@/utils/teamNameMap';
import http from '@/utils/http'
import { useRouter } from 'vue-router';
import { driver } from 'driver.js';
import 'driver.js/dist/driver.css';
import { useTeamThemeStore } from '@/stores/teamTheme.js'

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
const currentYear = 2025;
const currentMonth = ref(8); // 8월부터 시작
const today = new Date(); // 2025-08-04 (월은 0부터 시작)

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
};

function selectTeam(team) {
  selectedTeam.value = team;
  
  // 로컬 스토리지에 선택된 팀 저장
  localStorage.setItem('selectedTeam', team);
  
  // 선택된 팀 강조 표시 업데이트
  document.querySelectorAll('.team-card').forEach(card => {
    if (card.textContent === team) {
      card.classList.add('selected');
    } else {
      card.classList.remove('selected');
    }
  });
  
  // 팀 배경 이미지 업데이트 - SSG 팀일 때만 active 클래스 추가
  const teamBg = document.querySelector('.team-bg-image');
  if (teamBg) {
    if (team === 'SSG랜더스') {
      teamBg.classList.add('active');
    } else {
      teamBg.classList.remove('active');
    }
  }
  
  // 다음 버튼 활성화
  const nextButton = document.querySelector('.next-btn');
  if (nextButton) {
    nextButton.disabled = false;
  }
  
  // 팀 선택 팝오버의 설명문 업데이트
  updateTeamSelectionPopup();
  
  console.log('팀 선택됨:', team); // 디버깅용 로그
}

// 팀 선택 팝오버의 설명문을 업데이트하는 함수
function updateTeamSelectionPopup() {
  if (!driverObj.value) return;
  
  // 현재 활성화된 팝오버 요소 찾기
  const activePopup = document.querySelector('.driver-popover.driver-active-popup');
  if (!activePopup) return;
  
  // 설명문 요소 찾기
  const descriptionEl = activePopup.querySelector('.driver-popover-description');
  if (descriptionEl) {
    descriptionEl.textContent = `원하는 팀을 선택해주세요. (현재 선택: ${selectedTeam.value})`;
  }
}

// 달력 관련 함수들
function isPastDate(date) {
  const checkDate = new Date(currentYear, currentMonth.value - 1, date);
  return checkDate < today;
}

function isToday(date) {
  const today = new Date();
  const checkDate = new Date(currentYear, currentMonth.value - 1, date);
  const todayDate = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const compareDate = new Date(checkDate.getFullYear(), checkDate.getMonth(), checkDate.getDate());
  return compareDate.getTime() === todayDate.getTime();
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

// 캘린더 튜토리얼 시작
const startCalendarTutorial = () => {
  if (driverObj.value) {
    driverObj.value.destroy();
  }
  
  // 캘린더가 완전히 로드될 때까지 대기
  setTimeout(() => {
    driverObj.value = driver({
      showProgress: false,
      overlayColor: 'rgba(0, 0, 0, 0.7)',
      animate: 300,
      allowClose: true,
      doneBtnText: '완료',
      nextBtnText: '확인',
      prevBtnText: '이전',
      
      onPopoverRender: (popover, { config, state }) => {
        if (popover && typeof popover.querySelector === 'function') {
          const popoverElement = popover.querySelector('.driver-popover');
          if (popoverElement) {
            popoverElement.classList.add('tutorial-popover');
          }
        }
      },
      
      steps: [
        {
          element: '.calendar-area',
          popover: {
            title: '날짜 선택',
            description: '캘린더에서 원하는 경기 날짜를 선택해주세요. 날짜를 클릭하면 해당 날짜의 경기 목록이 표시됩니다.',
            side: 'right',
            align: 'start'
          }
        }
      ]
    });
    
    driverObj.value.drive();
  }, 300);
};

// 경기 목록 튜토리얼 시작
const startGameListTutorial = () => {
  if (driverObj.value) {
    driverObj.value.destroy();
  }
  
  setTimeout(() => {
    driverObj.value = driver({
      showProgress: true,
      overlayColor: 'rgba(0, 0, 0, 0.7)',
      animate: 300,
      allowClose: true,
      doneBtnText: '완료',
      nextBtnText: '응모하기',
      prevBtnText: '이전',
      
      onPopoverRender: (popover, { config, state }) => {
        if (popover && typeof popover.querySelector === 'function') {
          const popoverElement = popover.querySelector('.driver-popover');
          if (popoverElement) {
            popoverElement.classList.add('tutorial-popover');
          }
        }
      },
      
      onNextClick: () => {
        // 응모하기 버튼 클릭
        const applyButton = document.querySelector('.game-card .apply-btn');
        if (applyButton) {
          applyButton.click();
        }
        return false; // 기본 next 동작 방지
      },
      
      steps: [
        {
          element: '.game-list',
          popover: {
            title: '경기 선택',
            description: '선택한 날짜의 경기 목록입니다. 응모하고 싶은 경기의 응모하기 버튼을 클릭해주세요.',
            side: 'left',
            align: 'start',
            showButtons: ['next'], // 이전 버튼 숨김
            onNextClick: () => {
              const applyButton = document.querySelector('.game-card .apply-btn');
              if (applyButton) {
                applyButton.click();
              }
              return false;
            }
          }
        }
      ]
    });
    
    driverObj.value.drive();
  }, 300);
};

// 초기 팀 선택 튜토리얼 시작
const startTutorial = () => {
  if (driverObj.value) {
    driverObj.value.destroy();
  }

  // 다음 버튼 클릭 핸들러
  const handleNextClick = (fromTutorial = false) => {
    console.log('핸들러 호출:', { fromTutorial, currentStep: step.value });
    
    // 팀이 선택되어 있는지 확인
    if (!selectedTeam.value) {
      selectedTeam.value = 'SSG랜더스'; // 기본값 설정
    }
    
    const currentStep = step.value;
    
    // 튜토리얼에서 호출된 경우
    if (fromTutorial) {
      // 튜토리얼 먼저 종료
      if (driverObj.value) {
        driverObj.value.destroy();
        driverObj.value = null;
      }
      
      // 직접 step 변경
      if (currentStep === 1) {
        step.value = 2;
        // DOM 업데이트 후 캘린더 튜토리얼 시작
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
      
      // 단계가 1에서 2로 변경되었을 때 캘린더 튜토리얼 시작
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

  driverObj.value = driver({
    showProgress: false,
    overlayColor: 'rgba(0, 0, 0, 0.7)',
    animate: 300,
    allowClose: true,
    doneBtnText: '완료',
    nextBtnText: '다음',
    prevBtnText: '이전',
    
    // 단계별 커스텀 이벤트 처리
    onNextClick: (element, step, { state }) => {
      console.log('글로벌 onNextClick 호출, activeStep:', state.activeStep);
      
      if (state.activeStep === 0) {
        // 팀 선택 단계: 다음 단계로 이동
        return true; // 기본 동작 허용
      } else if (state.activeStep === 1) {
        // 다음 버튼 단계: goToNextStep 호출
        return goToNextStep();
      }
      return true;
    },
    
    onPopoverRender: (popover, { config, state }) => {
      // 팝오버가 DOM 요소인지 확인
      if (popover && typeof popover.querySelector === 'function') {
        // 팝오버가 렌더링될 때마다 클래스 추가
        const popoverElement = popover.nodeType === 1 ? popover : popover.querySelector('.driver-popover');
        if (popoverElement && popoverElement.classList) {
          popoverElement.classList.add('tutorial-popover');
          
          // 팀 선택 팝오버의 설명문을 실시간으로 업데이트
          if (state.activeStep === 0) { // 팀 선택 단계일 때만
            const descriptionEl = popoverElement.querySelector('.driver-popover-description');
            if (descriptionEl) {
              descriptionEl.textContent = `원하는 팀을 선택해주세요. (현재 선택: ${selectedTeam.value})`;
            }
          }
        }
        
        // 모든 버튼 찾기 (여러 가지 셀렉터 시도)
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
    
    steps: [
      {
        element: '.team-list',
        popover: {
          title: '팀 선택',
          description: '원하는 팀을 선택해주세요.',
          side: 'right',
          align: 'start'
        }
      },
      {
        element: '.next-btn',
        popover: {
          title: '다음 단계',
          description: '다음 버튼을 눌러주세요.',
          side: 'left',
          align: 'start'
        }
      }
    ]
  });
  
  // 튜토리얼 시작
  driverObj.value.drive();
};

// 다음 단계로 이동하는 함수
const goToNextStep = () => {
  console.log('goToNextStep called', { currentStep: step.value, hasSelectedTeam: !!selectedTeam.value });
  
  if (step.value === 1 && selectedTeam.value) {
    // 현재 튜토리얼이 있다면 종료
    if (driverObj.value) {
      driverObj.value.destroy();
      driverObj.value = null;
    }
    
    // 단계 변경
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
  const urlParams = new URLSearchParams(window.location.search);
  const tutorialParam = urlParams.get('tutorial');
  
  if (tutorialParam === 'true') {
    // 약간의 지연 후 튜토리얼 시작 (컴포넌트 렌더링 완료 대기)
    setTimeout(() => {
      startTutorial();
    }, 800); // 로딩 시간을 좀 더 여유롭게 조정
  }
  
  // 현재 날짜 가져오기
  const today = new Date();
  const currentDay = today.getDate();
  const todayMonth = today.getMonth() + 1; // 0부터 시작하므로 +1
  
  if (currentMonth.value === todayMonth) { // 현재 월인 경우
    selectedDate.value = currentDay; // 실제 현재 날짜 자동 선택
    
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
    
    // 날짜 선택 후 캘린더 튜토리얼이 활성화되어 있다면 종료하고 경기 목록 튜토리얼 시작
    if (driverObj.value) {
      driverObj.value.destroy();
      // 경기 데이터가 로드된 후 튜토리얼 시작
      setTimeout(() => {
        if (data && data.length > 0) {
          startGameListTutorial();
        }
      }, 500);
    }
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

<style>
/* Global tutorial styles */
:global(.driver-popover.tutorial-popover) {
  border-radius: 10px;
  max-width: 320px;
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
  width: 600px;
  height: 180px;
  margin: 0 auto 30px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}

.team-bg-container.active {
  border: 2px solid var(--theme-gradient, #e57373) !important;
}

.team-bg-image {
  width: 600px;
  height: 200px !important;
  border-radius: 16px;
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
.game-desc {
  font-size: 18px;
  margin-top: 10px;
  color: #888;
}
.apply-btn {
  margin-top: 30px;
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
