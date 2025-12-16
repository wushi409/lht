<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <span style="font-size: 16px; font-weight: 600; margin-right: 12px">企业信息</span>
            <el-tag :type="statusTag.type">{{ statusTag.label }}</el-tag>
            <span v-if="form?.rejectionReason" class="reject-reason">驳回原因：{{ form.rejectionReason }}</span>
          </div>
          <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
        </div>
      </template>

      <el-form v-if="form" :model="form" label-width="120px" class="form-body">
        <el-form-item label="企业名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="统一社会信用码">
          <el-input v-model="form.creditCode" disabled />
        </el-form-item>
        <el-form-item label="规模">
          <el-input v-model="form.scale" placeholder="如：100-500人" />
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="form.industry" placeholder="如：互联网" />
        </el-form-item>
        <el-form-item label="公司简介">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="Logo链接">
          <el-input v-model="form.logoUrl" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactName" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input v-model="form.contactEmail" />
        </el-form-item>
      </el-form>
      <el-skeleton v-else :rows="8" animated />
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { companyApi } from '@/api/company'

const form = ref(null)
const loading = ref(false)
const saving = ref(false)

const statusMap = {
  PENDING: { label: '待审核', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已驳回', type: 'danger' }
}

const statusTag = computed(() => {
  const status = form.value?.status || 'PENDING'
  return statusMap[status] || { label: status, type: 'info' }
})

const fetchProfile = async () => {
  loading.value = true
  try {
    const data = await companyApi.getProfile()
    form.value = data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (!form.value) return
  saving.value = true
  try {
    await companyApi.updateProfile({
      name: form.value.name,
      scale: form.value.scale,
      industry: form.value.industry,
      description: form.value.description,
      logoUrl: form.value.logoUrl,
      contactName: form.value.contactName,
      contactPhone: form.value.contactPhone,
      contactEmail: form.value.contactEmail
    })
    ElMessage.success('保存成功')
    await fetchProfile()
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.page-container {
  max-width: 900px;
  margin: 0 auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.form-body {
  max-width: 700px;
}
.reject-reason {
  margin-left: 12px;
  color: #f56c6c;
}
</style>
