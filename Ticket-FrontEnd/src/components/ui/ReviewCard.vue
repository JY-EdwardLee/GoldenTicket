<template>
  <v-card elevation="2" rounded="lg" class="h-100 review-card">
    <v-card-text class="pa-6">
      <div class="d-flex align-center mb-4">
        <v-avatar class="mr-4" :style="{ backgroundColor: 'white', border: '2px solid #e0e0e0' }">
          <img 
            :src="getTeamLogo(team)" 
            :alt="team + ' 로고'"
            style="width: 100%; height: 100%; object-fit: contain; padding: 4px;"
          />
        </v-avatar>
        <div>
          <div class="font-weight-bold">{{ name }}</div>
          <div class="text-caption text-grey-darken-1">{{ team }}</div>
        </div>
      </div>
      
      <blockquote class="text-body-1 mb-4 font-italic review-content">
        "{{ content }}"
      </blockquote>
      
      <div class="text-caption text-grey-darken-1">{{ date }}</div>
    </v-card-text>
  </v-card>
</template>

<script setup>
const props = defineProps({
  name: {
    type: String,
    required: true
  },
  team: {
    type: String,
    required: true
  },
  content: {
    type: String,
    required: true
  },
  date: {
    type: String,
    required: true
  },
  avatarColor: {
    type: String,
    default: 'primary'
  },
  avatarIcon: {
    type: String,
    default: 'mdi-account'
  }
});

// 팀명에 맞는 로고 파일 경로 반환
const getTeamLogo = (teamName) => {
  const teamLogoMap = {
    'KIA 타이거즈': '@/assets/logo/kia.svg',
    'LG 트윈스': '@/assets/logo/LG.svg',
    '두산 베어스': '@/assets/logo/DOOSAN.svg',
    'SSG 랜더스': '@/assets/logo/SSG_LANDERS.svg',
    '삼성 라이온즈': '@/assets/logo/samsung.svg',
    '롯데 자이언츠': '@/assets/logo/LOTTE.svg',
    'NC 다이노스': '@/assets/logo/NC.svg',
    '키움 히어로즈': '@/assets/logo/KIWOOM.svg',
    'KT 위즈': '@/assets/logo/KT.svg',
    '한화 이글스': '@/assets/logo/HanWha.svg'
  };
  
  return teamLogoMap[teamName] || '@/assets/logo/SSG_LANDERS.svg'; // 기본 로고
};
</script>

<style scoped>
.review-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.review-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1) !important;
}

.review-content {
  line-height: 1.6;
  color: rgba(0, 0, 0, 0.8);
}
</style>
