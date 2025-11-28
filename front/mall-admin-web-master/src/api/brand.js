import request from '@/utils/request'
export function fetchBrandList(params) {
  return request({
    url:'/admin/brand/listAll',
    method:'get',
    params:params
  })
}
export function createBrand(data) {
  return request({
    url:'/admin/brand/add',
    method:'post',
    data:data
  })
}
export function updateShowStatus(data) {
  return request({
    url:'/brand/update/showStatus',
    method:'post',
    data:data
  })
}

export function updateFactoryStatus(data) {
  return request({
    url:'/brand/update/factoryStatus',
    method:'post',
    data:data
  })
}

export function deleteBrand(id) {
  return request({
    url:'/admin/brand/delete/'+id,
    method:'delete',
  })
}

export function getBrand(id) {
  return request({
    url:'/admin/brand/getById/'+id,
    method:'get',
  })
}

export function updateBrand(data) {
  return request({
    url:'/admin/brand/update',
    method:'put',
    headers: {
      'Content-Type': 'application/json'
    },
    data:data
  })
}

