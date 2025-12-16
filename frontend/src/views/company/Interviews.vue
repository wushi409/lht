<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>面试管理</span>
          <el-button type="primary" @click="openCreate">安排面试</el-button>
        </div>
      </template>
      
      <el-table :data="interviews" v-loading="loading" stripe>
        <el-table-column label="应聘者" min-width="100">
          <template #default="{ row }">{{ row.student?.name || '未知' }}</template>
        </el-table-column>
        <el-table-column label="职位" prop="job.title" min-width="140" />
        <el-table-column label="面试时间" min-width="150">
          <template #default="{ row }">{{ formatDateTime(row.scheduledAt) }}</template>
        </el-table-column>
        <el-table-column label="地点/方式" prop="location" min-width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING' || row.status === 'CONFIRMED'">
              <span class="action-link" @click="openEdit(row)">改期</span>
              <el-divider direction="vertical" />
              <span class="action-link danger" @click="handleCancel(row)">取消</span>
            </template>
            <span v-else style="color:#9ca3af">-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && interviews.length === 0" description="暂无面试安排" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '修改面试' : '安排面试'" width="500px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="投递记录" required v-if="!isEdit">
          <el-select v-model="form.applicationId" placeholder="选择投递记录" style="width:100%">
            <el-option v-for="app in pendingApplications" :key="app.id" :label="`${app.student?.name} - ${app.job?.title}`" :value="app.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="面试时间" required>
          <el-date-picker v-model="form.scheduledAt" type="datetime" placeholder="选择时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="地点/方式">
          <el-input v-model="form.location" placeholder="如：线上/公司会议室" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
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
import request from '@/api/request'
import { companyApi } from '@/api/company'
import { ElMessage } from 'element-plus'

const interviews = ref([])
const pendingApplications = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = reactive({ applicationId: null, scheduledAt: null, location: '', remark: '' })

const fetchInterviews = async () => {
  loading.value = true
  try {
    interviews.value = await request.get('/interviews/company/me') || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchPendingApplications = async () => {
  try {
    const all = await companyApi.listApplications() || []
    pendingApplications.value = all.filter(a => a.status === 'PENDING' || a.status === 'VIEWED')
  } catch (e) {
    console.error(e)
  }
}

const resetForm = () => {
  Object.assign(form, { applicationId: null, scheduledAt: null, location: '', remark: '' })
}

const openCreate = () => {
  resetForm()
  isEdit.value = false
  editId.value = null
  fetchPendingApplications()
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.scheduledAt = row.scheduledAt
  form.location = row.location
  form.remark = row.remark
  isEdit.value = true
  editId.value = row.id
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!isEdit.value && !form.applicationId) return ElMessage.warning('请选择投递记录')
  if (!form.scheduledAt) return ElMessage.warning('请选择面试时间')
  
  saving.value = true
  try {
    if (isEdit.value) {
      await request.put(`/interviews/${editId.value}`, form)
    } else {
      await request.post('/interviews', form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchInterviews()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

const handleCancel = async (row) => {
  try {
    await request.post(`/interviews/${row.id}/cancel`)
    ElMessage.success('已取消面试')
    fetchInterviews()
  } catch (e) {
    console.error(e)
  }
}

const statusText = (status) => {
  const map = { PENDING: '待确认', CONFIRMED: '已确认', COMPLETED: '已完成', REJECTED: '已拒绝', CANCELLED: '已取消' }
  return map[status] || status
}

const statusType = (status) => {
  const map = { PENDING: 'warning', CONFIRMED: 'success', COMPLETED: '', REJECTED: 'danger', CANCELLED: 'info' }
  return map[status] || ''
}

onMounted(fetchInterviews)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
