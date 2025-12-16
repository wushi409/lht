<template>
  <div class="page-container">
    <h2>简历管理</h2>
    
    <el-card shadow="never">
      <template #header>
        <el-form :inline="true" :model="filters">
          <el-form-item label="职位筛选">
            <el-select v-model="filters.jobId" placeholder="全部职位" clearable style="width: 200px">
              <el-option 
                v-for="job in myJobs" 
                :key="job.id" 
                :label="job.title" 
                :value="job.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态筛选">
            <el-select v-model="filters.status" placeholder="全部状态" clearable style="width: 150px">
              <el-option label="已投递" value="SUBMITTED" />
              <el-option label="面试中" value="INTERVIEW" />
              <el-option label="已录用" value="HIRED" />
              <el-option label="已拒绝" value="REJECTED" />
              <el-option label="已撤回" value="WITHDRAWN" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchApplications">查询</el-button>
          </el-form-item>
        </el-form>
      </template>

      <el-table :data="applications" v-loading="loading" style="width: 100%">
        <el-table-column label="学生姓名" width="120">
          <template #default="scope">
            {{ scope.row.student?.name || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="学号" width="130">
          <template #default="scope">
            {{ scope.row.student?.studentNo || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="学院" width="150">
          <template #default="scope">
            {{ scope.row.student?.college || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="应聘职位" min-width="160">
          <template #default="scope">
            {{ scope.row.job?.title || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="投递时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewResume(scope.row)">查看简历</el-button>
            <el-dropdown @command="(cmd) => handleStatusChange(scope.row, cmd)">
              <el-button size="small" type="primary">
                更新状态<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="INTERVIEW">邀请面试</el-dropdown-item>
                  <el-dropdown-item command="HIRED">录用</el-dropdown-item>
                  <el-dropdown-item command="REJECTED">拒绝</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && applications.length === 0" description="暂无投递记录" />
    </el-card>

    <!-- 简历详情对话框 -->
    <el-dialog v-model="resumeDialogVisible" title="简历详情" width="70%">
      <div v-if="currentResume" class="resume-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="姓名">{{ currentApplication?.student?.name }}</el-descriptions-item>
          <el-descriptions-item label="学号">{{ currentApplication?.student?.studentNo }}</el-descriptions-item>
          <el-descriptions-item label="学院">{{ currentApplication?.student?.college }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ currentApplication?.student?.phone }}</el-descriptions-item>
          <el-descriptions-item label="邮箱" :span="2">{{ currentApplication?.student?.email }}</el-descriptions-item>
        </el-descriptions>

        <el-divider />

        <h3>{{ currentResume.title }}</h3>
        
        <div class="resume-section" v-if="currentResume.summary">
          <h4>个人简介</h4>
          <p>{{ currentResume.summary }}</p>
        </div>

        <div class="resume-section" v-if="currentResume.education">
          <h4>教育经历</h4>
          <p style="white-space: pre-wrap;">{{ currentResume.education }}</p>
        </div>

        <div class="resume-section" v-if="currentResume.experience">
          <h4>工作/实习经历</h4>
          <p style="white-space: pre-wrap;">{{ currentResume.experience }}</p>
        </div>

        <div class="resume-section" v-if="currentResume.skills">
          <h4>技能特长</h4>
          <p style="white-space: pre-wrap;">{{ currentResume.skills }}</p>
        </div>

        <div class="resume-section" v-if="currentApplication?.notes">
          <h4>备注</h4>
          <p>{{ currentApplication.notes }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="resumeDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { companyApi } from '@/api/company'
import { ElMessage } from 'element-plus'
import { ArrowDown } from '@element-plus/icons-vue'
import request from '@/api/request'

const loading = ref(false)
const applications = ref([])
const myJobs = ref([])
const resumeDialogVisible = ref(false)
const currentResume = ref(null)
const currentApplication = ref(null)

const filters = reactive({
  jobId: null,
  status: null
})

const statusMap = {
  SUBMITTED: { label: '已投递', type: '' },
  INTERVIEW: { label: '面试中', type: 'warning' },
  HIRED: { label: '已录用', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' },
  WITHDRAWN: { label: '已撤回', type: 'info' }
}

const getStatusLabel = (status) => statusMap[status]?.label || status
const getStatusType = (status) => statusMap[status]?.type || 'info'

const fetchMyJobs = async () => {
  try {
    const data = await request({ url: '/companies/me/jobs', method: 'get' })
    myJobs.value = data || []
  } catch (error) {
    console.error(error)
  }
}

const fetchApplications = async () => {
  loading.value = true
  try {
    const params = {}
    if (filters.jobId) params.jobId = filters.jobId
    if (filters.status) params.status = filters.status
    
    const data = await companyApi.listApplications(params)
    applications.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const viewResume = async (application) => {
  try {
    currentApplication.value = application
    // 获取简历详情
    const resumeId = application.resume?.id || application.resumeId
    if (resumeId) {
      const data = await request({ url: `/resumes/${resumeId}`, method: 'get' })
      currentResume.value = data
      resumeDialogVisible.value = true
      
      // 查看简历不自动更改状态
    } else {
      ElMessage.warning('该投递没有关联简历')
    }
  } catch (error) {
    console.error(error)
  }
}

const handleStatusChange = async (application, newStatus, showMessage = true) => {
  try {
    await request({
      url: `/applications/${application.id}/status`,
      method: 'put',
      data: { status: newStatus }
    })
    if (showMessage) {
      ElMessage.success('状态已更新')
    }
    fetchApplications()
  } catch (error) {
    console.error(error)
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchMyJobs()
  fetchApplications()
})
</script>

<style scoped>
.page-container {
  max-width: 1400px;
  margin: 0 auto;
}

.resume-detail {
  padding: 20px;
}

.resume-section {
  margin-top: 20px;
}

.resume-section h4 {
  color: #303133;
  margin-bottom: 10px;
  font-size: 16px;
}

.resume-section p {
  color: #606266;
  line-height: 1.8;
  margin: 0;
}
</style>
