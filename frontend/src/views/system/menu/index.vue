<template>
  <div class="page-container">
    <el-form :model="query" inline class="search-form">
      <el-form-item label="菜单名称">
        <el-input v-model="query.menuName" placeholder="请输入菜单名称" clearable style="width:180px" @keyup.enter="handleSearch" />
      </el-form-item>
      <el-form-item label="菜单类型">
        <el-select v-model="query.menuType" placeholder="全部" clearable style="width:120px">
          <el-option label="目录" :value="0" />
          <el-option label="菜单" :value="1" />
          <el-option label="按钮" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" placeholder="全部" clearable style="width:100px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增</el-button>
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">删除</el-button>
      </el-form-item>
    </el-form>

    <el-table
      v-loading="loading"
      :data="filteredData"
      border
      row-key="id"
      :tree-props="{ children: 'children' }"
      default-expand-all
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column prop="menuName" label="菜单名称" width="200" />
      <el-table-column prop="icon" label="图标" width="80">
        <template #default="{ row }">
          <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="90">
        <template #default="{ row }">
          <el-tag v-if="(row.menuType ?? row.type)===0" type="info">目录</el-tag>
          <el-tag v-else-if="(row.menuType ?? row.type)===1">菜单</el-tag>
          <el-tag v-else type="warning">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由路径" />
      <el-table-column prop="component" label="组件路径" />
      <el-table-column prop="perms" label="权限标识" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="status" label="状态" width="90">
        <template #default="{ row }">
          <el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(null, row.id)">新增子项</el-button>
          <el-button type="primary" size="small" @click="openDialog(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="上级菜单">
              <el-tree-select
                v-model="form.parentId"
                :data="menuSelectData"
                :props="{ label: 'menuName', value: 'id', children: 'children' }"
                check-strictly
                clearable
                placeholder="请选择上级菜单"
                style="width:100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单类型" prop="menuType">
              <el-radio-group v-model="form.menuType">
                <el-radio :value="0">目录</el-radio>
                <el-radio :value="1">菜单</el-radio>
                <el-radio :value="2">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜单名称" prop="menuName">
              <el-input v-model="form.menuName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="图标">
              <el-input v-model="form.icon" placeholder="如: Setting" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 2">
            <el-form-item label="路由路径">
              <el-input v-model="form.path" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 1">
            <el-form-item label="组件路径">
              <el-input v-model="form.component" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType === 2">
            <el-form-item label="权限标识">
              <el-input v-model="form.perms" placeholder="如: sys:user:add" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="form.sort" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.menuType !== 2">
            <el-form-item label="是否显示">
              <el-radio-group v-model="form.visible">
                <el-radio :value="1">显示</el-radio>
                <el-radio :value="0">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Plus, Search, Refresh, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/menu'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const formRef = ref()
const selectedRows = ref([])

function handleSelectionChange(selection) {
  selectedRows.value = selection
}

async function handleBatchDelete() {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请选择要删除的菜单')
    return
  }
  await ElMessageBox.confirm(`确定删除选中的 ${selectedRows.value.length} 个菜单吗？`, '提示', { type: 'warning' })
  try {
    await Promise.all(selectedRows.value.map(row => deleteMenu(row.id)))
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    ElMessage.error(e?.response?.data?.message || '删除失败，请先删除子菜单')
  }
}

const query = reactive({ menuName: '', menuType: undefined, status: undefined })

const form = reactive({
  id: undefined, parentId: 0, menuName: '', path: '', component: '',
  perms: '', icon: '', menuType: 1, visible: 1, status: 1, sort: 0
})

const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }]
}

// 用于上级菜单选择（只显示目录和菜单，不显示按钮）
const menuSelectData = computed(() => {
  const filterBtn = (list) => list
    .filter(m => m.menuType !== 2)
    .map(m => ({ ...m, children: m.children ? filterBtn(m.children) : [] }))
  return [{ id: 0, menuName: '根目录', children: filterBtn(tableData.value) }]
})

// 前端过滤：支持菜单名称、类型、状态
const filteredData = computed(() => {
  const { menuName, menuType, status } = query
  const hasFilter = menuName || menuType != null || status != null
  if (!hasFilter) return tableData.value
  const filterTree = (list) => {
    return list.reduce((acc, item) => {
      // 后端返回 type 字段，前端表格用 menuType，兼容两者
      const itemType = item.menuType ?? item.type
      const nameMatch = !menuName || (item.menuName || '').includes(menuName)
      const typeMatch = menuType == null || Number(itemType) === Number(menuType)
      const statusMatch = status == null || Number(item.status) === Number(status)
      const filteredChildren = item.children ? filterTree(item.children) : []
      if ((nameMatch && typeMatch && statusMatch) || filteredChildren.length > 0) {
        acc.push({ ...item, children: filteredChildren })
      }
      return acc
    }, [])
  }
  return filterTree(tableData.value)
})

function handleSearch() { /* 触发 computed 重新计算 */ }

function resetQuery() {
  query.menuName = ''
  query.menuType = undefined
  query.status = undefined
}

async function loadData() {
  loading.value = true
  try {
    const res = await getMenuTree()
    tableData.value = res.data
  } finally { loading.value = false }
}

function openDialog(row, parentId) {
  if (row) {
    dialogTitle.value = '编辑菜单'
    Object.assign(form, row)
  } else {
    dialogTitle.value = '新增菜单'
    Object.assign(form, {
      id: undefined, parentId: parentId || 0, menuName: '', path: '',
      component: '', perms: '', icon: '', menuType: 1, visible: 1, status: 1, sort: 0
    })
  }
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.id) await updateMenu(form)
    else await createMenu(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally { saving.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除菜单"${row.menuName}"吗？`, '提示', { type: 'warning' })
  await deleteMenu(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>
