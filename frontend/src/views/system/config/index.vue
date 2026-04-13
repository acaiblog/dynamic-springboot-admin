<template>
  <div class="page-container">
    <el-form :model="query" inline class="search-form">
      <el-form-item label="配置名称">
        <el-input v-model="query.name" placeholder="请输入配置名称" clearable @keyup.enter="loadData" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="loadData">搜索</el-button>
        <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" :icon="Plus" @click="openDialog()">新增</el-button>
      </el-form-item>
    </el-form>

    <!-- 系统标题设置 -->
    <el-card class="title-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>系统标题设置</span>
        </div>
      </template>
      <el-form inline>
        <el-form-item label="系统标题">
          <el-input v-model="systemTitle" placeholder="请输入系统标题" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSaveTitle">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 系统说明设置 -->
    <el-card class="intro-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>系统说明设置</span>
        </div>
      </template>
      <el-form :model="systemIntro" label-width="120px" style="max-width: 600px">
        <el-form-item label="系统名称">
          <el-input v-model="systemIntro.name" placeholder="如：动态菜单权限管理系统" />
        </el-form-item>
        <el-form-item label="技术栈">
          <el-input v-model="systemIntro.tech" placeholder="如：SpringBoot 3 + Vue3 + Element Plus" />
        </el-form-item>
        <el-form-item label="权限模型">
          <el-input v-model="systemIntro.rbac" placeholder="如：RBAC（基于角色的访问控制）" />
        </el-form-item>
        <el-form-item label="功能模块">
          <el-input v-model="systemIntro.modules" placeholder="如：用户管理、角色管理、菜单管理" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSaveIntro">保存系统说明</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 系统配置列表 -->
    <el-table v-loading="loading" :data="tableData" border stripe class="mt-16">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="configKey" label="配置键" width="200" />
      <el-table-column prop="configValue" label="配置值" />
      <el-table-column prop="remark" label="备注" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <el-tag :type="row.status===1?'success':'danger'">{{ row.status===1?'启用':'停用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button size="small" @click="openDialog(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="form.configKey" :disabled="!!form.id" placeholder="如：system_title" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="form.configValue" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getConfigList, createConfig, updateConfig, deleteConfig, getSystemTitle, setSystemTitle, getSystemIntro, setSystemIntro } from '@/api/config'
import { useConfigStore } from '@/store/config'

const configStore = useConfigStore()
const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增配置')
const formRef = ref()
const form = reactive({ id: undefined, configKey: '', configValue: '', remark: '', status: 1 })
const systemTitle = ref('')
const systemIntro = reactive({
  name: '',
  tech: '',
  rbac: '',
  modules: ''
})

const rules = {
  configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await getConfigList()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

function resetQuery() { query.name = ''; loadData() }

function openDialog(row) {
  dialogTitle.value = row ? '编辑配置' : '新增配置'
  if (row) Object.assign(form, row)
  else Object.assign(form, { id: undefined, configKey: '', configValue: '', remark: '', status: 1 })
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    if (form.id) await updateConfig(form.id, form)
    else await createConfig(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } finally { saving.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除配置"${row.configKey}"吗？`, '提示', { type: 'warning' })
  await deleteConfig(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleSaveTitle() {
  await setSystemTitle(systemTitle.value)
  configStore.setTitle(systemTitle.value)
  ElMessage.success('标题保存成功')
}

async function handleSaveIntro() {
  const intro = JSON.stringify(systemIntro)
  await setSystemIntro(intro)
  ElMessage.success('系统说明保存成功')
}

onMounted(async () => {
  loadData()
  const titleRes = await getSystemTitle()
  systemTitle.value = titleRes.data || ''
  // 加载系统说明
  const introRes = await getSystemIntro()
  if (introRes.data) {
    try {
      const introData = JSON.parse(introRes.data)
      Object.assign(systemIntro, introData)
    } catch (e) {
      // 解析失败，使用默认值
    }
  }
})

const query = reactive({ name: '' })
</script>

<style scoped>
.title-card {
  margin-bottom: 16px;
}
.intro-card {
  margin-bottom: 16px;
}
.mt-16 {
  margin-top: 16px;
}
.card-header {
  font-weight: 600;
}
</style>
