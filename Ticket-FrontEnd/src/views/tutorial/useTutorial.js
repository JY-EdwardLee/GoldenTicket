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
  .driver-popover:not(.transfer-tutorial-popover){
    max-width: 500px !important;
    width: 500px !important;
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

export function useTutorial() {
  // 튜토리얼 모달 열기
  const openTutorialModal = () => {
    showTutorialModal.value = true;
  };

  // 튜토리얼 모달 닫기
  const closeTutorialModal = () => {
    showTutorialModal.value = false;
  };

  // 튜토리얼 닫기 함수 추가
  const closeTutorial = () => {
    console.log('closeTutorial 호출됨');
    if (tutorialDriver) {
      console.log('closeTutorial - if문 진입');
      try {
        console.log('closeTutorial - if문 - try')
        tutorialDriver.destroy();
        tutorialDriver = null;
        console.log('튜토리얼이 성공적으로 닫혔습니다.');
      } catch (e) {
        console.error('튜토리얼 닫기 중 오류 발생:', e);
      }
    }
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
          console.log('메인 튜토리얼 종료: firstSignup=1 및 기능별 튜토리얼 플래그 설정');
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
      TutorialDriver.drive();
    }, 100);
  };

  // 양도 페이지 전용 튜토리얼
  const startTransferTutorial = () => {
    console.log('양도 튜토리얼 시작');
    
    // Store the driver instance in the module-level variable
    tutorialDriver = driver({
      showProgress: true,
      doneBtnText: '확인',
      showButtons: ['next'],
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
          console.log('양도 튜토리얼 완료');
          tutorialDriver = null;
        },
        
        // steps: [
        //   {
        //     element: '.provider-row',
        //     popover: {
        //       title: '플랫폼 선택',
        //       description: 'NOL 또는 티켓링크 바로가기를 눌러주세요. 각 플랫폼별로 보유한 티켓을 확인할 수 있습니다.',
        //       side: 'top',
        //       align: 'center'
        //     }
        //   }
        // ]
      });
      
      // 튜토리얼 시작
      // tutorialDriver.drive();
    }, 500);
  };

  // [양도] 티켓 선택 튜토리얼
  const selectTicketTutorial = (startStep=0) => {
    console.log('티켓 선택 튜토리얼 시작');
    
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
          console.log('티켓 선택 튜토리얼 완료');
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
    console.log('양도 확인 튜토리얼 시작');
    
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
        console.log('양도 확인 튜토리얼 완료');
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
    closeTutorial // closeTutorial 함수 내보내기 추가
  };
}
