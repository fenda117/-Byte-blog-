/**
 * 后端 Jackson 配的是 `yyyy-MM-dd HH:mm:ss`，正常应该直接给这个格式。
 * 但留言、音乐这些实体的 LocalDateTime 走的是 JDK8 时间类型的默认序列化，
 * 会输出成 `2026-11-25T10:08:04`，中间的 T 直接显示给用户很难看。
 *
 * 这里统一处理：把 T 换成空格，截到分钟。
 */
export function formatDateTime(s?: string | null): string {
  if (!s) return ''
  return s.replace('T', ' ').slice(0, 16)
}
