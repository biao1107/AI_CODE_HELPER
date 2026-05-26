<template>
  <div class="loading-dots" aria-label="AI 正在生成">
    <div class="dot"></div>
    <div class="dot"></div>
    <div class="dot"></div>
  </div>
</template>

<script>
export default {
  name: 'LoadingDots'
}
</script>

<style scoped>
.loading-dots {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding-top: 2px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--brand-500), #60a5fa);
  opacity: 0.38;
  animation: dot-bounce 1.15s infinite ease-in-out;
}

.dot:nth-child(2) {
  animation-delay: 0.12s;
}

.dot:nth-child(3) {
  animation-delay: 0.24s;
}

@keyframes dot-bounce {
  0%,
  80%,
  100% {
    transform: translateY(0) scale(0.9);
    opacity: 0.32;
  }

  40% {
    transform: translateY(-2px) scale(1.08);
    opacity: 1;
  }
}
</style>
