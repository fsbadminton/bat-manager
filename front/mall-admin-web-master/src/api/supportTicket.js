import request from '@/utils/request'

export function listSupportTickets(limit = 100) {
  return request({ url: '/admin/support-tickets', method: 'get', params: { limit } })
}

export function updateSupportTicket(ticketNo, data) {
  return request({ url: `/admin/support-tickets/${encodeURIComponent(ticketNo)}`, method: 'put', data })
}
