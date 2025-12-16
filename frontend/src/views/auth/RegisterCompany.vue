<template>
  <div class="register-container flex justify-center items-center min-h-screen bg-gray-100 py-10">
    <el-card class="box-card w-full max-w-lg">
      <template #header>
        <div class="card-header text-center">
          <h2 class="text-xl font-bold">企业注册申请</h2>
        </div>
      </template>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="企业名称" prop="companyName">
          <el-input v-model="registerForm.companyName" placeholder="请输入企业全称" />
        </el-form-item>
        <el-form-item label="行业" prop="industry">
          <el-input v-model="registerForm.industry" placeholder="例如：互联网、金融" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="registerForm.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="registerForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="registerForm.contactEmail" placeholder="请输入联系邮箱" />
        </el-form-item>
        <el-form-item label="企业简介" prop="description">
          <el-input v-model="registerForm.description" type="textarea" :rows="3" placeholder="简要介绍企业情况" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="w-full" :loading="loading" @click="handleRegister">提交申请</el-button>
        </el-form-item>
        <div class="text-center text-sm">
          <router-link to="/login" class="text-blue-500 hover:underline">已有账号？去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerCompany } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  companyName: '',
  industry: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  description: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const handleRegister = () => {
  registerFormRef.value.validate(async valid => {
    if (valid) {
      loading.value = true
      try {
        await registerCompany(registerForm)
        ElMessage.success('申请提交成功，请等待管理员审核')
        router.push('/login')
      } catch (error) {
        console.error(error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  background-image: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}
</style>
