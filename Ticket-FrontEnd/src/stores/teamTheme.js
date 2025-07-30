import { ref, computed } from 'vue'

// 전역 상태 관리
const createTeamThemeStore = () => {
  // KBO 팀별 색상 및 로고 정의
  const teamColors = {
    '삼성 라이온즈': {
      primary: '#1f4788',
      secondary: '#ffffff',
      accent: '#1f4788',
      gradient: 'linear-gradient(135deg, #1f4788 0%, #2856a3 100%)',
      logo: new URL('../assets/logo/samsung.svg', import.meta.url).href
    },
    '키움 히어로즈': {
      primary: '#820024',
      secondary: '#ffd700',
      accent: '#820024',
      gradient: 'linear-gradient(135deg, #820024 0%, #a0002d 100%)',
      logo: new URL('../assets/logo/KIWOOM.svg', import.meta.url).href
    },
    'LG 트윈스': {
      primary: '#c70025',
      secondary: '#000000',
      accent: '#c70025',
      gradient: 'linear-gradient(135deg, #c70025 0%, #e6002d 100%)',
      logo: new URL('../assets/logo/LG.svg', import.meta.url).href
    },
    '두산 베어스': {
      primary: '#131230',
      secondary: '#d4af37',
      accent: '#131230',
      gradient: 'linear-gradient(135deg, #131230 0%, #1a1a4a 100%)',
      logo: new URL('../assets/logo/DOOSAN.svg', import.meta.url).href
    },
    'KT 위즈': {
      primary: '#000000',
      secondary: '#ff0000',
      accent: '#ff0000',
      gradient: 'linear-gradient(135deg, #000000 0%, #333333 100%)',
      logo: new URL('../assets/logo/KT.svg', import.meta.url).href
    },
    'SSG 랜더스': {
      primary: '#ce0e2d',
      secondary: '#ffffff',
      accent: '#ce0e2d',
      gradient: 'linear-gradient(135deg, #ce0e2d 0%, #e51c3a 100%)',
      logo: new URL('../assets/logo/SSG.svg', import.meta.url).href
    },
    'NC 다이노스': {
      primary: '#315288',
      secondary: '#d4af37',
      accent: '#315288',
      gradient: 'linear-gradient(135deg, #315288 0%, #3d639c 100%)',
      logo: new URL('../assets/logo/NC.svg', import.meta.url).href
    },
    '기아 타이거즈': {
      primary: '#000000',
      secondary: '#ff0000',
      accent: '#ea1538',
      gradient: 'linear-gradient(135deg, #000000 0%, #ea1538 100%)',
      logo: new URL('../assets/logo/kia.svg', import.meta.url).href
    },
    '롯데 자이언츠': {
      primary: '#041e42',
      secondary: '#c5002b',
      accent: '#041e42',
      gradient: 'linear-gradient(135deg, #041e42 0%, #0b2d5a 100%)',
      logo: new URL('../assets/logo/LOTTE.svg', import.meta.url).href
    },
    '한화 이글스': {
      primary: '#ff6600',
      secondary: '#000000',
      accent: '#ff6600',
      gradient: 'linear-gradient(135deg, #ff6600 0%, #ff7a1a 100%)',
      logo: new URL('../assets/logo/HanWha.svg', import.meta.url).href
    },
    'KIA 타이거즈': {
      primary: '#ea1538',
      secondary: '#000000',
      accent: '#ea1538',
      gradient: 'linear-gradient(135deg, #ea1538 0%, #ff1c3f 100%)',
      logo: new URL('../assets/logo/kia.svg', import.meta.url).href
    }
  }

  // 기본 색상 (주황색)
  const defaultTheme = {
    primary: '#ff6b35',
    secondary: '#ffffff',
    accent: '#ff6b35',
    gradient: 'linear-gradient(135deg, #ff6b35 0%, #e55a2e 100%)',
    logo: null // 기본 로고 없음
  }

  // 현재 선택된 팀
  const selectedTeam = ref(localStorage.getItem('selectedTeam') || '')
  
  // 현재 테마 색상
  const currentTheme = computed(() => {
    if (selectedTeam.value && teamColors[selectedTeam.value]) {
      return teamColors[selectedTeam.value]
    }
    return defaultTheme
  })

  // 팀 변경 함수
  const setSelectedTeam = (teamName) => {
    selectedTeam.value = teamName
    localStorage.setItem('selectedTeam', teamName)
    applyThemeToDocument()
  }

  // CSS 변수로 테마 적용
  const applyThemeToDocument = () => {
    const theme = currentTheme.value
    const root = document.documentElement
    
    root.style.setProperty('--theme-primary', theme.primary)
    root.style.setProperty('--theme-secondary', theme.secondary)
    root.style.setProperty('--theme-accent', theme.accent)
    root.style.setProperty('--theme-gradient', theme.gradient)
  }

  // 초기화 시 테마 적용
  const initializeTheme = () => {
    applyThemeToDocument()
  }

  return {
    teamColors,
    selectedTeam,
    currentTheme,
    setSelectedTeam,
    applyThemeToDocument,
    initializeTheme
  }
}

// 싱글톤 인스턴스 생성
export const useTeamThemeStore = createTeamThemeStore() 