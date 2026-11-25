<script setup lang="ts">
import { useRouter } from 'vue-router'
import type { Project } from '../api'
import { toExternalUrl } from '../utils/url'

const props = defineProps<{ project: Project }>()
const router = useRouter()

/** 整张卡片可点，进项目详情；卡内的 GitHub/演示是外链，要阻止冒泡 */
function openDetail() {
  router.push(`/projects/${props.project.id}`)
}
</script>

<template>
  <div
    class="project-card card"
    role="button"
    tabindex="0"
    @click="openDetail"
    @keydown.enter="openDetail"
  >
    <div class="project-head">
      <h3 class="project-name">{{ project.name }}</h3>
      <el-tag v-if="project.featured" type="warning" size="small">精选</el-tag>
    </div>
    <p class="project-desc">{{ project.description }}</p>
    <div class="project-tags">
      <el-tag v-for="t in project.techStackList" :key="t" size="small" effect="plain">
        {{ t }}
      </el-tag>
    </div>
    <div class="project-links">
      <span class="project-view">
        <el-icon><View /></el-icon>
        <span>{{ project.viewCount }} 次浏览</span>
      </span>
      <a
        v-if="toExternalUrl(project.githubUrl)"
        :href="toExternalUrl(project.githubUrl)"
        target="_blank"
        rel="noopener"
        @click.stop
      >
        <el-icon><Link /></el-icon>
        <span>GitHub</span>
      </a>
      <a
        v-if="toExternalUrl(project.demoUrl)"
        :href="toExternalUrl(project.demoUrl)"
        target="_blank"
        rel="noopener"
        @click.stop
      >
        <el-icon><VideoPlay /></el-icon>
        <span>在线演示</span>
      </a>
      <span class="project-more">查看详情 <el-icon><ArrowRight /></el-icon></span>
    </div>
  </div>
</template>

<style scoped>
.project-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}
.project-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}
.project-card:focus-visible {
  outline: 2px solid var(--primary);
  outline-offset: 2px;
}
.project-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.project-name {
  font-size: 17px;
}
.project-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin: 0;
}
.project-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
.project-links {
  display: flex;
  align-items: center;
  gap: 18px;
}
.project-view {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--text-secondary);
  font-size: 13px;
}
.project-links a {
  display: flex;
  align-items: center;
  gap: 4px;
  color: var(--primary);
  font-size: 13.5px;
}
.project-links a:hover {
  text-decoration: underline;
}
.project-more {
  display: flex;
  align-items: center;
  gap: 2px;
  margin-left: auto;
  color: var(--primary);
  font-size: 13px;
}
</style>
