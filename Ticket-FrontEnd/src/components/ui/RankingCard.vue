<template>
  <v-card elevation="4" rounded="lg" class="h-100">
    <v-card-title class="d-flex align-center pa-4">
      <v-icon :color="iconColor" size="large" class="mr-3">{{ titleIcon }}</v-icon>
      <span class="text-h5 font-weight-bold">{{ title }}</span>
    </v-card-title>
    <v-card-text class="pa-4">
      <v-list class="transparent">
        <v-list-item 
          v-for="(item, index) in items" 
          :key="index"
          class="px-0 py-4"
        >
          <template v-slot:prepend>
            <div v-if="type === 'team'" class="team-image">
            <img :src="item.avatar" class="avatar-image">
            </div>
            <v-avatar v-else :color="getAvatarColor(index)" class="mr-4" size="large">
              <img :src="item.avatar" class="avatar-image">
            </v-avatar>
          </template>
          
          <v-list-item-title class="text-h6 font-weight-bold mb-1">{{ item.name }}</v-list-item-title>
          <v-list-item-subtitle class="text-body-2">
            {{ item.subtitle }}
            <span 
              v-if="item.change" 
              class="ml-2 text-caption font-weight-bold"
              :class="getChangeTextColor(item.change)"
            >
              {{ item.change }}
            </span>
          </v-list-item-subtitle>
          
          <template v-slot:append>
            <div class="d-flex flex-column align-end">
              <div class="text-h5 font-weight-bold">{{ formatNumber(item.score) }}</div>
              <div class="text-body-2 text-medium-emphasis">{{ scoreUnit }}</div>
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

const getChangeTextColor = (change) => {
  if (change.startsWith('+')) return 'text-success';
  if (change.startsWith('-')) return 'text-error';
  return 'text-info';
};

const formatNumber = (number) => {
  if (number === undefined || number === null) {
    return '0';
  }
  return number.toLocaleString();
};
</script>

<style scoped>
.avatar-image {
  width: px;
  height: 40px;
  object-fit: cover;
}
.team-image {
  width: 68px;
  text-align: center;
  object-fit: cover;
  margin-right: 16px;
}
.v-card:hover {
  transform: translateY(-2px);
  transition: transform 0.3s ease;
}
</style>
