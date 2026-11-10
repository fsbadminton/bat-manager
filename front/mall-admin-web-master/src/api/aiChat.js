import request from '@/utils/request'
import { getToken } from '@/utils/auth'

export function sendAiMessage(message, conversationId) {
  return request({
    url: '/user/ai/chat',
    method: 'post',
    data: { message, conversationId },
    timeout: 120000
  })
}

export async function streamAiMessage(message, conversationId, onEvent) {
  const response = await fetch('/api/user/ai/chat/stream', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: getToken('/user/ai/chat/stream') || ''
    },
    body: JSON.stringify({ message, conversationId })
  })

  if (!response.ok || !response.body) {
    throw new Error(`AI 客服请求失败（${response.status}）`)
  }

  const reader = response.body.getReader()
  const decoder = new TextDecoder('utf-8')
  let buffer = ''
  let completed = false

  const consume = chunk => {
    buffer += chunk
    const events = buffer.split(/\r?\n\r?\n/)
    buffer = events.pop() || ''
    events.forEach(event => {
      const data = event
        .split(/\r?\n/)
        .filter(line => line.indexOf('data:') === 0)
        .map(line => line.slice(5).trim())
        .join('\n')
      if (!data) return
      let payload
      try {
        payload = JSON.parse(data)
      } catch (error) {
        // Ignore an incomplete SSE frame; it will be completed in the next chunk.
        return
      }
      if (payload.type === 'done') completed = true
      onEvent(payload)
    })
  }

  while (true) {
    const result = await reader.read()
    if (result.done) break
    consume(decoder.decode(result.value, { stream: true }))
  }
  consume(decoder.decode())
  if (!completed) throw new Error('AI 客服连接提前结束')
}
