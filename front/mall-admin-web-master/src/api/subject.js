import request from '@/utils/request'
export function fetchListAll(config) {
  return request(Object.assign({
    url: '/admin/subject/listAll',
    method: 'get'
  }, config || {}))
}

export function fetchList(params) {
  return request({
    url:'/admin/subject/list',
    method:'get',
    params:params
  })
}
