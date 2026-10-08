import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '../views/layout/Layout'
import ClientLayout from '../views/client/ClientLayout'

// 项目使用 hash 路由。将 IDEA/浏览器打开的 history 风格地址统一到根路径，
// 避免出现 /admin/login#/admin/... 以及刷新后空白页。
if (typeof window !== 'undefined' && !['/', '/index.html'].includes(window.location.pathname)) {
  const hashRoute = window.location.hash.indexOf('#/') === 0
    ? window.location.hash.substring(1)
    : window.location.pathname + window.location.search
  window.location.replace('/#' + hashRoute)
}

export const constantRouterMap = [
  { path: '/', redirect: '/admin/login', hidden: true },
  { path: '/login', redirect: '/client/login', hidden: true },
  {
    path: '/admin/login',
    component: () => import('@/views/login/index'),
    hidden: true,
    meta: { role: 'ADMIN' }
  },
  {
    path: '/client/login',
    component: () => import('@/views/login/index'),
    hidden: true,
    meta: { role: 'USER' }
  },
  { path: '/404', component: () => import('@/views/404'), hidden: true },
  {
    path: '/admin',
    component: Layout,
    redirect: '/admin/home',
    name: 'dashboard',
    meta: { title: '首页', icon: 'home', roles: ['ADMIN'] },
    children: [
      {
        path: 'home',
        name: 'home',
        component: () => import('@/views/home/index'),
        meta: { title: '仪表盘', icon: 'dashboard', roles: ['ADMIN'] }
      }
    ]
  },
  { path: '/home', redirect: '/admin/home', hidden: true }
]

export const asyncRouterMap = [
  {
    path: '/pms',
    component: Layout,
    redirect: '/pms/product',
    name: 'pms',
    meta: { title: '球拍', icon: 'product' },
    children: [
      {
        path: 'product',
        name: 'product',
        component: () => import('@/views/pms/product/index'),
        meta: { title: '球拍列表', icon: 'product-list' }
      },
      {
        path: 'addProduct',
        name: 'addProduct',
        component: () => import('@/views/pms/product/add'),
        meta: { title: '添加球拍', icon: 'product-add' }
      },
      {
        path: 'updateProduct',
        name: 'updateProduct',
        component: () => import('@/views/pms/product/update'),
        meta: { title: '修改球拍', icon: 'product-add' },
        hidden: true
      },
      {
        path: 'productCate',
        name: 'productCate',
        component: () => import('@/views/pms/productCate/index'),
        meta: { title: '球拍分类', icon: 'product-cate' }
      },
      {
        path: 'addProductCate',
        name: 'addProductCate',
        component: () => import('@/views/pms/productCate/add'),
        meta: { title: '添加球拍分类' },
        hidden: true
      },
      {
        path: 'updateProductCate',
        name: 'updateProductCate',
        component: () => import('@/views/pms/productCate/update'),
        meta: { title: '修改球拍分类' },
        hidden: true
      },
      {
        path: 'productAttr',
        name: 'productAttr',
        component: () => import('@/views/pms/productAttr/index'),
        meta: { title: '球拍类型', icon: 'product-attr' },
        hidden: true
      },
      {
        path: 'productAttrList',
        name: 'productAttrList',
        component: () => import('@/views/pms/productAttr/productAttrList'),
        meta: { title: '球拍属性列表' },
        hidden: true
      },
      {
        path: 'addProductAttr',
        name: 'addProductAttr',
        component: () => import('@/views/pms/productAttr/addProductAttr'),
        meta: { title: '添加球拍属性' },
        hidden: true
      },
      {
        path: 'updateProductAttr',
        name: 'updateProductAttr',
        component: () => import('@/views/pms/productAttr/updateProductAttr'),
        meta: { title: '修改球拍属性' },
        hidden: true
      },
      {
        path: 'brand',
        name: 'brand',
        component: () => import('@/views/pms/brand/index'),
        meta: { title: '品牌管理', icon: 'product-brand' }
      },
      {
        path: 'addBrand',
        name: 'addBrand',
        component: () => import('@/views/pms/brand/add'),
        meta: { title: '添加品牌' },
        hidden: true
      },
      {
        path: 'updateBrand',
        name: 'updateBrand',
        component: () => import('@/views/pms/brand/update'),
        meta: { title: '编辑品牌' },
        hidden: true
      }
    ]
  },
  {
    path: '/client',
    component: Layout,
    redirect: '/client/products',
    name: 'user',
    meta: { title: '用户', icon: 'ums-admin' },
    children: [
      {
        path: 'index',
        name: 'userIndex',
        component: () => import('@/views/user/index'),
        hidden: true,
        meta: { title: '用户中心', icon: 'ums-admin' }
      },
      {
        path: 'products',
        name: 'userProducts',
        component: () => import('@/views/user/userProducts'),
        meta: { title: '浏览球拍' }
      },
      {
        path: 'orders',
        name: 'userOrders',
        component: () => import('@/views/user/userOrders'),
        meta: { title: '我的订单' }
      },
      {
        path: 'coupons',
        name: 'userCoupons',
        component: () => import('@/views/user/userCoupons'),
        meta: { title: '我的优惠券' }
      },
      {
        path: 'returns',
        name: 'userReturns',
        component: () => import('@/views/user/userReturns'),
        meta: { title: '我的退货' }
      },
      {
        path: 'comments',
        name: 'userComments',
        component: () => import('@/views/user/userComments'),
        meta: { title: '评论' }
      }
    ]
  },
  {
    path: '/oms',
    component: Layout,
    redirect: '/oms/order',
    name: 'oms',
    meta: { title: '订单', icon: 'order' },
    children: [
      {
        path: 'order',
        name: 'order',
        component: () => import('@/views/oms/order/index'),
        meta: { title: '订单列表', icon: 'product-list' }
      },
      {
        path: 'orderDetail',
        name: 'orderDetail',
        component: () => import('@/views/oms/order/orderDetail'),
        meta: { title: '订单详情' },
        hidden: true
      },
      {
        path: 'deliverOrderList',
        name: 'deliverOrderList',
        component: () => import('@/views/oms/order/deliverOrderList'),
        meta: { title: '发货列表' },
        hidden: true
      },
      {
        path: 'returnApply',
        name: 'returnApply',
        component: () => import('@/views/oms/apply/index'),
        meta: { title: '退货申请处理', icon: 'order-return' }
      },
      {
        path: 'returnReason',
        name: 'returnReason',
        component: () => import('@/views/oms/apply/reason'),
        meta: { title: '退货原因设置', icon: 'order-return-reason' }
      },
      {
        path: 'returnApplyDetail',
        name: 'returnApplyDetail',
        component: () => import('@/views/oms/apply/applyDetail'),
        meta: { title: '退货原因详情' },
        hidden: true
      },
      {
        path: 'comments',
        name: 'reviewManage',
        component: () => import('@/views/user/userComments'),
        meta: { title: '评论管理', icon: 'product-comment' }
      }
    ]
  },
  {
    path: '/sms',
    component: Layout,
    redirect: '/sms/coupon',
    name: 'sms',
    meta: { title: '营销', icon: 'sms' },
    children: [
      {
        path: 'flash',
        name: 'flash',
        component: () => import('@/views/sms/flash/index'),
        meta: { title: '秒杀活动列表', icon: 'sms-flash' }
      },
      {
        path: 'flashSession',
        name: 'flashSession',
        component: () => import('@/views/sms/flash/sessionList'),
        meta: { title: '秒杀时间段列表' },
        hidden: true
      },
      {
        path: 'selectSession',
        name: 'selectSession',
        component: () => import('@/views/sms/flash/selectSessionList'),
        meta: { title: '秒杀时间段选择' },
        hidden: true
      },
      {
        path: 'flashProductRelation',
        name: 'flashProductRelation',
        component: () => import('@/views/sms/flash/productRelationList'),
        meta: { title: '秒杀商品列表' },
        hidden: true
      },
      {
        path: 'coupon',
        name: 'coupon',
        component: () => import('@/views/sms/coupon/index'),
        meta: { title: '优惠券列表', icon: 'sms-coupon' }
      },
      {
        path: 'addCoupon',
        name: 'addCoupon',
        component: () => import('@/views/sms/coupon/add'),
        meta: { title: '添加优惠券' },
        hidden: true
      },
      {
        path: 'updateCoupon',
        name: 'updateCoupon',
        component: () => import('@/views/sms/coupon/update'),
        meta: { title: '修改优惠券' },
        hidden: true
      },
      {
        path: 'couponHistory',
        name: 'couponHistory',
        component: () => import('@/views/sms/coupon/history'),
        meta: { title: '优惠券领取详情' },
        hidden: true
      },
      {
        path: 'brand',
        name: 'homeBrand',
        component: () => import('@/views/sms/brand/index'),
        meta: { title: '品牌推荐', icon: 'product-brand' }
      },
      {
        path: 'new',
        name: 'homeNew',
        component: () => import('@/views/sms/new/index'),
        meta: { title: '新品推荐', icon: 'sms-new' }
      },
      {
        path: 'hot',
        name: 'homeHot',
        component: () => import('@/views/sms/hot/index'),
        meta: { title: '人气推荐', icon: 'sms-hot' }
      },
      {
        path: 'subject',
        name: 'homeSubject',
        component: () => import('@/views/sms/subject/index'),
        meta: { title: '专题推荐', icon: 'sms-subject' }
      },
      {
        path: 'advertise',
        name: 'homeAdvertise',
        component: () => import('@/views/sms/advertise/index'),
        meta: { title: '广告列表', icon: 'sms-ad' }
      },
      {
        path: 'addAdvertise',
        name: 'addHomeAdvertise',
        component: () => import('@/views/sms/advertise/add'),
        meta: { title: '添加广告' },
        hidden: true
      },
      {
        path: 'updateAdvertise',
        name: 'updateHomeAdvertise',
        component: () => import('@/views/sms/advertise/update'),
        meta: { title: '编辑广告' },
        hidden: true
      }
    ]
  },
  { path: '*', redirect: '/404', hidden: true }
]

function cloneRouteWithRole(route, role) {
  const cloned = { ...route }
  cloned.meta = { ...(route.meta || {}), roles: [role] }
  if (route.children) {
    cloned.children = route.children.map(child => cloneRouteWithRole(child, role))
  }
  return cloned
}

function prefixAdminRoute(route) {
  const cloned = cloneRouteWithRole(route, 'ADMIN')
  cloned.path = `/admin${route.path}`
  if (typeof route.redirect === 'string' && route.redirect.startsWith('/')) {
    cloned.redirect = `/admin${route.redirect}`
  }
  return cloned
}

export const adminRouterMap = asyncRouterMap
  .filter(route => route.name !== 'user' && route.path !== '*')
  .map(route => {
    const cloned = prefixAdminRoute(route)
    if (cloned.name === 'sms') {
      const supportedMarketingRoutes = ['coupon', 'addCoupon', 'updateCoupon', 'couponHistory']
      cloned.children = cloned.children.filter(child => supportedMarketingRoutes.includes(child.name))
    }
    return cloned
  })

const userRoute = asyncRouterMap.find(route => route.name === 'user')
export const clientRouterMap = userRoute
  ? [{
      ...cloneRouteWithRole(userRoute, 'USER'),
      path: '/client',
      component: ClientLayout,
      redirect: '/client/products'
    }]
  : []

const createRouter = () =>
  new Router({
    // mode: 'history', //后端支持可开
    scrollBehavior: () => ({ y: 0 }),
    routes: [
      ...constantRouterMap,
      { path: '/pms', redirect: '/admin/pms', hidden: true },
      { path: '/pms/*', redirect: to => `/admin/pms/${to.params.pathMatch}`, hidden: true },
      { path: '/oms', redirect: '/admin/oms', hidden: true },
      { path: '/oms/*', redirect: to => `/admin/oms/${to.params.pathMatch}`, hidden: true },
      { path: '/sms', redirect: '/admin/sms', hidden: true },
      { path: '/sms/*', redirect: to => `/admin/sms/${to.params.pathMatch}`, hidden: true },
      { path: '/ums', redirect: '/admin/ums', hidden: true },
      { path: '/ums/*', redirect: to => `/admin/ums/${to.params.pathMatch}`, hidden: true },
      { path: '/user', redirect: '/client/products', hidden: true },
      { path: '/user/*', redirect: to => `/client/${to.params.pathMatch}`, hidden: true }
    ]
  })

const router = createRouter()

// 重新生成 matcher，避免角色切换后旧动态路由残留导致空白页
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher
}

export default router
