import request from '@/utils/request'

// 用户提交退货申请
export function createUserReturnApply(data) {
  return request({
    url: '/user/returnApply/create',
    method: 'post',
    data
  })
}

// 用户查看自己的退货记录（/user/returnApply/listAll）
export function listUserReturnApplies(params) {
  return request({
    url: '/user/returnApply/listAll',
    method: 'get',
    params: params
  })
}
