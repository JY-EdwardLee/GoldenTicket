<template>
  <div class="common-layout">
    <NavBar />
    <main>
      <slot />
    </main>
    <FooterBar />
  </div>
</template>

<script setup>
import NavBar from '../components/common/NavBar.vue';
import FooterBar from '../components/common/FooterBar.vue';
</script>

<style scoped>
.common-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}
main {
  width: 100vw;
  background-color: #f8f8f8;
}
</style>
