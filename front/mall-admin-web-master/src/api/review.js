import request from '@/utils/request'

export function listAdminReviews(params) {
  return request({
    url: '/admin/review/list',
    method: 'get',
    params
  })
}

export function deleteAdminReview(reviewId) {
  return request({
    url: '/admin/review/delete/' + reviewId,
    method: 'delete'
  })
}
