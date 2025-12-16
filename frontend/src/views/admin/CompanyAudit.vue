<template>
  <div class="page-container">
    <div class="header-row">
      <h2>企业审核</h2>
      <el-button type="primary" @click="fetchList" :loading="loading">刷新</el-button>
    </div>
    <el-card shadow="never" v-loading="loading">
      <el-table :data="companies" style="width: 100%">
        <el-table-column prop="name" label="企业名称" min-width="160" />
        <el-table-column prop="creditCode" label="统一社会信用码" min-width="180" />
        <el-table-column prop="industry" label="行业" width="140" />
        <el-table-column prop="scale" label="规模" width="120" />
        <el-table-column prop="status" label="状态" width="120">
          <template #default="scope">
            <el-tag type="warning">待审核</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="scope">
            <el-button size="small" type="success" @click="approve(scope.row)">通过</el-button>
            <el-button size="small" type="danger" @click="reject(scope.row)">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && companies.length === 0" description="暂无待审核企业" />
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { adminApi } from '@/api/admin'

const companies = ref([])
const loading = ref(false)

const fetchList = async () => {
  loading.value = true
  try {
    const data = await adminApi.getPendingCompanies()
    companies.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const approve = async (row) => {
  try {
    await adminApi.reviewCompany(row.id, true, null)
    ElMessage.success('已通过')
    fetchList()
  } catch (error) {
    console.error(error)
  }
}

const reject = (row) => {
  ElMessageBox.prompt('请输入驳回原因', '驳回申请', {
    confirmButtonText: '确认驳回',
    cancelButtonText: '取消',
    inputPlaceholder: '例如：资料不完整'
  })
    .then(async ({ value }) => {
      await adminApi.reviewCompany(row.id, false, value || '')
      ElMessage.success('已驳回')
      fetchList()
    })
    .catch(() => {})
}

onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.page-container {
  max-width: 1100px;
  margin: 0 auto;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
</style>
