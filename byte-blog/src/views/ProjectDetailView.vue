<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { fetchProjectDetail } from '../api'
import type { Project } from '../api'
import { formatDateTime } from '../utils/time'
import { normalizeMediaUrl } from '../utils/media'
import { toExternalUrl } from '../utils/url'

const route = useRoute()
const router = useRouter()

const project = ref<Project | null>(null)
const loading = ref(true)
const errorMsg = ref('')

async function load() {
  loading.value = true
  errorMsg.value = ''
  project.value = null
  const id = Number(route.params.id)
  if (!id || Number.isNaN(id)) {
    errorMsg.value = '项目地址不正确'
    loading.value = false
    return
  }
  try {
    project.value = await fetchProjectDetail(id)
  } catch (e: any) {
    // 后端那句「还没有项目哦，新建一个吧」是写给管理端看的，
    // 访客看到会莫名其妙，这里按网络故障 / 项目不存在两种情况分别给话术
    const msg = e?.message || ''
    const isNetwork = !msg || /network|timeout|网络|超时/i.test(msg)
    errorMsg.value = isNetwork ? '项目加载失败，请稍后重试' : '这个项目不存在或已下线'
  } finally {
    loading.value = false
  }
}

onMounted(load)
// 在详情页之间直接切 id（比如从相关项目点进另一个）时也要重新拉
watch(() => route.params.id, load)
</script>

<template>
  <div>
    <div v-if="loading" class="card project-detail" v-loading="true" style="min-height: 240px"></div>

    <template v-else-if="project">
      <div class="back-line">
        <el-button text @click="router.push('/projects')">
          <el-icon><ArrowLeft /></el-icon>
          返回项目列表
        </el-button>
      </div>

      <div class="card project-detail">
        <div class="detail-head">
          <h1 class="detail-title">{{ project.name }}</h1>
          <el-tag v-if="project.featured" type="warning" size="small">精选</el-tag>
        </div>

        <div class="detail-meta">
          <span><el-icon><View /></el-icon> {{ project.viewCount ?? 0 }} 次浏览</span>
          <span>{{ formatDateTime(project.createTime) }}</span>
        </div>

        <img
          v-if="project.coverUrl"
          class="detail-cover"
          :src="normalizeMediaUrl(project.coverUrl)"
          :alt="project.name"
        />

        <div class="detail-section">
          <h3>项目简介</h3>
          <p class="detail-desc">{{ project.description }}</p>
        </div>

        <div class="detail-section">
          <h3>技术栈</h3>
          <div class="detail-tags">
            <el-tag v-for="t in project.techStackList" :key="t" effect="plain">{{ t }}</el-tag>
            <span v-if="!project.techStackList?.length" class="detail-empty">暂未填写</span>
          </div>
        </div>

        <div class="detail-section">
          <h3>相关链接</h3>
          <div class="detail-links">
            <a v-if="toExternalUrl(project.githubUrl)" :href="toExternalUrl(project.githubUrl)" target="_blank" rel="noopener">
              <el-icon><Link /></el-icon> GitHub 源码
            </a>
            <a v-if="toExternalUrl(project.demoUrl)" :href="toExternalUrl(project.demoUrl)" target="_blank" rel="noopener">
              <el-icon><VideoPlay /></el-icon> 在线演示
            </a>
            <span v-if="!toExternalUrl(project.githubUrl) && !toExternalUrl(project.demoUrl)" class="detail-empty">
              暂未提供源码或演示链接
            </span>
          </div>
        </div>
      </div>
    </template>

    <el-empty v-else :description="errorMsg || '项目不存在'">
      <el-button @click="router.push('/projects')">回到项目列表</el-button>
    </el-empty>
  </div>
</template>

<style scoped>
.back-line {
  margin-bottom: 10px;
}
.project-detail {
  padding: 30px;
}
.detail-head {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}
.detail-title {
  font-size: 24px;
  margin: 0;
}
.detail-meta {
  display: flex;
  align-items: center;
  gap: 18px;
  margin: 12px 0 22px;
  color: var(--text-secondary);
  font-size: 13.5px;
}
.detail-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}
.detail-cover {
  width: 100%;
  max-height: 340px;
  object-fit: cover;
  border-radius: 10px;
  margin-bottom: 22px;
}
.detail-section {
  margin-bottom: 22px;
}
.detail-section:last-child {
  margin-bottom: 0;
}
.detail-section h3 {
  font-size: 15px;
  margin-bottom: 10px;
}
.detail-desc {
  color: var(--text-secondary);
  font-size: 14.5px;
  line-height: 1.9;
  margin: 0;
  white-space: pre-wrap;
}
.detail-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.detail-links {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}
.detail-links a {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--primary);
  font-size: 14px;
}
.detail-links a:hover {
  text-decoration: underline;
}
.detail-empty {
  color: var(--text-secondary);
  font-size: 13.5px;
}
</style>
