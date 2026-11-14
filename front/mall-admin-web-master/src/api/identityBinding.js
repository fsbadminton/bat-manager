import request from '@/utils/request'

export function startIdentityBinding(platform) {
  return request({
    url: '/user/identity-bindings/start',
    method: 'post',
    data: { platform }
  })
}

export function listIdentityBindings() {
  return request({
    url: '/user/identity-bindings',
    method: 'get'
  })
}

export function unbindIdentity(platform, bindingReference) {
  return request({
    url: '/user/identity-bindings',
    method: 'delete',
    data: { platform, bindingReference }
  })
}

export function listAdminIdentityBindings(customerId) {
  return request({ url: `/admin/identity-bindings/${customerId}`, method: 'get' })
}

export function listIdentityBindingAudit(customerId) {
  return request({ url: '/admin/identity-bindings/audit', method: 'get', params: { customerId } })
}

export function adminUnbindIdentity(customerId, data) {
  return request({ url: `/admin/identity-bindings/${customerId}/unbind`, method: 'post', data })
}

export function adminMoveIdentity(customerId, data) {
  return request({ url: `/admin/identity-bindings/${customerId}/move`, method: 'post', data })
}
