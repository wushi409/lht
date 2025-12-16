<template>
  <div class="company-interviews p-6">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">面试管理</h2>
        </div>
      </template>

      <el-table :data="interviews" style="width: 100%" v-loading="loading">
        <el-table-column prop="jobTitle" label="职位" />
        <el-table-column prop="studentName" label="候选人" />
        <el-table-column prop="startTime" label="时间">
           <template #default="scope">
             {{ new Date(scope.row.startTime).toLocaleString() }} - {{ new Date(scope.row.endTime).toLocaleTimeString() }}
           </template>
        </el-table-column>
        <el-table-column prop="location" label="地点/方式" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const interviews = ref([])
const loading = ref(false)

const fetchInterviews = async () => {
  loading.value = true
  try {
    const data = await request({
      url: '/interviews/company/me',
      method: 'get'
    })
    interviews.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const getStatusType = (status) => {
  const map = {
    'SCHEDULED': 'warning',
    'ACCEPTED': 'success',
    'DECLINED': 'danger',
    'COMPLETED': 'info',
    'CANCELLED': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'SCHEDULED': '待确认',
    'ACCEPTED': '已接受',
    'DECLINED': '已拒绝',
    'COMPLETED': '已结束',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

onMounted(() => {
  fetchInterviews()
})
</script>
