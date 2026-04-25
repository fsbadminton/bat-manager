import request from '@/utils/request'

export function listAvailableCoupons(params) {
  return request({
    url: '/user/coupon/available',
    method: 'get',
    params
  })
}

export function listMyCoupons(params) {
  return request({
    url: '/user/coupon/my',
    method: 'get',
    params
  })
}

export function claimCoupon(couponId) {
  return request({
    url: '/user/coupon/claim/' + couponId,
    method: 'post'
  })
}
