import request from '@/utils/request'
export function fetchList(params, config) {
  return request(Object.assign({
    url: '/admin/productAttribute/category/list',
    method: 'get',
    params: params
  }, config || {}))
}

export function createProductAttrCate(data) {
  return request({
    url:'/admin/productAttribute/category/create',
    method:'post',
    data:data
  })
}

export function deleteProductAttrCate(id) {
  return request({
    url:'/admin/productAttribute/category/delete/'+id,
    method:'delete'
  })
}

export function updateProductAttrCate(id,data) {
  return request({
    url:'/admin/productAttribute/category/update/'+id,
    method:'put',
    data:data
  })
}
export function fetchListWithAttr(config) {
  return request(Object.assign({
    url: '/admin/productAttribute/category/list/withAttr',
    method: 'get'
  }, config || {}))
}
