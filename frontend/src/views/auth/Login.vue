<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-header">
          <h2>🎓 校园双选会系统</h2>
          <p>欢迎登录</p>
        </div>
      </template>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0px">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" show-password size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">登录</el-button>
        </el-form-item>
        <div class="register-links">
          <router-link to="/register/student">学生注册</router-link>
          <router-link to="/register/company">企业入驻</router-link>
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
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
}

.login-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.login-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.register-links {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.register-links a {
  color: var(--primary-color);
  text-decoration: none;
}

.register-links a:hover {
  text-decoration: underline;
}
</style>
