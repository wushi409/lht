<template>
  <div class="audit-container">
    <h2>企业审核管理</h2>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="待审核" name="PENDING">
        <el-badge :value="pendingCount" :hidden="pendingCount === 0" class="tab-badge" />
      </el-tab-pane>
      <el-tab-pane label="已通过" name="APPROVED" />
      <el-tab-pane label="已拒绝" name="REJECTED" />
    </el-tabs>

    <el-card shadow="never" v-loading="loading">
      <el-table :data="companies" style="width: 100%">
        <el-table-column prop="name" label="企业名称" min-width="180" />
        <el-table-column prop="creditCode" label="信用代码" width="180" />
        <el-table-column prop="industry" label="行业" width="120" />
        <el-table-column prop="scale" label="规模" width="120" />
        <el-table-column label="联系人" width="120">
          <template #default="scope">
            {{ scope.row.contactName || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="联系电话" width="140">
          <template #default="scope">
            {{ scope.row.contactPhone || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button 
              v-if="scope.row.status === 'PENDING'" 
              size="small" 
              type="success" 
              @click="approve(scope.row)"
            >通过</el-button>
            <el-button 
              v-if="scope.row.status === 'PENDING'" 
              size="small" 
              type="danger" 
              @click="showRejectDialog(scope.row)"
            >拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-empty v-if="!loading && companies.length === 0" description="暂无数据" />
    </el-card>

    <!-- 企业详情对话框 -->
    <el-dialog v-model="detailVisible" title="企业详情" width="70%">
      <el-descriptions :column="2" border v-if="currentCompany">
        <el-descriptions-item label="企业名称" :span="2">
          {{ currentCompany.name }}
        </el-descriptions-item>
        <el-descriptions-item label="统一社会信用代码" :span="2">
          {{ currentCompany.creditCode }}
        </el-descriptions-item>
        <el-descriptions-item label="企业规模">
          {{ currentCompany.scale || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="所属行业">
          {{ currentCompany.industry || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="企业简介" :span="2">
          {{ currentCompany.description || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系人">
          {{ currentCompany.contactName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ currentCompany.contactPhone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="联系邮箱" :span="2">
          {{ currentCompany.contactEmail || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="注册时间" :span="2">
          {{ formatDate(currentCompany.createdAt) }}
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(currentCompany.status)">
            {{ getStatusLabel(currentCompany.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="拒绝原因" v-if="currentCompany.rejectionReason">
          {{ currentCompany.rejectionReason }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button 
          v-if="currentCompany?.status === 'PENDING'" 
          type="success" 
          @click="approve(currentCompany)"
        >通过审核</el-button>
        <el-button 
          v-if="currentCompany?.status === 'PENDING'" 
          type="danger" 
          @click="showRejectDialog(currentCompany)"
        >拒绝审核</el-button>
      </template>
    </el-dialog>

    <!-- 拒绝原因对话框 -->
    <el-dialog v-model="rejectVisible" title="拒绝审核" width="500px">
      <el-form :model="rejectForm" label-width="100px">
        <el-form-item label="拒绝原因" required>
          <el-input 
            v-model="rejectForm.reason" 
            type="textarea" 
            :rows="4"
            placeholder="请输入拒绝原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" @click="confirmReject" :loading="rejecting">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const loading = ref(false)
const rejecting = ref(false)
const activeTab = ref('PENDING')
const companies = ref([])
const detailVisible = ref(false)
const rejectVisible = ref(false)
const currentCompany = ref(null)

const rejectForm = reactive({
  reason: ''
})

const statusMap = {
  PENDING: { label: '待审核', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' }
}

const pendingCount = computed(() => {
  return companies.value.filter(c => c.status === 'PENDING').length
})

const getStatusLabel = (status) => statusMap[status]?.label || status
const getStatusType = (status) => statusMap[status]?.type || 'info'

const fetchCompanies = async () => {
  loading.value = true
  try {
    const params = activeTab.value !== 'ALL' ? { status: activeTab.value } : {}
    const data = await request({
      url: '/admin/companies',
      method: 'get',
      params
    })
    companies.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  fetchCompanies()
}

const viewDetail = (company) => {
  currentCompany.value = company
  detailVisible.value = true
}

const approve = (company) => {
  ElMessageBox.confirm(
    `确定要通过【${company.name}】的审核吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(async () => {
    try {
      await request({
        url: `/admin/companies/${company.id}/approve`,
        method: 'post'
      })
      ElMessage.success('审核通过')
      detailVisible.value = false
      fetchCompanies()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const showRejectDialog = (company) => {
  currentCompany.value = company
  rejectForm.reason = ''
  detailVisible.value = false
  rejectVisible.value = true
}

const confirmReject = async () => {
  if (!rejectForm.reason.trim()) {
    ElMessage.warning('请输入拒绝原因')
    return
  }

  rejecting.value = true
  try {
    await request({
      url: `/admin/companies/${currentCompany.value.id}/reject`,
      method: 'post',
      data: { reason: rejectForm.reason }
    })
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    fetchCompanies()
  } catch (error) {
    console.error(error)
  } finally {
    rejecting.value = false
  }
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchCompanies()
})
</script>

<style scoped>
.audit-container {
  max-width: 1400px;
  margin: 0 auto;
}

.tab-badge {
  margin-left: 10px;
}
</style>
