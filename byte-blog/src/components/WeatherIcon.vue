<script setup lang="ts">
import { computed } from 'vue'

/**
 * 天气图标。
 *
 * 不用图标库、不用图片文件，直接内联 SVG：
 * 一个文件管七种天气，颜色跟随父级文字色，深色模式下自动适配，
 * 也不会多出七个网络请求。
 *
 * 图形沿用 Lucide 的线条风格，和站内 el-icon 观感一致。
 */
const props = withDefaults(
  defineProps<{
    /** clear 晴 / partly 多云 / cloudy 阴 / rain 雨 / thunder 雷 / snow 雪 / fog 雾 */
    name?: string
    size?: number
  }>(),
  { name: 'clear', size: 44 },
)

const KINDS = ['clear', 'partly', 'cloudy', 'rain', 'thunder', 'snow', 'fog'] as const

// 后端偶尔会返回没见过的值，兜到「阴」，不让卡片因为一个图标崩掉
const kind = computed(() => {
  const n = (props.name || '').toLowerCase()
  return (KINDS as readonly string[]).includes(n) ? n : 'cloudy'
})
</script>

<template>
  <svg
    :width="size"
    :height="size"
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    stroke-width="1.7"
    stroke-linecap="round"
    stroke-linejoin="round"
    class="weather-icon"
    aria-hidden="true"
  >
    <!-- 晴 -->
    <g v-if="kind === 'clear'">
      <circle cx="12" cy="12" r="4" />
      <path d="M12 2v2" />
      <path d="M12 20v2" />
      <path d="m4.93 4.93 1.41 1.41" />
      <path d="m17.66 17.66 1.41 1.41" />
      <path d="M2 12h2" />
      <path d="M20 12h2" />
      <path d="m6.34 17.66-1.41 1.41" />
      <path d="m19.07 4.93-1.41 1.41" />
    </g>

    <!-- 多云：太阳 + 云 -->
    <g v-else-if="kind === 'partly'">
      <path d="M12 2v2" />
      <path d="m4.93 4.93 1.41 1.41" />
      <path d="M20 12h2" />
      <path d="m19.07 4.93-1.41 1.41" />
      <path d="M15.947 12.65a4 4 0 0 0-5.925-4.128" />
      <path d="M13 22H7a5 5 0 1 1 4.9-6H13a3 3 0 0 1 0 6Z" />
    </g>

    <!-- 阴 -->
    <g v-else-if="kind === 'cloudy'">
      <path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z" />
    </g>

    <!-- 雨 -->
    <g v-else-if="kind === 'rain'">
      <path d="M4 14.899A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 0 1 2.5 8.242" />
      <path d="M16 14v6" />
      <path d="M8 14v6" />
      <path d="M12 16v6" />
    </g>

    <!-- 雷 -->
    <g v-else-if="kind === 'thunder'">
      <path d="M6 16.326A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 0 1 .5 8.973" />
      <path d="m13 12-3 5h4l-3 5" />
    </g>

    <!-- 雪 -->
    <g v-else-if="kind === 'snow'">
      <path d="M4 14.899A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 0 1 2.5 8.242" />
      <path d="M8 15h.01" />
      <path d="M8 19h.01" />
      <path d="M12 17h.01" />
      <path d="M12 21h.01" />
      <path d="M16 15h.01" />
      <path d="M16 19h.01" />
    </g>

    <!-- 雾 / 霾 -->
    <g v-else>
      <path d="M4 14.899A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 0 1 2.5 8.242" />
      <path d="M16 17H7" />
      <path d="M17 21H9" />
    </g>
  </svg>
</template>

<style scoped>
.weather-icon {
  display: block;
  flex: none;
}
</style>
