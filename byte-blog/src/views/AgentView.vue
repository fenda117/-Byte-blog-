<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchAgentStatus, streamAgentChat } from '../api'
import type { AgentStatus } from '../api'
import { renderSimpleMarkdown } from '../utils/markdown'

interface ChatMessage {
  role: 'user' | 'assistant'
  content: string
  error?: boolean
  streaming?: boolean
}

const GREETING =
  '你好，我是站长的 AI 助手 👋\n\n' +
  '可以问我关于他的**项目**、**技术栈**、**文章内容**，或者这个博客是怎么搭起来的。\n\n' +
  '超出这个范围的问题我会礼貌拒绝，见谅。'

const messages = ref<ChatMessage[]>([{ role: 'assistant', content: GREETING }])
const input = ref('')
const sending = ref(false)
const listRef = ref<HTMLElement>()
const status = ref<AgentStatus | null>(null)

let controller: AbortController | null = null

const suggestions = [
  '站长做过哪些项目？',
  '这个博客用的是什么技术栈？',
  '有没有讲 Spring Boot 的文章？',
  '怎么联系站长？',
]

const ready = computed(() => status.value?.ready === true)
const modelLabel = computed(() => status.value?.model || '未接入')
const showSuggestions = computed(() => messages.value.length <= 1 && !sending.value)

onMounted(async () => {
  try {
    status.value = await fetchAgentStatus()
  } catch {
    // 状态接口挂了不影响提问，只是标签显示不出来
    status.value = null
  }
})

onBeforeUnmount(() => controller?.abort())

function scrollToBottom() {
  nextTick(() => {
    listRef.value?.scrollTo({ top: listRef.value.scrollHeight, behavior: 'smooth' })
  })
}

async function send(text?: string) {
  const q = (text ?? input.value).trim()
  if (!q || sending.value) return

  input.value = ''
  messages.value.push({ role: 'user', content: q })
  messages.value.push({ role: 'assistant', content: '', streaming: true })
  // 用索引拿代理对象，直接改局部变量不会触发视图更新
  const idx = messages.value.length - 1
  const reply = messages.value[idx]

  sending.value = true
  scrollToBottom()

  controller = new AbortController()
  try {
    await streamAgentChat(
      q,
      {
        onDelta: (t) => {
          reply.content += t
          scrollToBottom()
        },
        onError: (m) => {
          reply.error = true
          reply.content = m
        },
        onDone: (reason) => {
          if (!reply.content) {
            reply.content =
              reason === 'not_configured'
                ? '后端还没有配置 API Key，去 `application.properties` 里填上就能用了。'
                : '（这次没有返回内容，换个问法试试）'
          }
        },
      },
      controller.signal,
    )
  } catch (e: any) {
    if (e?.name === 'AbortError') {
      reply.content += reply.content ? '\n\n_（已停止生成）_' : '（已停止）'
    } else {
      reply.error = true
      reply.content = e?.message || '请求失败，请稍后重试'
    }
  } finally {
    reply.streaming = false
    sending.value = false
    controller = null
    scrollToBottom()
  }
}

function stop() {
  controller?.abort()
}

function onKeydown(e: KeyboardEvent) {
  // Enter 发送，Shift+Enter 换行
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    send()
  }
}

function clearChat() {
  if (sending.value) return
  messages.value = [{ role: 'assistant', content: GREETING }]
  ElMessage.success('已清空对话')
}
</script>

<template>
  <div class="chat card">
    <div class="chat-header">
      <el-icon :size="20"><Cpu /></el-icon>
      <span>AI 助手</span>
      <el-tag size="small" :type="ready ? 'success' : 'info'" effect="light">
        {{ ready ? modelLabel : '未配置 Key' }}
      </el-tag>
      <div class="header-right">
        <el-button v-if="messages.length > 1" size="small" text @click="clearChat">清空</el-button>
      </div>
    </div>

    <div ref="listRef" class="chat-list">
      <div v-for="(m, i) in messages" :key="i" class="chat-msg" :class="m.role">
        <div class="bubble" :class="{ error: m.error }">
          <template v-if="m.role === 'assistant'">
            <div
              v-if="m.content"
              class="md-body"
              v-html="renderSimpleMarkdown(m.content)"
            ></div>
            <span v-if="m.streaming" class="cursor" :class="{ alone: !m.content }">
              <template v-if="!m.content">正在思考…</template>
            </span>
          </template>
          <template v-else>{{ m.content }}</template>
        </div>
      </div>

      <div v-if="showSuggestions" class="suggestions">
        <span class="sug-title">试试这样问：</span>
        <el-tag
          v-for="s in suggestions"
          :key="s"
          class="sug"
          effect="plain"
          @click="send(s)"
        >
          {{ s }}
        </el-tag>
      </div>
    </div>

    <div class="chat-input">
      <el-input
        v-model="input"
        type="textarea"
        :rows="2"
        resize="none"
        placeholder="问我关于站长的问题…（Enter 发送，Shift+Enter 换行）"
        @keydown="onKeydown"
      />
      <div class="input-actions">
        <el-button v-if="sending" @click="stop">停止</el-button>
        <el-button type="primary" :disabled="sending" @click="send()">发送</el-button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.chat {
  display: flex;
  flex-direction: column;
  height: 600px;
  padding: 0;
  overflow: hidden;
}
.chat-header {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border);
  font-weight: 600;
}
.header-right {
  margin-left: auto;
}
.chat-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 14px;
}
.chat-msg {
  display: flex;
}
.chat-msg.user {
  justify-content: flex-end;
}
.chat-msg.assistant {
  justify-content: flex-start;
}
.bubble {
  max-width: 78%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.chat-msg.user .bubble {
  background: var(--primary);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.chat-msg.assistant .bubble {
  background: var(--primary-soft);
  color: var(--text);
  border-bottom-left-radius: 4px;
}
.bubble.error {
  background: #fef2f2;
  color: #b91c1c;
}
/* 流式光标：一个闪烁的竖线，让"正在打字"有实感 */
.cursor::after {
  content: '▍';
  margin-left: 2px;
  animation: blink 1s steps(2, start) infinite;
  color: var(--primary);
}
.cursor.alone::after {
  content: '';
}
@keyframes blink {
  to {
    visibility: hidden;
  }
}
.md-body :deep(p) {
  margin: 0 0 8px;
}
.md-body :deep(p:last-child) {
  margin-bottom: 0;
}
.md-body :deep(ul),
.md-body :deep(ol) {
  margin: 4px 0 8px;
  padding-left: 20px;
}
/* 全局 reset 把 list-style 清掉了，这里补回来，否则模型回答里的列表没有圆点 */
.md-body :deep(ul) {
  list-style: disc;
}
.md-body :deep(ol) {
  list-style: decimal;
}
.md-body :deep(li) {
  margin-bottom: 4px;
}
.md-body :deep(h3),
.md-body :deep(h4) {
  font-size: 15px;
  margin: 10px 0 6px;
}
.md-body :deep(code) {
  background: rgba(0, 0, 0, 0.06);
  padding: 1px 5px;
  border-radius: 3px;
  font-size: 13px;
}
.md-body :deep(pre) {
  background: rgba(0, 0, 0, 0.06);
  padding: 10px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 8px 0;
}
.md-body :deep(pre code) {
  background: none;
  padding: 0;
}
.suggestions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
  padding-left: 4px;
}
.sug-title {
  font-size: 13px;
  color: var(--text-secondary);
}
.sug {
  cursor: pointer;
}
.sug:hover {
  color: var(--primary);
  border-color: var(--primary);
}
.chat-input {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 12px 20px;
  border-top: 1px solid var(--border);
}
.chat-input :deep(.el-textarea) {
  flex: 1;
}
.input-actions {
  display: flex;
  gap: 8px;
  padding-bottom: 2px;
}
</style>
