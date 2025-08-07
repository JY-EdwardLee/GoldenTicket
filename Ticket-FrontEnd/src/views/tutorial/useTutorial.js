import { ref } from 'vue';
import { driver } from 'driver.js';
import 'driver.js/dist/driver.css';

// driver.js 닫기 버튼의 포커스 아웃라인 제거 및 팝오버 크기 조정
const style = document.createElement('style');
style.textContent = `
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
  
  /* 팝오버 크기 조정 */
  .driver-popover {
    max-width: 500px !important;
    width: 500px !important;
  }

`;
document.head.appendChild(style);

// 전역 상태로 튜토리얼 모달 관리
const showTutorialModal = ref(false);

export function useTutorial() {
  // 튜토리얼 모달 열기
  const openTutorialModal = () => {
    showTutorialModal.value = true;
  };

  // 튜토리얼 모달 닫기
  const closeTutorialModal = () => {
    showTutorialModal.value = false;
  };

  // 아이콘 클래스 적용 함수
  const applyStepIcon = (step) => {
    // 모든 단계에서 아이콘을 추가하지 않음
    return;
  };

  // 튜토리얼 시작
  const startTutorial = (authStore = null, showLoginModal = null) => {
    // 로그인 상태 확인 (매개변수로 전달된 경우)
    if (authStore && showLoginModal && !authStore.isAuthenticated) {
      console.log('비로그인 상태에서 튜토리얼 시도 - 로그인 모달 표시');
      showTutorialModal.value = false; // 튜토리얼 모달 닫기
      showLoginModal.value = true; // 로그인 모달 표시
      return;
    }
    
    // 모달 닫기
    showTutorialModal.value = false;
    
    // 약간의 지연 후 튜토리얼 시작 (모달이 완전히 닫히는 시간 확보)
    setTimeout(() => {
      // 간단한 설정으로 드라이버 초기화
      const driverObj = driver({
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
          // 튜토리얼 완료 시 로컬 스토리지에 기록
          localStorage.setItem('tutorialCompleted', 'true');
          // 모든 기능의 튜토리얼 플래그 설정 (완료 후 각 기능 사용 시 튜토리얼 표시)
          localStorage.setItem('showApplyTutorial', 'true');
          localStorage.setItem('showTransferTutorial', 'true');
          console.log('메인 튜토리얼 완료 및 모든 기능 튜토리얼 플래그 설정됨');
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
            element: '.enter-card',
            popover: {
              title: '티켓 응모',
              description: '여기를 클릭하여 원하는 야구 경기 티켓에 응모해보세요. 공정한 추첨을 통해 티켓을 받을 수 있습니다!',
              side: 'top',
              align: 'center',
              onNext: () => {
                // 응모하기 버튼 클릭 시 응모 튜토리얼 플래그 설정
                localStorage.setItem('showApplyTutorial', 'true');
                console.log('응모 튜토리얼 플래그 설정됨');
                return true; // 다음 단계로 진행 허용
              }
            }
          },
          {
            element: '.transfer-card',
            popover: {
              title: '티켓 양도',
              description: '보유하고 있는 티켓을 다른 사람에게 양도하고 싶다면 여기를 클릭하세요. 안전한 거래를 보장합니다!',
              side: 'top',
              align: 'center',
              onNext: () => {
                // 양도하기 버튼 클릭 시 양도 튜토리얼 플래그 설정
                localStorage.setItem('showTransferTutorial', 'true');
                console.log('양도 튜토리얼 플래그 설정됨');
                return true; // 다음 단계로 진행 허용
              }
            }
          }
        ]
      });
      
      // 튜토리얼 시작
      driverObj.drive();
    }, 100);
  };

  return {
    showTutorialModal,
    openTutorialModal,
    closeTutorialModal,
    startTutorial
  };
}
