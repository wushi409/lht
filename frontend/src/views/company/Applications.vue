<template>
  <div class="page-container">
    <el-card>
      <template #header>简历处理</template>
      
      <el-table :data="applications" v-loading="loading" stripe>
        <el-table-column label="应聘者" min-width="100">
          <template #default="{ row }">{{ row.student?.name || '未知' }}</template>
        </el-table-column>
        <el-table-column label="职位" prop="job.title" min-width="140" />
        <el-table-column label="学校/学院" min-width="140">
          <template #default="{ row }">{{ row.student?.college || '-' }}</template>
        </el-table-column>
        <el-table-column label="投递时间" min-width="110">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewResume(row)">
              查看简历
            </el-button>
            <template v-if="row.status !== 'HIRED' && row.status !== 'REJECTED' && row.status !== 'WITHDRAWN'">
              <el-divider direction="vertical" />
              <el-dropdown trigger="click" @command="(cmd) => handleAction(row, cmd)">
                <el-button type="primary" link size="small">
                  处理 <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="INTERVIEW" v-if="row.status !== 'INTERVIEW'">
                      <el-icon><calendar /></el-icon> 安排面试
                    </el-dropdown-item>
                    <el-dropdown-item command="HIRED">
                      <el-icon><check /></el-icon> 录用
                    </el-dropdown-item>
                    <el-dropdown-item command="REJECTED">
                      <el-icon><close /></el-icon> 拒绝
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && applications.length === 0" description="暂无简历" />
    </el-card>

    <!-- 简历详情弹窗 -->
    <el-dialog v-model="resumeVisible" title="简历详情" width="600px">
      <div v-if="currentResume">
        <h3 style="margin:0 0 16px">{{ currentResume.title || currentResume.name }}</h3>
        <p><strong>个人简介：</strong>{{ currentResume.summary || '-' }}</p>
        <el-divider />
        <p><strong>教育经历：</strong></p>
        <p style="white-space:pre-wrap">{{ currentResume.education || '-' }}</p>
        <p><strong>工作/实习经历：</strong></p>
        <p style="white-space:pre-wrap">{{ currentResume.experience || '-' }}</p>
        <p><strong>技能特长：</strong></p>
        <p>{{ currentResume.skills || '-' }}</p>
      </div>
      <el-empty v-else description="暂无简历信息" />
    </el-dialog>

    <!-- 安排面试弹窗 -->
    <el-dialog v-model="interviewDialogVisible" title="安排面试" width="500px">
      <el-form :model="interviewForm" label-width="90px">
        <el-form-item label="应聘者">
          <el-input :value="currentApplication?.student?.name" disabled />
        </el-form-item>
        <el-form-item label="职位">
          <el-input :value="currentApplication?.job?.title" disabled />
        </el-form-item>
        <el-form-item label="面试时间" required>
          <el-date-picker 
            v-model="interviewForm.scheduledAt" 
            type="datetime" 
            placeholder="选择时间" 
            style="width:100%" 
          />
        </el-form-item>
        <el-form-item label="地点/方式">
          <el-input v-model="interviewForm.location" placeholder="如：线上/公司会议室" />
        </el-form-item>
        <el-form-item label="面试官">
          <el-input v-model="interviewForm.interviewer" placeholder="面试官姓名" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="interviewDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="scheduling" @click="confirmInterview">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { companyApi } from '@/api/company'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import { ArrowDown, Calendar, Check, Close } from '@element-plus/icons-vue'

const applications = ref([])
const loading = ref(false)
const resumeVisible = ref(false)
const currentResume = ref(null)

// 面试相关
const interviewDialogVisible = ref(false)
const scheduling = ref(false)
const currentApplication = ref(null)
const interviewForm = ref({
  scheduledAt: null,
  location: '',
  interviewer: ''
})

const fetchApplications = async () => {
  loading.value = true
  try {
    applications.value = await companyApi.listApplications() || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const viewResume = async (row) => {
  try {
    // 优先使用已加载的 resume 对象，否则通过 ID 获取
    if (row.resume) {
      currentResume.value = row.resume
    } else if (row.resumeId) {
      currentResume.value = await request.get(`/resumes/${row.resumeId}`)
    } else {
      // 没有简历，显示学生基本信息
      currentResume.value = row.student ? {
        name: row.student.name + ' 的信息',
        intention: '-',
        education: row.student.college || '-',
        experience: '-',
        skills: '-',
        selfEvaluation: '-'
      } : null
    }
    resumeVisible.value = true
    // 标记为已查看
    if (row.status === 'SUBMITTED') {
      await companyApi.updateApplicationStatus(row.id, { status: 'VIEWED' })
      row.status = 'VIEWED'
    }
  } catch (e) {
    console.error(e)
    currentResume.value = null
    resumeVisible.value = true
  }
}

const handleAction = (row, action) => {
  if (action === 'INTERVIEW') {
    // 打开面试安排对话框
    currentApplication.value = row
    interviewForm.value = {
      scheduledAt: null,
      location: '',
      interviewer: ''
    }
    interviewDialogVisible.value = true
  } else {
    // 直接更新状态（录用或拒绝）
    updateStatus(row, action)
  }
}

const updateStatus = async (row, status) => {
  try {
    await companyApi.updateApplicationStatus(row.id, { status })
    ElMessage.success('状态已更新')
    fetchApplications()
  } catch (e) {
    console.error(e)
  }
}

const confirmInterview = async () => {
  if (!interviewForm.value.scheduledAt) {
    ElMessage.warning('请选择面试时间')
    return
  }

  scheduling.value = true
  try {
    // 创建面试记录
    await request.post('/interviews', {
      applicationId: currentApplication.value.id,
      jobId: currentApplication.value.job.id,
      studentId: currentApplication.value.student.id,
      scheduledAt: interviewForm.value.scheduledAt.toISOString(),
      location: interviewForm.value.location,
      interviewer: interviewForm.value.interviewer
    })
    
    // 更新投递状态为面试中
    await companyApi.updateApplicationStatus(currentApplication.value.id, { status: 'INTERVIEW' })
    
    ElMessage.success('面试安排成功')
    interviewDialogVisible.value = false
    fetchApplications()
  } catch (e) {
    console.error(e)
    ElMessage.error('安排失败，请重试')
  } finally {
    scheduling.value = false
  }
}

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

const statusText = (status) => {
  const map = { PENDING: '待处理', SUBMITTED: '已投递', VIEWED: '已查看', INTERVIEW: '面试中', ACCEPTED: '已录用', HIRED: '已录用', REJECTED: '已拒绝', WITHDRAWN: '已撤回' }
  return map[status] || status
}

const statusType = (status) => {
  const map = { PENDING: 'info', SUBMITTED: 'info', VIEWED: '', INTERVIEW: 'warning', ACCEPTED: 'success', HIRED: 'success', REJECTED: 'danger', WITHDRAWN: 'info' }
  return map[status] || ''
}

onMounted(fetchApplications)
</script>
