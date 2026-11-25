<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { fetchBlogInfo, updateBlogInfo } from '../../api'

const loading = ref(false)
const saving = ref(false)

// 表单结构对齐后端 BlogInfoDto：name / avatarUrl / signature / aboutMe / socialLinks
const form = reactive({
  name: '',
  avatarUrl: '',
  signature: '',
  aboutMe: '',
  // 后端 DTO 收的是 Map<String,String>，这里用数组做编辑，提交时再转回 Map
  links: [] as { label: string; value: string }[],
})

async function load() {
  loading.value = true
  try {
    const info = await fetchBlogInfo()
    form.name = info?.name ?? ''
    form.avatarUrl = info?.avatarUrl ?? ''
    form.signature = info?.signature ?? ''
    form.aboutMe = info?.aboutMe ?? ''
    // 后端 VO 返回的是 [{label, value}]，正好直接给列表用
    form.links = (info?.socialLinks ?? []).map(l => ({ label: l.label, value: l.value }))
  } catch (e: any) {
    ElMessage.error(e?.message || '加载站点信息失败')
  } finally {
    loading.value = false
  }
}

onMounted(load)

function addLink() {
  form.links.push({ label: '', value: '' })
}

function removeLink(i: number) {
  form.links.splice(i, 1)
}

async function save() {
  if (!form.name.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  saving.value = true
  try {
    // 数组 -> Map，空 label 的行丢掉
    const socialLinks: Record<string, string> = {}
    form.links.forEach(l => {
      if (l.label.trim()) socialLinks[l.label.trim()] = l.value.trim()
    })
    await updateBlogInfo({
      name: form.name.trim(),
      avatarUrl: form.avatarUrl.trim(),
      signature: form.signature,
      aboutMe: form.aboutMe,
      socialLinks,
    })
    ElMessage.success('保存成功')
    await load()
  } catch (e: any) {
    ElMessage.error(e?.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<template>
  <div class="manage-panel" v-loading="loading">
    <div class="panel-bar">
      <span class="panel-title">个人信息</span>
      <el-button type="primary" :loading="saving" @click="save">保存</el-button>
    </div>

    <el-form label-width="90px" class="info-form">
      <el-form-item label="昵称">
        <el-input v-model="form.name" maxlength="50" show-word-limit placeholder="显示在左侧栏和浏览器标题" />
      </el-form-item>

      <el-form-item label="头像地址">
        <el-input v-model="form.avatarUrl" placeholder="图片 URL，留空则用默认头像" />
        <div v-if="form.avatarUrl" class="avatar-preview">
          <img :src="form.avatarUrl" alt="头像预览" />
        </div>
      </el-form-item>

      <el-form-item label="个性签名">
        <el-input v-model="form.signature" type="textarea" :rows="2" placeholder="一句话介绍自己" />
      </el-form-item>

      <el-form-item label="关于我">
        <el-input
          v-model="form.aboutMe"
          type="textarea"
          :rows="8"
          placeholder="支持 Markdown，会显示在 /about 页面"
        />
      </el-form-item>

      <el-form-item label="社交链接">
        <div class="link-list">
          <div v-for="(l, i) in form.links" :key="i" class="link-row">
            <el-input v-model="l.label" placeholder="名称，如 GitHub" style="width: 160px" />
            <el-input v-model="l.value" placeholder="链接或账号" />
            <el-button text type="danger" @click="removeLink(i)">删除</el-button>
          </div>
          <el-button text type="primary" @click="addLink">+ 添加一条</el-button>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<style scoped>
.manage-panel {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
}
.panel-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}
.info-form {
  max-width: 720px;
}
.avatar-preview {
  margin-top: 10px;
}
.avatar-preview img {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid #e5e7eb;
}
.link-list {
  width: 100%;
}
.link-row {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}
</style>
