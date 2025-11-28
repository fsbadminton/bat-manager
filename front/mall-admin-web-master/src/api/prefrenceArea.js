import request from '@/utils/request'
export function fetchList(config) {
  return request(Object.assign({
    url: '/admin/prefrenceArea/listAll',
    method: 'get'
  }, config || {}))
}
