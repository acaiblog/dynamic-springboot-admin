import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '@/api/auth'
import { getRouters } from '@/api/menu'
import router, { addDynamicRoutes } from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const username = ref(localStorage.getItem('username') || '')
  const realName = ref('')
  const email = ref('')
  const avatar = ref('')
  const permissions = ref([])
  const menus = ref([])

  async function loginAction(loginForm) {
    const res = await loginApi(loginForm)
    const { token: t, username: u, realName: rn, email: em, avatar: av, permissions: p, menus: m } = res.data
    token.value = t
    username.value = u
    realName.value = rn || ''
    email.value = em || ''
    avatar.value = av || ''
    permissions.value = p || []
    menus.value = m || []
    localStorage.setItem('token', t)
    localStorage.setItem('username', u)
    // 动态注册路由
    addDynamicRoutes(m || [])
  }

  async function loadRouters() {
    const res = await getRouters()
    menus.value = res.data || []
    addDynamicRoutes(menus.value)
  }

  function logout() {
    token.value = ''
    username.value = ''
    permissions.value = []
    menus.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }

  return { token, username, realName, email, avatar, permissions, menus, loginAction, loadRouters, logout }
})
