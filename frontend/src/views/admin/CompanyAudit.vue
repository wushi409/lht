<template>
  <div class="page-container">
    <el-card>
      <template #header>企业审核</template>
      
      <el-table :data="companies" v-loading="loading" stripe>
        <el-table-column label="企业名称" prop="name" min-width="150" />
        <el-table-column label="行业" prop="industry" width="100" />
        <el-table-column label="规模" prop="scale" width="100" />
        <el-table-column label="联系人" prop="contactName" width="100" />
        <el-table-column label="联系电话" prop="contactPhone" width="130" />
        <el-table-column label="申请时间" min-width="110">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <span class="action-link" @click="viewDetail(row)">详情</span>
            <el-divider direction="vertical" />
            <span class="action-link success" @click="handleReview(row, true)">通过</span>
            <el-divider direction="vertical" />
            <span class="action-link danger" @click="handleReview(row, false)">拒绝</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && companies.length === 0" description="暂无待审核企业" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="企业详情" width="550px">
      <div v-if="current">
        <p><strong>企业名称：</strong>{{ current.name }}</p>
        <p><strong>行业：</strong>{{ current.industry }}</p>
        <p><strong>规模：</strong>{{ current.scale }}</p>
        <p><strong>联系人：</strong>{{ current.contactName }}</p>
        <p><strong>联系电话：</strong>{{ current.contactPhone }}</p>
        <p><strong>联系邮箱：</strong>{{ current.contactEmail }}</p>
        <p v-if="current.licenseUrl">
          <strong>资质证明：</strong>
          <el-link :href="current.licenseUrl" target="_blank" type="primary">点击查看</el-link>
        </p>
        <el-divider />
        <p><strong>企业简介：</strong></p>
        <p>{{ current.description || '暂无' }}</p>
      </div>
    </el-dialog>

    <el-dialog v-model="rejectVisible" title="拒绝原因" width="400px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请输入拒绝原因（可选）" />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="submitting" @click="confirmReject">确认拒绝</el-button>
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
const submitting = ref(false)
const dialogVisible = ref(false)
const rejectVisible = ref(false)
const current = ref(null)
const rejectReason = ref('')
const rejectTarget = ref(null)

const fetchCompanies = async () => {
  loading.value = true
  try {
    companies.value = await adminApi.getPendingCompanies() || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const viewDetail = (row) => {
  current.value = row
  dialogVisible.value = true
}

const handleReview = async (row, approved) => {
  if (approved) {
    submitting.value = true
    try {
      await adminApi.reviewCompany(row.id, true)
      ElMessage.success('已通过')
      fetchCompanies()
    } catch (e) {
      console.error(e)
    } finally {
      submitting.value = false
    }
  } else {
    rejectTarget.value = row
    rejectReason.value = ''
    rejectVisible.value = true
  }
}

const confirmReject = async () => {
  submitting.value = true
  try {
    await adminApi.reviewCompany(rejectTarget.value.id, false, rejectReason.value)
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    fetchCompanies()
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

onMounted(fetchCompanies)
</script>
