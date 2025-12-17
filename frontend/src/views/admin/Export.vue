<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>数据导出</span>
          <el-button type="primary" @click="openCreate">新建导出任务</el-button>
        </div>
      </template>
      
      <el-table :data="tasks" v-loading="loading" stripe>
        <el-table-column label="导出类型" min-width="120">
          <template #default="{ row }">{{ typeText(row.type) }}</template>
        </el-table-column>
        <el-table-column label="创建时间" min-width="150">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'COMPLETED' && row.fileUrl" size="small" text type="primary" @click="download(row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && tasks.length === 0" description="暂无导出任务" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="新建导出任务" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="导出类型" required>
          <el-select v-model="form.type" style="width:100%">
            <el-option label="学生投递数据" value="STUDENT_APPLICATIONS" />
            <el-option label="企业参与数据" value="COMPANY_PARTICIPATION" />
            <el-option label="就业统计数据" value="EMPLOYMENT_STATS" />
            <el-option label="系统日志" value="LOGS" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="creating" @click="handleCreate">创建</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const tasks = ref([])
const loading = ref(false)
const creating = ref(false)
const dialogVisible = ref(false)
const form = reactive({ type: 'STUDENT_APPLICATIONS' })

const fetchTasks = async () => {
  loading.value = true
  try {
    tasks.value = await request.get('/admin/exports') || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  form.type = 'STUDENT_APPLICATIONS'
  dialogVisible.value = true
}

const handleCreate = async () => {
  creating.value = true
  try {
    await request.post('/admin/exports', form)
    ElMessage.success('任务已创建')
    dialogVisible.value = false
    fetchTasks()
  } catch (e) {
    console.error(e)
  } finally {
    creating.value = false
  }
}

const download = async (row) => {
  try {
    const result = await request.get(`/admin/exports/${row.id}/download`)
    if (result) {
      // 创建 Blob 并触发下载
      const blob = new Blob([result], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.href = url
      link.download = `export_${typeText(row.type)}_${row.id}.csv`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
      ElMessage.success('导出成功')
    }
  } catch (e) {
    console.error(e)
    ElMessage.error('下载失败')
  }
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

const typeText = (type) => {
  const map = { 
    STUDENT_APPLICATIONS: '学生投递数据', 
    COMPANY_PARTICIPATION: '企业参与数据', 
    EMPLOYMENT_STATS: '就业统计数据', 
    LOGS: '系统日志' 
  }
  return map[type] || type
}

const statusText = (status) => {
  const map = { PENDING: '处理中', COMPLETED: '已完成', FAILED: '失败' }
  return map[status] || status
}

const statusType = (status) => {
  const map = { PENDING: 'warning', COMPLETED: 'success', FAILED: 'danger' }
  return map[status] || ''
}

onMounted(fetchTasks)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
