import request from '@/utils/request'

export function listMySupportTickets(limit = 20) {
  return request({ url: '/user/support-tickets', method: 'get', params: { limit } })
}

export function getMySupportTicket(ticketNo) {
  return request({ url: `/user/support-tickets/${encodeURIComponent(ticketNo)}`, method: 'get' })
}

export function createMySupportTicket(data) {
  return request({ url: '/user/support-tickets', method: 'post', data, timeout: 15000 })
}
