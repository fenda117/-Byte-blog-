// ============ Mock 假数据 ============
// 后端接口就绪后，这些数据会被真实 API 替换，前端结构不变

export interface BlogInfo {
    nickname: string
    signature: string
    socialLinks: { label: string; value: string }[]
}

export interface Project {
    id: number
    name: string
    description: string
    techStack: string[]
    githubUrl: string
    demoUrl?: string
    featured: boolean
}

export interface Article {
    id: number
    title: string
    summary: string
    category: string
    tags: string[]
    viewCount: number
    createdAt: string
    contentHtml?: string
}

export interface Stats {
    articles: number
    projects: number
    visits: number
    days: number
}

export const mockBlogInfo: BlogInfo = {
    nickname: '芬达的站',
    signature: 'Java 后端 / Vue3 全栈学习者 · 正在求职',
    socialLinks: [
        { label: 'GitHub', value: 'https://github.com/yourname' },
        { label: '邮箱', value: 'yourname@example.com' },
        { label: 'QQ', value: '123456789' },
    ],
}

export const mockProjects: Project[] = [
    {
        id: 1,
        name: '个人技术博客',
        description: '前后端分离的个人博客，内置 AI 智能体，支持三主题切换。',
        techStack: ['Java', 'Spring Boot', 'Vue3', 'MyBatis-Plus', 'MySQL'],
        githubUrl: 'https://github.com/yourname/blog',
        featured: true,
    },
    {
        id: 2,
        name: 'AI 智能问答助手',
        description: '基于 Spring AI 与 DeepSeek 的流式对话智能体，支持 Function Calling 工具调用。',
        techStack: ['Java', 'Spring AI', 'DeepSeek', 'SSE'],
        githubUrl: 'https://github.com/yourname/ai-agent',
        featured: true,
    },
]

export const mockArticles: Article[] = [
    {
        id: 1,
        title: '从零搭建 Spring Boot + Vue3 全栈项目',
        summary: '记录我搭建个人博客的全过程，包括技术选型、目录结构和踩坑记录。',
        category: '后端',
        tags: ['Spring Boot', 'Vue3', '全栈'],
        viewCount: 128,
        createdAt: '2026-10-12',
    },
    {
        id: 2,
        title: 'Spring AI 接入 DeepSeek 实现流式对话',
        summary: '用 Spring AI 的 ChatClient 接入 DeepSeek，实现打字机效果的流式输出。',
        category: 'AI',
        tags: ['Spring AI', 'DeepSeek', 'SSE'],
        viewCount: 96,
        createdAt: '2026-10-05',
    },
]

export const mockStats: Stats = {
    articles: 12,
    projects: 6,
    visits: 3280,
    days: 120,
}

export interface Message {
    id: number
    nickname: string
    content: string
    createdAt: string
}

export const mockMessages: Message[] = [
    {
        id: 1,
        nickname: '小张',
        content: '博客做得真好看，封面和主题切换都很丝滑！',
        createdAt: '2026-10-19 21:30',
    },
    {
        id: 2,
        nickname: '面试官老王',
        content: 'AI 智能体模块很有意思，期待你的面试表现。',
        createdAt: '2026-10-18 14:22',
    },
    {
        id: 3,
        nickname: '同学小李',
        content: '求一篇 JVM 调优入门教程，蹲一个！',
        createdAt: '2026-10-17 09:15',
    },
]