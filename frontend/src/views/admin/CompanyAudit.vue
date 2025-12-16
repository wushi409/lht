<template>
  <div class="company-audit p-6">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">企业审核</h2>
        </div>
      </template>

      <el-table :data="companies" style="width: 100%" v-loading="loading">
        <el-table-column prop="companyName" label="企业名称" />
        <el-table-column prop="industry" label="行业" />
        <el-table-column prop="contactPerson" label="联系人" />
        <el-table-column prop="contactPhone" label="联系电话" />
        <el-table-column prop="createdAt" label="注册时间">
           <template #default="scope">
             {{ new Date(scope.row.createdAt).toLocaleDateString() }}
           </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button size="small" type="success" @click="handleReview(scope.row, true)">通过</el-button>
            <el-button size="small" type="danger" @click="openRejectDialog(scope.row)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="30%">
      <el-input v-model="rejectReason" placeholder="请输入拒绝原因" type="textarea" :rows="3" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject">确定拒绝</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminApi } from '@/api/admin'
import { ElMessage } from 'element-plus'

const companies = ref([])
const loading = ref(false)
const rejectDialogVisible = ref(false)
const rejectReason = ref('')
const currentCompany = ref(null)

const fetchPending = async () => {
  loading.value = true
  try {
    // Check if adminApi has getPendingCompanies, otherwise import from company.js
    // Based on previous read, admin.js has getPendingCompanies
    const data = await adminApi.getPendingCompanies()
    companies.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleReview = async (row, approved) => {
  try {
    await adminApi.reviewCompany(row.id, approved, approved ? 'Approved' : rejectReason.value)
    ElMessage.success(approved ? '已通过' : '已拒绝')
    rejectDialogVisible.value = false
    fetchPending()
  } catch (error) {
    console.error(error)
  }
}

const openRejectDialog = (row) => {
  currentCompany.value = row
  rejectReason.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = () => {
  if (!currentCompany.value) return
  handleReview(currentCompany.value, false)
}

onMounted(() => {
  fetchPending()
})
</script>
