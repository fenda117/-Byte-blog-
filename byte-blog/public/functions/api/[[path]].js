export async function onRequest({ request }) {
    const url = new URL(request.url)
    const target = 'https://api.byteblog.dpdns.org' + url.pathname + url.search

    // 只保留业务需要的头，去掉会引发问题的那些
    const drop = new Set([
        'host', 'connection', 'accept-encoding',
        'cf-connecting-ip', 'cf-ray', 'cf-visitor', 'cf-ipcountry',
        'x-forwarded-proto', 'x-real-ip',
    ])
    const headers = new Headers()
    for (const [k, v] of request.headers) {
        if (!drop.has(k.toLowerCase())) headers.set(k, v)
    }

    const init = { method: request.method, headers, redirect: 'manual' }
    if (!['GET', 'HEAD'].includes(request.method)) {
        init.body = request.body          // 保留原始请求体，POST/PUT 才能用
    }

    const resp = await fetch(target, init)

    const out = new Headers(resp.headers)
    out.delete('content-encoding')       // 不删会导致浏览器解压失败、页面空白
    out.delete('content-length')
    out.set('access-control-allow-origin', '*')

    return new Response(resp.body, { status: resp.status, headers: out })
}
