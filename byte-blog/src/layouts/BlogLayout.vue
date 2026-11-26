<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useThemeStore } from '../stores/theme'
import type { ThemeName } from '../stores/theme'
import { fetchBlogInfo, fetchMusic, fetchStats, fetchWeather, searchAll } from '../api'
import type { Music, SearchItem, WeatherNow } from '../api'
import type { BlogInfo, Stats } from '../api'
import TimeCard from '../components/TimeCard.vue'
import WeatherIcon from '../components/WeatherIcon.vue'
import { normalizeMediaUrl } from '../utils/media'

const router = useRouter()
const themeStore = useThemeStore()

function onThemeCommand(command: string | number | object) {
  themeStore.setTheme(command as ThemeName)
}

// ---- 全站搜索 ----
const searchKeyword = ref('')

function querySearch(queryString: string, cb: (results: SearchItem[]) => void) {
  searchAll(queryString).then(cb)
}

function onSelectSearch(item: SearchItem) {
  if (item.type === 'article') {
    router.push(`/articles/${item.id}`)
  } else {
    router.push('/projects')
  }
  searchKeyword.value = ''
}

const blogInfo = ref<BlogInfo | null>(null)
const stats = ref<Stats | null>(null)
const musicList = ref<Music[]>([])

// ---- 侧边栏天气 ----
const weather = ref<WeatherNow | null>(null)
const weatherLoading = ref(true)

/**
 * 天气要请求外部接口，可能比其它请求慢，所以单独发起、不参与下面那串 await：
 * 谁先回来谁先渲染，天气慢也不会拖住文章和音乐。
 */
async function loadWeather() {
  try {
    weather.value = await fetchWeather()
  } catch (e) {
    console.warn('加载天气失败', e)
    weather.value = null
  } finally {
    weatherLoading.value = false
  }
}

onMounted(async () => {
  void loadWeather()
  // 三个请求互相独立：任何一个失败都不影响其他模块显示
  try {
    blogInfo.value = await fetchBlogInfo()
  } catch (e) {
    console.warn('加载博主信息失败', e)
  }
  try {
    stats.value = await fetchStats()
  } catch (e) {
    console.warn('加载站点统计失败', e)
  }
  try {
    musicList.value = await fetchMusic()
  } catch (e) {
    console.warn('加载音乐列表失败', e)
  }
})

// ---- 音乐播放（点击列表项播放/暂停）----
const audioEl = ref<HTMLAudioElement | null>(null)
const playingId = ref<number | null>(null)

function togglePlay(song: Music) {
  if (!song.audioUrl) {
    ElMessage.warning('这首还没有上传音频')
    return
  }
  // 再点一次同一首 = 暂停
  if (playingId.value === song.id) {
    audioEl.value?.pause()
    playingId.value = null
    return
  }
  if (!audioEl.value) {
    audioEl.value = new Audio()
    audioEl.value.addEventListener('ended', () => {
      playingId.value = null
    })
  }
  // 后台可能填的是 D:/xxx/public/music/bgm.mp3 这类本地路径，
  // 浏览器会拒绝加载（Not allowed to load local resource），先归一化成站内路径
  const src = normalizeMediaUrl(song.audioUrl)
  if (!src) {
    ElMessage.warning('音频地址为空，去后台补一下吧')
    return
  }
  audioEl.value.src = src
  audioEl.value
      .play()
      .then(() => {
        playingId.value = song.id
      })
      .catch((e) => {
        playingId.value = null
        console.warn('[music] 播放失败', src, e)
        ElMessage.error(`播放失败，检查音频地址是否可访问：${src}`)
      })
}

// ---- 简易当月日历 ----
const now = new Date()
const year = now.getFullYear()
const month = now.getMonth()
const today = now.getDate()
const firstDay = new Date(year, month, 1).getDay() // 1 号是星期几（0=周日）
const daysInMonth = new Date(year, month + 1, 0).getDate()
const weekDays = ['日', '一', '二', '三', '四', '五', '六']
const calendarCells = computed(() => [
  ...Array.from({ length: firstDay }, () => null),
  ...Array.from({ length: daysInMonth }, (_, i) => i + 1),
])
</script>

<template>
  <div class="blog-page">
    <!-- 顶部导航 -->
    <header class="blog-header">
      <div class="header-inner">
        <router-link to="/" class="blog-logo">
          <span class="logo-dot"></span>
          {{ blogInfo?.name || '我的博客' }}
        </router-link>

        <nav class="header-nav">
          <router-link to="/home"><el-icon><HomeFilled /></el-icon>主页</router-link>
          <router-link to="/articles"><el-icon><Document /></el-icon>文章</router-link>
          <router-link to="/projects"><el-icon><Folder /></el-icon>项目</router-link>
          <router-link to="/agent"><el-icon><Cpu /></el-icon>AI 智能体</router-link>
          <router-link to="/messages"><el-icon><ChatDotRound /></el-icon>留言</router-link>
          <router-link to="/about"><el-icon><InfoFilled /></el-icon>关于</router-link>
        </nav>

        <div class="header-right">
          <el-autocomplete
              v-model="searchKeyword"
              class="search-input"
              placeholder="搜索文章 / 项目"
              :fetch-suggestions="querySearch"
              :trigger-on-focus="false"
              clearable
              @select="onSelectSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #default="{ item }">
              <div class="search-item">
                <el-icon class="search-item-icon">
                  <Document v-if="item.type === 'article'" />
                  <Folder v-else />
                </el-icon>
                <div class="search-item-text">
                  <div class="si-title">{{ item.title }}</div>
                  <div class="si-desc">{{ item.desc }}</div>
                </div>
              </div>
            </template>
          </el-autocomplete>

          <el-dropdown trigger="click" @command="onThemeCommand">
            <button class="theme-btn" title="切换主题">
              <el-icon :size="18">
                <Moon v-if="themeStore.theme === 'dark'" />
                <Pear v-else-if="themeStore.theme === 'green'" />
                <Sunny v-else />
              </el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="light">
                  <el-icon><Sunny /></el-icon>亮色 · 极简白
                </el-dropdown-item>
                <el-dropdown-item command="dark">
                  <el-icon><Moon /></el-icon>暗色 · 暗夜黑
                </el-dropdown-item>
                <el-dropdown-item command="green">
                  <el-icon><Pear /></el-icon>护眼 · 绿意
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 三栏主体 -->
    <div class="blog-body">
      <!-- 左侧栏 -->
      <aside class="left-sidebar">
        <!-- 个人信息 -->
        <div class="side-card">
          <div style="text-align: center; margin-bottom: 12px">
            <el-avatar :size="64" style="background: var(--primary)">
              {{ blogInfo?.name?.charAt(0) || '我' }}
            </el-avatar>
          </div>
          <div style="text-align: center">
            <div style="font-weight: 700; font-size: 17px">{{ blogInfo?.name }}</div>
            <div style="color: var(--text-secondary); font-size: 13px; margin-top: 4px">
              {{ blogInfo?.signature }}
            </div>
          </div>
          <div style="margin-top: 14px; display: flex; flex-direction: column; gap: 8px">
            <a
                v-for="link in blogInfo?.socialLinks"
                :key="link.label"
                class="social-link"
                :href="link.value"
                target="_blank"
                rel="noopener"
            >
              <el-icon><Link /></el-icon>
              <span>{{ link.label }}</span>
              <span class="social-value">{{ link.value }}</span>
            </a>
          </div>
        </div>

        <!-- 音乐播放器 -->
        <div class="side-card">
          <h3><el-icon><Headset /></el-icon> 音乐</h3>
          <ul class="playlist">
            <li
                v-for="(song, i) in musicList"
                :key="song.id"
                :class="{ playing: playingId === song.id }"
                @click="togglePlay(song)"
            >
              <span class="song-index">{{ i + 1 }}</span>
              <div class="song-info">
                <div class="song-title">{{ song.title }}</div>
                <div class="song-artist">{{ song.author }}</div>
              </div>
              <el-icon class="song-play">
                <VideoPause v-if="playingId === song.id" />
                <VideoPlay v-else />
              </el-icon>
            </li>
            <li v-if="!musicList.length" class="song-empty">还没有音乐</li>
          </ul>
        </div>

        <!-- AI 智能体入口 -->
        <div class="side-card ai-entry" @click="$router.push('/agent')">
          <el-icon :size="26"><Cpu /></el-icon>
          <div>
            <div class="ai-entry-title">AI 智能体</div>
            <div class="ai-entry-sub">点我和 AI 助手聊聊</div>
          </div>
        </div>
      </aside>

      <!-- 中间内容区 -->
      <main class="main-content">
        <router-view />
      </main>

      <!-- 右侧栏 -->
      <aside class="right-sidebar">
        <!-- 时间问候卡片 -->
        <TimeCard />

        <!-- 站点统计 -->
        <div class="side-card">
          <h3><el-icon><TrendCharts /></el-icon> 站点统计</h3>
          <div class="stats-grid">
            <div class="stats-item">
              <div class="stats-num">{{ stats?.articleCount ?? '-' }}</div>
              <div class="stats-label">文章</div>
            </div>
            <div class="stats-item">
              <div class="stats-num">{{ stats?.projectCount ?? '-' }}</div>
              <div class="stats-label">项目</div>
            </div>
            <div class="stats-item">
              <div class="stats-num">{{ stats?.visitorCount ?? '-' }}</div>
              <div class="stats-label">访客</div>
            </div>
            <div class="stats-item">
              <div class="stats-num">{{ stats?.runDays ?? '-' }}</div>
              <div class="stats-label">运行天数</div>
            </div>
          </div>
        </div>

        <!-- 日历 -->
        <div class="side-card">
          <h3><el-icon><Calendar /></el-icon> 日历</h3>
          <div class="calendar-header">{{ year }} 年 {{ month + 1 }} 月</div>
          <div class="calendar-week">
            <span v-for="w in weekDays" :key="w">{{ w }}</span>
          </div>
          <div class="calendar-grid">
            <span
                v-for="(d, i) in calendarCells"
                :key="i"
                class="calendar-cell"
                :class="{ today: d === today }"
            >{{ d ?? '' }}</span>
          </div>
        </div>

        <!-- 天气：未配置或接口异常时整块不渲染，不留半成品 -->
        <div v-if="weatherLoading || weather" class="side-card">
          <h3><el-icon><Cloudy /></el-icon> 天气</h3>

          <div v-if="weatherLoading" class="weather-skeleton">
            <div class="sk-row sk-circle" />
            <div class="sk-row sk-line" />
            <div class="sk-row sk-line short" />
          </div>

          <div v-else-if="weather" class="weather-body">
            <div class="weather-main">
              <WeatherIcon :name="weather.icon" :size="46" class="weather-ico" />
              <div class="weather-temp">
                <span class="num">{{ weather.temperature }}</span>
                <span class="unit">°C</span>
              </div>
            </div>
            <div class="weather-desc">
              <span class="city">{{ weather.city }}</span>
              <span class="text">{{ weather.text }}</span>
            </div>
            <div class="weather-meta">
              <span>体感 {{ weather.feelsLike }}°</span>
              <span>湿度 {{ weather.humidity }}%</span>
              <span v-if="weather.windDir">{{ weather.windDir }} {{ weather.windScale }} 级</span>
            </div>
            <div class="weather-time">{{ weather.updateTime }} 更新</div>
          </div>
        </div>
      </aside>
    </div>
  </div>
</template>

<style scoped>
/* 社交链接 */
.social-link {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13.5px;
  color: var(--text-secondary);
  padding: 6px 10px;
  border-radius: 8px;
  transition: all 0.2s;
}
.social-link:hover {
  background: var(--primary-soft);
  color: var(--primary);
}
.social-value {
  margin-left: auto;
  font-size: 12px;
  opacity: 0.7;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 90px;
}

/* 播放列表 */
.playlist li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 7px 6px;
  cursor: pointer;
  border-radius: 6px;
  transition: background 0.15s;
}
.playlist li:hover {
  background: var(--primary-soft);
}
.playlist li.playing .song-title,
.playlist li.playing .song-index {
  color: var(--primary);
  font-weight: 600;
}
.song-empty {
  color: var(--text-secondary);
  font-size: 13px;
  cursor: default !important;
  justify-content: center;
}
.song-empty:hover {
  background: transparent !important;
}
.song-index {
  color: var(--text-secondary);
  font-size: 12px;
  width: 16px;
}
.song-info {
  flex: 1;
  min-width: 0;
}
.song-title {
  font-size: 13.5px;
}
.song-artist {
  font-size: 12px;
  color: var(--text-secondary);
}
.song-play {
  color: var(--primary);
}

/* AI 入口 */
.ai-entry {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  color: var(--primary);
  transition: transform 0.2s;
}
.ai-entry:hover {
  transform: translateY(-2px);
}
.ai-entry-title {
  font-weight: 700;
  font-size: 15px;
}
.ai-entry-sub {
  font-size: 12px;
  color: var(--text-secondary);
}

/* 统计 */
.stats-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.stats-item {
  text-align: center;
  padding: 8px;
  border-radius: 8px;
  background: var(--primary-soft);
}
.stats-num {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary);
}
.stats-label {
  font-size: 12px;
  color: var(--text-secondary);
}

/* 日历 */
.calendar-header {
  text-align: center;
  font-weight: 600;
  margin-bottom: 8px;
}
.calendar-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  text-align: center;
  font-size: 12px;
  color: var(--text-secondary);
  margin-bottom: 4px;
}
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 2px;
}
.calendar-cell {
  text-align: center;
  font-size: 12.5px;
  padding: 4px 0;
  border-radius: 6px;
}
.calendar-cell.today {
  background: var(--primary);
  color: #fff;
  font-weight: 700;
}

/* 天气 */
.weather-body {
  animation: weather-in .28s ease-out;
}
@keyframes weather-in {
  from { opacity: 0; transform: translateY(4px); }
  to { opacity: 1; transform: none; }
}

.weather-main {
  display: flex;
  align-items: center;
  gap: 12px;
}
.weather-ico {
  color: var(--primary);
}

.weather-temp {
  display: flex;
  align-items: baseline;
}
.weather-temp .num {
  font-size: 32px;
  font-weight: 700;
  line-height: 1;
  letter-spacing: -1px;
  color: var(--text-primary);
}
.weather-temp .unit {
  margin-left: 3px;
  font-size: 13px;
  color: var(--text-secondary);
}

.weather-desc {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 10px;
  font-size: 13px;
}
.weather-desc .city {
  font-weight: 600;
  color: var(--text-primary);
}
.weather-desc .text {
  color: var(--text-secondary);
}

.weather-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 4px 10px;
  margin-top: 8px;
  font-size: 12px;
  color: var(--text-secondary);
}

.weather-time {
  margin-top: 10px;
  font-size: 11px;
  color: var(--text-secondary);
  opacity: .7;
}

/* 加载骨架：先占住高度，避免数据回来时卡片突然长高把下面的内容顶走 */
.weather-skeleton {
  display: flex;
  flex-direction: column;
  gap: 9px;
}
.sk-row {
  border-radius: 6px;
  background: var(--primary-soft);
  animation: weather-pulse 1.4s ease-in-out infinite;
}
.sk-circle {
  width: 44px;
  height: 44px;
  border-radius: 50%;
}
.sk-line {
  height: 12px;
  width: 74%;
}
.sk-line.short {
  width: 48%;
}
@keyframes weather-pulse {
  0%, 100% { opacity: .45; }
  50% { opacity: 1; }
}
</style>
