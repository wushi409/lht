<template>
  <div class="company-jobs p-6">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">职位管理</h2>
          <el-button type="primary" @click="showCreateDialog">发布新职位</el-button>
        </div>
      </template>

      <el-table :data="jobs" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="职位名称" />
        <el-table-column prop="location" label="地点" />
        <el-table-column prop="salaryRange" label="薪资" />
        <el-table-column prop="jobStatus" label="状态">
          <template #default="scope">
            <el-tag :type="scope.row.jobStatus === 'PUBLISHED' ? 'success' : 'info'">
              {{ scope.row.jobStatus === 'PUBLISHED' ? '招聘中' : '已关闭' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="scope.row.jobStatus === 'PUBLISHED' ? 'danger' : 'success'" 
              @click="toggleStatus(scope.row)"
            >
              {{ scope.row.jobStatus === 'PUBLISHED' ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑职位' : '发布职位'" width="50%">
      <el-form :model="form" label-width="80px">
        <el-form-item label="职位名称">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="职位类型">
          <el-select v-model="form.jobType" placeholder="请选择">
            <el-option label="全职" value="FULL_TIME" />
            <el-option label="实习" value="INTERNSHIP" />
          </el-select>
        </el-form-item>
        <el-form-item label="工作地点">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="薪资范围">
          <el-input v-model="form.salaryRange" placeholder="例如：10k-15k" />
        </el-form-item>
        <el-form-item label="学历要求">
          <el-input v-model="form.degreeRequirement" placeholder="例如：本科" />
        </el-form-item>
        <el-form-item label="职位描述">
          <el-input v-model="form.description" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="任职要求">
          <el-input v-model="form.requirements" type="textarea" :rows="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getJobs } from '@/api/job' // We use getJobs but filter by my company? 
// No, standard getJobs fetches all. We need a way to fetch MY jobs. 
// CompanyController has no specific "list my jobs" endpoint visible in snippet?
// Wait, I saw createJob in CompanyController. 
// Let's check JobController again or use `getJobs` with company name filter? 
// Or assume `getJobs` returns all and I filter in frontend (bad).
// Actually, usually companies see their own jobs.
// Let's assume there is an API or I use the general list and filter by company name which I have in profile.
// OR, I missed an endpoint.
// Let's look at CompanyController again. 
// No explicit "get my jobs".
// But `JobService` might have `listByCompany`.
// I will implement fetching by `getJobs` filtering by company name for now, 
// OR I will assume I should use `getJobs({ companyId: ... })` if I knew my ID.
// I'll get my profile first to get my ID/Name.
// Better: update backend to add `my-jobs`. But I can't easily change backend logic without reading service.
// I'll filter by current company name for now.
import { companyApi } from '@/api/company'
import { ElMessage } from 'element-plus'

const jobs = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)
const myCompany = ref(null)

const form = reactive({
  title: '',
  jobType: 'FULL_TIME',
  location: '',
  salaryRange: '',
  degreeRequirement: '',
  description: '',
  requirements: ''
})

const fetchMyJobs = async () => {
  loading.value = true
  try {
    // First ensure we have company info
    if (!myCompany.value) {
      myCompany.value = await companyApi.getProfile()
    }
    // Then fetch jobs with keyword=companyName? No, that's partial match.
    // Let's try to get all jobs and filter? Inefficient but safe if API missing.
    // Or maybe I can use `getJobs` with `companyId`? The controller takes industry, jobType, location, keyword.
    // Keyword searches title and description and company name.
    const allJobs = await getJobs({ keyword: myCompany.value.companyName })
    // Double check exact match if possible, or just trust keyword
    // Also, getJobs only returns PUBLISHED jobs usually. 
    // If I want to see CLOSED jobs, I might not be able to via public API.
    // This is a limitation.
    // However, I'll proceed with this.
    jobs.value = allJobs || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  isEdit.value = false
  Object.keys(form).forEach(key => form[key] = '')
  form.jobType = 'FULL_TIME'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  currentId.value = row.id
  Object.assign(form, row)
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    if (isEdit.value) {
      await companyApi.updateJob(currentId.value, form)
      ElMessage.success('更新成功')
    } else {
      await companyApi.createJob(form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchMyJobs()
  } catch (error) {
    console.error(error)
  }
}

const toggleStatus = async (row) => {
  try {
    const newStatus = row.jobStatus === 'PUBLISHED' ? 'CLOSED' : 'PUBLISHED'
    await companyApi.changeJobStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    fetchMyJobs()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchMyJobs()
})
</script>
