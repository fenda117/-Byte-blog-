<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchCategories, createCategory, updateCategory, deleteCategory } from '../../api'
import type { Category } from '../../api'

const categories = ref<Category[]>([])
const loading = ref(false)
const dialog = ref(false)
const editingId = ref<number | null>(null)
const form = ref({ name: '', sort: 0 })

async function load() {
  loading.value = true
  try {
    categories.value = await fetchCategories()
  } finally {
    loading.value = false
  }
}

onMounted(load)

function openCreate() {
  editingId.value = null
  form.value = { name: '', sort: 0 }
  dialog.value = true
}

function openEdit(c: Category) {
  editingId.value = c.id
  form.value = { name: c.name, sort: c.sort }
  dialog.value = true
}

async function save() {
  if (!form.value.name) {
    ElMessage.warning('分类名不能为空')
    return
  }
  if (editingId.value == null) {
    await createCategory(form.value)
    ElMessage.success('分类创建成功')
  } else {
    await updateCategory(editingId.value, form.value)
    ElMessage.success('分类更新成功')
  }
  dialog.value = false
  await load()
}

async function remove(id: number) {
  await ElMessageBox.confirm('确定删除这个分类吗？', '提示', { type: 'warning' })
  await deleteCategory(id)
  ElMessage.success('删除成功')
  await load()
}
</script>

<template>
  <div class="manage-panel">
    <div class="panel-bar">
      <el-button type="primary" @click="openCreate">新增分类</el-button>
    </div>
    <el-table :data="categories" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="名称" min-width="150" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="articleCount" label="文章数" width="90" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="editingId == null ? '新增分类' : '编辑分类'" width="420px">
      <el-form label-width="70px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="form.sort" :min="0" /></el-form-item>
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
