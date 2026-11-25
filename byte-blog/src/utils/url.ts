/**
 * 外链地址判断。
 *
 * 后台的 GitHub / 演示地址是手填的，脚手架自带的是示例值：
 *   https://github.com/yourname/blog
 *   https://example.com/demo
 * 这些地址以 http 开头，直接渲染成 <a> 完全合法，但点开是 404 或广告页，
 * 比不显示链接更糟。所以统一在这里过一道：**只放行真正能打开的外链**。
 */
const PLACEHOLDER_HOSTS = ['yourname', 'your-name', 'example.com', 'example.org', 'xxx.com']

/** 是不是「看着像地址、其实还没填」的占位符 */
export function isPlaceholderUrl(url?: string | null): boolean {
  if (!url) return true
  const v = url.trim().toLowerCase()
  if (!v) return true
  return PLACEHOLDER_HOSTS.some((p) => v.includes(p))
}

/**
 * 转成可安全跳转的外链地址；不是 http(s) 或仍是占位符时返回空串，
 * 调用方用 v-if 判断，空串就不渲染链接。
 */
export function toExternalUrl(url?: string | null): string {
  if (!url) return ''
  const v = url.trim()
  if (!/^https?:\/\//i.test(v)) return ''
  if (isPlaceholderUrl(v)) return ''
  return v
}
