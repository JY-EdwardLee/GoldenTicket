<template>
  <v-card elevation="2" rounded="lg" class="h-100 text-center service-card">
    <v-card-text class="pa-8">
      <div class="mb-6">
        <v-icon :color="iconColor" size="64" class="service-icon">{{ icon }}</v-icon>
      </div>
      
      <h5 class="text-h5 font-weight-bold mb-4">{{ title }}</h5>
      
      <p class="text-body-1 text-grey-darken-1">{{ description }}</p>
    </v-card-text>
  </v-card>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    required: true
  },
  icon: {
    type: String,
    required: true
  },
  iconColor: {
    type: String,
    default: 'primary'
  }
});
</script>

<style scoped>
.service-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.service-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1) !important;
}

.service-icon {
  transition: transform 0.3s ease;
}

.service-card:hover .service-icon {
  transform: scale(1.1);
}
</style>
