<template>
  <v-btn
    :size="size"
    :variant="variant"
    :color="color"
    :class="buttonClass"
    :disabled="disabled"
    :loading="loading"
    @click="handleClick"
  >
    <v-icon v-if="prependIcon" start>{{ prependIcon }}</v-icon>
    <slot>{{ text }}</slot>
    <v-icon v-if="appendIcon" end>{{ appendIcon }}</v-icon>
  </v-btn>
</template>

<script setup>
import { computed } from 'vue';

const props = defineProps({
  text: {
    type: String,
    default: ''
  },
  size: {
    type: String,
    default: 'default',
    validator: (value) => ['x-small', 'small', 'default', 'large', 'x-large'].includes(value)
  },
  variant: {
    type: String,
    default: 'elevated',
    validator: (value) => ['elevated', 'flat', 'tonal', 'outlined', 'text', 'plain'].includes(value)
  },
  color: {
    type: String,
    default: 'primary'
  },
  prependIcon: {
    type: String,
    default: null
  },
  appendIcon: {
    type: String,
    default: null
  },
  disabled: {
    type: Boolean,
    default: false
  },
  loading: {
    type: Boolean,
    default: false
  },
  customClass: {
    type: String,
    default: ''
  }
});

const emit = defineEmits(['click']);

const buttonClass = computed(() => {
  return `font-weight-bold ${props.customClass}`;
});

const handleClick = (event) => {
  if (!props.disabled && !props.loading) {
    emit('click', event);
  }
};
</script>

<style scoped>
.v-btn {
  transition: all 0.3s ease;
}

.v-btn:hover {
  transform: translateY(-2px);
}
</style>
