<template>
  <el-container class="layout-wrapper">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <el-icon v-if="isCollapse" size="24" color="#fff"><Setting /></el-icon>
        <span v-else>🛡️ {{ configStore.systemTitle }}</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          background-color="#001529"
          text-color="rgba(255,255,255,.65)"
          active-text-color="#fff"
          router
          unique-opened
        >
          <template v-for="route in visibleRoutes" :key="route.path">
            <!-- 有子菜单 -->
            <el-sub-menu v-if="hasChildren(route)" :index="route.path">
              <template #title>
                <el-icon v-if="route.meta?.icon"><component :is="route.meta.icon" /></el-icon>
                <span>{{ route.meta?.title }}</span>
              </template>
              <el-menu-item
                v-for="child in visibleChildren(route)"
                :key="fullPath(route, child)"
                :index="fullPath(route, child)"
              >
                <el-icon v-if="child.meta?.icon"><component :is="child.meta.icon" /></el-icon>
                <span>{{ child.meta?.title }}</span>
              </el-menu-item>
            </el-sub-menu>
            <!-- 无子菜单 -->
            <el-menu-item v-else :index="route.path">
              <el-icon v-if="route.meta?.icon"><component :is="route.meta.icon" /></el-icon>
              <template #title>{{ route.meta?.title }}</template>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="layout-main">
      <!-- 顶栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" size="20" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
          <!-- 面包屑 -->
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="b in breadcrumbs" :key="b.path" :to="b.path">
              {{ b.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="avatar-wrap">
              <el-avatar :size="32" :src="userStore.avatar || undefined">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">{{ userStore.realName || userStore.username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-content">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { useConfigStore } from '@/store/config'
import { logout } from '@/api/auth'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const configStore = useConfigStore()
const isCollapse = ref(false)

// 当前激活菜单
const activeMenu = computed(() => route.path)

// 面包屑
const breadcrumbs = computed(() => {
  return route.matched
    .filter(r => r.meta?.title)
    .map(r => ({ title: r.meta.title, path: r.path }))
})

// 获取有权限的路由
const allRoutes = router.getRoutes()

// 静态路由配置中的子菜单权限映射（用于控制顶层菜单的显示）
const STATIC_PARENT_PERMS = {
  '/system': 'sys:user:list'  // 系统管理只要有一个子菜单权限就显示
}

const visibleRoutes = computed(() => {
  return allRoutes.filter(r => {
    if (r.meta?.hidden) return false
    if (!r.meta?.title) return false
    // 只保留顶层路由（有子菜单的目录 or dashboard）
    if (!(r.children?.length > 0 || r.path === '/dashboard' || r.name === 'Dashboard')) return false
    // 静态路由中的顶层目录，检查是否有对应子菜单权限
    if (STATIC_PARENT_PERMS[r.path]) {
      const requiredPerm = STATIC_PARENT_PERMS[r.path]
      if (userStore.permissions.length > 0 && !userStore.permissions.includes(requiredPerm) && !userStore.permissions.includes('*:*:*')) {
        return false
      }
    }
    return true
  }).map(r => {
    // 对于 / 路由，转换成 dashboard
    if (r.path === '/') return { ...r, path: '/dashboard', children: [] }
    return r
  })
})

function hasChildren(route) {
  return route.children && visibleChildren(route).length > 0
}

// 静态路由不需要前端权限控制，权限在后端 API 层验证
function visibleChildren(route) {
  return (route.children || []).filter(c => {
    if (c.meta?.hidden) return false
    if (!c.meta?.title) return false
    return true
  })
}

function fullPath(parent, child) {
  if (child.path.startsWith('/')) return child.path
  return `${parent.path}/${child.path}`
}

async function handleCommand(cmd) {
  if (cmd === 'logout') {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.layout-wrapper {
  height: 100vh;
}

.layout-aside {
  background: #001529;
  transition: width .2s;
  overflow: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 16px;
    font-weight: bold;
    border-bottom: 1px solid rgba(255,255,255,.1);
    white-space: nowrap;
    overflow: hidden;
  }

  :deep(.el-menu) { border-right: none; }
}

.layout-header {
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  height: 60px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;
  }

  .collapse-btn {
    cursor: pointer;
    color: #666;
    &:hover { color: #409eff; }
  }

  .header-right {
    .avatar-wrap {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      .username { font-size: 14px; }
    }
  }
}

.layout-content {
  background: #f5f7fa;
  padding: 16px;
  overflow-y: auto;
}
</style>
