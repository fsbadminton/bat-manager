import Cookies from 'js-cookie'

const TokenKey = 'loginToken'
const RoleKey = 'userRole'
const UsernameKey = 'username'
const PermissionKey = 'permissions'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}

export function getUserRole() {
  return Cookies.get(RoleKey)
}

export function setUserRole(role) {
  return Cookies.set(RoleKey, role)
}

export function removeUserRole() {
  return Cookies.remove(RoleKey)
}

export function getUsername() {
  return Cookies.get(UsernameKey)
}

export function setUsername(username) {
  return Cookies.set(UsernameKey, username)
}

export function removeUsername() {
  return Cookies.remove(UsernameKey)
}

export function getPermissions() {
  const raw = Cookies.get(PermissionKey)
  if (!raw) return []
  try {
    const parsed = JSON.parse(raw)
    return Array.isArray(parsed) ? parsed : []
  } catch (e) {
    return []
  }
}

export function setPermissions(permissions) {
  const list = Array.isArray(permissions) ? permissions : []
  return Cookies.set(PermissionKey, JSON.stringify(list))
}

export function removePermissions() {
  return Cookies.remove(PermissionKey)
}
