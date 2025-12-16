<template>
  <div class="applications-container p-6">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">我的申请</h2>
        </div>
      </template>

      <el-table :data="applications" style="width: 100%" v-loading="loading">
        <el-table-column prop="job.title" label="职位" />
        <el-table-column prop="job.companyName" label="公司" />
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="申请时间">
          <template #default="scope">
             {{ new Date(scope.row.createdAt).toLocaleDateString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-popconfirm title="确定撤回申请吗？" @confirm="handleWithdraw(scope.row.id)" v-if="scope.row.status === 'PENDING'">
              <template #reference>
                <el-button size="small" type="danger">撤回</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyApplications, withdrawApplication } from '@/api/student'
import { ElMessage } from 'element-plus'

const applications = ref([])
const loading = ref(false)

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

const handleWithdraw = async (id) => {
  try {
    await withdrawApplication(id)
    ElMessage.success('撤回成功')
    fetchApplications()
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
    'ACCEPTED': '已通过',
    'REJECTED': '已拒绝',
    'WITHDRAWN': '已撤回'
  }
  return map[status] || status
}

onMounted(() => {
  fetchApplications()
})
</script>
