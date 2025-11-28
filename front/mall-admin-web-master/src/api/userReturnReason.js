import request from '@/utils/request'

// 用户端获取退货原因（/user/returnApply/list）
export function listUserReturnReasons() {
  return request({
    url: '/user/returnApply/list',
    method: 'get'
  })
}
