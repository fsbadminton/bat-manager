import { login, userLogin, logout, getInfo } from '@/api/login'
import {
  getToken,
  setToken,
  removeToken,
  setUserRole,
  setUsername,
  removeUserRole,
  removeUsername,
  getUserRole,
  getUsername,
  getPermissions,
  setPermissions,
  removePermissions,
  getCurrentRoleByRoute
} from '@/utils/auth'

function getContextRole(routeHint) {
  return getCurrentRoleByRoute(routeHint || (typeof window !== 'undefined' ? window.location.pathname : '/admin'))
}

function normalizeRole(role) {
  const value = String(role || '').toUpperCase()
  return value === 'ADMIN' || value === 'USER' ? value : ''
}

function resolveRoleInput(roleLike) {
  if (!roleLike) return getContextRole()
  if (typeof roleLike === 'object' && roleLike.role) return normalizeRole(roleLike.role)
  return getCurrentRoleByRoute(roleLike)
}

const user = {
  state: {
    token: getToken(getContextRole()),
    name: '',
    avatar: '',
    roles: [],
    permissions: getPermissions(getContextRole())
  },

  mutations: {
    SET_TOKEN: (state, token) => { state.token = token },
    SET_NAME: (state, name) => { state.name = name },
    SET_AVATAR: (state, avatar) => { state.avatar = avatar },
    SET_ROLES: (state, roles) => { state.roles = roles },
    SET_PERMISSIONS: (state, permissions) => { state.permissions = permissions }
  },

  actions: {
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      const role = normalizeRole(userInfo.role || getContextRole())
      if (!role) return Promise.reject(new Error('Invalid login role'))

      const apiCall = role === 'ADMIN' ? login : userLogin
      return apiCall(username, password)
        .then(response => {
          const outer = response && response.data ? response.data : response
          const payload = outer && outer.data ? outer.data : outer
          const returnedRole = normalizeRole(payload && (payload.role || payload.userRole || payload.authority))
          if (returnedRole !== role) throw new Error('登录身份与入口不匹配')

          const tokenStr = payload.tokenHead ? payload.tokenHead + payload.token : payload.token
          setToken(tokenStr, role)
          setUserRole(role, role)
          commit('SET_TOKEN', tokenStr)
          commit('SET_ROLES', [role])

          const permissions = Array.isArray(payload.permissions) ? payload.permissions : []
          commit('SET_PERMISSIONS', permissions)
          setPermissions(permissions, role)

          if (payload.username) {
            commit('SET_NAME', payload.username)
            setUsername(payload.username, role)
          }
          return payload
        })
    },

    GetInfo({ commit }, payload) {
      const contextRole = getContextRole(payload && payload.role)
      const storedRole = normalizeRole(getUserRole(contextRole) || contextRole)
      return getInfo(storedRole).then(response => {
        const outer = response && response.data ? response.data : response
        const info = outer && outer.data ? outer.data : outer
        const token = getToken(storedRole)
        if (token) commit('SET_TOKEN', token)

        const roles = info.roles && info.roles.length > 0
          ? info.roles.map(role => normalizeRole(role)).filter(Boolean)
          : [storedRole]
        commit('SET_ROLES', roles)

        const name = info.username || getUsername(storedRole)
        if (name) {
          commit('SET_NAME', name)
          setUsername(name, storedRole)
        }

        const permissions = Array.isArray(info.permissions) ? info.permissions : getPermissions(storedRole)
        commit('SET_PERMISSIONS', permissions)
        setPermissions(permissions, storedRole)
        return { data: { menus: info.menus || [], username: name, permissions, roles } }
      })
    },

    LogOut({ commit, state }) {
      const role = normalizeRole((state.roles && state.roles[0]) || getContextRole())
      return logout(role).then(() => {
        commit('SET_TOKEN', '')
        commit('SET_NAME', '')
        commit('SET_ROLES', [])
        commit('SET_PERMISSIONS', [])
        removeToken(role)
        removeUserRole(role)
        removeUsername(role)
        removePermissions(role)
      })
    },

    FedLogOut({ commit }, payload) {
      const role = resolveRoleInput(payload && payload.role)
      commit('SET_TOKEN', '')
      commit('SET_NAME', '')
      commit('SET_ROLES', [])
      commit('SET_PERMISSIONS', [])
      removeToken(role)
      removeUserRole(role)
      removeUsername(role)
      removePermissions(role)
      return Promise.resolve()
    }
  }
}

export default user
