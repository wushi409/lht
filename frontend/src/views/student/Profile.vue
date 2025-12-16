<template>
  <div class="profile-container p-6">
    <el-card class="max-w-3xl mx-auto">
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">个人资料</h2>
          <el-button type="primary" v-if="!isEditing" @click="isEditing = true">编辑</el-button>
          <div v-else>
            <el-button @click="cancelEdit">取消</el-button>
            <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
          </div>
        </div>
      </template>
      
      <el-form :model="form" label-width="100px" :disabled="!isEditing">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="form.major" />
        </el-form-item>
        <el-form-item label="毕业年份">
          <el-input-number v-model="form.graduationYear" :min="2020" :max="2030" />
        </el-form-item>
        <el-form-item label="简历链接">
           <el-input v-model="form.resumeUrl" placeholder="简历文件链接" />
        </el-form-item>
        <el-form-item label="自我介绍">
          <el-input v-model="form.bio" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProfile, updateProfile } from '@/api/student'
import { ElMessage } from 'element-plus'

const isEditing = ref(false)
const saving = ref(false)
const form = reactive({
  username: '',
  name: '',
  email: '',
  phone: '',
  major: '',
  graduationYear: null,
  resumeUrl: '',
  bio: ''
})

const originalData = ref({})

const fetchProfile = async () => {
  try {
    const data = await getProfile()
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
    const data = await updateProfile(form)
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
