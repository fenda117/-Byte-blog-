<script setup lang="ts">
import { ref,computed } from 'vue'
import { ElMessage } from 'element-plus'
import {hasToken,login,saveToken,clearToken} from "../api";

import ArticleManage from "./admin/ArticleManage.vue";
import ProjectManage from "./admin/ProjectManage.vue";
import CategoryManage from "./admin/CategoryManage.vue";
import MessageManage from "./admin/MessageManage.vue";
import MusicManage from "./admin/MusicManage.vue";
import BlogInfoManage from "./admin/BlogInfoManage.vue";
import {useRouter} from "vue-router";
const router = useRouter()

const logged = ref(hasToken())
const username = ref('')
const password = ref('')
const loading = ref(false)

const menus = [
  { key: 'article', label: '文章管理', icon: 'Document' },
  { key: 'project', label: '项目管理', icon: 'Folder' },
  { key: 'category', label: '分类管理', icon: 'Collection' },
  { key: 'music', label: '音乐管理', icon: 'Headset' },
  { key: 'message', label: '留言管理', icon: 'ChatDotRound' },
  { key: 'info', label: '个人信息', icon: 'User' },
]
const active = ref('article')
const currentLabel = computed(() => menus.find((m) => m.key === active.value)?.label ?? '')

async function doLogin() {
  if (!username.value || !password.value) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(username.value, password.value)
    saveToken(res.token)
    logged.value = true
    ElMessage.success('登录成功')
  } finally {
    loading.value = false
  }
}

function logout() {
  clearToken()
  logged.value = false
}
</script>

<template>
  <!-- 未登录：登录页 -->
  <div v-if="!logged" class="login-page">
    <div class="login-card">
      <div class="login-logo">B</div>
      <h2>博客后台管理</h2>
      <p class="login-tip">请使用管理员账号登录</p>
      <el-input
          v-model="username"
          size="large"
          placeholder="用户名"
          class="login-input"
          @keyup.enter="doLogin"
      />
      <el-input
          v-model="password"
          size="large"
          type="password"
          placeholder="密码"
          show-password
          class="login-input"
          @keyup.enter="doLogin"
      />
      <el-button type="primary" size="large" class="login-btn" :loading="loading" @click="doLogin">
        登 录
      </el-button>
      <el-link class="back-link" :underline="false" @click="router.push('/home')">
        ← 返回博客首页
      </el-link>
    </div>
  </div>

  <!-- 已登录：后台布局 -->
  <div v-else class="admin-layout">
    <aside class="admin-side">
      <div class="side-brand">
        <span class="brand-dot">B</span>
        <span class="brand-text">ByteBlog 后台</span>
      </div>
      <ul class="side-menu">
        <li
            v-for="m in menus"
            :key="m.key"
            :class="{ active: active === m.key }"
            @click="active = m.key"
        >
          <el-icon><component :is="m.icon" /></el-icon>
          <span>{{ m.label }}</span>
        </li>
      </ul>
      <div class="side-foot">
        <el-button text @click="router.push('/home')">← 返回前台</el-button>
      </div>
    </aside>

    <div class="admin-main">
      <header class="admin-head">
        <div class="head-title">{{ currentLabel }}</div>
        <div class="head-right">
          <el-tag type="success" size="small" effect="plain">已登录</el-tag>
          <el-button size="small" @click="logout">退出登录</el-button>
        </div>
      </header>

      <section class="admin-body">
        <ArticleManage v-if="active === 'article'" />
        <ProjectManage v-else-if="active === 'project'" />
        <CategoryManage v-else-if="active === 'category'" />
        <MusicManage v-else-if="active === 'music'" />
        <MessageManage v-else-if="active === 'message'" />
        <BlogInfoManage v-else-if="active === 'info'" />
      </section>
    </div>
  </div>
</template>

<style scoped>
/* ---------- 登录页 ---------- */
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}
.login-card {
  width: 380px;
  background: #fff;
  border-radius: 16px;
  padding: 36px 32px 26px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.18);
  text-align: center;
}
.login-logo {
  width: 52px;
  height: 52px;
  margin: 0 auto 14px;
  border-radius: 14px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff;
  font-size: 26px;
  font-weight: 700;
  line-height: 52px;
}
.login-card h2 {
  margin: 0 0 6px;
  font-size: 20px;
  color: #303133;
}
.login-tip {
  margin: 0 0 22px;
  color: #909399;
  font-size: 13px;
}
.login-input {
  margin-bottom: 14px;
}
.login-btn {
  width: 100%;
}
.back-link {
  margin-top: 16px;
  font-size: 13px;
}

/* ---------- 后台布局 ---------- */
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #f0f2f5;
}
.admin-side {
  width: 220px;
  flex-shrink: 0;
  background: #1f2937;
  color: #cbd5e1;
  display: flex;
  flex-direction: column;
}
.side-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 18px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.brand-dot {
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background: #6366f1;
  color: #fff;
  font-weight: 700;
  text-align: center;
  line-height: 30px;
}
.brand-text {
  font-size: 14px;
  font-weight: 600;
  color: #f1f5f9;
}
.side-menu {
  list-style: none;
  margin: 12px 0;
  padding: 0 10px;
  flex: 1;
}
.side-menu li {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 12px;
  margin-bottom: 4px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.18s;
}
.side-menu li:hover {
  background: rgba(255, 255, 255, 0.07);
  color: #fff;
}
.side-menu li.active {
  background: #6366f1;
  color: #fff;
  font-weight: 600;
}
.side-foot {
  padding: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}
.side-foot :deep(.el-button) {
  color: #94a3b8;
  width: 100%;
  justify-content: flex-start;
}

.admin-main {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
}
.admin-head {
  height: 58px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 22px;
}
.head-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}
.head-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.admin-body {
  padding: 20px;
  flex: 1;
}
</style>

