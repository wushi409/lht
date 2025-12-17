<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>企业信息</span>
          <div>
            <el-button v-if="!editing" type="primary" @click="editing = true">编辑</el-button>
            <template v-else>
              <el-button @click="cancelEdit">取消</el-button>
              <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
            </template>
          </div>
        </div>
      </template>
      
      <el-form :model="form" label-width="100px" :disabled="!editing" v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业名称">
              <el-input v-model="form.name" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="行业">
              <el-select v-model="form.industry" style="width:100%">
                <el-option label="互联网" value="互联网" />
                <el-option label="金融" value="金融" />
                <el-option label="教育" value="教育" />
                <el-option label="制造业" value="制造业" />
                <el-option label="医疗健康" value="医疗健康" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="企业规模">
              <el-select v-model="form.scale" style="width:100%">
                <el-option label="1-50人" value="1-50人" />
                <el-option label="50-200人" value="50-200人" />
                <el-option label="200-500人" value="200-500人" />
                <el-option label="500-1000人" value="500-1000人" />
                <el-option label="1000人以上" value="1000人以上" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系人">
              <el-input v-model="form.contactName" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.contactPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系邮箱">
              <el-input v-model="form.contactEmail" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="企业简介">
          <el-input v-model="form.description" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="企业 LOGO">
          <div class="logo-row">
            <input 
              ref="logoInput" 
              type="file" 
              accept="image/*" 
              style="display: none" 
              @change="handleLogoChange"
            />
            <el-button type="primary" :loading="uploadingLogo" @click="$refs.logoInput.click()">
              上传 LOGO
            </el-button>
            <div v-if="form.logoUrl" class="logo-preview">
              <img :src="getFileUrl(form.logoUrl)" alt="LOGO" style="max-width: 200px; max-height: 100px; border: 1px solid #ddd; border-radius: 4px;" />
            </div>
          </div>
        </el-form-item>
        <el-form-item label="资质证明">
          <div class="logo-row">
            <input 
              ref="licenseInput" 
              type="file" 
              style="display: none" 
              @change="handleLicenseChange"
            />
            <el-button type="primary" :loading="uploadingLicense" @click="$refs.licenseInput.click()">
              上传资质证明
            </el-button>
            <div v-if="form.licenseUrl" class="logo-preview">
              <el-link :href="getFileUrl(form.licenseUrl)" target="_blank" type="primary">查看资质证明</el-link>
              <img :src="getFileUrl(form.licenseUrl)" alt="资质证明" style="max-width: 200px; max-height: 100px; border: 1px solid #ddd; border-radius: 4px; margin-left: 10px;" />
            </div>
          </div>
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
const editing = ref(false)
const uploadingLogo = ref(false)
const uploadingLicense = ref(false)
const form = reactive({ name: '', industry: '', scale: '', contactName: '', contactPhone: '', contactEmail: '', description: '', logoUrl: '', licenseUrl: '' })
const original = ref({})

const fetchProfile = async () => {
  loading.value = true
  try {
    const data = await companyApi.getProfile()
    Object.assign(form, data)
    original.value = { ...data }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const logoInput = ref(null)
const licenseInput = ref(null)

const getFileUrl = (url) => {
  if (!url) return ''
  // 如果已经是完整URL，直接返回
  if (url.startsWith('http')) return url
  // 否则添加API前缀
  return '/api' + url
}

const handleLogoChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  uploadingLogo.value = true
  try {
    const data = await companyApi.uploadLogo(file)
    Object.assign(form, data)
    original.value = { ...data }
    ElMessage.success('LOGO 上传成功')
    event.target.value = '' // 清空input，允许重复上传同一文件
  } catch (e) {
    console.error(e)
    ElMessage.error('上传失败')
  } finally {
    uploadingLogo.value = false
  }
}

const handleLicenseChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  
  uploadingLicense.value = true
  try {
    const data = await companyApi.uploadLicense(file)
    Object.assign(form, data)
    original.value = { ...data }
    ElMessage.success('资质证明上传成功')
    event.target.value = '' // 清空input，允许重复上传同一文件
  } catch (e) {
    console.error(e)
    ElMessage.error('上传失败')
  } finally {
    uploadingLicense.value = false
  }
}

const cancelEdit = () => {
  Object.assign(form, original.value)
  editing.value = false
}

const saveProfile = async () => {
  saving.value = true
  try {
    const data = await companyApi.updateProfile(form)
    Object.assign(form, data)
    original.value = { ...data }
    editing.value = false
    ElMessage.success('保存成功')
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

onMounted(fetchProfile)
</script>

<style scoped>
.page-container { max-width: 900px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.logo-row { display: flex; align-items: center; gap: 12px; }
.logo-preview { font-size: 13px; color: #6b7280; }
</style>
