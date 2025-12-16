<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>职位管理</span>
          <el-button type="primary" @click="openCreate">发布职位</el-button>
        </div>
      </template>
      
      <el-table :data="jobs" v-loading="loading" stripe>
        <el-table-column label="职位名称" prop="title" min-width="150" />
        <el-table-column label="薪资" prop="salaryRange" width="120" />
        <el-table-column label="地点" prop="location" width="100" />
        <el-table-column label="招聘人数" prop="headcount" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
              {{ row.status === 'PUBLISHED' ? '招聘中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <span class="action-link" @click="openEdit(row)">编辑</span>
            <el-divider direction="vertical" />
            <span v-if="row.status === 'PUBLISHED'" class="action-link warning" @click="changeStatus(row, 'CLOSED')">关闭</span>
            <span v-else class="action-link success" @click="changeStatus(row, 'PUBLISHED')">开启</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && jobs.length === 0" description="暂无职位，点击上方按钮发布" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑职位' : '发布职位'" width="650px">
      <el-form :model="form" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="职位名称" required>
              <el-input v-model="form.title" placeholder="如：前端开发工程师" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="薪资范围">
              <el-input v-model="form.salaryRange" placeholder="如：8k-15k" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="工作地点">
              <el-input v-model="form.location" placeholder="如：北京" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="招聘人数">
              <el-input-number v-model="form.headcount" :min="1" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="职位类型">
              <el-select v-model="form.jobType" style="width:100%">
                <el-option label="全职" value="全职" />
                <el-option label="实习" value="实习" />
                <el-option label="兼职" value="兼职" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期">
              <el-date-picker v-model="form.deadline" type="date" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="职位描述">
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="职位职责、工作内容等" />
        </el-form-item>
        <el-form-item label="技能要求">
          <el-input v-model="form.skills" type="textarea" :rows="2" placeholder="所需技能、学历要求等" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { companyApi } from '@/api/company'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const jobs = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = reactive({ title: '', salaryRange: '', location: '', headcount: 1, jobType: '全职', deadline: null, description: '', skills: '' })

const fetchJobs = async () => {
  loading.value = true
  try {
    jobs.value = await request.get('/companies/me/jobs') || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, { title: '', salaryRange: '', location: '', headcount: 1, jobType: '全职', deadline: null, description: '', skills: '' })
}

const openCreate = () => {
  resetForm()
  isEdit.value = false
  editId.value = null
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, row)
  isEdit.value = true
  editId.value = row.id
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.title) return ElMessage.warning('请输入职位名称')
  saving.value = true
  try {
    if (isEdit.value) {
      await companyApi.updateJob(editId.value, form)
    } else {
      await companyApi.createJob(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchJobs()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

const changeStatus = async (row, status) => {
  try {
    await companyApi.changeJobStatus(row.id, status)
    ElMessage.success('状态已更新')
    fetchJobs()
  } catch (e) {
    console.error(e)
  }
}

onMounted(fetchJobs)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
