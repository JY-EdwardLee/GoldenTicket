<template>
  <v-card
    :class="['hero-card', cardClass]"
    :height="height"
    :elevation="elevation"
    :rounded="rounded"
    @mouseover="isHovered = true"
    @mouseleave="isHovered = false"
  >
    <v-icon class="ticket-icon" :class="{ 'animate': isHovered }">mdi-ticket-confirmation-outline</v-icon>
    <v-card-text class="pa-8 d-flex flex-column justify-space-between fill-height">
      <div>
        <h1 class="text-h2 font-weight-bold text-white mb-6">{{ title }}</h1>
        <div class="text-h6 text-white mb-8">
          <p class="mb-2" v-for="(line, index) in descriptionLines" :key="index">
            {{ line }}
          </p>
        </div>
      </div>
      <v-btn
        size="large"
        variant="elevated"
        color="white"
        :class="buttonClass"
        @click="handleClick"
      >
        {{ buttonText }}
        <v-icon end>{{ buttonIcon }}</v-icon>
      </v-btn>
    </v-card-text>
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
    default: 400
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
.hero-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  overflow: hidden; /* 아이콘이 카드 밖으로 나가지 않도록 */
  position: relative; /* 자식 ticket-icon의 position: absolute 기준점 */
}

.enter-card .ticket-icon {
  position: absolute;
  left: 70%;
  top: 50%;
  font-size: 150px; /* 아이콘 크기 조정 */
  color: rgba(255, 255, 255, 0.2); /* 아이콘 색상 및 투명도 조정 */
  transform: translate(-100%, -50%);
  transition: transform 0.4s ease-in-out;
  z-index: 0;
  opacity: 0;
}

.enter-card .ticket-icon.animate {
  transform: translate(200%, -50%); /* 호버 시 아이콘 위치 */
  transition: transform 0.4s ease-in-out;
  opacity: 1;
}


.transfer-card .ticket-icon {
  position: absolute;
  left: 0;
  top: 50%;
  font-size: 150px; /* 아이콘 크기 조정 */
  color: rgba(255, 255, 255, 0.2); /* 아이콘 색상 및 투명도 조정 */
  transform: translate(-100%, -50%);
  transition: transform 0.4s ease-in-out;
  z-index: 0;
  opacity: 0;
}

.transfer-card .ticket-icon.animate {
  transform: translate(200%, -50%); /* 호버 시 아이콘 위치 */
  opacity: 1;
}

.hero-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15) !important;
}

.enter-card {
  background-color: var(--theme-primary, #ff6b35) !important;
}

.transfer-card {
  background-color: var(--theme-secondary, #ff6b35) !important;
}
</style>
