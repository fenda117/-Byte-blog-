<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchProjects, createProject, updateProject, deleteProject } from '../../api'
import type { Project, ProjectPayload } from '../../api'

const projects = ref<Project[]>([])
const loading = ref(false)
const dialog = ref(false)
const editingId = ref<number | null>(null)
const form = ref<ProjectPayload>({
  name: '',
  description: '',
  techStack: '',
  githubUrl: '',
  demoUrl: '',
  coverUrl: '',
  featured: 0,
  sort: 0,
})

async function load() {
  loading.value = true
  try {
    projects.value = await fetchProjects()
  } finally {
    loading.value = false
  }
}

onMounted(load)

function openCreate() {
  editingId.value = null
  form.value = { name: '', description: '', techStack: '', githubUrl: '', demoUrl: '', coverUrl: '', featured: 0, sort: 0 }
  dialog.value = true
}

function openEdit(p: Project) {
  editingId.value = p.id
  form.value = {
    name: p.name,
    description: p.description,
    techStack: p.techStackList.join(','),
    githubUrl: p.githubUrl,
    demoUrl: p.demoUrl ?? '',
    coverUrl: p.coverUrl ?? '',
    featured: p.featured ? 1 : 0,
    sort: 0,
  }
  dialog.value = true
}

async function save() {
  if (!form.value.name || !form.value.description) {
    ElMessage.warning('项目名称和介绍不能为空')
    return
  }
  if (editingId.value == null) {
    await createProject(form.value)
    ElMessage.success('项目创建成功')
  } else {
    await updateProject(editingId.value, form.value)
    ElMessage.success('项目更新成功')
  }
  dialog.value = false
  await load()
}

async function remove(id: number) {
  await ElMessageBox.confirm('确定删除这个项目吗？', '提示', { type: 'warning' })
  await deleteProject(id)
  ElMessage.success('删除成功')
  await load()
}
</script>

<template>
  <div class="manage-panel">
    <div class="panel-bar">
      <el-button type="primary" @click="openCreate">新增项目</el-button>
    </div>
    <el-table :data="projects" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="name" label="名称" min-width="160" />
      <el-table-column prop="description" label="描述" min-width="220" show-overflow-tooltip />
      <el-table-column label="精选" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.featured" type="warning" size="small">精选</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="editingId == null ? '新增项目' : '编辑项目'" width="640px">
      <el-form label-width="80px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="技术栈"><el-input v-model="form.techStack" placeholder="逗号分隔，如 Java,SpringBoot" /></el-form-item>
        <el-form-item label="GitHub"><el-input v-model="form.githubUrl" /></el-form-item>
        <el-form-item label="演示地址"><el-input v-model="form.demoUrl" /></el-form-item>
        <el-form-item label="封面"><el-input v-model="form.coverUrl" placeholder="封面图 URL（可空）" /></el-form-item>
        <el-form-item label="精选"><el-switch v-model="form.featured" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog = false">取消</el-button>
        <el-button type="primary" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.manage-panel {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}
.panel-bar {
  margin-bottom: 16px;
}
</style>
