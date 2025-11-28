import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/admin/returnReason/list',
    method:'get',
    params:params
  })
}

export function deleteReason(ids) {
  return request({
    url:'/admin/returnReason/delete',
    method:'delete',
    data:ids
  })
}

export function updateStatus(params) {
  return request({
    url:'/returnReason/update/status',
    method:'post',
    params:params
  })
}

export function addReason(data) {
  return request({
    url:'/admin/returnReason/add',
    method:'post',
    data:data
  })
}

export function getReasonDetail(id) {
  return request({
    url:'/admin/returnReason/getById/'+id,
    method:'get'
  })
}

export function updateReason(id,data) {
  return request({
    url:'/admin/returnReason/update/'+id,
    method:'post',
    data:data
  })
}
