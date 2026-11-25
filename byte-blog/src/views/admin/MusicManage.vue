<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchMusic, createMusic, updateMusic, deleteMusic } from '../../api'
import type { Music, MusicPayload } from '../../api'
import { isLocalFilePath, normalizeMediaUrl } from '../../utils/media'

const list = ref<Music[]>([])
const loading = ref(false)
const dialog = ref(false)
const editingId = ref<number | null>(null)
const form = ref<MusicPayload>({ title: '', author: '', coverUrl: '', audioUrl: '', duration: 0, sort: 0 })

async function load() {
  loading.value = true
  try {
    list.value = await fetchMusic()
  } finally {
    loading.value = false
  }
}

onMounted(load)

function openCreate() {
  editingId.value = null
  form.value = { title: '', author: '', coverUrl: '', audioUrl: '', duration: 0, sort: 0 }
  dialog.value = true
}

function openEdit(m: Music) {
  editingId.value = m.id
  form.value = {
    title: m.title,
    author: m.author,
    // 库里如果存的是 D:/xxx/public/... 这类本地路径，编辑时直接显示成可用的站内路径
    coverUrl: normalizeMediaUrl(m.coverUrl),
    audioUrl: normalizeMediaUrl(m.audioUrl),
    duration: m.duration,
    sort: m.sort,
  }
  dialog.value = true
}

async function save() {
  if (!form.value.title || !form.value.author) {
    ElMessage.warning('歌名和歌手不能为空')
    return
  }
  // 提交前先归一化，避免把一个浏览器加载不了的本地路径存进库
  const payload: MusicPayload = {
    ...form.value,
    coverUrl: normalizeMediaUrl(form.value.coverUrl),
    audioUrl: normalizeMediaUrl(form.value.audioUrl),
  }
  if (isLocalFilePath(payload.audioUrl) || isLocalFilePath(payload.coverUrl)) {
    ElMessage.error('不能填 D:/... 这样的本地路径，请改用 /music/xxx.mp3 形式')
    return
  }
  if (editingId.value == null) {
    await createMusic(payload)
    ElMessage.success('添加成功')
  } else {
    await updateMusic(editingId.value, payload)
    ElMessage.success('更新成功')
  }
  dialog.value = false
  await load()
}

async function remove(id: number) {
  await ElMessageBox.confirm('确定删除这首音乐吗？', '提示', { type: 'warning' })
  await deleteMusic(id)
  ElMessage.success('删除成功')
  await load()
}
</script>

<template>
  <div class="manage-panel">
    <div class="panel-bar">
      <el-button type="primary" @click="openCreate">新增音乐</el-button>
    </div>
    <el-table :data="list" v-loading="loading" border>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="title" label="歌名" min-width="140" />
      <el-table-column prop="author" label="歌手" width="120" />
      <el-table-column prop="duration" label="时长(秒)" width="90" />
      <el-table-column prop="sort" label="排序" width="70" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialog" :title="editingId == null ? '新增音乐' : '编辑音乐'" width="520px">
      <el-form label-width="90px">
        <el-form-item label="歌名"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="歌手"><el-input v-model="form.author" /></el-form-item>
        <el-form-item label="封面">
          <el-input v-model="form.coverUrl" placeholder="如 /music/cover.jpg，或完整的 https 链接（可空）" />
        </el-form-item>
        <el-form-item label="音频地址">
          <el-input v-model="form.audioUrl" placeholder="如 /music/bgm.mp3（可空）" />
          <div class="field-tip">
            放在 <code>public/music/</code> 下的文件，填 <code>/music/文件名</code> 即可；
            <strong>不要填 D:/... 这样的本地路径</strong>，浏览器会以 file:// 协议拒绝加载。
          </div>
        </el-form-item>
        <el-form-item label="时长(秒)"><el-input-number v-model="form.duration" :min="0" /></el-form-item>
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
.field-tip {
  margin-top: 6px;
  font-size: 12px;
  line-height: 1.7;
  color: #909399;
}
.field-tip code {
  background: #f5f7fa;
  padding: 1px 5px;
  border-radius: 3px;
  color: #606266;
}
</style>
