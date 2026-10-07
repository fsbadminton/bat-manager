import request from '@/utils/request'
export function fetchList(params, config = {}) {
  return request({
    url:'/admin/order/list',
    method:'get',
    params:params,
    ...config
  })
}

export function closeOrder(data) {
  return request({
    url:'/admin/order/update/close',
    method:'post',
    data:data,
    headers: {
      'Content-Type': 'application/json'  // 一定要加
    }
  })
}

export function deleteOrder(params) {
  return request({
    url:'/admin/order/delete',
    method:'delete',
    params:params
  })
}

export function deliveryOrder(data) {
  return request({
    url:'/admin/order/update/delivery',
    method:'post',
    data:data
  });
}

export function getOrderDetail(id) {
  return request({
    url:'/admin/order/getById/'+id,
    method:'get'
  });
}

export function updateReceiverInfo(data) {
  return request({
    url:'/admin/order/update/receiverInfo',
    method:'post',
    data:data
  });
}

export function updateMoneyInfo(data) {
  return request({
    url:'/admin/order/update/moneyInfo',
    method:'post',
    data:data
  });
}

export function updateOrderNote(data) {
  return request({
    url:'/admin/order/update/note',
    method:'post',
    data:data,
    headers: {
      'Content-Type': 'application/json'  // 一定要加
    }
  });
}
