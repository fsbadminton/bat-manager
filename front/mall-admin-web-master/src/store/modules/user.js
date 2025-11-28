import { login, userLogin, logout, getInfo } from '@/api/login'
import { getToken, setToken, removeToken, setUserRole, setUsername, getUserRole, getUsername } from '@/utils/auth'

const user = {
  state: {
    token: getToken(),
    name: '',
    avatar: '',
    roles: []
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
    }
  },

  actions: {
    // 登录
    Login({ commit }, userInfo) {
      const username = userInfo.username.trim()
      const password = userInfo.password
      return new Promise((resolve, reject) => {
        const apiCall = (username.toLowerCase() === 'admin') ? login : userLogin
        apiCall(username, password).then(response => {
          const outer = response && response.data ? response.data : response
          const payload = (outer && outer.data) ? outer.data : outer
          const tokenStr = payload.tokenHead ? (payload.tokenHead + payload.token) : payload.token
          setToken(tokenStr)
          commit('SET_TOKEN', tokenStr)
          const roleRaw = payload.role || payload.userRole || payload.authority || ''
          const role = typeof roleRaw === 'string' ? roleRaw.toUpperCase() : roleRaw
          if (role) {
            commit('SET_ROLES', [role])
            setUserRole(role)
          }
          if (payload.username) {
            commit('SET_NAME', payload.username)
            setUsername(payload.username)
          }
          resolve(payload)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 获取用户信息
    GetInfo({ commit, state }) {
      return new Promise((resolve) => {
        getInfo().then(response => {
          const outer = response && response.data ? response.data : response
          const payload = (outer && outer.data) ? outer.data : outer
          if (payload.roles && payload.roles.length > 0) {
            const upperRoles = payload.roles.map(r => String(r).toUpperCase())
            commit('SET_ROLES', upperRoles)
          } else {
            const role = getUserRole()
            if (role) commit('SET_ROLES', [role])
          }
          if (payload.username) {
            commit('SET_NAME', payload.username)
          } else {
            const name = getUsername()
            if (name) commit('SET_NAME', name)
          }
          resolve({ data: { menus: payload.menus || [], username: payload.username || getUsername() } })
        }).catch(() => {
          const role = getUserRole()
          const name = getUsername()
          if (role) commit('SET_ROLES', [role])
          if (name) commit('SET_NAME', name)
          resolve({ data: { menus: [], username: name } })
        })
      })
    },

    // 登出
    LogOut({ commit, state }) {
      return new Promise((resolve, reject) => {
        logout(state.token).then(() => {
          commit('SET_TOKEN', '')
          commit('SET_ROLES', [])
          removeToken()
          resolve()
        }).catch(error => {
          reject(error)
        })
      })
    },

    // 前端 登出
    FedLogOut({ commit }) {
      return new Promise(resolve => {
        commit('SET_TOKEN', '')
        removeToken()
        resolve()
      })
    }
  }
}

export default user
