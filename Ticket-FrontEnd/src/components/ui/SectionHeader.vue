<template>
  <div class="text-center mb-12">
    <h2 class="text-h3 font-weight-bold mb-4">{{ title }}</h2>
    <p v-if="subtitle" class="text-h6 text-grey-darken-1">{{ subtitle }}</p>
  </div>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: null
  }
});
</script>

<style scoped>
/* 반응형 텍스트 크기 조정 */
@media (max-width: 960px) {
  .text-h3 {
    font-size: 2rem !important;
  }
}

@media (max-width: 600px) {
  .text-h3 {
    font-size: 1.75rem !important;
  }
  
  .text-h6 {
    font-size: 1rem !important;
  }
}
</style>
