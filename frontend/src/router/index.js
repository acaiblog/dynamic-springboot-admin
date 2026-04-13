import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import { useUserStore } from '@/store/user'

// 静态路由
const staticRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'House', affix: true }
      }
    ]
  },
  // 固定后台管理路由（不通过后端动态获取）
  {
    path: '/system',
    component: () => import('@/layout/index.vue'),
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', perms: 'sys:user:list' }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled', perms: 'sys:role:list' }
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu', perms: 'sys:menu:list' }
      },
      {
        path: 'group',
        name: 'Group',
        component: () => import('@/views/system/group/index.vue'),
        meta: { title: '用户组管理', icon: 'Grid', perms: 'sys:group:list' }
      },
      {
        path: 'config',
        name: 'Config',
        component: () => import('@/views/system/config/index.vue'),
        meta: { title: '系统设置', icon: 'Tools', perms: 'sys:config:list' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/dashboard' }
]

const router = createRouter({
  history: createWebHistory(),
  routes: staticRoutes
})

// 动态路由（后端返回结构时使用）
export function addDynamicRoutes(routeList) {
  // 此版本已静态注册，可扩展为完全动态
}

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()
  const token = localStorage.getItem('token')
  if (to.path === '/login') {
    if (token) { next('/'); return }
    next(); return
  }
  if (!token) { next('/login'); return }
  next()
})

router.afterEach(() => { NProgress.done() })

export default router
