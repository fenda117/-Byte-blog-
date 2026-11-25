import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'


export default defineConfig({
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    port: 5173,
    allowedHosts: true,
    // 后端接口代理：前端请求 /api/** 会转发到本地 Spring Boot 8080
    proxy: {
      '/api': {
        target: 'https://api.byteblog.dpdns.org',
        changeOrigin: true,
      },
    },
  },
  preview: {
    host: '0.0.0.0',
    allowedHosts: true,
  },
})
