<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref } from 'vue'

const now = ref(new Date())
let timer: number | undefined

onMounted(() => {
  timer = window.setInterval(() => (now.value = new Date()), 1000)
})
onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const hour = computed(() => now.value.getHours())
const timeText = computed(
    () =>
        `${String(hour.value).padStart(2, '0')}:${String(now.value.getMinutes()).padStart(2, '0')}`
)
const weekday = computed(() =>
    now.value.toLocaleDateString('en-US', { weekday: 'long' }).toUpperCase()
)
const dateText = computed(
    () =>
        `${String(now.value.getDate()).padStart(2, '0')}/${String(now.value.getMonth() + 1).padStart(2, '0')}`
)
const greeting = computed(() => {
  const h = hour.value
  if (h < 6) return '夜深了，早点休息！'
  if (h < 9) return '早上好，新的一天加油！'
  if (h < 12) return '上午好，保持专注！'
  if (h < 14) return '中午好，记得吃饭！'
  if (h < 18) return '下午好，来杯茶吧！'
  return '晚上好，来放松一下吧！'
})
const isNight = computed(() => hour.value >= 18 || hour.value < 6)
</script>

<template>
  <div class="side-card time-card">
    <div class="time-icon">
      <el-icon v-if="isNight" :size="24"><Moon /></el-icon>
      <el-icon v-else :size="24"><Sunny /></el-icon>
    </div>
    <p class="greeting">{{ greeting }}</p>
    <div class="time-row">
      <span class="clock">{{ timeText }}</span>
      <span class="date-block">
        <span class="weekday">{{ weekday }}</span>
        <span class="date">{{ dateText }}</span>
      </span>
    </div>
  </div>
</template>

<style scoped>
.time-card {
  position: relative;
  padding: 18px 20px;
}
.time-icon {
  position: absolute;
  top: 16px;
  right: 16px;
  color: var(--primary);
}
.greeting {
  font-size: 13.5px;
  color: var(--text-secondary);
  margin-bottom: 8px;
  padding-right: 30px;
}
.time-row {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}
.clock {
  font-size: 34px;
  font-weight: 800;
  line-height: 1;
  letter-spacing: 1px;
  color: var(--text);
  font-variant-numeric: tabular-nums;
}
.date-block {
  display: flex;
  flex-direction: column;
  gap: 2px;
  padding-bottom: 3px;
}
.weekday {
  font-size: 10.5px;
  font-weight: 700;
  letter-spacing: 1px;
  color: var(--text);
}
.date {
  font-size: 11px;
  color: var(--text-secondary);
}
</style>