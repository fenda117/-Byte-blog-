<script setup lang="ts">
import { useRouter } from 'vue-router'
import heroBg from '../assets/hero-bg.jpg'
import imgProjects from '../assets/module-projects.jpg'
import imgArticles from '../assets/module-articles.jpg'
import imgAgent from '../assets/module-agent.jpg'

const router = useRouter()
const bgStyle = { backgroundImage: `url(${heroBg})` }

const navItems = [
  { label: '主页', path: '/home' },
  { label: '文章', path: '/articles' },
  { label: '项目', path: '/projects' },
  { label: 'Agent', path: '/agent' },
  { label: '关于', path: '/about' },
]

const modules = [
  { title: '项目', desc: '我做过的作品与实战', image: imgProjects, path: '/projects' },
  { title: '文章', desc: '学习笔记与技术分享', image: imgArticles, path: '/articles' },
  { title: 'AI 智能体', desc: '和我的数字分身聊聊', image: imgAgent, path: '/agent' },
]
</script>

<template>
  <div class="landing" :style="bgStyle">
    <!-- 悬浮导航（参考图：logo 左 / 药丸菜单中 / 按钮右） -->
    <nav class="landing-nav">
      <a class="nav-logo" @click="router.push('/')">我的博客</a>
      <div class="nav-menu">
        <a v-for="item in navItems" :key="item.path" @click="router.push(item.path)">
          {{ item.label }}
        </a>
      </div>
      <button class="nav-cta" @click="router.push('/home')">进入主页</button>
    </nav>

    <!-- 封面主体 -->
    <section class="hero">
      <p class="kicker">欢迎来到我的数字花园</p>
      <h1 class="title">记录技术<br />分享成长</h1>
      <p class="sub">
        Java 后端 / Vue3 全栈 / AI 智能体 / JVM 底层<br />
        每一篇都来自真实的动手实践
      </p>
      <div class="actions">
        <button class="btn-light" @click="router.push('/home')">进入主页</button>
        <button class="btn-glass" @click="router.push('/about')">了解我</button>
      </div>
      <div class="hero-scroll">
        <el-icon :size="22"><ArrowDown /></el-icon>
      </div>
    </section>

    <!-- 三个插画模块卡片 -->
    <section class="modules">
      <div class="modules-head">
        <h2>探索我的站点</h2>
        <a class="browse-all" @click="router.push('/home')">
          浏览更多
          <el-icon><ArrowRight /></el-icon>
        </a>
      </div>
      <div class="modules-grid">
        <div
            v-for="m in modules"
            :key="m.title"
            class="module-card"
            @click="router.push(m.path)"
        >
          <img :src="m.image" :alt="m.title" />
          <div class="module-info">
            <div class="module-text">
              <h3>{{ m.title }}</h3>
              <p>{{ m.desc }}</p>
            </div>
            <span class="module-go">
              <el-icon><ArrowRight /></el-icon>
            </span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* 整页一幅画：背景固定，内容滚动滑过 */
.landing {
  min-height: 100vh;
  background-size: cover;
  background-position: center;
  background-attachment: fixed;
  padding-bottom: 70px;
}

/* ===== 悬浮导航 ===== */
.landing-nav {
  position: fixed;
  top: 18px;
  left: 0;
  right: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 28px;
}
.nav-logo {
  color: #fff;
  font-size: 20px;
  font-weight: 800;
  letter-spacing: 1px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.35);
  cursor: pointer;
}
.nav-menu {
  display: flex;
  gap: 2px;
  background: rgba(120, 150, 120, 0.32);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.25);
  border-radius: 999px;
  padding: 6px 10px;
}
.nav-menu a {
  color: #fff;
  font-size: 14.5px;
  font-weight: 600;
  padding: 7px 18px;
  border-radius: 999px;
  cursor: pointer;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.25);
  transition: background 0.2s;
}
.nav-menu a:hover {
  background: rgba(255, 255, 255, 0.3);
}
.nav-cta {
  background: #fff;
  color: #2c3a2c;
  border: none;
  padding: 11px 24px;
  border-radius: 999px;
  font-size: 14.5px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.18);
  transition: transform 0.2s, box-shadow 0.2s;
}
.nav-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.25);
}

/* ===== 封面主体 ===== */
.hero {
  text-align: center;
  padding: 150px 24px 90px;
  animation: hero-in 0.9s ease both;
}
@keyframes hero-in {
  from {
    opacity: 0;
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.kicker {
  color: #fff;
  font-weight: 800;
  font-size: 16px;
  letter-spacing: 8px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.4);
  margin-bottom: 14px;
}
.title {
  color: #fff;
  font-size: clamp(48px, 7vw, 84px);
  font-weight: 900;
  line-height: 1.16;
  letter-spacing: 6px;
  text-shadow: 0 4px 26px rgba(0, 0, 0, 0.4);
  margin-bottom: 20px;
}
.sub {
  color: rgba(255, 255, 255, 0.96);
  font-size: 15.5px;
  line-height: 1.95;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
  margin-bottom: 30px;
}
.actions {
  display: flex;
  justify-content: center;
  gap: 14px;
  flex-wrap: wrap;
}
.btn-light,
.btn-glass {
  padding: 13px 32px;
  border-radius: 999px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s, background 0.2s;
}
.btn-light {
  background: #fff;
  color: #2c3a2c;
  border: none;
}
.btn-light:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 24px rgba(0, 0, 0, 0.25);
}
.btn-glass {
  background: rgba(255, 255, 255, 0.22);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(6px);
}
.btn-glass:hover {
  background: rgba(255, 255, 255, 0.35);
  transform: translateY(-2px);
}
.hero-scroll {
  margin-top: 70px;
  color: #fff;
  opacity: 0.85;
  animation: bob 1.6s ease-in-out infinite;
}
@keyframes bob {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(7px);
  }
}

/* ===== 三个插画卡片 ===== */
.modules {
  max-width: 1280px;
  margin: 0 auto;
  padding: 0 28px;
}
.modules-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;
}
.modules-head h2 {
  color: #fff;
  font-size: 21px;
  font-weight: 800;
  letter-spacing: 3px;
  text-shadow: 0 2px 10px rgba(0, 0, 0, 0.4);
}
.browse-all {
  color: #fff;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.35);
}
.browse-all:hover {
  text-decoration: underline;
}
.modules-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.module-card {
  position: relative;
  border-radius: 18px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: 0 8px 26px rgba(0, 0, 0, 0.2);
  transition: transform 0.25s, box-shadow 0.25s;
}
.module-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 16px 36px rgba(0, 0, 0, 0.28);
}
.module-card img {
  width: 100%;
  height: 230px;
  object-fit: cover;
  display: block;
  transition: transform 0.45s ease;
}
.module-card:hover img {
  transform: scale(1.06);
}
.module-info {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, transparent 52%, rgba(0, 0, 0, 0.5));
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding: 16px 18px;
}
.module-text h3 {
  color: #fff;
  font-size: 19px;
  font-weight: 800;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.45);
}
.module-text p {
  color: rgba(255, 255, 255, 0.92);
  font-size: 13px;
  margin-top: 2px;
  text-shadow: 0 1px 5px rgba(0, 0, 0, 0.4);
}
.module-go {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  color: #2c3a2c;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

@media (max-width: 900px) {
  .nav-menu {
    display: none;
  }
  .modules-grid {
    grid-template-columns: 1fr;
  }
  .module-card img {
    height: 260px;
  }
}
</style>