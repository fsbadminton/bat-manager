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
  return getCurrentRoleByRoute(routeHint || (typeof window !== 'undefined' ? window.location.pathname : '/pms'))
}

function resolveRoleInput(roleLike) {
  if (!roleLike) return getContextRole()
  if (typeof roleLike === 'object' && roleLike.role) return getCurrentRoleByRoute(roleLike.role)
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
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, name) => {
      state.name = name
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_ROLES: (state, roles) => {
      state.roles = roles
    },
    SET_PERMISSIONS: (state, permissions) => {
      state.permissions = permissions
    }
  },

  actions: {
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      return new Promise((resolve, reject) => {
        const apiCall = username.toLowerCase() === 'admin' ? login : userLogin
        apiCall(username, password)
          .then(response => {
            const outer = response && response.data ? response.data : response
            const payload = outer && outer.data ? outer.data : outer
            const tokenStr = payload.tokenHead ? payload.tokenHead + payload.token : payload.token
            const roleRaw = payload.role || payload.userRole || payload.authority || ''
            const role = (typeof roleRaw === 'string' ? roleRaw : getContextRole()).toUpperCase()

            // 按角色写入独立存储，避免 admin/user 登录态互相覆盖
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
            resolve(payload)
          })
          .catch(error => {
            reject(error)
          })
      })
    },

    GetInfo({ commit }, payload) {
      return new Promise(resolve => {
        // 优先使用路由守卫传入的目标路由来判断身份，避免在 /login 页面误判成 ADMIN
        const contextRole = getContextRole(payload && payload.role)
        const storedRole = (getUserRole(contextRole) || contextRole).toUpperCase()

        getInfo(storedRole)
          .then(response => {
            const outer = response && response.data ? response.data : response
            const payload = outer && outer.data ? outer.data : outer
            const token = getToken(storedRole)
            if (token) commit('SET_TOKEN', token)

            if (payload.roles && payload.roles.length > 0) {
              const upperRoles = payload.roles.map(r => String(r).toUpperCase())
              commit('SET_ROLES', upperRoles)
            } else {
              commit('SET_ROLES', [storedRole])
            }

            if (payload.username) {
              commit('SET_NAME', payload.username)
              setUsername(payload.username, storedRole)
            } else {
              const name = getUsername(storedRole)
              if (name) commit('SET_NAME', name)
            }

            const permissions = Array.isArray(payload.permissions) ? payload.permissions : getPermissions(storedRole)
            commit('SET_PERMISSIONS', permissions)
            setPermissions(permissions, storedRole)

            resolve({
              data: {
                menus: payload.menus || [],
                username: payload.username || getUsername(storedRole),
                permissions
              }
            })
          })
          .catch(() => {
            const token = getToken(storedRole)
            const name = getUsername(storedRole)
            const permissions = getPermissions(storedRole)
            if (token) commit('SET_TOKEN', token)
            commit('SET_ROLES', [storedRole])
            if (name) commit('SET_NAME', name)
            commit('SET_PERMISSIONS', permissions)
            resolve({ data: { menus: [], username: name, permissions } })
          })
      })
    },

    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        const contextRole = (state.roles && state.roles[0]) || getContextRole()
        logout(contextRole)
          .then(() => {
            // 仅清理当前端身份，保留另一端登录状态
            commit('SET_TOKEN', '')
            commit('SET_NAME', '')
            commit('SET_ROLES', [])
            commit('SET_PERMISSIONS', [])
            removeToken(contextRole)
            removeUserRole(contextRole)
            removeUsername(contextRole)
            removePermissions(contextRole)
            resolve()
          })
          .catch(error => {
            reject(error)
          })
      })
    },

    FedLogOut({ commit, state }, payload) {
      return new Promise(resolve => {
        // 允许按指定端登出（如 401 来自 /user/* 时仅清理用户态）
        const contextRole = resolveRoleInput((payload && payload.role) || (state.roles && state.roles[0]) || getContextRole())
        commit('SET_TOKEN', '')
        commit('SET_NAME', '')
        commit('SET_ROLES', [])
        commit('SET_PERMISSIONS', [])
        removeToken(contextRole)
        removeUserRole(contextRole)
        removeUsername(contextRole)
        removePermissions(contextRole)
        resolve()
      })
    }
  }
}

export default user
