<script setup lang="ts">
import { onMounted, ref } from 'vue'
import ProjectCard from '../components/ProjectCard.vue'
import { fetchArticles, fetchFeaturedProjects } from '../api'
import type { Article, Project } from '../api'

const projects = ref<Project[]>([])
const articles = ref<Article[]>([])

onMounted(async () => {
  projects.value = await fetchFeaturedProjects()
  articles.value = await fetchArticles()
})
</script>

<template>
  <div>
    <!-- 精选项目 -->
    <h2 class="section-title">精选项目</h2>
    <ProjectCard v-for="p in projects" :key="p.id" :project="p" />

    <!-- 最新文章 -->
    <h2 class="section-title">最新文章</h2>
    <router-link
        v-for="a in articles"
        :key="a.id"
        :to="`/articles/${a.id}`"
        class="article-item card"
    >
      <div class="article-main">
        <h3 class="article-title">{{ a.title }}</h3>
        <p class="article-summary">{{ a.summary }}</p>
        <div class="article-meta">
          <el-tag size="small" effect="plain">{{ a.category }}</el-tag>
          <span>{{ a.createTime }}</span>
          <span><el-icon><View /></el-icon> {{ a.viewCount }}</span>
        </div>
      </div>
    </router-link>
  </div>
</template>

<style scoped>
.section-title {
  font-size: 18px;
  margin: 8px 0 14px;
  padding-left: 10px;
  border-left: 4px solid var(--primary);
}
.article-item {
  display: block;
  transition: transform 0.2s, box-shadow 0.2s;
}
.article-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}
.article-title {
  font-size: 16px;
  margin-bottom: 6px;
}
.article-summary {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 10px;
}
.article-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 12.5px;
  color: var(--text-secondary);
}
.article-meta span {
  display: flex;
  align-items: center;
  gap: 3px;
}
</style>
