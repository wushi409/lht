<template>
  <div class="page-container">
    <el-card>
      <template #header>我的投递</template>
      
      <el-table :data="applications" v-loading="loading" stripe>
        <el-table-column label="职位" prop="job.title" min-width="150" />
        <el-table-column label="公司" prop="job.company.name" min-width="120" />
        <el-table-column label="投递时间" min-width="120">
          <template #default="{ row }">{{ formatDate(row.applyTime) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <span v-if="row.status === 'PENDING' || row.status === 'SUBMITTED'" class="action-link danger" @click="handleWithdraw(row)">撤回</span>
            <span v-else style="color:#9ca3af">-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && applications.length === 0" description="暂无投递记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyApplications, withdrawApplication } from '@/api/student'
import { ElMessage, ElMessageBox } from 'element-plus'

const applications = ref([])
const loading = ref(false)

const fetchApplications = async () => {
  loading.value = true
  try {
    applications.value = await getMyApplications() || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleWithdraw = async (row) => {
  try {
    await ElMessageBox.confirm('确定撤回该投递吗？', '提示', { type: 'warning' })
    await withdrawApplication(row.id)
    ElMessage.success('撤回成功')
    fetchApplications()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

const statusText = (status) => {
  const map = { 
    PENDING: '待处理', 
    SUBMITTED: '已投递',
    VIEWED: '已查看', 
    INTERVIEW: '面试中', 
    ACCEPTED: '已录用', 
    HIRED: '已录用',
    REJECTED: '已拒绝', 
    WITHDRAWN: '已撤回' 
  }
  return map[status] || status
}

const statusType = (status) => {
  const map = { 
    PENDING: 'info', 
    SUBMITTED: 'info',
    VIEWED: '', 
    INTERVIEW: 'warning', 
    ACCEPTED: 'success', 
    HIRED: 'success',
    REJECTED: 'danger', 
    WITHDRAWN: 'info' 
  }
  return map[status] || ''
}

onMounted(fetchApplications)
</script>

<style scoped>
.page-container { max-width: 1000px; }
</style>
