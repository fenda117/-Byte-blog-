<script setup lang="ts">
import { onMounted, ref } from 'vue'
import ProjectCard from '../components/ProjectCard.vue'
import { fetchProjects } from '../api'
import type { Project } from '../api'

const projects = ref<Project[]>([])

onMounted(async () => {
  projects.value = await fetchProjects()
})
</script>

<template>
  <div>
    <h2 class="page-title">项目展示</h2>
    <p class="page-desc">我独立完成和参与过的项目，点击卡片可以查看详情。</p>
    <ProjectCard v-for="p in projects" :key="p.id" :project="p" />
    <el-empty v-if="!projects.length" description="暂无项目" />
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
</style>
