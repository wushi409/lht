<template>
  <div class="page-container">
    <div class="header-row">
      <h2>职位管理</h2>
      <el-button type="primary" @click="openCreate">发布职位</el-button>
    </div>

    <el-card shadow="never" class="list-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>已发布职位（仅显示已发布状态，使用公司名称关键字筛选）</span>
          <el-tag type="info">关键字：{{ companyName || '—' }}</el-tag>
        </div>
      </template>
      <el-table :data="jobs" style="width: 100%">
        <el-table-column prop="title" label="标题" min-width="160" />
        <el-table-column prop="jobType" label="类型" width="100" />
        <el-table-column prop="location" label="地点" width="140" />
        <el-table-column prop="salaryRange" label="薪资" width="140" />
        <el-table-column prop="headcount" label="人数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="jobStatusMap[scope.row.status]?.type || 'info'">
              {{ jobStatusMap[scope.row.status]?.label || scope.row.status }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
            <el-button
              size="small"
              type="warning"
              @click="changeStatus(scope.row, scope.row.status === 'PUBLISHED' ? 'UNPUBLISHED' : 'PUBLISHED')"
            >{{ scope.row.status === 'PUBLISHED' ? '下架' : '发布' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && jobs.length === 0" description="暂无职位" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑职位' : '发布职位'" width="600px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="职位类型">
          <el-input v-model="form.jobType" placeholder="全职/实习等" />
        </el-form-item>
        <el-form-item label="工作地点">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="薪资范围">
          <el-input v-model="form.salaryRange" placeholder="如 10k-15k" />
        </el-form-item>
        <el-form-item label="招聘人数">
          <el-input type="number" v-model.number="form.headcount" />
        </el-form-item>
        <el-form-item label="截止日期">
          <el-date-picker v-model="form.deadline" type="date" placeholder="可不填" style="width: 100%" />
        </el-form-item>
        <el-form-item label="技能要求">
          <el-input v-model="form.skills" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="职位描述">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { companyApi } from '@/api/company'
import { getJobs } from '@/api/job'
import { JobStatus } from '@/stores/dict'

const jobs = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = reactive({
  id: null,
  title: '',
  jobType: '',
  location: '',
  salaryRange: '',
  headcount: 1,
  deadline: null,
  skills: '',
  description: ''
})
const companyName = ref('')
const jobStatusMap = JobStatus

const resetForm = () => {
  form.id = null
  form.title = ''
  form.jobType = ''
  form.location = ''
  form.salaryRange = ''
  form.headcount = 1
  form.deadline = null
  form.skills = ''
  form.description = ''
}

const fetchCompanyName = async () => {
  const data = await companyApi.getProfile()
  companyName.value = data?.name || ''
}

const fetchJobs = async () => {
  loading.value = true
  try {
    if (!companyName.value) await fetchCompanyName()
    const keyword = companyName.value || undefined
    const data = await getJobs(keyword ? { keyword } : {})
    jobs.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  resetForm()
  isEdit.value = false
  dialogVisible.value = true
}

const openEdit = (row) => {
  resetForm()
  isEdit.value = true
  form.id = row.id
  form.title = row.title
  form.jobType = row.jobType
  form.location = row.location
  form.salaryRange = row.salaryRange
  form.headcount = row.headcount
  form.skills = row.skills
  form.description = row.description
  form.deadline = row.deadline ? new Date(row.deadline) : null
  dialogVisible.value = true
}

const buildPayload = () => {
  const payload = {
    title: form.title,
    jobType: form.jobType,
    location: form.location,
    salaryRange: form.salaryRange,
    headcount: form.headcount,
    skills: form.skills,
    description: form.description,
  }
  if (form.deadline) {
    payload.deadline = new Date(form.deadline).toISOString()
  }
  return payload
}

const handleSubmit = async () => {
  saving.value = true
  try {
    if (isEdit.value && form.id) {
      await companyApi.updateJob(form.id, buildPayload())
      ElMessage.success('更新成功')
    } else {
      await companyApi.createJob(buildPayload())
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    await fetchJobs()
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

const changeStatus = async (row, status) => {
  ElMessageBox.confirm(`确定要将【${row.title}】标记为 ${status === 'PUBLISHED' ? '发布' : '下架'} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(async () => {
      await companyApi.changeJobStatus(row.id, status)
      ElMessage.success('状态已更新')
      fetchJobs()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchCompanyName().finally(fetchJobs)
})
</script>

<style scoped>
.page-container {
  max-width: 1100px;
  margin: 0 auto;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.list-card {
  margin-top: 10px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
