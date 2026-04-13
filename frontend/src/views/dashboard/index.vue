<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="s in stats" :key="s.title">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-inner">
            <el-icon :size="40" :color="s.color"><component :is="s.icon" /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ s.value }}</div>
              <div class="stat-label">{{ s.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="12">
        <el-card header="系统说明" shadow="hover">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="系统名称">动态菜单权限管理系统</el-descriptions-item>
            <el-descriptions-item label="技术栈">SpringBoot 3 + MyBatis-Plus + JWT + Vue3 + Element Plus</el-descriptions-item>
            <el-descriptions-item label="权限模型">RBAC（基于角色的访问控制）+ 用户组</el-descriptions-item>
            <el-descriptions-item label="功能模块">用户管理、角色管理、菜单管理、用户组管理</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card header="当前账号信息" shadow="hover">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="用户名">{{ userStore.username }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userStore.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="权限数量">{{ userStore.permissions.length }}</el-descriptions-item>
            <el-descriptions-item label="登录状态">
              <el-tag type="success">在线</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getUserPage } from '@/api/user'
import { getRolePage } from '@/api/role'
import { getMenuTree } from '@/api/menu'
import request from '@/utils/request'

const userStore = useUserStore()

const stats = ref([
  { title: '用户总数', value: 0, icon: 'User',       color: '#409eff' },
  { title: '角色总数', value: 0, icon: 'UserFilled', color: '#67c23a' },
  { title: '菜单总数', value: 0, icon: 'Menu',       color: '#e6a23c' },
  { title: '用户组数', value: 0, icon: 'Grid',       color: '#f56c6c' }
])

// 递归统计树节点数量
function countTree(list) {
  return list.reduce((sum, item) => {
    return sum + 1 + (item.children ? countTree(item.children) : 0)
  }, 0)
}

onMounted(async () => {
  try {
    const [userRes, roleRes, menuRes, groupRes] = await Promise.allSettled([
      getUserPage({ page: 1, size: 1 }),
      getRolePage({ page: 1, size: 1 }),
      getMenuTree(),
      request.get('/system/group/page', { params: { page: 1, size: 1 } })
    ])
    if (userRes.status === 'fulfilled')  stats.value[0].value = userRes.value?.data?.total ?? 0
    if (roleRes.status === 'fulfilled')  stats.value[1].value = roleRes.value?.data?.total ?? 0
    if (menuRes.status === 'fulfilled')  stats.value[2].value = countTree(menuRes.value?.data ?? [])
    if (groupRes.status === 'fulfilled') stats.value[3].value = groupRes.value?.data?.total ?? 0
  } catch (e) {
    // 接口异常时保持显示 0
  }
})
</script>

<style scoped lang="scss">
.dashboard { padding: 4px; }
.stat-card {
  .stat-inner {
    display: flex;
    align-items: center;
    gap: 16px;
    .stat-info {
      .stat-value { font-size: 28px; font-weight: bold; color: #333; }
      .stat-label { color: #999; font-size: 13px; margin-top: 4px; }
    }
  }
}
</style>
