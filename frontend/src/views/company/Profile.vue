<template>
  <div class="profile-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业信息</span>
          <el-button type="primary" size="small" @click="editMode = true" v-if="!editMode">编辑</el-button>
          <div v-else>
            <el-button size="small" @click="cancelEdit">取消</el-button>
            <el-button type="primary" size="small" @click="saveProfile" :loading="saving">保存</el-button>
          </div>
        </div>
      </template>

      <el-form :model="companyInfo" label-width="120px" :disabled="!editMode" v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业名称">
              <el-input v-model="companyInfo.name" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="信用代码">
              <el-input v-model="companyInfo.creditCode" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业规模">
              <el-select v-model="companyInfo.scale" placeholder="请选择" style="width: 100%">
                <el-option label="0-20人" value="0-20人" />
                <el-option label="20-99人" value="20-99人" />
                <el-option label="100-499人" value="100-499人" />
                <el-option label="500-999人" value="500-999人" />
                <el-option label="1000人以上" value="1000人以上" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属行业">
              <el-select v-model="companyInfo.industry" placeholder="请选择" style="width: 100%">
                <el-option label="互联网" value="互联网" />
                <el-option label="金融" value="金融" />
                <el-option label="教育" value="教育" />
                <el-option label="制造业" value="制造业" />
                <el-option label="房地产" value="房地产" />
                <el-option label="医疗健康" value="医疗健康" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="企业简介">
          <el-input 
            v-model="companyInfo.description" 
            type="textarea" 
            :rows="4"
            placeholder="请输入企业简介"
          />
        </el-form-item>

        <el-divider content-position="left">联系人信息</el-divider>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人">
              <el-input v-model="companyInfo.contactName" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话">
              <el-input v-model="companyInfo.contactPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系邮箱">
              <el-input v-model="companyInfo.contactEmail" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">审核状态</el-divider>

        <el-form-item label="审核状态">
          <el-tag :type="getStatusType(companyInfo.status)" size="large">
            {{ getStatusLabel(companyInfo.status) }}
          </el-tag>
          <span v-if="companyInfo.rejectionReason" style="margin-left: 20px; color: #F56C6C">
            拒绝原因：{{ companyInfo.rejectionReason }}
          </span>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { companyApi } from '@/api/company'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const editMode = ref(false)

const companyInfo = reactive({
  name: '',
  creditCode: '',
  scale: '',
  industry: '',
  description: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  status: '',
  rejectionReason: ''
})

const originalInfo = reactive({})

const statusMap = {
  PENDING: { label: '待审核', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' }
}

const getStatusLabel = (status) => statusMap[status]?.label || status
const getStatusType = (status) => statusMap[status]?.type || 'info'

const fetchProfile = async () => {
  loading.value = true
  try {
    const data = await companyApi.getProfile()
    Object.assign(companyInfo, data)
    Object.assign(originalInfo, data)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const saveProfile = async () => {
  saving.value = true
  try {
    await companyApi.updateProfile({
      scale: companyInfo.scale,
      industry: companyInfo.industry,
      description: companyInfo.description,
      contactName: companyInfo.contactName,
      contactPhone: companyInfo.contactPhone,
      contactEmail: companyInfo.contactEmail
    })
    ElMessage.success('保存成功')
    editMode.value = false
    Object.assign(originalInfo, companyInfo)
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
  }
}

const cancelEdit = () => {
  Object.assign(companyInfo, originalInfo)
  editMode.value = false
}

onMounted(() => {
  fetchProfile()
})
</script>

<style scoped>
.profile-container {
  max-width: 900px;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
