<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  fetchArticles,
  fetchCategories,
  createArticle,
  updateArticle,
  deleteArticle,
} from '../../api'
import type { Article, ArticlePayload, Category } from '../../api'

const articles = ref<Article[]>([])
const categories = ref<Category[]>([])
const loading = ref(false)
const dialog = ref(false)
const editingId = ref<number | null>(null)
const form = ref<ArticlePayload>({
  title: '',
  summary: '',
  contentMd: '',
  categoryId: null,
  tags: '',
  coverUrl: '',
  featured: 0,
  status: 1,
})

async function load() {
  loading.value = true
  try {
    articles.value = await fetchArticles()
    categories.value = await fetchCategories()
  } finally {
    loading.value = false
  }
}

onMounted(load)

function openCreate() {
  editingId.value = null
  form.value = { title: '', summary: '', contentMd: '', categoryId: null, tags: '', coverUrl: '', featured: 0, status: 1 }
  dialog.value = true
}

function openEdit(a: Article) {
  editingId.value = a.id
  form.value = {
    title: a.title,
    summary: a.summary,
    contentMd: a.contentMd,
    categoryId: categories.value.find((c) => c.name === a.category)?.id ?? null,
    tags: a.tags.join(','),
    coverUrl: a.coverUrl ?? '',
    featured: a.featured,
    status: a.status,
  }
  dialog.value = true
}

async function save() {
  if (!form.value.title || !form.value.contentMd) {
    ElMessage.warning('标题和正文不能为空')
    return
  }
  if (editingId.value == null) {
    await createArticle(form.value)
    ElMessage.success('文章创建成功')
  } else {
    await updateArticle(editingId.value, form.value)
    ElMessage.success('文章更新成功')
  }
  dialog.value = false
  await load()
}

async function remove(id: number) {
  await ElMessageBox.confirm('确定删除这篇文章吗？', '提示', { type: 'warning' })
  await deleteArticle(id)
  ElMessage.success('删除成功')
  await load()
}
</script>

<template>
  <div class="manage-panel">
    <div class="panel-bar">
      <el-button type="primary" @click="openCreate">新增文章</el-button>
    </div>
    <el-table :data="articles" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="category" label="分类" width="100" />
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="editingId == null ? '新增文章' : '编辑文章'" width="640px">
      <el-form label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="选择分类" clearable>
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tags" placeholder="逗号分隔，如 SpringBoot,Vue3" />
        </el-form-item>
        <el-form-item label="正文">
          <el-input v-model="form.contentMd" type="textarea" :rows="8" placeholder="Markdown 内容" />
        </el-form-item>
        <el-form-item label="封面">
          <el-input v-model="form.coverUrl" placeholder="封面图 URL（可空）" />
        </el-form-item>
        <el-form-item label="精选">
          <el-switch v-model="form.featured" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
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
