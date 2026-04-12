import Cookies from 'js-cookie'

const ROLE_ADMIN = 'ADMIN'
const ROLE_USER = 'USER'

// 按身份分桶存储，避免同域下 admin/user 登录态互相覆盖
const KEY_MAP = {
  ADMIN: {
    token: 'adminToken',
    role: 'adminRole',
    username: 'adminUsername',
    permissions: 'adminPermissions'
  },
  USER: {
    token: 'userToken',
    role: 'userRole',
    username: 'userUsername',
    permissions: 'userPermissions'
  }
}

function resolveRoleFromPath(path) {
  const p = String(path || '').toLowerCase()
  if (p.startsWith('/user')) return ROLE_USER
  return ROLE_ADMIN
}

function resolveRoleFromRequestUrl(url) {
  const u = String(url || '').toLowerCase()
  if (u.includes('/user/')) return ROLE_USER
  if (u.includes('/admin/')) return ROLE_ADMIN
  return null
}

function normalizeRole(roleOrContext) {
  if (!roleOrContext) {
    if (typeof window !== 'undefined') {
      return resolveRoleFromPath(window.location.pathname)
    }
    return ROLE_ADMIN
  }

  const raw = String(roleOrContext).trim()
  const upper = raw.toUpperCase()
  if (upper === ROLE_ADMIN || upper === ROLE_USER) return upper

  if (raw.startsWith('/')) {
    return resolveRoleFromPath(raw)
  }

  const byUrl = resolveRoleFromRequestUrl(raw)
  if (byUrl) return byUrl

  return ROLE_ADMIN
}

function keyFor(roleOrContext) {
  const role = normalizeRole(roleOrContext)
  return KEY_MAP[role]
}

export function getCurrentRoleByRoute(path) {
  return normalizeRole(path)
}

export function getRoleByRequestUrl(url) {
  return resolveRoleFromRequestUrl(url)
}

export function hasAnyToken() {
  return !!(Cookies.get(KEY_MAP.ADMIN.token) || Cookies.get(KEY_MAP.USER.token))
}

export function getToken(roleOrContext) {
  return Cookies.get(keyFor(roleOrContext).token)
}

export function setToken(token, roleOrContext) {
  return Cookies.set(keyFor(roleOrContext).token, token)
}

export function removeToken(roleOrContext) {
  return Cookies.remove(keyFor(roleOrContext).token)
}

export function getUserRole(roleOrContext) {
  return Cookies.get(keyFor(roleOrContext).role)
}

export function setUserRole(roleValue, roleOrContext) {
  return Cookies.set(keyFor(roleOrContext || roleValue).role, String(roleValue || '').toUpperCase())
}

export function removeUserRole(roleOrContext) {
  return Cookies.remove(keyFor(roleOrContext).role)
}

export function getUsername(roleOrContext) {
  return Cookies.get(keyFor(roleOrContext).username)
}

export function setUsername(username, roleOrContext) {
  return Cookies.set(keyFor(roleOrContext).username, username)
}

export function removeUsername(roleOrContext) {
  return Cookies.remove(keyFor(roleOrContext).username)
}

export function getPermissions(roleOrContext) {
  const raw = Cookies.get(keyFor(roleOrContext).permissions)
  if (!raw) return []
  try {
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch (e) {
    return []
  }
}

export function setPermissions(permissions, roleOrContext) {
  const list = Array.isArray(permissions) ? permissions : []
  return Cookies.set(keyFor(roleOrContext).permissions, JSON.stringify(list))
}

export function removePermissions(roleOrContext) {
  return Cookies.remove(keyFor(roleOrContext).permissions)
}
