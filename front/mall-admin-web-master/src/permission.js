import router, { resetRouter } from './router'
import store from './store'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { Message } from 'element-ui'
import { getToken, getCurrentRoleByRoute } from '@/utils/auth'

const whiteList = ['/login']

router.beforeEach((to, from, next) => {
  NProgress.start()

  // 登录页始终允许进入，确保任一端退出后都能稳定回到登录界面
  if (to.path === '/login') {
    next()
    NProgress.done()
    return
  }

  // 按目标路由判断使用哪套 token（/user 走用户 token，其余走管理员 token）
  const targetRole = getCurrentRoleByRoute(to.path)
  const token = getToken(to.path)

  if (token) {
    const hasTargetRoute = router.match(to.path).matched.length > 0
    const needBuildRoutes =
      store.getters.roles.length === 0 ||
      store.getters.permissionRole !== targetRole ||
      !hasTargetRoute

    if (needBuildRoutes) {
      store
        .dispatch('GetInfo', { role: targetRole })
        .then(res => {
          const menus = res.data.menus
          const username = res.data.username
          const roles = store.getters.roles && store.getters.roles.length > 0 ? store.getters.roles : [targetRole]

          store.dispatch('GenerateRoutes', { menus, username, roles }).then(() => {
            // 切换身份端时先重置 matcher，再注入当前端动态路由，避免旧路由污染
            resetRouter()
            router.addRoutes(store.getters.addRouters)
            next({ ...to, replace: true })
          })
        })
        .catch(err => {
          store.dispatch('FedLogOut', { role: targetRole }).then(() => {
            Message.error(err || 'Verification failed, please login again')
            next({ path: '/login' })
          })
        })
    } else {
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      // 登录页始终允许进入，保证任一端都能独立重新登录
      next()
    } else {
      next('/login')
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
