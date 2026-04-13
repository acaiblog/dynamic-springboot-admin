<template>
  <div class="page-container">
    <el-form :model="query" inline class="search-form">
      <el-form-item label="用户组名称">
        <el-input v-model="query.name" placeholder="请输入用户组名称" clearable @keyup.enter="loadData" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" :icon="Plus" @click="openDialog()">创建</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">删除</el-button>
        <el-button type="info" @click="handleManageMembers">管理成员</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" />
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="用户组名称" width="180" />
      <el-table-column prop="remark" label="描述" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="warning" @click="openRoleDialog(row)">分配角色</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="query.page"
      v-model:page-size="query.size"
      :total="total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next"
      style="margin-top:16px;justify-content:flex-end"
      @change="loadData"
    />

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户组名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="roleDialogVisible" title="为用户组分配角色" width="480px">
      <p class="tip">用户组内所有成员将获得以下角色的权限</p>
      <el-checkbox-group v-model="selectedRoleIds">
        <el-checkbox
          v-for="role in allRoles"
          :key="role.id"
          :value="role.id"
          :label="role.name || role.roleName"
          style="width:33%;margin-bottom:8px"
        />
      </el-checkbox-group>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleAssignRoles">确定</el-button>
      </template>
    </el-dialog>

    <!-- 管理成员弹窗 -->
    <el-dialog v-model="userDialogVisible" title="管理用户组成员" width="600px">
      <el-transfer
        v-model="selectedUserIds"
        :data="allUserTransfer"
        :titles="['全部用户', '组内成员']"
        filterable
        filter-placeholder="请输入用户名"
        :props="{ key: 'key', label: 'label' }"
      />
      <template #footer>
        <el-button @click="userDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleAssignUsers">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGroupPage, createGroup, updateGroup, deleteGroup, assignGroupRoles, assignGroupUsers, getGroupRoleIds, getGroupUserIds } from '@/api/group'
import { getRoleList } from '@/api/role'
import { getUserPage } from '@/api/user'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const roleDialogVisible = ref(false)
const userDialogVisible = ref(false)
const dialogTitle = ref('新增用户组')
const formRef = ref()
const allRoles = ref([])
const allUserTransfer = ref([])
const selectedRoleIds = ref([])
const selectedUserIds = ref([])
const currentGroupId = ref(null)
const selectedRows = ref([])

function handleSelectionChange(selection) {
  selectedRows.value = selection
}

async function handleBatchDelete() {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的用户组')
    return
  }
  await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个用户组吗？`, '提示', { type: 'warning' })
  await Promise.all(selectedRows.value.map(row => deleteGroup(row.id)))
  ElMessage.success('删除成功')
  loadData()
}

const query = reactive({ page: 1, size: 10, name: '' })
const form = reactive({ id: undefined, name: '', remark: '', sort: 0, status: 1 })
const rules = { name: [{ required: true, message: '请输入用户组名称', trigger: 'blur' }] }

async function loadData() {
  loading.value = true
  try {
    const res = await getGroupPage({ ...query })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function resetQuery() { query.name = ''; query.page = 1; loadData() }

function openDialog(row) {
  dialogTitle.value = row ? '编辑用户组' : '新增用户组'
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: undefined, name: '', remark: '', sort: 0, status: 1 })
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.id) await updateGroup(form)
    else await createGroup(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally { saving.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除用户组"${row.name}"吗？`, '提示', { type: 'warning' })
  await deleteGroup(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function openRoleDialog(row) {
  currentGroupId.value = row.id
  if (allRoles.value.length === 0) {
    const res = await getRoleList()
    allRoles.value = res.data
  }
  const res2 = await getGroupRoleIds(row.id)
  selectedRoleIds.value = res2.data
  roleDialogVisible.value = true
}

async function handleAssignRoles() {
  saving.value = true
  try {
    await assignGroupRoles(currentGroupId.value, selectedRoleIds.value)
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
  } finally { saving.value = false }
}

async function handleManageMembers() {
  if (selectedRows.value.length !== 1) {
    ElMessage.warning('请先勾选一个用户组')
    return
  }
  openUserDialog(selectedRows.value[0])
}

async function openUserDialog(row) {
  currentGroupId.value = row.id
  // 获取所有用户
  const res = await getUserPage({ page: 1, size: 1000 })
  allUserTransfer.value = res.data.records.map(u => ({
    key: u.id,
    label: `${u.username}${u.realName ? ' (' + u.realName + ')' : ''}`
  }))
  const res2 = await getGroupUserIds(row.id)
  selectedUserIds.value = res2.data
  userDialogVisible.value = true
}

async function handleAssignUsers() {
  saving.value = true
  try {
    await assignGroupUsers(currentGroupId.value, selectedUserIds.value)
    ElMessage.success('成员更新成功')
    userDialogVisible.value = false
  } finally { saving.value = false }
}

onMounted(loadData)
</script>

<style scoped>
.tip { color: #999; font-size: 13px; margin-bottom: 12px; }
</style>
