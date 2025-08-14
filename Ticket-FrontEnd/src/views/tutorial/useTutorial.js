import { ref, watch } from 'vue';
import { driver } from 'driver.js';
import 'driver.js/dist/driver.css';
import { useTeamThemeStore } from '@/stores/teamTheme';

const teamThemeStore = useTeamThemeStore;
const teamGradient = teamThemeStore.teamColors[teamThemeStore.selectedTeam]?.gradient;
const teamPrimary = teamThemeStore.teamColors[teamThemeStore.selectedTeam]?.primary;
document.documentElement.style.setProperty('--theme-gradient', teamGradient);
document.documentElement.style.setProperty('--theme-primary', teamPrimary);

// 팀 변경 감지
watch(() => teamThemeStore.selectedTeam, (newTeam) => {
  if (newTeam) {
    const gradient = teamThemeStore.teamColors[newTeam]?.gradient;
    const primary = teamThemeStore.teamColors[newTeam]?.primary;
    document.documentElement.style.setProperty('--theme-gradient', gradient);
    document.documentElement.style.setProperty('--theme-primary', primary);
  }
});



// driver.js 닫기 버튼의 포커스 아웃라인 제거 및 팝오버 크기 조정
const style = document.createElement('style');
style.textContent = `
  .driver-popover-close-btn{
    outline: none !important;
    box-shadow: none !important;
    border: none !important;
  }
  .driver-popover-close-btn:focus {
    outline: none !important;
    box-shadow: none !important;
    border: none !important;
  }
  
  .driver-popover-close-btn:focus-visible {
    outline: none !important;
    box-shadow: none !important;
    border: none !important;
  }
  .driver-popover-close-btn:hover {
  transform: none !important;
  box-shadow: none !important;
  background-color: transparent !important;
}
  
  /* 팝오버 크기 조정 */
  .driver-popover:not(.transfer-tutorial-popover){
    max-width: 400px !important;
    width: 400px !important;
    max-height: 200px !important;
    height: 200px !important;
    padding: 20px !important;
  }

  /* 양도 튜토리얼 시작 팝오버 스타일 */
  .driver-popover.transfer-tutorial-popover {
    max-width: 200px !important;
    max-height: 100px !important;
    width: 200px !important;
    height: 100px !important;
    padding: 20px !important;
  }

  /* 티켓 선택 튜토리얼 팝오버 스타일 */
  .driver-popover.ticket-select-tutorial-popover {
    max-width: 200px !important;
    max-height: 150px !important;
    width: 200px !important;
    height: 150px !important;
    padding: 20px !important;
  }

  // 띄어쓰기 구현
  .driver-popover.ticket-select-tutorial-popover .driver-popover-description {
    white-space: pre-line !important;
    word-break: keep-all !important;
  }

  // 플랫폼 선택 튜토리얼 팝오버 스타일
  /* 기존 스타일을 이렇게 수정해보세요 */
  .driver-popover.platform-popover {
    position: fixed !important;
    top: 50% !important;
    // left: calc(50% + 150px) !important;
    transform: translateY(-50%) !important;
  }

  /* 인라인 스타일을 오버라이드하기 위한 추가 조치 */
  .driver-popover.platform-popover[style] {
    top: 39% !important;
    // left: calc(50% + 150px) !important;
    transform: translateY(-50%) !important;
  }

  /* 양도 확인 튜토리얼 팝오버 스타일 */
  .driver-popover.transferConfirm {
    max-width: 250px !important;
    max-height: 150px !important;
    width: 250px !important;
    height: 150px !important;
    padding: 20px !important;
  }


`;
document.head.appendChild(style);

// 전역 상태로 튜토리얼 모달 관리
const showTutorialModal = ref(false);
// 전역 변수로 driver 인스턴스 저장
let tutorialDriver = null;

// 스크롤 잠금 유틸리티 (튜토리얼 중에만 사용)
let __scrollLocked = false;
let __lockedScrollY = 0;
function lockScroll(targetY) {
  if (__scrollLocked) return;
    const current = window.scrollY || window.pageYOffset || 0;
    const y = typeof targetY === 'number' ? Math.max(0, targetY) : current;
    if (y !== current) {
      window.scrollTo(0, y);
    }
    __lockedScrollY = y;
    const b = document.body;
    b.style.position = 'fixed';
    b.style.top = `-${__lockedScrollY}px`;
    b.style.left = '0';
    b.style.right = '0';
    b.style.width = '100%';
    b.style.overflow = 'hidden';
  __scrollLocked = true;
}
function unlockScroll() {
  if (!__scrollLocked) return;
    const b = document.body;
    const y = Math.abs(parseInt(b.style.top || '0', 10)) || 0;
    b.style.position = '';
    b.style.top = '';
    b.style.left = '';
    b.style.right = '';
    b.style.width = '';
    b.style.overflow = '';
    __scrollLocked = false;
  // 원래 위치로 복구
    window.scrollTo(0, y);
}

export function useTutorial() {
  // 튜토리얼 모달 열기
  const openTutorialModal = () => {
    showTutorialModal.value = true;
  };

  // 날짜 선택 + 경기 선택 통합 튜토리얼
  // startAt: 'calendar' | 'game' (기본 'game'에서 시작하여 Prev 버튼을 바로 노출)
  const startApplyGameSelectTutorial = (startAt = 'game') => {
    // 기존 드라이버 정리
    if (tutorialDriver) {
      try { tutorialDriver.destroy(); } catch (_) {}
      tutorialDriver = null;
    }

    setTimeout(() => {
      tutorialDriver = driver({
        showProgress: false,
        overlayColor: 'rgba(0, 0, 0, 0.7)',
        animate: 300,
        allowClose: true,
        doneBtnText: '완료',
        nextBtnText: '다음',
        prevBtnText: '이전',
        showButtons: ['close', 'next', 'prev'],
        popoverClass: 'tutorial-game-list-modal',
        steps: [
          {
            element: '.calendar-area',
            popover: {
              title: '날짜 선택',
              description: '원하는 날짜를 클릭하세요. 다음으로 넘어가면 경기 목록이 표시됩니다.',
              side: 'right',
              align: 'start'
            }
          },
          {
            element: '.game-list-box',
            popover: {
              title: '경기 선택',
              description: '선택한 날짜의 경기 목록입니다. 원하는 경기의 "응모하기" 버튼을 클릭하세요.',
              side: 'left',
              align: 'start'
            }
          }
        ],
        onDestroyed: () => {
          try { unlockScroll(); } catch (_) {}
          tutorialDriver = null;
        }
      });

      tutorialDriver.drive();
      // game 스텝부터 시작하도록 이동 (Prev 버튼이 즉시 보임)
      if (startAt === 'game') {
        setTimeout(() => {
          try { tutorialDriver.moveNext(); } catch (_) {}
        }, 50);
      }
    }, 300);
  };

  // 특정 모달(reactive boolean)이 열려있는 동안만 스크롤 고정
  // 사용법: const stop = bindScrollLock(showModalRef, 120); // 필요 시 stop()
  const bindScrollLock = (modalVisibleRef, lockY = undefined) => {
    if (!modalVisibleRef || typeof modalVisibleRef !== 'object' || !('value' in modalVisibleRef)) {
      console.warn('bindScrollLock: modalVisibleRef는 ref여야 합니다.');
      return () => {};
    }
    const stop = watch(
      modalVisibleRef,
      (v) => { if (v) lockScroll(lockY); else unlockScroll(); },
      { immediate: false } // 변경
    )
    return () => {
      stop();
      unlockScroll();
    };
  };

  // 튜토리얼 모달 닫기
  const closeTutorialModal = () => {
    showTutorialModal.value = false;
  };

  // 튜토리얼 닫기 함수 추가
  const closeTutorial = () => {
    if (tutorialDriver) {
      try {
        tutorialDriver.destroy();
        tutorialDriver = null;
      } catch (e) {
        console.error('튜토리얼 닫기 중 오류 발생:', e);
      }
    }
    // 튜토리얼 강제 종료 시에도 스크롤 복구
    unlockScroll();
  };

  // 아이콘 클래스 적용 함수
  const applyStepIcon = (step) => {
    // 모든 단계에서 아이콘을 추가하지 않음
    return;
  };

  // 튜토리얼 시작
  const startTutorial = (authStore = null, showLoginModal = null, lockY = undefined) => {
    // 로그인 상태 확인 (매개변수로 전달된 경우)
    if (authStore && showLoginModal && !authStore.isAuthenticated) {
      showTutorialModal.value = false; // 튜토리얼 모달 닫기
      showLoginModal.value = true; // 로그인 모달 표시
      return;
    }
    
    // 모달 닫기
    showTutorialModal.value = false;
    
    // 약간의 지연 후 튜토리얼 시작 (모달이 완전히 닫히는 시간 확보)
    setTimeout(() => {
      // 간단한 설정으로 드라이버 초기화
      // 튜토리얼 시작 시 스크롤 잠금 (원하는 위치로)
      lockScroll(lockY);
      const TutorialDriver = driver({
        showProgress: false,
        overlayColor: 'rgba(0, 0, 0, 0.7)',
        animate: 300,
        allowClose: true,
        doneBtnText: '완료',
        nextBtnText: '다음',
        prevBtnText: '이전',
        
        // 각 단계가 활성화될 때 아이콘 업데이트
        onHighlighted: (element, step) => {
          // 다음 애니메이션 프레임에서 아이콘 업데이트 (렌더링 완료 보장)
          requestAnimationFrame(() => {
            applyStepIcon(step);
          });
        },
        
        // 팝오버가 렌더링될 때 아이콘 업데이트 (이중 보장)
        onPopoverRender: (popover, { config, state }) => {
          const currentStep = state.activeStep;
          if (currentStep) {
            requestAnimationFrame(() => {
              applyStepIcon(currentStep);
            });
          }
        },
        
        // 튜토리얼 완료 시 실행
        onDestroyed: () => {
          // 스크롤 복구
          unlockScroll();
          // 튜토리얼 완료/종료 시 로컬 스토리지에 기록
          localStorage.setItem('tutorialCompleted', 'true');
          // 최초 가입 튜토리얼은 한 번만 자동 실행되도록 플래그 업데이트
          try {
            localStorage.setItem('firstSignup', '1');
          } catch (e) {
            console.warn('firstSignup 플래그 업데이트 실패:', e);
          }
          // 모든 기능의 튜토리얼 플래그 설정 (완료 후 각 기능 사용 시 튜토리얼 표시)
          localStorage.setItem('showApplyTutorial', 'true');
          localStorage.setItem('showTransferTutorial', 'true');
        },
        
        steps: [
          {
            popover: {
              title: '환영합니다!',
              description: '야구 티켓 양도 플랫폼에 오신 것을 환영합니다. 안전하고 편리한 티켓 거래를 시작해보세요!',
              side: 'center',
              align: 'center'
            }
          },
          {
            // element: '.v-card-text.pt-2.px-5.pb-8.d-flex.flex-column.fill-height',
            element: '.enter-card',
            popover: {
              title: '티켓 응모',
              description: '여기를 클릭하여 원하는 야구 경기 티켓에 응모해보세요. 공정한 추첨을 통해 티켓을 받을 수 있습니다!',
              side: 'top',
              align: 'center',
              onNext: () => {
                // 응모하기 버튼 클릭 시 응모 튜토리얼 플래그 설정
                localStorage.setItem('showApplyTutorial', 'true');
                return true; // 다음 단계로 진행 허용
              }
            }
          },
          {
            element: '#transfer-card',
            popover: {
              title: '티켓 양도',
              description: '보유하고 있는 티켓을 다른 사람에게 양도하고 싶다면 여기를 클릭하세요. 안전한 거래를 보장합니다!',
              side: 'top',
              align: 'center',
              onNext: () => {
                // 양도하기 버튼 클릭 시 양도 튜토리얼 플래그 설정
                localStorage.setItem('showTransferTutorial', 'true');
                return true; // 다음 단계로 진행 허용
              }
            }
          }
        ]
      });
      
      // 튜토리얼 시작
      TutorialDriver.drive();
    }, 100);
  };

  // =========================
  // [APPLY] 응모 페이지 튜토리얼
  // =========================
  // 팀 선택 튜토리얼 (초기) - lockScroll(70)로 스크롤 70px 아래 고정 후 시작
  const startApplyTutorial = () => {

    unlockScroll();

    // 기존 드라이버 정리
    if (tutorialDriver) {
      try { tutorialDriver.destroy(); } catch (_) {}
      tutorialDriver = null;
    }

    // 요구사항: 70px 위치로 스크롤 고정
    lockScroll(70);

    // 레이아웃이 스크롤 잠금 상태로 완전히 정착된 후 드라이버 초기화
    requestAnimationFrame(() => {
        tutorialDriver = driver({
          showProgress: false,
          overlayColor: 'rgba(0, 0, 0, 0.7)',
          animate: 300,
          allowClose: true,
          doneBtnText: '완료',
          nextBtnText: '다음',
          showButtons: ['next', 'close'],
          onDestroyed: () => {
            // 응모 초기 튜토리얼이 닫히면 스크롤 복구
            unlockScroll();
            tutorialDriver = null;
          },
          steps: [
            {
              element: '.team-list',
              popover: {
                title: '팀 선택',
                description: '원하는 팀을 선택해주세요.',
                side: 'bottom',
                align: 'center'
              }
            },
            {
              element: '.next-btn',
              popover: {
                title: '다음 단계',
                description: '다음 버튼을 눌러주세요.',
                side: 'top',
                align: 'center'
              }
            }
          ]
        });
        tutorialDriver.drive();
    });
  };

  // 날짜 선택 튜토리얼 (캘린더)
  const startApplyCalendarTutorial = () => {
    // 기존 드라이버 정리
    if (tutorialDriver) {
      try { tutorialDriver.destroy(); } catch (_) {}
      tutorialDriver = null;
    }

    lockScroll(70);

    // 약간 지연 후 시작 (DOM 안정화)
    setTimeout(() => {
      tutorialDriver = driver({
        showProgress: true,
        overlayColor: 'rgba(0, 0, 0, 0.7)',
        animate: false,
        allowClose: true,
        doneBtnText: '완료',
        nextBtnText: '다음',
        prevBtnText: '이전',
        showButtons: ['close'],
        popoverClass: 'tutorial-calendar-modal',
        steps: [
          {
            element: '.calendar-area',
            popover: {
              title: '날짜 선택',
              description: '원하는 경기 날짜를 선택해주세요. 선택한 날짜의 경기 목록이 아래에 표시됩니다.',
              side: 'right',
              align: 'start',
            }
          }
        ],
        onDestroyed: () => {
          // 달력 팝오버 종료 시 별도 동작 없음
          unlockScroll();
          tutorialDriver = null;
        }
      });

      tutorialDriver.drive();

      // setupApplyDateClickListener();
    }, 500);
  };

  // 날짜 클릭 리스너: 날짜 클릭 시 경기 목록 튜토리얼 또는 '경기 없음' 안내로 분기
  // options: { onHasGames: () => void, onNoGames: () => void, delayMs?: number }
  const setupApplyDateClickListener = (options = {}) => {
    const { onHasGames, onNoGames, delayMs = 100 } = options;

    const handleDateClick = (event) => {
      const dateCell = event.target.closest('.calendar-cell');
      if (dateCell && !dateCell.classList.contains('disabled')) {
        // 기존 드라이버가 있으면 닫기 (중복 방지)
        if (tutorialDriver) {
          try { tutorialDriver.destroy(); } catch (_) {}
          tutorialDriver = null;
        }
        document.removeEventListener('click', handleDateClick);
        setTimeout(() => {
          
          const gameCards = document.querySelectorAll('.game-list-box .game-card');
          if (gameCards && gameCards.length > 0) {
            // 외부 콜백 유지
            if (typeof onHasGames === 'function') onHasGames();
            // 통합 투어 사용: 게임 스텝부터 시작 -> Prev 버튼 활성화
            try { startApplyGameSelectTutorial('game'); } catch (_) { try { startApplyGameListTutorial(); } catch (_) {} }
          } else {
            // 외부 콜백에서 경기 없음 모달 호출
            if (typeof onNoGames === 'function') onNoGames();
          }
        }, delayMs);
      }
    };

    document.addEventListener('click', handleDateClick);
    setTimeout(() => {
      document.removeEventListener('click', handleDateClick);
    }, 30000);

  };

  // 경기 목록 튜토리얼
  const startApplyGameListTutorial = () => {
    // 기존 드라이버 정리
    if (tutorialDriver) {
      try { tutorialDriver.destroy(); } catch (_) {}
      tutorialDriver = null;
    }

    setTimeout(() => {
      tutorialDriver = driver({
        showProgress: false,
        overlayColor: 'rgba(0, 0, 0, 0.7)',
        animate: 300,
        allowClose: true,
        doneBtnText: '완료',
        nextBtnText: '완료',
        prevBtnText: '이전',
        showButtons: ['close', 'next', 'prev'],
        popoverClass: 'tutorial-game-list-modal',
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
        onDestroyed: () => {
          // 스크롤 복구
          unlockScroll();
          tutorialDriver = null;
        },
      });

      tutorialDriver.drive();
    }, 300);

  };

  // 양도 페이지 전용 튜토리얼
  const startTransferTutorial = (lockY = undefined) => {
    
    // Store the driver instance in the module-level variable
    // 튜토리얼 시작 시 스크롤 잠금 (원하는 위치로)
    lockScroll(lockY);
    tutorialDriver = driver({
      showProgress: true,
      doneBtnText: '확인',
      showButtons: ['next'],
      onDestroyed: () => {
        // 초기 드라이버가 닫히는 경우에도 스크롤 복구 보장
        unlockScroll();
        tutorialDriver = null;
      },
      steps: [
          {
            element: '.provider-row',
            popover: {
              title: '플랫폼 선택',
              description: 'NOL 또는 티켓링크 바로가기를 눌러주세요. 각 플랫폼별로 보유한 티켓을 확인할 수 있습니다.',
              side: 'top',
              align: 'center'
            }
          }
        ]
    });
    
    // Start the tutorial
    tutorialDriver.drive();

    // 페이지를 맨 위로 스크롤
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
    
    // 약간의 지연 후 튜토리얼 시작 (페이지 로딩 완료 보장)
    setTimeout(() => {
      tutorialDriver = driver({
        showProgress: false,
        overlayColor: 'rgba(0, 0, 0, 0.7)',
        animate: 300,
        allowClose: true,
        doneBtnText: '확인',
        showButtons: ['next'],

        // 팝오버 커스터마이징
        popoverClass: 'transfer-tutorial-popover',
        
        // 각 단계가 활성화될 때 아이콘 업데이트
        onHighlighted: (element, step) => {
          requestAnimationFrame(() => {
            applyStepIcon(step);
          });
        },
        
        // 팝오버가 렌더링될 때 아이콘 업데이트
        onPopoverRender: (popover, { config, state }) => {
          const currentStep = state.activeStep;
          if (currentStep) {
            requestAnimationFrame(() => {
              applyStepIcon(currentStep);
            });
          }
        },
        
        // 튜토리얼 완료 시 실행
        onDestroyed: () => {
          // 스크롤 복구
          unlockScroll();
          tutorialDriver = null;
        },
      });
    }, 500);
  };

  // [양도] 티켓 선택 튜토리얼
  const selectTicketTutorial = (startStep=0) => {
    // 페이지를 맨 위로 스크롤
    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
    
    // main-center-card 전체를 오른쪽으로 이동
    const mainCenterCard = document.querySelector('.main-center-card');
    if (mainCenterCard) {
      mainCenterCard.style.transform = 'translateX(300px)';
      mainCenterCard.style.transition = 'transform 0.3s ease';
    }
    
    setTimeout(() => {
      // 기존 튜토리얼이 있으면 정리
      if (tutorialDriver) {
        try {
          tutorialDriver.destroy();
        } catch (e) {
          console.error('기존 튜토리얼 정리 중 오류:', e);
        }
        tutorialDriver = null;
      }
      tutorialDriver = driver({
        showProgress: false,
        overlayColor: 'rgba(0, 0, 0, 0.7)',
        animate: 300,
        allowClose: true,

        // 팝오버 커스터마이징
        popoverClass: 'ticket-select-tutorial-popover',
        
        // 각 단계가 활성화될 때 아이콘 업데이트
        onHighlighted: (element, step) => {
          requestAnimationFrame(() => {
            applyStepIcon(step);
          });
        },
        
        // 팝오버가 렌더링될 때 아이콘 업데이트
        onPopoverRender: (popover, { config, state }) => {
          const currentStep = state.activeStep;
          if (currentStep) {
            requestAnimationFrame(() => {
              applyStepIcon(currentStep);
            });
          }
        },
        
        // 튜토리얼 완료 시 실행
        onDestroyed: () => {
          // main-center-card 위치 복원
          const mainCenterCard = document.querySelector('.main-center-card');
          if (mainCenterCard) {
            mainCenterCard.style.transform = 'translateX(0)';
          }
        },
        
        steps: [
          {
            element: '.ticket-tab-row',
            popover: {
              title: '플랫폼 선택',
              description: '탭 버튼을 눌러 다른 플랫폼의 티켓을 확인할 수 있습니다.',
              side: 'left',
              align: 'center',
              popoverClass: 'platform-popover',
              nextBtnText: '다음',
              showButtons: ['next'],
              onHighlighted: (element) => {
                const popover = document.querySelector('.driver-popover');
                if (popover) {
                  popover.style.position = 'fixed';
                  popover.style.top = '50%';
                  popover.style.left = 'calc(50% + 150px)'; // 오른쪽으로 조정
                  popover.style.transform = 'translateY(-50%)';
                }
              }
            }
          },
          {
            element: '.ticket-list',
            popover: {
              title: '티켓 선택',
              description: '양도하고 싶은 티켓을 선택해주세요.<br>티켓을 선택하고 양도하기 버튼을 눌러주세요.',
              side: 'left',
              align: 'center',
              nextBtnText: '확인',
              showButtons: ['next']
            }
          }
        ]
      });
      
      // 튜토리얼 시작
      // tutorialDriver.drive();
      tutorialDriver.drive(startStep);
      // driverObj.drive(startStep);
    }, 800);
  };

  // [양도] 양도 확인 튜토리얼 (상세 페이지에서 호출)
  const transferConfirmTutorial = () => {
    
    window.scrollTo(0, 10);
    lockScroll(10);

    // 기존 튜토리얼이 있으면 정리
    if (tutorialDriver) {
      try {
        tutorialDriver.destroy();
      } catch (e) {
        console.error('기존 튜토리얼 정리 중 오류:', e);
      }
      tutorialDriver = null;
    }

    // 요소가 존재하는지 확인
    const detailApplyBox = document.querySelector('.detail-apply-box');
    if (!detailApplyBox) {
      console.error('detail-apply-box 요소를 찾을 수 없어 튜토리얼을 시작할 수 없습니다');
      return;
    }

    tutorialDriver = driver({
      showProgress: false,
      overlayColor: 'rgba(0, 0, 0, 0.7)',
      animate: 300,
      allowClose: true,
      doneBtnText: '완료',
      nextBtnText: '확인',
      prevBtnText: '이전',

      // 팝오버 커스터마이징
      popoverClass: 'transferConfirm',
      
      // 각 단계가 활성화될 때 아이콘 업데이트
      onHighlighted: (element, step) => {
        requestAnimationFrame(() => {
          applyStepIcon(step);
        });
      },
      
      // 팝오버가 렌더링될 때 아이콘 업데이트
      onPopoverRender: (popover, { config, state }) => {
        const currentStep = state.activeStep;
        if (currentStep) {
          requestAnimationFrame(() => {
            applyStepIcon(currentStep);
          });
        }
      },
      
      // 튜토리얼 완료 시 실행
      onDestroyed: () => {
        // 스크롤 복구
        unlockScroll();
        tutorialDriver = null;
      },
      
      steps: [
        {
          element: '.detail-apply-box',
          popover: {
            title: '양도 확인',
            description: '티켓 정보를 확인하고 양도하기를 눌러주세요',
            side: 'left',
            align: 'center',
            nextBtnText: '확인',
            showButtons: ['next']
          }
        }
      ]
    });
    
    // 튜토리얼 시작
    tutorialDriver.drive();
  };

  return {
    showTutorialModal,
    openTutorialModal,
    closeTutorialModal,
    startTutorial,
    startTransferTutorial,
    selectTicketTutorial,
    transferConfirmTutorial, // 새로운 함수 추가
    closeTutorial, // closeTutorial 함수 내보내기 추가
    // 페이지별에서 직접 사용 가능하도록 노출`
    lockScrollAt: lockScroll,
    unlockScroll,
    bindScrollLock,
    startApplyTutorial,
    startApplyCalendarTutorial,
    setupApplyDateClickListener,
    startApplyGameListTutorial,
    startApplyGameSelectTutorial
  };
}
