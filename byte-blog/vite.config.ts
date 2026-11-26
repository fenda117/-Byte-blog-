import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'

// 后端接口代理：前端请求 /api/** 会转发到本地 Spring Boot 8080
// 抽成常量，server（开发）和 preview（本地预览打包产物）共用，
// 否则 npm run preview 时接口全部 502
const proxy = {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true,
  },
}

export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    allowedHosts: true,
    proxy,
  },
  preview: {
    host: '0.0.0.0',
    allowedHosts: true,
    proxy,
  },
})
