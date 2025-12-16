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
const form = reactive({ name: '', industry: '', scale: '', contactName: '', contactPhone: '', contactEmail: '', description: '' })
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
</style>
