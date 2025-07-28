<template>
  <footer class="footer-bar">
    <div class="footer-content">
      <div class="footer-logo">logo</div>
      <div class="footer-desc">Building amazing digital experiences<br>with modern design and technology.</div>
      <div class="footer-social">
        <a href="#"><i class="icon icon-facebook"></i></a>
        <a href="#"><i class="icon icon-instagram"></i></a>
        <a href="#"><i class="icon icon-twitter"></i></a>
      </div>
    </div>
  </footer>
</template>

<script setup>
// Footer logic here if needed
</script>

<style scoped>
.footer-bar {
  width: 100vw  ;
  background: #fafafa;
  border-top: 1px solid #eee;
  padding: 32px 0 16px 0;
  color: #888;
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.footer-logo {
  font-size: 1.1rem;
  font-weight: bold;
  margin-bottom: 4px;
}
.footer-desc {
  font-size: 0.98rem;
  text-align: center;
}
.footer-social {
  margin-top: 8px;
  display: flex;
  gap: 16px;
}
.footer-social a {
  color: #bbb;
  font-size: 1.2rem;
  text-decoration: none;
}
</style>
