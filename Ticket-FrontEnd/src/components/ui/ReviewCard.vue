<template>
  <v-card elevation="2" rounded="lg" class="h-100 review-card">
    <v-card-text class="pa-6">
      <div class="d-flex align-center mb-4">
        <v-avatar :color="avatarColor" class="mr-4">
          <v-icon color="white">{{ avatarIcon }}</v-icon>
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
