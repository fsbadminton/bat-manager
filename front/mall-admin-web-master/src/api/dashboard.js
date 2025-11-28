import request from '@/utils/request'

// 获取订单折线图数据
export function fetchOrderChart(params) {
  // params = { start: '2025-11-01', end: '2025-11-15' }
  return request({
    url: '/admin/dashboard/order-chart',
    method: 'get',
    params: params
  })
}