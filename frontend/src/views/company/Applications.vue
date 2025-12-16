<template>
  <div class="company-applications p-6">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">收到简历</h2>
        </div>
      </template>

      <el-table :data="applications" style="width: 100%" v-loading="loading">
        <el-table-column prop="job.title" label="应聘职位" />
        <el-table-column prop="studentName" label="申请人" />
        <el-table-column prop="resume.title" label="简历">
           <template #default="scope">
             <a :href="scope.row.resume.fileUrl" target="_blank" class="text-blue-500 hover:underline">{{ scope.row.resume.title }}</a>
           </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
            <div v-if="scope.row.status === 'PENDING'">
              <el-button size="small" type="success" @click="handleStatus(scope.row, 'ACCEPTED')">通过</el-button>
              <el-button size="small" type="danger" @click="handleStatus(scope.row, 'REJECTED')">拒绝</el-button>
            </div>
            <div v-if="scope.row.status === 'ACCEPTED'">
               <el-button size="small" type="primary" @click="openInterviewDialog(scope.row)">安排面试</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Interview Dialog -->
    <el-dialog v-model="interviewDialogVisible" title="安排面试" width="40%">
      <el-form :model="interviewForm">
        <el-form-item label="面试时间">
           <el-date-picker
            v-model="interviewForm.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
          />
        </el-form-item>
        <el-form-item label="地点/方式">
          <el-input v-model="interviewForm.location" placeholder="例如：腾讯会议号 123456" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="interviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="scheduleInterview">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { companyApi } from '@/api/company'
import { ElMessage } from 'element-plus'
import request from '@/api/request' // Import raw request for interview call if not in companyApi

const applications = ref([])
const loading = ref(false)
const interviewDialogVisible = ref(false)
const currentApp = ref(null)
const interviewForm = reactive({
  timeRange: [],
  location: ''
})

const fetchApplications = async () => {
  loading.value = true
  try {
    const data = await companyApi.listApplications()
    applications.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleStatus = async (row, status) => {
  try {
    await companyApi.updateApplicationStatus(row.id, { status })
    ElMessage.success('操作成功')
    fetchApplications()
  } catch (error) {
    console.error(error)
  }
}

const openInterviewDialog = (row) => {
  currentApp.value = row
  interviewForm.timeRange = []
  interviewForm.location = ''
  interviewDialogVisible.value = true
}

const scheduleInterview = async () => {
  if (!interviewForm.timeRange || interviewForm.timeRange.length !== 2) {
    ElMessage.warning('请选择面试时间')
    return
  }
  try {
    // Call interview API manually since it might not be in companyApi helper
    await request({
      url: '/interviews',
      method: 'post',
      data: {
        applicationId: currentApp.value.id,
        startTime: interviewForm.timeRange[0],
        endTime: interviewForm.timeRange[1],
        location: interviewForm.location
      }
    })
    ElMessage.success('面试安排成功')
    interviewDialogVisible.value = false
  } catch (error) {
    console.error(error)
  }
}

const getStatusType = (status) => {
  const map = {
    'PENDING': 'info',
    'ACCEPTED': 'success',
    'REJECTED': 'danger',
    'WITHDRAWN': 'warning'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待处理',
    'ACCEPTED': '初筛通过',
    'REJECTED': '不合适',
    'WITHDRAWN': '已撤回'
  }
  return map[status] || status
}

onMounted(() => {
  fetchApplications()
})
</script>
