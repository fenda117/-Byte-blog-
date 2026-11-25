<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { fetchArticleDetail } from '../api'
import type { Article } from '../api'

const route = useRoute()
const article = ref<Article | null>(null)
const loading = ref(true)
const errorMsg = ref('')

async function load() {
  loading.value = true
  errorMsg.value = ''
  article.value = null
  const id = Number(route.params.id)
  if (!id || Number.isNaN(id)) {
    errorMsg.value = '文章地址不正确'
    loading.value = false
    return
  }
  try {
    article.value = await fetchArticleDetail(id)
  } catch (e: any) {
    // 接口报错（网络/服务端异常）和"文章真的不存在"要区分开
    errorMsg.value = e?.message || '文章加载失败，请稍后重试'
  } finally {
    loading.value = false
  }
}

onMounted(load)
watch(() => route.params.id, load)
</script>

<template>
  <div>
    <div v-if="loading" class="card article-detail" v-loading="true" style="min-height: 240px"></div>

    <div v-else-if="article" class="card article-detail">
      <h1 class="detail-title">{{ article.title }}</h1>
      <div class="detail-meta">
        <el-tag v-if="article.category" size="small" effect="plain">{{ article.category }}</el-tag>
        <span>{{ article.createTime }}</span>
        <span><el-icon><View /></el-icon> {{ article.viewCount ?? 0 }}</span>
      </div>
      <div class="detail-body" v-html="article.contentHtml || article.contentMd || article.summary"></div>
    </div>

    <el-empty v-else :description="errorMsg || '文章不存在'" />
  </div>
</template>

<style scoped>
.article-detail {
  padding: 30px;
}
.detail-title {
  font-size: 24px;
  margin-bottom: 14px;
}
.detail-meta {
  display: flex;
  align-items: center;
  gap: 14px;
  font-size: 13px;
  color: var(--text-secondary);
  padding-bottom: 18px;
  border-bottom: 1px solid var(--border);
}
.detail-meta span {
  display: flex;
  align-items: center;
  gap: 3px;
}
.detail-body {
  margin-top: 20px;
  font-size: 15px;
  line-height: 1.9;
}
.detail-body :deep(p) {
  margin-bottom: 14px;
}
.detail-body :deep(h1),
.detail-body :deep(h2),
.detail-body :deep(h3) {
  margin: 18px 0 10px;
}
.detail-body :deep(pre) {
  background: var(--primary-soft);
  padding: 14px;
  border-radius: 8px;
  overflow: auto;
  margin: 12px 0;
}
.detail-body :deep(code) {
  font-family: 'SF Mono', Consolas, monospace;
  font-size: 13px;
}
</style>
