/**
 * 权限工具 & v-perms 指令
 */
import { useUserStore } from '@/store/user'

export function hasPermission(perm) {
  const userStore = useUserStore()
  const perms = userStore.permissions
  return perms.includes('*:*:*') || perms.includes(perm)
}

export function setupPermission(app) {
  // v-perms="'sys:user:add'"  — 无权限则隐藏元素
  app.directive('perms', {
    mounted(el, binding) {
      const perm = binding.value
      if (!hasPermission(perm)) {
        el.parentNode?.removeChild(el)
      }
    }
  })
}
