<template>
  <div class="interviews-container p-6">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">我的面试</h2>
        </div>
      </template>

      <el-table :data="interviews" style="width: 100%" v-loading="loading">
        <el-table-column prop="jobTitle" label="职位" />
        <el-table-column prop="companyName" label="公司" />
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
        <el-table-column label="操作">
          <template #default="scope">
            <div v-if="scope.row.status === 'SCHEDULED'">
               <el-button size="small" type="success" @click="respond(scope.row.id, 'ACCEPTED')">接受</el-button>
               <el-button size="small" type="danger" @click="respond(scope.row.id, 'DECLINED')">拒绝</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listStudentInterviews, respondToInterview } from '@/api/student'
import { ElMessage } from 'element-plus'

const interviews = ref([])
const loading = ref(false)

const fetchInterviews = async () => {
  loading.value = true
  try {
    const data = await listStudentInterviews()
    interviews.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const respond = async (id, status) => {
  try {
    await respondToInterview(id, status)
    ElMessage.success('操作成功')
    fetchInterviews()
  } catch (error) {
    console.error(error)
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
