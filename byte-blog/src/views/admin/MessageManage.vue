<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { fetchAdminMessages, auditMessage, topMessage, deleteMessage } from '../../api'
import type { Message } from '../../api'
import { formatDateTime } from '../../utils/time'

const list = ref<Message[]>([])
const loading = ref(false)
const filter = ref<'all' | 'pending' | 'pass' | 'reject'>('all')

async function load() {
  loading.value = true
  try {
    list.value = await fetchAdminMessages()
  } finally {
    loading.value = false
  }
}

onMounted(load)

/** AI 判不了、还没人处理的，就是需要站长亲自看的 */
function isPending(m: Message) {
  return m.auditStatus === 3 && m.status === 0
}

const pendingCount = computed(() => list.value.filter(isPending).length)

const counts = computed(() => ({
  all: list.value.length,
  pending: pendingCount.value,
  pass: list.value.filter(m => m.auditStatus === 1).length,
  reject: list.value.filter(m => m.auditStatus === 2).length,
}))

const shown = computed(() => {
  if (filter.value === 'pending') return list.value.filter(isPending)
  if (filter.value === 'pass') return list.value.filter(m => m.auditStatus === 1)
  if (filter.value === 'reject') return list.value.filter(m => m.auditStatus === 2)
  return list.value
})

/** AI 那一列显示什么 */
function aiLabel(m: Message) {
  switch (m.auditStatus) {
    case 1: return 'AI 通过'
    case 2: return 'AI 拒绝'
    // 3 有两种情况：还等着人处理，或者人已经处理过了
    case 3: return m.auditBy === 'manual' ? '人工介入' : '待人工'
    default: return '待处理'
  }
}

function aiTagType(m: Message) {
  switch (m.auditStatus) {
    case 1: return 'success'
    case 2: return 'danger'
    case 3: return m.auditBy === 'manual' ? 'info' : 'warning'
    default: return 'info'
  }
}

/** 对外是否可见 */
function visibleLabel(m: Message) {
  if (m.status === 1) return '已展示'
  if (m.status === 2) return '已拒绝'
  return '未展示'
}

function visibleTagType(m: Message) {
  if (m.status === 1) return 'success'
  if (m.status === 2) return 'danger'
  return 'info'
}

async function audit(m: Message, status: number) {
  await auditMessage(m.id, status)
  ElMessage.success(status === 1 ? '已通过，留言已展示' : '已拒绝，留言不展示')
  await load()
}

async function top(m: Message) {
  await topMessage(m.id, !m.isTop)
  ElMessage.success(m.isTop ? '已取消置顶' : '已置顶')
  await load()
}

async function remove(id: number) {
  await ElMessageBox.confirm('确定删除这条留言吗？', '提示', { type: 'warning' })
  await deleteMessage(id)
  ElMessage.success('删除成功')
  await load()
}
</script>

<template>
  <div class="manage-panel">
    <!-- 有待人工的才提示，没有就别占地方 -->
    <el-alert
      v-if="pendingCount > 0"
      type="warning"
      show-icon
      :closable="false"
      class="pending-tip"
    >
      <template #title>
        有 <b>{{ pendingCount }}</b> 条留言 AI 拿不准，等你决定
      </template>
      <template #default>
        这些留言大多语气模糊、疑似广告但不确定，或者内容像是在试探审核规则。AI 没把握就不替你做决定。
      </template>
    </el-alert>

    <div class="toolbar">
      <el-radio-group v-model="filter" size="small">
        <el-radio-button value="all">全部 {{ counts.all }}</el-radio-button>
        <el-radio-button value="pending">待人工 {{ counts.pending }}</el-radio-button>
        <el-radio-button value="pass">AI 通过 {{ counts.pass }}</el-radio-button>
        <el-radio-button value="reject">AI 拒绝 {{ counts.reject }}</el-radio-button>
      </el-radio-group>
      <el-button size="small" @click="load">刷新</el-button>
    </div>

    <el-table :data="shown" v-loading="loading" border row-key="id">
      <el-table-column prop="id" label="ID" width="58" />

      <el-table-column prop="name" label="昵称" width="92" show-overflow-tooltip />

      <el-table-column label="内容" min-width="200">
        <template #default="{ row }">
          <div class="cell-content">{{ row.content }}</div>
          <!-- AI 理由放在内容下面，站长判断时不用再点开 -->
          <div v-if="row.auditReason" class="cell-reason">{{ row.auditReason }}</div>
        </template>
      </el-table-column>

      <el-table-column label="AI 审批" width="128">
        <template #default="{ row }">
          <el-tag :type="aiTagType(row)" size="small">{{ aiLabel(row) }}</el-tag>
          <el-tag v-if="row.auditBy === 'system'" size="small" type="info" class="ml4">系统</el-tag>
        </template>
      </el-table-column>

      <el-table-column label="状态" width="82">
        <template #default="{ row }">
          <el-tag :type="visibleTagType(row)" size="small" effect="plain">
            {{ visibleLabel(row) }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column prop="ipLocation" label="IP 属地" width="96" show-overflow-tooltip />

      <el-table-column label="时间" width="132">
        <template #default="{ row }">{{ formatDateTime(row.createTime) }}</template>
      </el-table-column>

      <el-table-column label="操作" width="266" fixed="right">
        <template #default="{ row }">
          <el-button
            v-if="row.status !== 1"
            size="small"
            type="success"
            @click="audit(row, 1)"
          >通过</el-button>
          <el-button
            v-if="row.status !== 2"
            size="small"
            type="danger"
            plain
            @click="audit(row, 2)"
          >拒绝</el-button>
          <el-button size="small" @click="top(row)">{{ row.isTop ? '取消置顶' : '置顶' }}</el-button>
          <el-button size="small" type="danger" text @click="remove(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && !shown.length" description="这个分类下没有留言" />
  </div>
</template>

<style scoped>
.manage-panel {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}

.pending-tip {
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.cell-content {
  line-height: 1.5;
  word-break: break-word;
}

.cell-reason {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.ml4 {
  margin-left: 4px;
}
</style>
