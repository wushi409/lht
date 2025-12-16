<template>
  <div class="page-container">
    <el-card>
      <template #header>我的面试</template>
      
      <el-table :data="interviews" v-loading="loading" stripe>
        <el-table-column label="职位" prop="job.title" min-width="140" />
        <el-table-column label="公司" prop="company.name" min-width="120" />
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
            <template v-if="row.status === 'PENDING'">
              <span class="action-link success" @click="handleRespond(row, 'CONFIRMED')">确认</span>
              <el-divider direction="vertical" />
              <span class="action-link danger" @click="handleRespond(row, 'REJECTED')">拒绝</span>
            </template>
            <span v-else style="color:#9ca3af">-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && interviews.length === 0" description="暂无面试安排" />
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
    interviews.value = await listStudentInterviews() || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleRespond = async (row, status) => {
  try {
    await respondToInterview(row.id, status)
    ElMessage.success(status === 'CONFIRMED' ? '已确认' : '已拒绝')
    fetchInterviews()
  } catch (e) {
    console.error(e)
  }
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
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
.page-container { max-width: 1000px; }
</style>
