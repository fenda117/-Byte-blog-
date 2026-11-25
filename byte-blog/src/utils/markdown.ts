/**
 * 极简 Markdown 渲染。
 *
 * 只处理这几种：`#` 标题、`-` / `*` 列表、`**加粗**`、`` `代码` ``、空行分段。
 * 没有引入 marked / markdown-it——它们加起来 30KB+，而这里只用来渲染
 * 「关于我」和 AI 气泡，语法范围完全可控。
 *
 * 安全说明：所有文本先经过 HTML 转义，再拼标签，
 * 所以模型输出的 <script> 之类只会显示成文本，不会被执行。
 */
export function renderSimpleMarkdown(src?: string | null): string {
  const text = (src ?? '').replace(/\r\n/g, '\n').trim()
  if (!text) return ''

  const esc = (s: string) =>
    s.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')

  const inline = (s: string) =>
    esc(s)
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/`([^`]+)`/g, '<code>$1</code>')

  const out: string[] = []
  let list: string[] = []
  let inCode = false
  let codeBuffer: string[] = []

  const flushList = () => {
    if (list.length) {
      out.push('<ul>' + list.map(i => `<li>${i}</li>`).join('') + '</ul>')
      list = []
    }
  }
  const flushCode = () => {
    if (codeBuffer.length) {
      out.push('<pre><code>' + esc(codeBuffer.join('\n')) + '</code></pre>')
      codeBuffer = []
    }
  }

  for (const raw of text.split('\n')) {
    const line = raw.trimEnd()

    // 三反引号代码块
    if (line.trim().startsWith('```')) {
      if (inCode) {
        flushCode()
        inCode = false
      } else {
        flushList()
        inCode = true
      }
      continue
    }
    if (inCode) {
      codeBuffer.push(raw)
      continue
    }

    if (!line.trim()) {
      flushList()
      continue
    }

    const h = line.match(/^(#{1,4})\s+(.*)$/)
    if (h) {
      flushList()
      const lv = Math.min(h[1].length + 2, 6)
      out.push(`<h${lv}>${inline(h[2])}</h${lv}>`)
      continue
    }

    const li = line.match(/^\s*[-*]\s+(.*)$/)
    if (li) {
      list.push(inline(li[1]))
      continue
    }

    const ol = line.match(/^\s*\d+\.\s+(.*)$/)
    if (ol) {
      list.push(inline(ol[1]))
      continue
    }

    flushList()
    out.push(`<p>${inline(line)}</p>`)
  }
  flushList()
  flushCode()
  return out.join('')
}
