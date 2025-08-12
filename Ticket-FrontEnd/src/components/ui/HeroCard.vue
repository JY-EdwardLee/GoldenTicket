<template>
  <v-card
    :class="['hero-card', cardClass]"
    :height="height"
    :elevation="0"
    :rounded="rounded"
    @mouseover="isHovered = true"
    @mouseleave="isHovered = false"
  >
    <div v-if="type === 'sub'" class="hero-card-sub-bg" :style="{ backgroundImage: `url('/components/default_card_sub.svg')` }"></div>
    <div v-else class="hero-card-bg" :style="{ backgroundImage: `url('/components/default_card_main.svg')` }"></div>
    <div class="d-flex fill-height">
      <v-card-text class="pt-2 px-5 pb-8 d-flex flex-column justify-space-between fill-height">
      <div class="d-flex">
        <div class="d-flex flex-column">
        <span v-if="type === 'sub'" class="title-sub-text font-weight-medium text-black mt-2 mb-6 position-relative">
          <span class="orange-dot"></span>
          {{ title }}
        </span>
        <span v-else class="title-text font-weight-medium text-black mt-1 mb-6 position-relative">
          <span class="orange-dot"></span>
          {{ title }}
        </span>
        <div v-if="type === 'primary' || type === 'secondary'" class="d-flex justify-space-between">
        <div class="text-h6 text-black mb-8" v-if="isHovered">
          <p class="mb-2" v-for="(line, index) in descriptionLines" :key="index">
            {{ line }}
          </p>
        </div>
        <!-- <div class="pitcher-image-container" v-if="!isHovered">
          <img src="/avatar/pitcher3.png" alt="Pitcher" class="pitcher-image" />
        </div>  -->
        </div>
      </div>
      </div>
      <v-btn
        v-if="type === 'primary' || type === 'secondary'"
        size="large"
        variant="elevated"
        color="white"
        rounded="lg"
        :class="buttonClass"
        @click="handleClick"
      >
        {{ buttonText }}
        <v-icon end>{{ buttonIcon }}</v-icon>
      </v-btn>
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
.pitcher-image-container {
  width: 250px;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.pitcher-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
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
  background-position: leftt;
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

.title-text {
  display: inline-block;
  font-size: 1.2rem;
}
.title-sub-text {
  display: inline-block;
  font-size: 1.1rem;
}
.orange-dot {
  display: inline-block;
  width: 12px;
  height: 12px;
  background-color: #e55a2e  ; /* Orange color */
  border-radius: 50%;
  margin-right: 16px;
  vertical-align: middle;
  position: relative;
  top: -2px;
}

.hero-card:hover {
  transform: translateY(-8px);
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
