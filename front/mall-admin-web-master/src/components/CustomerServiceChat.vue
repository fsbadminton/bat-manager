<template>
  <div class="customer-service">
    <el-button
      v-if="!opened"
      class="customer-service-trigger"
      type="primary"
      icon="el-icon-chat-dot-round"
      circle
      title="打开 AI 客服"
      @click="open"
    />
    <section v-else class="customer-service-panel" aria-label="AI 客服">
      <header class="customer-service-header">
        <div>
          <strong>AI 客服</strong>
          <small>在线为您查询商品和订单</small>
        </div>
        <el-button type="text" icon="el-icon-close" title="关闭客服" @click="opened = false" />
      </header>
      <div ref="messages" class="customer-service-messages">
        <div v-for="(item, index) in messages" :key="index" :class="['customer-message', item.role]">
          <div class="customer-message-bubble">
            <div v-if="item.role === 'assistant'" class="customer-message-content" v-html="renderMessage(item.content)"></div>
            <div v-else class="customer-message-content customer-message-plain">{{ item.content }}</div>
            <span v-if="item.streaming" class="customer-message-cursor" aria-hidden="true" />
          </div>
        </div>
      </div>
      <div class="customer-service-composer">
        <el-input
          v-model="draft"
          type="textarea"
          :rows="2"
          resize="none"
          maxlength="500"
          show-word-limit
          placeholder="请输入您的问题"
          @keyup.enter.native.exact="send"
        />
        <el-button type="primary" icon="el-icon-s-promotion" :loading="loading" :disabled="!draft.trim()" @click="send">发送</el-button>
      </div>
    </section>
  </div>
</template>

<script>
import { streamAiMessage } from '@/api/aiChat'

export default {
  name: 'CustomerServiceChat',
  data() {
    return {
      opened: false,
      loading: false,
      draft: '',
      conversationId: '',
      messages: [
        { role: 'assistant', content: '您好，我是 AI 客服，可以帮您查询球拍商品、订单、优惠券和售后流程。' }
      ]
    }
  },
  methods: {
    open() {
      this.opened = true
      this.scrollToBottom()
    },
    send() {
      const message = this.draft.trim()
      if (!message || this.loading) return
      this.messages.push({ role: 'user', content: message })
      this.draft = ''
      this.loading = true
      const assistant = { role: 'assistant', content: '', streaming: true }
      this.messages.push(assistant)
      this.scrollToBottom()
      streamAiMessage(message, this.conversationId, event => {
        if (!event) return
        if (event.type === 'delta') {
          assistant.content += event.content || ''
          this.scrollToBottom()
        } else if (event.type === 'done') {
          this.conversationId = event.conversationId || this.conversationId
          assistant.streaming = false
          if (!assistant.content) assistant.content = '抱歉，客服暂时没有返回结果。'
        } else if (event.type === 'error') {
          throw new Error(event.message || 'AI 客服返回错误')
        }
      })
        .catch(() => {
          assistant.streaming = false
          assistant.content = '暂时无法连接 AI 客服，请稍后重试或联系人工客服。'
        })
        .finally(() => {
          this.loading = false
          this.$nextTick(this.scrollToBottom)
        })
    },
    renderMessage(content) {
      const source = String(content || '').replace(/\r\n/g, '\n')
      if (!source) return '<span class="customer-message-placeholder">正在生成...</span>'

      const escapeHtml = value => value
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
      const inline = value => escapeHtml(value)
        .replace(/`([^`]+)`/g, '<code>$1</code>')
        .replace(/\*\*([^*]+)\*\*/g, '<strong>$1</strong>')
        .replace(/__([^_]+)__/g, '<strong>$1</strong>')
        .replace(/\[([^\]]+)\]\((https?:\/\/[^\s)]+)\)/g, '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>')
        .replace(/\*([^*]+)\*/g, '<em>$1</em>')
      const lines = source.split('\n')
      const html = []
      let listType = null
      let inCode = false
      let codeLines = []
      const closeList = () => {
        if (listType) html.push(`</${listType}>`)
        listType = null
      }

      for (let index = 0; index < lines.length; index += 1) {
        const line = lines[index]
        if (line.trim().indexOf('```') === 0) {
          if (inCode) {
            html.push(`<pre><code>${escapeHtml(codeLines.join('\n'))}</code></pre>`)
            codeLines = []
          }
          inCode = !inCode
          return
        }
        if (inCode) {
          codeLines.push(line)
          return
        }
        const heading = line.match(/^\s*(#{1,3})\s+(.+)$/)
        const bullet = line.match(/^\s*[-*]\s+(.+)$/)
        const ordered = line.match(/^\s*\d+[.)]\s+(.+)$/)
        const tableRow = /^\s*\|.*\|\s*$/.test(line)
        const nextLine = lines[index + 1] || ''
        const tableSeparator = /^\s*\|?\s*:?-{3,}:?\s*(\|\s*:?-{3,}:?\s*)+\|?\s*$/.test(nextLine)
        if (tableRow && tableSeparator) {
          closeList()
          const rows = []
          let rowIndex = index
          while (rowIndex < lines.length && /^\s*\|.*\|\s*$/.test(lines[rowIndex])) {
            rows.push(lines[rowIndex].trim().replace(/^\|\s*|\s*\|$/g, '').split('|').map(cell => cell.trim()))
            rowIndex += 1
          }
          const header = rows.shift() || []
          rows.shift()
          html.push('<table><thead><tr>')
          header.forEach(cell => html.push(`<th>${inline(cell)}</th>`))
          html.push('</tr></thead><tbody>')
          rows.forEach(row => {
            html.push('<tr>')
            header.forEach((_, cellIndex) => html.push(`<td>${inline(row[cellIndex] || '')}</td>`))
            html.push('</tr>')
          })
          html.push('</tbody></table>')
          index = rowIndex - 1
        } else if (heading) {
          closeList()
          html.push(`<h${heading[1].length}>${inline(heading[2])}</h${heading[1].length}>`)
        } else if (bullet || ordered) {
          const type = bullet ? 'ul' : 'ol'
          if (listType !== type) {
            closeList()
            listType = type
            html.push(`<${type}>`)
          }
          html.push(`<li>${inline((bullet || ordered)[1])}</li>`)
        } else if (!line.trim()) {
          closeList()
        } else {
          closeList()
          html.push(`<p>${inline(line)}</p>`)
        }
      }
      if (inCode) html.push(`<pre><code>${escapeHtml(codeLines.join('\n'))}</code></pre>`)
      closeList()
      return html.join('')
    },
    scrollToBottom() {
      this.$nextTick(() => {
        const node = this.$refs.messages
        if (node) node.scrollTop = node.scrollHeight
      })
    }
  }
}
</script>

<style scoped>
.customer-service-trigger {
  position: fixed;
  right: 28px;
  bottom: 28px;
  z-index: 1100;
  width: 48px;
  height: 48px;
  box-shadow: 0 6px 18px rgba(32, 52, 75, 0.2);
}

.customer-service-panel {
  position: fixed;
  right: 28px;
  bottom: 28px;
  z-index: 1100;
  display: flex;
  flex-direction: column;
  width: min(380px, calc(100vw - 32px));
  height: min(560px, calc(100vh - 48px));
  overflow: hidden;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  box-shadow: 0 12px 32px rgba(32, 52, 75, 0.2);
}

.customer-service-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 16px 18px;
  color: #1f2d3d;
  background: #fff;
  border-bottom: 1px solid #ebeef5;
}

.customer-service-header small {
  display: block;
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}

.customer-service-messages {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #f4f7fb;
}

.customer-message {
  display: flex;
  margin-bottom: 12px;
}

.customer-message.user {
  justify-content: flex-end;
}

.customer-message-bubble {
  max-width: 84%;
  padding: 11px 14px;
  color: #303133;
  line-height: 1.55;
  word-break: break-word;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px 10px 10px 3px;
  box-shadow: 0 2px 8px rgba(32, 52, 75, 0.05);
}

.customer-message.user .customer-message-bubble {
  color: #fff;
  background: #409eff;
  border-color: #409eff;
  border-radius: 10px 10px 3px 10px;
  box-shadow: none;
}

.customer-message-content >>> p {
  margin: 0 0 8px;
}

.customer-message-content >>> p:last-child {
  margin-bottom: 0;
}

.customer-message-content >>> h1,
.customer-message-content >>> h2,
.customer-message-content >>> h3 {
  margin: 0 0 8px;
  color: #1f2d3d;
  font-weight: 600;
  line-height: 1.35;
}

.customer-message-content >>> h1 { font-size: 17px; }
.customer-message-content >>> h2 { font-size: 16px; }
.customer-message-content >>> h3 { font-size: 15px; }

.customer-message-content >>> ul,
.customer-message-content >>> ol {
  margin: 5px 0 8px;
  padding-left: 21px;
}

.customer-message-content >>> li {
  margin: 3px 0;
}

.customer-message-content >>> table {
  width: 100%;
  margin: 8px 0;
  border-collapse: collapse;
  font-size: 12px;
}

.customer-message-content >>> th,
.customer-message-content >>> td {
  padding: 5px 7px;
  text-align: left;
  border: 1px solid #e5eaf2;
}

.customer-message-content >>> th {
  color: #52606d;
  font-weight: 600;
  background: #f5f7fa;
}

.customer-message-content >>> a {
  color: #2f80ed;
  text-decoration: none;
}

.customer-message-content >>> a:hover {
  text-decoration: underline;
}

.customer-message-content >>> code {
  padding: 1px 5px;
  color: #c0392b;
  font-size: 12px;
  background: #f7f1f0;
  border-radius: 4px;
}

.customer-message-content >>> pre {
  margin: 8px 0;
  padding: 9px 10px;
  overflow-x: auto;
  color: #eaf2ff;
  font-size: 12px;
  line-height: 1.5;
  background: #263445;
  border-radius: 6px;
}

.customer-message-content >>> pre code {
  padding: 0;
  color: inherit;
  background: transparent;
}

.customer-message-plain {
  white-space: pre-wrap;
}

.customer-message-placeholder {
  color: #909399;
}

.customer-message-cursor {
  display: inline-block;
  width: 2px;
  height: 15px;
  margin-left: 3px;
  vertical-align: -2px;
  background: #409eff;
  animation: customer-cursor-blink 1s step-end infinite;
}

@keyframes customer-cursor-blink {
  50% { opacity: 0; }
}

.customer-service-composer {
  display: flex;
  gap: 8px;
  align-items: flex-end;
  padding: 12px;
  background: #fff;
  border-top: 1px solid #ebeef5;
}

.customer-service-composer .el-button {
  flex: 0 0 auto;
}

@media (max-width: 600px) {
  .customer-service-trigger,
  .customer-service-panel {
    right: 16px;
    bottom: 16px;
  }
}
</style>
