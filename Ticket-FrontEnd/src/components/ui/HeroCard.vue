<template>
  <v-card
    :class="['hero-card', cardClass, 'cursor-pointer']"
    :height="height"
    :elevation="0"
    :rounded="rounded"
    @mouseover="isHovered = true"
    @mouseleave="isHovered = false"
    @click="handleClick"
  >
    <!-- <div v-if="type === 'sub'" class="hero-card-sub-bg" :style="{ backgroundImage: `url('/components/default_card_sub.svg')` }"></div> -->
    <!-- <div v-else class="hero-card-bg" :style="{ backgroundImage: `url('/components/card5_smal_title.svg')` }"></div> -->
    <div class="d-flex fill-height">
      <v-card-text class="pt-2 px-5 pb-8 d-flex flex-column fill-height">
      <div class="d-flex justify-space-between">
        <div class="d-flex flex-column">
        <span v-if="type === 'sub'" class="title-sub-text font-weight-bold text-black mt-2 mb-6 position-relative">
          <span class="orange-dot"></span>
          {{ title }}
        </span>
        <span v-else class="title-text font-weight-bold text-black mt-1 mb-6 position-relative">
          <span class="orange-dot"></span>
          {{ title }}
        </span>
      </div>
      <div v-if="type === 'primary' || type === 'secondary'" class="d-flex justify-space-between">
        <span>아아아아아아</span>
      </div>
      </div>
      <div v-if="type === 'primary' || type === 'secondary'" class="ticket-image-container justify-center pt-10">
        <img
          v-if="type === 'primary'"
           src="/avatar/ticket_transfer.png"
           alt="ticket"
           class="ticket-image"
           />
          <img
          v-if="type === 'secondary'"
           src="/avatar/stadium_apply.png"
           alt="apply"
           class="ticket-image"
           />
        </div>
      <div v-if="type === 'sub'" class="ticket-sub-image-container justify-center pt-10">
        <img 
           v-if="title === '응모 내역'"
           src="/avatar/my_application.png"
           alt="응모 내역"
           class="ticket-sub-image"
           />
           <img 
           v-if="title === '나의 티켓'"
           src="/avatar/my_ticket.png"
           alt="나의 티켓"
           class="ticket-sub-image"
           />
      </div>

    </v-card-text>
    </div>
    <slot></slot>
  </v-card>
</template>

<script setup>
import { computed, ref } from 'vue';

const isHovered = ref(false);

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: [String, Array],
    required: true
  },
  buttonText: {
    type: String,
    required: true
  },
  buttonIcon: {
    type: String,
    default: 'mdi-arrow-right'
  },
  type: {
    type: String,
    default: 'primary',
    validator: (value) => ['primary', 'secondary'].includes(value)
  },
  height: {
    type: [String, Number],
    default: 800
  },
  elevation: {
    type: [String, Number],
    default: 8
  },
  rounded: {
    type: String,
    default: 'xl'
  }
});

const emit = defineEmits(['click']);

const descriptionLines = computed(() => {
  return Array.isArray(props.description) ? props.description : [props.description];
});

const cardClass = computed(() => {
  return props.type === 'primary' ? 'enter-card' : 'transfer-card';
});

const buttonClass = computed(() => {
  return props.type === 'primary' ? 'text-primary font-weight-bold' : 'text-secondary font-weight-bold';
});

const handleClick = () => {
  emit('click');
};

</script>

<style scoped>
.ticket-image-container {
  width: 200px;
  height: 300px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.ticket-image {
  object-fit: cover;
  transform: scale(0.35);
  margin-bottom: 60px;
}

.hero-card:hover .ticket-image {
  transform: scale(0.4);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
}

.ticket-sub-image-container {
  width: 200px;
  height: 230px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}


.ticket-sub-image {
  object-fit: cover;
  transform: scale(0.15);
  margin-bottom: 60px;
}

.hero-card:hover .ticket-sub-image {
  transform: scale(0.2);
  transition: all 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  margin-bottom: 60px;
}


.hero-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  overflow: hidden; /* 아이콘이 카드 밖으로 나가지 않도록 */
  position: relative; /* 자식 요소의 position: absolute 기준점 */
}

.hero-card-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;  
  background-size: cover;
  background-position: left;
  background-repeat: no-repeat;
  z-index: 0;
  transition: none !important;
  transform: none !important;
}

.hero-card-sub-bg {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-repeat: no-repeat;
  transform: scale(1.0);
  transform-origin: left;
}

.hero-card > :not(.hero-card-bg, .hero-card-sub-bg) {
  position: relative;
  z-index: 1;
}

/* Title text styles with enhanced hover effect */
.title-text {
  display: inline-block;
  font-size: 1.3rem;
  transition: color 0.3s ease;
}

/* .hero-card:hover .title-text {
  color: #ffffff !important;
} */

/* Sub title text styles with enhanced hover effect */
.title-sub-text {
  display: inline-block;
  font-size: 1.1rem;
  transition: color 0.3s ease;
}

/* .hero-card:hover .title-sub-text {
  color: #ffffff !important;
} */
.orange-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  background-color: #e0bc2a  ; /* Orange color */
  border-radius: 50%;
  margin-right: 16px;
  vertical-align: middle;
  position: relative;
  top: -2px;
}
/* 
.hero-card:hover .orange-dot {
  background-color: #ffffff !important;
} */

.hero-card:hover {
  /* transform: translateY(-8px); */
  /* box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15) !important; */
}

.hero-card:hover .hero-card-bg {
  transform: none !important;
}

/* .enter-card {
  background-color: var(--theme-primary, #ff6b35) !important;
}

.transfer-card {
  background-color: var(--theme-secondary, #ff6b35) !important;
} */
</style>
