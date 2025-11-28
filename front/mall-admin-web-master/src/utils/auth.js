import Cookies from 'js-cookie'

const TokenKey = 'loginToken'
const RoleKey = 'userRole'
const UsernameKey = 'username'

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

export function getUsername() {
  return Cookies.get(UsernameKey)
}

export function setUsername(username) {
  return Cookies.set(UsernameKey, username)
}
