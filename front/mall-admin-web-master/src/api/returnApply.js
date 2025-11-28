import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/admin/returnApply/list',
    method:'get',
    params:params
  })
}

export function deleteApply(params) {
  return request({
    url:'/returnApply/delete',
    method:'post',
    params:params
  })
}
export function updateApplyStatus(id,data) {
  return request({
    url:'/admin/returnApply/update/status/'+id,
    method:'post',
    data:data
  })
}

export function getApplyDetail(id) {
  return request({
    url:'/admin/returnApply/'+id,
    method:'get'
  })
}
