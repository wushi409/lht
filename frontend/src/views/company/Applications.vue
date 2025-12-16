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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <span class="action-link" @click="viewResume(row)">查看简历</span>
            <template v-if="row.status === 'PENDING' || row.status === 'VIEWED' || row.status === 'SUBMITTED'">
              <el-divider direction="vertical" />
              <el-dropdown trigger="click" @command="(cmd) => updateStatus(row, cmd)">
                <span class="action-link primary">处理</span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="INTERVIEW">安排面试</el-dropdown-item>
                    <el-dropdown-item command="ACCEPTED">录用</el-dropdown-item>
                    <el-dropdown-item command="REJECTED">拒绝</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && applications.length === 0" description="暂无简历" />
    </el-card>

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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { companyApi } from '@/api/company'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const applications = ref([])
const loading = ref(false)
const resumeVisible = ref(false)
const currentResume = ref(null)

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

const updateStatus = async (row, status) => {
  try {
    await companyApi.updateApplicationStatus(row.id, { status })
    ElMessage.success('状态已更新')
    fetchApplications()
  } catch (e) {
    console.error(e)
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
