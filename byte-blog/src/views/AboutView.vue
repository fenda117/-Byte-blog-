<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchBlogInfo } from '../api'
import { renderSimpleMarkdown } from '../utils/markdown'
import type { BlogInfo } from '../api'

const skills = [
  { group: '后端', items: ['Java', 'Spring Boot', 'MyBatis-Plus', 'Spring AI', 'MySQL', 'Redis'] },
  { group: '前端', items: ['Vue3', 'TypeScript', 'Pinia', 'Vue Router', 'Element Plus'] },
  { group: '底层 / 工程', items: ['JVM', 'Git', 'Docker', 'Linux', 'Nginx'] },
]

const info = ref<BlogInfo | null>(null)
const loading = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    info.value = await fetchBlogInfo()
  } catch (e: any) {
    // 站点信息只是展示用，拿不到就退回静态文案，不要让整页报错
    ElMessage.warning(e?.message || '站点信息加载失败，已显示默认内容')
  } finally {
    loading.value = false
  }
})

const aboutMe = computed(() => (info.value?.aboutMe ?? '').trim())

// 渲染逻辑抽到 utils/markdown.ts，Agent 气泡也用同一套
const aboutHtml = computed(() => renderSimpleMarkdown(aboutMe.value))
</script>

<template>
  <div v-loading="loading">
    <h2 class="page-title">关于我</h2>

    <div class="card">
      <div class="profile-head">
        <img v-if="info?.avatarUrl" :src="info.avatarUrl" class="profile-avatar" alt="头像" />
        <div class="profile-meta">
          <h3 class="profile-name">{{ info?.name || '你的名字' }}</h3>
          <p class="profile-sign">{{ info?.signature || '这个人很懒，什么都没写' }}</p>
        </div>
      </div>

      <!-- 后台填了「关于我」就渲染它，没填就退回下面的静态文案 -->
      <div v-if="aboutHtml" class="markdown-body" v-html="aboutHtml"></div>
      <template v-else>
        <p class="about-text">
          一名正在求职的 Java 后端开发工程师。系统学习过 Spring Boot 与 Vue3
          全栈开发，近期在钻研 AI 应用（Spring AI + 大模型）与 JVM 底层原理。
          这个博客就是我的求职作品，全部由我独立开发并部署上线。
        </p>
        <p class="about-text">
          求职意向：Java 后端 / 全栈开发（偏后端）初级岗位。
        </p>
      </template>
    </div>

    <div v-if="info?.socialLinks?.length" class="card">
      <h3 class="card-title">联系方式</h3>
      <div class="social-list">
        <span v-for="l in info.socialLinks" :key="l.label" class="social-item">
          <span class="social-label">{{ l.label }}</span>
          <span class="social-value">{{ l.value }}</span>
        </span>
      </div>
    </div>

    <div class="card" v-for="s in skills" :key="s.group">
      <h3 class="card-title">{{ s.group }}</h3>
      <div class="skill-tags">
        <el-tag v-for="item in s.items" :key="item" size="large" effect="plain">
          {{ item }}
        </el-tag>
      </div>
    </div>
  </div>
</template>

<style scoped>
.page-title {
  font-size: 22px;
  margin-bottom: 16px;
}
.card-title {
  font-size: 16px;
  margin-bottom: 12px;
}
.about-text {
  color: var(--text-secondary);
  margin-bottom: 10px;
  line-height: 1.9;
}
.profile-head {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}
.profile-avatar {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--border-color, #e5e7eb);
}
.profile-name {
  font-size: 18px;
  margin: 0 0 6px;
}
.profile-sign {
  margin: 0;
  color: var(--text-secondary);
  font-size: 13px;
}
.markdown-body :deep(h3) {
  font-size: 16px;
  margin: 14px 0 8px;
}
.markdown-body :deep(p) {
  color: var(--text-secondary);
  line-height: 1.9;
  margin-bottom: 10px;
}
.markdown-body :deep(ul) {
  padding-left: 20px;
  margin-bottom: 10px;
  color: var(--text-secondary);
  line-height: 1.9;
}
.social-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 24px;
}
.social-item {
  font-size: 13px;
}
.social-label {
  color: var(--text-secondary);
  margin-right: 8px;
}
.skill-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
</style>
