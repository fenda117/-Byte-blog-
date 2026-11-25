/**
 * 资源地址归一化。
 *
 * 后台「音频地址 / 封面」是手填的，很容易填成编辑器里复制出来的本地绝对路径，
 * 比如 D:/byte-blog/public/music/bgm.mp3。这种地址丢给 <audio> 或 <img>，
 * 浏览器会按 file:// 协议处理并直接拒绝：
 *   Not allowed to load local resource: file:///D:/byte-blog/public/music/bgm.mp3
 *
 * public 目录下的文件由 Vite 托管在**网站根路径**，正确写法是 /music/bgm.mp3。
 * 这个函数把常见的几种错误写法都拉回正确形态，脏数据不用改库也能先播起来。
 */
export function normalizeMediaUrl(url?: string | null): string {
  if (!url) return ''
  let v = url.trim().replace(/\\/g, '/')

  // 1. file:///D:/xxx → D:/xxx
  v = v.replace(/^file:\/\/\/?/i, '')

  // 2. 本地盘符绝对路径：取 /public 或 /dist 之后的部分
  if (/^[a-zA-Z]:\//.test(v)) {
    const lower = v.toLowerCase()
    const pub = lower.indexOf('/public/')
    if (pub >= 0) return v.slice(pub + '/public'.length)
    const dist = lower.indexOf('/dist/')
    if (dist >= 0) return v.slice(dist + '/dist'.length)
    // 找不到可识别的目录，原样返回，让播放失败暴露出来，而不是静默变成空串
    return v
  }

  // 3. 多写了 public 前缀：/public/music/a.mp3 → /music/a.mp3
  if (v.toLowerCase().startsWith('/public/')) return v.slice('/public'.length)

  // 4. 误存成 http://localhost:5173/music/a.mp3，转成站内路径，换域名部署也不会坏
  const local = v.match(/^https?:\/\/(?:localhost|127\.0\.0\.1)(?::\d+)?(\/.*)$/i)
  if (local) return local[1]

  return v
}

/** 判断是不是本地文件路径（用于后台表单给出明确提示） */
export function isLocalFilePath(url?: string | null): boolean {
  if (!url) return false
  const v = url.trim()
  return /^file:\/\//i.test(v) || /^[a-zA-Z]:[\\/]/.test(v)
}
