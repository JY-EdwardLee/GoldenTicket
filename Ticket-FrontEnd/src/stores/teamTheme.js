import { ref, computed } from 'vue'

// 전역 상태 관리
const createTeamThemeStore = () => {
  // KBO 팀별 색상 및 로고 정의
  const teamColors = {
    'SAMSUNG_LIONS': {
      primary: '#074CA1',
      secondary: '#C0C0C0',
      accent: '#C0C0C0',
      gradient: 'linear-gradient(135deg, #074CA1 0%, #C0C0C0 100%)',
      logo: new URL('@/assets/logo/samsung.svg', import.meta.url).href,
      stadium: '대구 삼성 라이온즈 파크'
    },
    'KIWOOM_HEROES': {
      primary: '#570514',
      secondary: '#B07F4A',
      accent: '#B07F4A',
      gradient: 'linear-gradient(135deg, #570514 0%, #B07F4A 100%)',
      logo: new URL('@/assets/logo/KIWOOM.svg', import.meta.url).href,
      stadium: '고척 스카이돔'
    },
    'LG_TWINS': {
      primary: '#C30452',
      secondary: '#000000',
      accent: '#000000',
      gradient: 'linear-gradient(135deg, #C30452 0%, #000000 100%)',
      logo: new URL('@/assets/logo/LG.svg', import.meta.url).href,
      stadium: '서울종합운동장 야구장'
    },
    'DOOSAN_BEARS': {
      primary: '#1A1748',
      secondary: '#EB1D25',
      accent: '#EB1D25',
      gradient: 'linear-gradient(135deg, #1A1748 0%, #EB1D25 100%)',
      logo: new URL('@/assets/logo/DOOSAN.svg', import.meta.url).href,
      stadium: '서울종합운동장 야구장'
    },
    'KT_WIZ': {
      primary: '#000000',
      secondary: '#ff0000',
      accent: '#ff0000',
      gradient: 'linear-gradient(135deg, #000000 0%, #333333 100%)',
      logo: new URL('@/assets/logo/KT.svg', import.meta.url).href,
      stadium: '수원 케이티 위즈 파크'
    },
    'SSG_LANDERS': {
      primary: '#CE0E2D',
      secondary: '#FFB81C',
      accent: '#FFB81C',
      gradient: 'linear-gradient(135deg, #CE0E2D 0%, #FFB81C 100%)',
      logo: new URL('@/assets/logo/SSG.svg', import.meta.url).href,
      stadium: '인천 SSG 랜더스필드'
    },
    'NC_DINOS': {
      primary: '#315288',
      secondary: '#AF917B',
      accent: '#AF917B',
      gradient: 'linear-gradient(135deg, #315288 0%, #AF917B 100%)',
      logo: new URL('@/assets/logo/NC.svg', import.meta.url).href,
      stadium: '창원NC파크'
    },
    'KIA_TIGERS': {
      primary: '#EA0029',
      secondary: '#06141F',
      accent: '#06141F',
      gradient: 'linear-gradient(135deg, #EA0029 0%, #06141F 100%)',
      logo: new URL('@/assets/logo/kia.svg', import.meta.url).href,
      stadium: '광주 기아 챔피언스 필드'
    },
    'LOTTE_GIANTS': {
      primary: '#041E42',
      secondary: '#D00F31',
      accent: '#D00F31',
      gradient: 'linear-gradient(135deg, #041E42 0%, #D00F31 100%)',
      logo: new URL('@/assets/logo/LOTTE.svg', import.meta.url).href,
      stadium: '사직 야구장'
    },
    'HANHWA_EAGLES': {
      primary: '#FC4E00',
      secondary: '#07111F',
      accent: '#07111F',
      gradient: 'linear-gradient(135deg, #FC4E00 0%, #07111F 100%)',
      logo: new URL('@/assets/logo/HanWha.svg', import.meta.url).href,
      stadium: '대전 한화생명 볼파크'
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
    root.style.setProperty('--theme-stadium', theme.stadium)
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
export const useTeamThemeStore = createTeamThemeStore();