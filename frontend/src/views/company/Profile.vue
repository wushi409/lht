<template>
  <div class="company-profile p-6">
    <el-card class="max-w-3xl mx-auto">
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">企业资料</h2>
          <el-button type="primary" v-if="!isEditing" @click="isEditing = true">编辑</el-button>
          <div v-else>
             <el-button @click="cancelEdit">取消</el-button>
             <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
          </div>
        </div>
      </template>

      <el-form :model="form" label-width="100px" :disabled="!isEditing">
        <el-form-item label="企业名称">
          <el-input v-model="form.companyName" />
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="form.industry" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactPerson" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.contactPhone" />
        </el-form-item>
        <el-form-item label="联系邮箱">
          <el-input v-model="form.contactEmail" />
        </el-form-item>
        <el-form-item label="企业简介">
          <el-input v-model="form.description" type="textarea" :rows="5" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-tag :type="form.approved ? 'success' : 'warning'">{{ form.approved ? '已审核' : '待审核' }}</el-tag>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { companyApi } from '@/api/company'
import { ElMessage } from 'element-plus'

const isEditing = ref(false)
const saving = ref(false)
const form = reactive({
  companyName: '',
  industry: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  description: '',
  approved: false
})
const originalData = ref({})

const fetchProfile = async () => {
  try {
    const data = await companyApi.getProfile()
    Object.assign(form, data)
    originalData.value = JSON.parse(JSON.stringify(data))
  } catch (error) {
    console.error(error)
  }
}

const cancelEdit = () => {
  Object.assign(form, originalData.value)
  isEditing.value = false
}

const saveProfile = async () => {
  saving.value = true
  try {
    const data = await companyApi.updateProfile(form)
    Object.assign(form, data)
    originalData.value = JSON.parse(JSON.stringify(data))
    isEditing.value = false
    ElMessage.success('保存成功')
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
