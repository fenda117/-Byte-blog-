import axios from 'axios'
import { ElMessage } from 'element-plus'

// ============ 类型定义（对齐后端 VO） ============
export interface SocialLink {
    label: string
    value: string
}

export interface BlogInfo {
    name: string
    signature: string
    avatarUrl?: string
    aboutMe?: string
    socialLinks: SocialLink[]
}

export interface Project {
    id: number
    name: string
    description: string
    techStackList: string[]
    githubUrl: string
    demoUrl: string | null
    coverUrl: string | null
    featured: boolean
    viewCount: number
    createTime: string
}

export interface Article {
    id: number
    title: string
    summary: string
    contentMd: string
    contentHtml: string
    category: string
    tags: string[]
    coverUrl: string | null
    viewCount: number
    featured: number
    status: number
    createTime: string
}

export interface Archive {
    month: string
    count: number
}

export interface Message {
    id: number
    name: string
    avatar: string
    content: string
    parentId: number
    replyName: string | null
    isTop: boolean
    ipLocation: string
    createTime: string
    children: Message[]

    /* ---------- 审核相关（后端 message 表 2026-11 新增） ---------- */
    /** 对外状态：0 待审核 1 通过 2 拒绝 */
    status?: number
    /** AI 审批结果：0 待处理 1 AI通过 2 AI拒绝 3 转人工 */
    auditStatus?: number
    /** AI 给的理由，或转人工的原因 */
    auditReason?: string | null
    /** 最终由谁定的：ai / manual / system */
    auditBy?: string | null
    auditTime?: string | null
}

export interface Category {
    id: number
    name: string
    sort: number
    articleCount: number | null
}

export interface SearchItem {
    id: number
    title: string
    summary: string
    type: string
    category: string | null
    createTime: string
}

export interface Music {
    id: number
    title: string
    author: string
    coverUrl: string | null
    audioUrl: string | null
    duration: number
    sort: number
    createTime: string
}

export interface Stats {
    articleCount: number
    projectCount: number
    visitorCount: number
    runDays: number
}

// ============ axios 封装 ============
const http = axios.create({
        baseURL: 'https://api.byteblog.dpdns.org/api',
    timeout: 15000,
})

const TOKEN_KEY = 'admin-token'

// 请求拦截器：自动携带 token
http.interceptors.request.use((config) => {
    const token = localStorage.getItem(TOKEN_KEY)
    if (token) {
        config.headers.Authorization = `Bearer ${token}`
    }
    return config
})

// 响应拦截器：后端返回 { code, msg, data }，code=200 成功
http.interceptors.response.use(
    (response) => {
        const res = response.data
        if (res.code === 200) {
            return res.data
        }
        ElMessage.error(res.msg || '请求失败')
        return Promise.reject(new Error(res.msg))
    },
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem(TOKEN_KEY)
            ElMessage.error('未登录或登录已过期')
            if (!window.location.pathname.startsWith('/admin')) {
                window.location.href = '/admin'
            }
        } else {
            ElMessage.error(error.message || '网络错误')
        }
        return Promise.reject(error)
    }
)

function get<T>(url: string, params?: object): Promise<T> {
    return http.get(url, { params }) as unknown as Promise<T>
}

function post<T>(url: string, data?: object): Promise<T> {
    return http.post(url, data) as unknown as Promise<T>
}

function put<T>(url: string, data?: object): Promise<T> {
    return http.put(url, data) as unknown as Promise<T>
}

function del<T>(url: string): Promise<T> {
    return http.delete(url) as unknown as Promise<T>
}

export function saveToken(token: string) {
    localStorage.setItem(TOKEN_KEY, token)
}
export function clearToken() {
    localStorage.removeItem(TOKEN_KEY)
}
export function hasToken(): boolean {
    return !!localStorage.getItem(TOKEN_KEY)
}

// ============ 接口函数 ============
export function fetchBlogInfo(): Promise<BlogInfo> {
    return get<BlogInfo>('/blog/info')
}

export function fetchStats(): Promise<Stats> {
    return get<Stats>('/stats')
}

export function fetchProjects(): Promise<Project[]> {
    return get<Project[]>('/projects')
}

export function fetchFeaturedProjects(): Promise<Project[]> {
    return get<Project[]>('/projects/featured')
}

export function fetchProjectDetail(id: number): Promise<Project> {
    return get<Project>(`/projects/${id}`)
}

export function fetchArticles(params?: {
    category?: string
    tag?: string
    keyword?: string
}): Promise<Article[]> {
    return get<Article[]>('/articles', params)
}

export function fetchArticleDetail(id: number): Promise<Article> {
    return get<Article>(`/articles/${id}`)
}

export function fetchArchives(): Promise<Archive[]> {
    return get<Archive[]>('/articles/archives')
}

export function fetchCategories(): Promise<Category[]> {
    return get<Category[]>('/categories')
}

export function fetchMessages(): Promise<Message[]> {
    return get<Message[]>('/messages')
}

export function submitMessage(data: {
    name: string
    avatar?: string
    content: string
    parentId?: number
    replyName?: string
}): Promise<void> {
    return post<void>('/messages', data)
}

export function fetchMusic(): Promise<Music[]> {
    return get<Music[]>('/music')
}

export function searchAll(keyword: string): Promise<SearchItem[]> {
    return get<SearchItem[]>('/search', { keyword })
}

// ============ 后台写接口（需登录） ============
export function login(username: string, password: string): Promise<{ token: string }> {
    return post('/admin/login', { username, password })
}

export interface ArticlePayload {
    title: string
    summary?: string
    contentMd: string
    categoryId?: number | null
    tags?: string
    coverUrl?: string | null
    featured?: number
    status?: number
}
export function createArticle(data: ArticlePayload): Promise<Article> {
    return post('/articles', data)
}
export function updateArticle(id: number, data: ArticlePayload): Promise<Article> {
    return put(`/articles/${id}`, data)
}
export function deleteArticle(id: number): Promise<void> {
    return del(`/articles/${id}`)
}

export interface ProjectPayload {
    name: string
    description: string
    techStack?: string
    githubUrl?: string | null
    demoUrl?: string | null
    coverUrl?: string | null
    featured?: number
    sort?: number
}
export function createProject(data: ProjectPayload): Promise<Project> {
    return post('/projects', data)
}
export function updateProject(id: number, data: ProjectPayload): Promise<Project> {
    return put(`/projects/${id}`, data)
}
export function deleteProject(id: number): Promise<void> {
    return del(`/projects/${id}`)
}

export function createCategory(data: { name: string; sort?: number }): Promise<Category> {
    return post('/categories', data)
}
export function updateCategory(id: number, data: { name: string; sort?: number }): Promise<Category> {
    return put(`/categories/${id}`, data)
}
export function deleteCategory(id: number): Promise<void> {
    return del(`/categories/${id}`)
}

export interface MusicPayload {
    title: string
    author: string
    coverUrl?: string | null
    audioUrl?: string | null
    duration?: number
    sort?: number
}
export function createMusic(data: MusicPayload): Promise<Music> {
    return post('/music', data)
}
export function updateMusic(id: number, data: MusicPayload): Promise<Music> {
    return put(`/music/${id}`, data)
}
export function deleteMusic(id: number): Promise<void> {
    return del(`/music/${id}`)
}

export function topMessage(id: number, isTop: boolean): Promise<void> {
    return put(`/messages/${id}/top?isTop=${isTop}`)
}
export function auditMessage(id: number, status: number): Promise<void> {
    return put(`/messages/${id}/audit?status=${status}`)
}
/** 后台用：返回全部留言（含待审核 status=0），前台接口会把待审核过滤掉 */
export function fetchAdminMessages(): Promise<Message[]> {
    return get<Message[]>('/messages/admin/all')
}

export function deleteMessage(id: number): Promise<void> {
    return del(`/messages/${id}`)
}

export function updateBlogInfo(data: object): Promise<BlogInfo> {
    return put('/blog/info', data)
}

// ============ AI Agent（SSE 流式） ============

export interface AgentStatus {
    ready: boolean
    model: string
    baseUrl: string
}

/** 查询后端是否已配置 API Key，用于在页面上显示"已接入 / 未配置" */
export function fetchAgentStatus(): Promise<AgentStatus> {
    return get<AgentStatus>('/agent/status')
}

export interface AgentStreamHandlers {
    /** 收到一段增量文本 */
    onDelta: (text: string) => void
    /** 流正常结束，reason 可能是 done / refuse / not_configured */
    onDone?: (reason: string) => void
    /** 服务端推过来的错误文案 */
    onError?: (message: string) => void
}

/**
 * 流式对话。
 *
 * 为什么不用 axios：axios 的 responseType 不支持增量读取，
 * 要拿到"边生成边显示"的效果，只能用 fetch + ReadableStream 自己读 SSE。
 * EventSource 也不行——它只支持 GET，而提问需要 POST body。
 */
export async function streamAgentChat(
    question: string,
    handlers: AgentStreamHandlers,
    signal?: AbortSignal,
): Promise<void> {
    const resp = await fetch('/api/agent/chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ question }),
        signal,
    })

    if (!resp.ok) {
        // 400 是参数校验失败，401/500 也走这里，尽量把后端的话带出来
        const text = await resp.text().catch(() => '')
        let msg = `请求失败（${resp.status}）`
        try {
            const j = JSON.parse(text)
            if (j?.msg) msg = j.msg
        } catch {
            if (text) msg += '：' + text.slice(0, 120)
        }
        throw new Error(msg)
    }

    if (!resp.body) throw new Error('当前浏览器不支持流式响应')

    const reader = resp.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''
    let doneReason = 'done'

    // 按空行切分 SSE 事件块
    const consume = (block: string) => {
        let event = 'message'
        const dataLines: string[] = []
        for (const line of block.split('\n')) {
            if (/^event\s*:/.test(line)) {
                event = line.replace(/^event\s*:/, '').trim()
            } else if (/^data\s*:/.test(line)) {
                dataLines.push(line.replace(/^data\s*:/, '').replace(/^\s/, ''))
            }
        }
        if (!dataLines.length) return
        const payload = dataLines.join('\n')
        if (event === 'delta') {
            try {
                const obj = JSON.parse(payload)
                if (obj?.text) handlers.onDelta(obj.text)
            } catch {
                handlers.onDelta(payload)
            }
        } else if (event === 'error') {
            try {
                const obj = JSON.parse(payload)
                handlers.onError?.(obj?.text || payload)
            } catch {
                handlers.onError?.(payload)
            }
        } else if (event === 'done') {
            try {
                doneReason = JSON.parse(payload)?.reason || 'done'
            } catch {
                doneReason = 'done'
            }
        }
    }

    try {
        for (;;) {
            const { done, value } = await reader.read()
            if (done) break
            buffer += decoder.decode(value, { stream: true }).replace(/\r\n/g, '\n')

            let idx: number
            while ((idx = buffer.indexOf('\n\n')) >= 0) {
                const block = buffer.slice(0, idx)
                buffer = buffer.slice(idx + 2)
                if (block.trim()) consume(block)
            }
        }
        // 收尾：有些实现最后不带空行
        if (buffer.trim()) consume(buffer)
        handlers.onDone?.(doneReason)
    } finally {
        reader.releaseLock()
    }
}
