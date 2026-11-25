<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { fetchArticles } from '../api'
import type { Article } from '../api'

const articles = ref<Article[]>([])

onMounted(async () => {
  articles.value = await fetchArticles()
})
</script>

<template>
  <div>
    <h2 class="page-title">文章</h2>
    <p class="page-desc">记录我的学习笔记和技术踩坑。</p>
    <router-link
        v-for="a in articles"
        :key="a.id"
        :to="`/articles/${a.id}`"
        class="article-item card"
    >
      <h3 class="article-title">{{ a.title }}</h3>
      <p class="article-summary">{{ a.summary }}</p>
      <div class="article-meta">
        <el-tag size="small" effect="plain">{{ a.category }}</el-tag>
        <el-tag v-for="t in a.tags" :key="t" size="small" type="info" effect="plain">{{ t }}</el-tag>
        <span class="meta-right">
          <span>{{ a.createTime }}</span>
          <span><el-icon><View /></el-icon> {{ a.viewCount }}</span>
        </span>
      </div>
    </router-link>
    <el-empty v-if="!articles.length" description="暂无文章" />
  </div>
</template>

<style scoped>
.page-title {
  font-size: 22px;
  margin-bottom: 6px;
}
.page-desc {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 20px;
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
  flex-wrap: wrap;
  gap: 8px;
  font-size: 12.5px;
  color: var(--text-secondary);
}
.meta-right {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 12px;
}
.meta-right span {
  display: flex;
  align-items: center;
  gap: 3px;
}
</style>
