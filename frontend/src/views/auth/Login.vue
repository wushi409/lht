<template>
  <div class="login-container flex justify-center items-center min-h-screen bg-gray-100">
    <el-card class="box-card w-full max-w-md">
      <template #header>
        <div class="card-header text-center">
          <h2 class="text-xl font-bold">登录 - 校园招聘会系统</h2>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0px">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="w-full" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
        <div class="flex justify-between text-sm">
          <router-link to="/register/student" class="text-blue-500 hover:underline">学生注册</router-link>
          <router-link to="/register/company" class="text-blue-500 hover:underline">企业入驻</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = () => {
  loginFormRef.value.validate(async valid => {
    if (valid) {
      loading.value = true
      try {
        const data = await login(loginForm)
        userStore.setToken(data.token)
        userStore.setRole(data.role)
        userStore.setUserInfo({ username: data.username, role: data.role })
        ElMessage.success('登录成功')
        
        if (data.role === 'STUDENT') {
          router.push('/student/profile')
        } else if (data.role === 'COMPANY') {
          router.push('/company/dashboard')
        } else if (data.role === 'ADMIN') {
          router.push('/admin/dashboard')
        } else {
          router.push('/')
        }
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
.login-container {
  background-image: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}
</style>
