<template>
  <div class="page-container">
    <h2>我的投递记录</h2>
    <el-card shadow="never">
      <el-table :data="applications" style="width: 100%" v-loading="loading">
        <el-table-column prop="jobTitle" label="职位名称" min-width="150" />
        <el-table-column prop="companyName" label="公司名称" min-width="150" />
        <el-table-column prop="createTime" label="投递时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button 
              v-if="scope.row.status === 'PENDING'" 
              type="danger" 
              link 
              @click="handleWithdraw(scope.row)"
            >撤回</el-button>
             <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyApplications, withdrawApplication } from '@/api/student'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ApplicationStatus } from '@/stores/dict'

const applications = ref([])
const loading = ref(false)

const getStatusLabel = (status) => {
  return ApplicationStatus[status]?.label || status
}

const getStatusType = (status) => {
  return ApplicationStatus[status]?.type || 'info'
}

const fetchApplications = async () => {
  loading.value = true
  try {
    const data = await getMyApplications()
    applications.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleWithdraw = (row) => {
  ElMessageBox.confirm(
    '确定要撤回这条投递申请吗？撤回后可能无法再次投递该职位。',
    '警告',
    {
      confirmButtonText: '撤回',
      cancelButtonText: '取消',
      type: 'warning',
    }
  )
    .then(async () => {
      try {
        await withdrawApplication(row.id)
        ElMessage.success('已撤回')
        fetchApplications()
      } catch (error) {
        console.error(error)
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchApplications()
})
</script>

<style scoped>
.page-container {
  max-width: 1000px;
  margin: 0 auto;
}
</style>
