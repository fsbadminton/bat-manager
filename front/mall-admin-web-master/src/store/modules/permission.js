import { asyncRouterMap, constantRouterMap } from '@/router/index'

function hasPermission(menus, route) {
  if (!route.name) return true
  const currMenu = getMenu(route.name, menus)
  if (currMenu != null) {
    route.meta = route.meta || {}
    if (currMenu.title != null && currMenu.title !== '') route.meta.title = currMenu.title
    if (currMenu.icon != null && currMenu.icon !== '') route.meta.icon = currMenu.icon
    if (currMenu.hidden != null) route.hidden = currMenu.hidden !== 0
    if (currMenu.sort != null && currMenu.sort !== '') route.sort = currMenu.sort
    return true
  }

  route.sort = 0
  if (route.hidden === true) {
    route.sort = -1
    return true
  }
  return false
}

function getMenu(name, menus) {
  for (let i = 0; i < menus.length; i++) {
    if (name === menus[i].name) return menus[i]
  }
  return null
}

function compareBySortDesc(a, b) {
  return (b.sort || 0) - (a.sort || 0)
}

function sortRouters(accessedRouters) {
  for (let i = 0; i < accessedRouters.length; i++) {
    const router = accessedRouters[i]
    if (router.children && router.children.length > 0) {
      router.children.sort(compareBySortDesc)
    }
  }
  accessedRouters.sort(compareBySortDesc)
}

function cloneRoute(route) {
  const cloned = { ...route }
  if (route.meta) cloned.meta = { ...route.meta }
  if (route.children && Array.isArray(route.children)) {
    cloned.children = route.children.map(child => cloneRoute(child))
  }
  return cloned
}

function cloneAsyncRouters() {
  // 关键：每次生成动态路由都做深拷贝，且保留 component 懒加载函数
  return asyncRouterMap.map(route => cloneRoute(route))
}

const permission = {
  state: {
    routers: constantRouterMap,
    addRouters: [],
    currentRole: ''
  },
  mutations: {
    SET_ROUTERS: (state, routers) => {
      state.addRouters = routers
      state.routers = constantRouterMap.concat(routers)
    },
    SET_CURRENT_ROLE: (state, role) => {
      state.currentRole = role
    }
  },
  actions: {
    GenerateRoutes({ commit }, data) {
      return new Promise(resolve => {
        const menus = (data && data.menus) || []
        const username = data && data.username
        const roles = (data && data.roles) || []
        const roleUpper = Array.isArray(roles) && roles.length > 0 ? String(roles[0]).toUpperCase() : ''

        const sourceRouters = cloneAsyncRouters()
        const accessedRouters = sourceRouters.filter(v => {
          if (username === 'admin' || roleUpper === 'ADMIN') {
            if (v.name === 'sms') return false
            if (v.name === 'user') return false
            return true
          }

          if (roleUpper === 'USER') {
            // 用户端只保留 user 菜单（保留其完整子路由，避免 /user 重定向到 404）
            return v.name === 'user'
          }

          if (!hasPermission(menus, v)) return false
          if (v.children && v.children.length > 0) {
            v.children = v.children.filter(child => hasPermission(menus, child))
          }
          return true
        })

        sortRouters(accessedRouters)
        commit('SET_ROUTERS', accessedRouters)
        commit('SET_CURRENT_ROLE', roleUpper || (username === 'admin' ? 'ADMIN' : 'USER'))
        resolve()
      })
    }
  }
}

export default permission
