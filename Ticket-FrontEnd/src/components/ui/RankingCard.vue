<template>
  <v-card elevation="4" rounded="lg" class="h-100">
    <v-card-title class="d-flex align-center pa-6">
      <v-icon :color="iconColor" size="large" class="mr-3">{{ titleIcon }}</v-icon>
      <span class="text-h5 font-weight-bold">{{ title }}</span>
    </v-card-title>
    <v-card-text class="pa-6">
      <v-list class="transparent">
        <v-list-item 
          v-for="(item, index) in items" 
          :key="index"
          class="px-0 py-3"
        >
          <template v-slot:prepend>
            <v-avatar :color="getAvatarColor(index)" class="mr-4">
              <v-icon color="white">{{ getAvatarIcon(item.type) }}</v-icon>
            </v-avatar>
          </template>
          
          <v-list-item-title class="font-weight-bold">{{ item.name }}</v-list-item-title>
          <v-list-item-subtitle>
            {{ item.subtitle }}
            <v-chip 
              v-if="item.change" 
              size="small" 
              :color="getChangeColor(item.change)" 
              variant="flat"
              class="ml-2"
            >
              {{ item.change }}
            </v-chip>
          </v-list-item-subtitle>
          
          <template v-slot:append>
            <div class="text-center">
              <div class="text-h6 font-weight-bold">{{ formatNumber(item.score) }}</div>
              <div class="text-caption">{{ scoreUnit }}</div>
            </div>
          </template>
        </v-list-item>
      </v-list>
    </v-card-text>
  </v-card>
</template>

<script setup>
const props = defineProps({
  title: {
    type: String,
    required: true
  },
  titleIcon: {
    type: String,
    required: true
  },
  iconColor: {
    type: String,
    default: 'amber'
  },
  items: {
    type: Array,
    required: true,
    validator: (items) => {
      return items.every(item => 
        item.name && 
        item.subtitle && 
        typeof item.score === 'number'
      );
    }
  },
  scoreUnit: {
    type: String,
    default: '건'
  },
  type: {
    type: String,
    default: 'user',
    validator: (value) => ['user', 'team'].includes(value)
  }
});

const getAvatarColor = (index) => {
  const colors = ['primary', 'secondary', 'grey', 'info'];
  return colors[index] || 'grey';
};

const getAvatarIcon = (type) => {
  return type === 'team' ? 'mdi-soccer' : 'mdi-account';
};

const getChangeColor = (change) => {
  if (change.startsWith('+')) return 'success';
  if (change.startsWith('-')) return 'error';
  return 'info';
};

const formatNumber = (number) => {
  return number.toLocaleString();
};
</script>

<style scoped>
.v-card:hover {
  transform: translateY(-2px);
  transition: transform 0.3s ease;
}
</style>
