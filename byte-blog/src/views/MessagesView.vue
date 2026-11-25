<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchMessages, submitMessage } from '../api'
import type { Message } from '../api'
import { formatDateTime } from '../utils/time'

const messages = ref<Message[]>([])
const name = ref('')
const content = ref('')
const replyTo = ref<Message | null>(null)

async function load() {
  messages.value = await fetchMessages()
}

onMounted(load)

function reply(m: Message) {
  replyTo.value = m
}

function cancelReply() {
  replyTo.value = null
}

async function submit() {
  const text = content.value.trim()
  if (!text) {
    ElMessage.warning('写点什么再提交吧')
    return
  }
  await submitMessage({
    name: name.value.trim() || '匿名访客',
    content: text,
    parentId: replyTo.value?.id ?? 0,
    replyName: replyTo.value?.name,
  })
  content.value = ''
  replyTo.value = null
  ElMessage.success('留言成功，博主审核后会展示')
  await load()
}
</script>

<template>
  <div>
    <h2 class="page-title">留言板</h2>
    <p class="page-desc">路过就留句话吧，每一条我都会认真看。</p>

    <!-- 留言输入 -->
    <div class="card msg-form">
      <div v-if="replyTo" class="reply-tip">
        回复 @{{ replyTo.name }}：
        <span class="reply-cancel" @click="cancelReply">取消</span>
      </div>
      <el-input v-model="name" placeholder="你的昵称（可留空，默认匿名）" maxlength="20" />
      <el-input
          v-model="content"
          type="textarea"
          :rows="3"
          placeholder="说点什么吧…"
          maxlength="200"
          show-word-limit
      />
      <div class="form-actions">
        <el-button type="primary" @click="submit">发表留言</el-button>
      </div>
    </div>

    <!-- 留言列表 -->
    <div v-for="m in messages" :key="m.id" class="card msg-item">
      <img class="msg-avatar" :src="m.avatar" :alt="m.name" />
      <div class="msg-body">
        <div class="msg-head">
          <span class="msg-name">{{ m.name }}</span>
          <el-tag v-if="m.isTop" size="small" type="warning">置顶</el-tag>
          <span class="msg-ip">{{ m.ipLocation }}</span>
          <span class="msg-time">{{ formatDateTime(m.createTime) }}</span>
        </div>
        <p class="msg-content">{{ m.content }}</p>
        <div class="msg-actions">
          <span @click="reply(m)">回复</span>
        </div>

        <!-- 子留言（回复） -->
        <div v-if="m.children && m.children.length" class="msg-children">
          <div v-for="c in m.children" :key="c.id" class="msg-child">
            <img class="msg-avatar sm" :src="c.avatar" :alt="c.name" />
            <div class="msg-body">
              <div class="msg-head">
                <span class="msg-name">{{ c.name }}</span>
                <span class="msg-reply">回复 @{{ c.replyName }}</span>
                <span class="msg-ip">{{ c.ipLocation }}</span>
                <span class="msg-time">{{ formatDateTime(c.createTime) }}</span>
              </div>
              <p class="msg-content">{{ c.content }}</p>
              <div class="msg-actions">
                <span @click="reply(m)">回复</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-if="!messages.length" description="还没有留言，来抢沙发！" />
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
.msg-form {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.reply-tip {
  color: var(--primary);
  font-size: 13px;
  background: var(--primary-soft);
  padding: 8px 12px;
  border-radius: 8px;
}
.reply-cancel {
  margin-left: 8px;
  cursor: pointer;
  text-decoration: underline;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
}
.msg-item {
  display: flex;
  gap: 12px;
}
.msg-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  flex-shrink: 0;
}
.msg-avatar.sm {
  width: 30px;
  height: 30px;
}
.msg-body {
  flex: 1;
  min-width: 0;
}
.msg-head {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 6px;
}
.msg-name {
  font-weight: 700;
  font-size: 14px;
}
.msg-ip {
  color: var(--text-secondary);
  font-size: 12px;
}
.msg-reply {
  color: var(--primary);
  font-size: 12.5px;
}
.msg-time {
  color: var(--text-secondary);
  font-size: 12px;
}
.msg-content {
  color: var(--text);
  font-size: 14px;
  line-height: 1.7;
  margin: 0 0 6px;
}
.msg-actions {
  font-size: 12.5px;
  color: var(--primary);
}
.msg-actions span {
  cursor: pointer;
}
.msg-children {
  margin-top: 12px;
  padding-left: 8px;
  border-left: 2px solid var(--border);
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.msg-child {
  display: flex;
  gap: 10px;
}
</style>
