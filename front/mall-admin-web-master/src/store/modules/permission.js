import { adminRouterMap, clientRouterMap, constantRouterMap } from '@/router/index'

function hasPermission(menus, route) {
  if (!menus || menus.length === 0 || !route.name) return true
  const currMenu = menus.find(menu => menu.name === route.name)
  if (!currMenu) return route.hidden === true

  route.meta = route.meta || {}
  if (currMenu.title) route.meta.title = currMenu.title
  if (currMenu.icon) route.meta.icon = currMenu.icon
  if (currMenu.hidden != null) route.hidden = currMenu.hidden !== 0
  if (currMenu.sort != null && currMenu.sort !== '') route.sort = currMenu.sort
  return true
}

function cloneRoute(route) {
  const cloned = { ...route }
  if (route.meta) cloned.meta = { ...route.meta }
  if (route.children) cloned.children = route.children.map(child => cloneRoute(child))
  return cloned
}

function sortRouters(routes) {
  routes.forEach(route => {
    if (route.children) route.children.sort((a, b) => (b.sort || 0) - (a.sort || 0))
  })
  routes.sort((a, b) => (b.sort || 0) - (a.sort || 0))
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
        const role = String((data && data.roles && data.roles[0]) || '').toUpperCase()
        const menus = (data && data.menus) || []
        const source = role === 'ADMIN' ? adminRouterMap : clientRouterMap
        const accessedRouters = source.map(route => cloneRoute(route)).filter(route => {
          if (!hasPermission(menus, route)) return false
          if (route.children) route.children = route.children.filter(child => hasPermission(menus, child))
          return true
        })

        sortRouters(accessedRouters)
        commit('SET_ROUTERS', accessedRouters)
        commit('SET_CURRENT_ROLE', role)
        resolve()
      })
    }
  }
}

export default permission
