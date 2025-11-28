import request from '@/utils/request'
export function fetchList(parentId,params) {
  return request({
    url:'/productCategory/list/'+parentId,
    method:'get',
    params:params
  })
}
export function deleteProductCate(id) {
  return request({
    url:'/admin/category/delete/'+id,
    method:'post'
  })
}

// 新建球拍分类
export function createProductCate(data) {
  return request({
    url:'/admin/category/add',
    method:'post',
    data:data
  })
}

export function updateProductCate(id,data) {
  return request({
    url:'/admin/category/update/'+id,
    method:'put',
    data:data
  })
}

export function getProductCateById(id) {
  return request({
    url:'/admin/category/getById/'+id,
    method:'get',
  })
}

export function updateShowStatus(data) {
  return request({
    url:'/productCategory/update/showStatus',
    method:'post',
    data:data
  })
}

export function updateNavStatus(data) {
  return request({
    url:'/productCategory/update/navStatus',
    method:'post',
    data:data
  })
}

export function fetchListWithChildren() {
  return request({
    url:'/productCategory/list/withChildren',
    method:'get'
  })
}

// 备用接口：如果 withChildren 不存在，尝试使用其他接口
export function fetchListWithChildrenAdmin() {
  return request({
    url:'/admin/productCategory/list/withChildren',
    method:'get'
  })
}

export function fetchListAll() {
  return request({
    url:'/admin/category/list',
    method:'get'
  })
}

// 新的分类接口：/admin/category/list
export function fetchCategoryList() {
  return request({
    url:'/admin/category/list',
    method:'get'
  })
}

export function fetchCategoryPage(params) {
  return request({
    url:'/admin/category/page',
    method:'get',
    params: params
  })
}
