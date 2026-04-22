import request from '@/utils/request'

export function login(username, password) {
  return request({
    url: '/user/login',
    method: 'post',
    data: {
      username,
      password
    }
  })
}

export function listUserProducts(params) {
  return request({
    url: '/user/product/list',
    method: 'get',
    params
  })
}

export function listUserBrands(params) {
  return request({
    url: '/user/brand/list',
    method: 'get',
    params
  })
}

export function createUserOrder(data) {
  return request({
    url: '/user/order/create',
    method: 'post',
    data
  })
}

export function listUserOrders(params) {
  return request({
    url: '/user/order/list',
    method: 'get',
    params
  })
}

export function getUserOrderDetail(orderId) {
  return request({
    url: '/user/order/detail/' + orderId,
    method: 'get'
  })
}

export function updateUserOrder(data) {
  return request({
    url: '/user/order/update',
    method: 'post',
    data
  })
}

export function confirmUserOrderReceive(orderId) {
  return request({
    url: '/user/order/confirmReceive/' + orderId,
    method: 'post'
  })
}

export function addUserReview(data) {
  return request({
    url: '/user/review/add',
    method: 'post',
    data
  })
}

export function updateUserReview(data) {
  return request({
    url: '/user/review/update',
    method: 'put',
    data
  })
}

export function deleteUserReview(reviewId) {
  return request({
    url: '/user/review/delete/' + reviewId,
    method: 'delete'
  })
}

export function listUserReviews(params) {
  return request({
    url: '/user/review/list',
    method: 'get',
    params
  })
}
