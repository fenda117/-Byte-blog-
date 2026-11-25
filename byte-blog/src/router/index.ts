import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(),
    routes: [
        // 封面入口页：全屏沉浸式，不套三栏布局
        {
            path: '/',
            name: 'landing',
            component: () => import('../views/LandingView.vue'),
            meta: { title: '首页' },
        },
        // 后台管理（独立布局，不套三栏）
        {
            path: '/admin/login_fenda',
            name: 'admin',
            component: () => import('../views/AdminView.vue'),
            meta: { title: '后台管理' },
        },
        // 其余页面：带三栏布局
        {
            path: '/',
            component: () => import('../layouts/BlogLayout.vue'),
            children: [
                { path: 'home', name: 'home', component: () => import('../views/HomeView.vue'), meta: { title: '主页' } },
                { path: 'articles', name: 'articles', component: () => import('../views/ArticlesView.vue'), meta: { title: '文章' } },
                { path: 'articles/:id', name: 'article-detail', component: () => import('../views/ArticleDetailView.vue'), meta: { title: '文章详情' } },
                { path: 'projects', name: 'projects', component: () => import('../views/ProjectsView.vue'), meta: { title: '项目' } },
                { path: 'projects/:id', name: 'project-detail', component: () => import('../views/ProjectDetailView.vue'), meta: { title: '项目详情' } },
                { path: 'agent', name: 'agent', component: () => import('../views/AgentView.vue'), meta: { title: 'AI 智能体' } },
                { path: 'messages', name: 'messages', component: () => import('../views/MessagesView.vue'), meta: { title: '留言' } },
                { path: 'about', name: 'about', component: () => import('../views/AboutView.vue'), meta: { title: '关于我' } },
            ],
        },
    ],
})

// 路由切换后更新页面标题
router.afterEach((to) => {
    document.title = to.meta.title ? `${to.meta.title} · 我的博客` : '我的博客'
})

export default router
