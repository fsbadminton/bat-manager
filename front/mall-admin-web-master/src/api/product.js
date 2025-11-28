import request from '@/utils/request'
export function fetchList(params) {
  return request({
    url:'/admin/product/list',
    method:'get',
    params:params
  })
}

export function fetchSimpleList(params) {
  return request({
    url:'/product/simpleList',
    method:'get',
    params:params
  })
}

export function updateDeleteStatus(params) {
  return request({
    url:'/product/update/deleteStatus',
    method:'post',
    params:params
  })
}
// 更新球拍状态：上架、新品、推荐
export function updateStatus(data, id) {
  return request({
    url: `/admin/product/status/`+id,
    method: 'post',
    data: data
  });
}



export function updateNewStatus(params) {
  return request({
    url:'/product/update/newStatus',
    method:'post',
    params:params
  })
}

export function updateRecommendStatus(params) {
  return request({
    url:'/product/update/recommendStatus',
    method:'post',
    params:params
  })
}

export function updatePublishStatus(params) {
  return request({
    url:'/product/update/publishStatus',
    method:'post',
    params:params
  })
}

export function createProduct(data) {
  return request({
    url:'/admin/product/add',
    method:'post',
    data:data
  })
}

export function updateProduct(id,data) {
  return request({
    url:'/admin/product/update',
    method:'put',
    headers: {
      'Content-Type': 'application/json'
    },
    data: { ...data, productId:id }
  })
}

export function getProductById(id) {
  return request({
    url:'/admin/product/getById/'+id,
    method:'get'
    //params: { id }
  })
}

export function fetchPageList(params) {
  return request({
    url:'/admin/product/page',
    method:'get',
    params:params
  })
}

export function deleteProduct(id) {
  return request({
    url:'/admin/product/delete/'+id,
    method:'delete',
  })

}
