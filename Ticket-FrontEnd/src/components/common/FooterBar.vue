<template>
  <footer class="footer-bar">
    <div class="footer-content">
      <div class="footer-logo" aria-label="Golden Ticket">
          <text x="0" y="20" font-family="Arial" font-size="20" font-weight="bold" fill="#333">Golden Ticket</text>
      </div>
      <div class="footer-desc">모두에게 직관의 기회가 될 수 있도록. <br>골든티켓</div>
      <div class="footer-social">
        <a href="https://github.com/SSAFY-13P11A109" aria-label="github" target="_blank" rel="noopener noreferrer">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="#333" xmlns="http://www.w3.org/2000/svg">
            <path fill-rule="evenodd" clip-rule="evenodd" d="M12 2C6.477 2 2 6.484 2 12.017c0 4.425 2.865 8.18 6.839 9.504.5.092.682-.217.682-.483 0-.237-.008-.868-.013-1.703-2.782.605-3.369-1.343-3.369-1.343-.454-1.158-1.11-1.466-1.11-1.466-.908-.62.069-.608.069-.608 1.003.07 1.531 1.032 1.531 1.032.892 1.53 2.341 1.088 2.91.832.092-.647.35-1.088.636-1.338-2.22-.253-4.555-1.11-4.555-4.943 0-1.091.39-1.984 1.029-2.683-.103-.254-.446-1.272.098-2.65 0 0 .84-.27 2.75 1.025A9.565 9.565 0 0112 6.844c.85.004 1.705.115 2.504.337 1.909-1.296 2.747-1.027 2.747-1.027.546 1.379.202 2.398.1 2.651.64.699 1.028 1.592 1.028 2.683 0 3.842-2.339 4.687-4.566 4.935.359.309.678.919.678 1.852 0 1.336-.012 2.415-.012 2.743 0 .267.18.578.688.48A10.02 10.02 0 0022 12.017C22 6.484 17.522 2 12 2z"/>
          </svg>
        </a>
        <a href="https://lab.ssafy.com/s13-webmobile2-sub1/S13P11A109" aria-label="gitlab" target="_blank" rel="noopener noreferrer">
          <svg width="36" height="36" viewBox="0 0 24 24" fill="#FC6D26" xmlns="http://www.w3.org/2000/svg">
            <path d="M22.65 14.39L12 22.13 1.35 14.39a.84.84 0 0 1-.3-.94l1.22-3.78 2.44-7.51A.42.42 0 0 1 4.82 2a.43.43 0 0 1 .58 0 .42.42 0 0 1 .11.18l2.44 7.49h8.1l2.44-7.51a.42.42 0 0 1 .11-.18.43.43 0 0 1 .58 0 .42.42 0 0 1 .11.18l2.44 7.51 1.22 3.78a.84.84 0 0 1-.3.94z"/>
          </svg>
        </a>
      </div>
    </div>
  </footer>
</template>

<script setup>
// Footer logic here if needed
</script>

<style scoped>
.footer-bar {
  width: 100%;
  background: #fafafa;
  border-top: 1px solid #eee;
  padding: 32px 0 16px 0;
  color: #888;
  font-display: swap;
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
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.footer-social a:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.footer-social svg {
  width: 36px;
  height: 36px;
}
</style>
