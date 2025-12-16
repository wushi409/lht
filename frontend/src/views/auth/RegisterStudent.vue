<template>
  <div class="register-container flex justify-center items-center min-h-screen bg-gray-100 py-10">
    <el-card class="box-card w-full max-w-lg">
      <template #header>
        <div class="card-header text-center">
          <h2 class="text-xl font-bold">学生注册</h2>
        </div>
      </template>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="registerForm.name" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="registerForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="registerForm.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="毕业年份" prop="graduationYear">
          <el-input-number v-model="registerForm.graduationYear" :min="2020" :max="2030" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="w-full" :loading="loading" @click="handleRegister">注册</el-button>
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
import { registerStudent } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  name: '',
  email: '',
  phone: '',
  major: '',
  graduationYear: 2024
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const handleRegister = () => {
  registerFormRef.value.validate(async valid => {
    if (valid) {
      loading.value = true
      try {
        await registerStudent(registerForm)
        ElMessage.success('注册成功，请登录')
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
