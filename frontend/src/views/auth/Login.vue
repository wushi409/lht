<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-left">
        <div class="login-welcome">
          <h2>欢迎回来</h2>
          <p>登录以继续访问您的个人中心</p>
        </div>
      </div>
      <div class="login-right">
        <div class="login-form-wrapper">
          <div class="login-header">
            <h2>双选会系统</h2>
            <p>账号登录</p>
          </div>
          
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" size="large" label-position="top">
            <el-form-item label="用户名/学号" prop="username">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User" />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin">立即登录</el-button>
            </el-form-item>
          </el-form>
          
          <div class="login-footer">
            <p>还没有账号？</p>
            <div class="register-links">
              <router-link to="/register/student">学生注册</router-link>
              <span class="divider">|</span>
              <router-link to="/register/company">企业入驻</router-link>
            </div>
          </div>
        </div>
      </div>
    </div>
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
          router.push('/student/dashboard') // Ensure this points to dashboard
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
  background-color: var(--bg-body);
  background-image: 
    radial-gradient(at 0% 0%, hsla(253,16%,7%,1) 0, transparent 50%), 
    radial-gradient(at 50% 0%, hsla(225,39%,30%,1) 0, transparent 50%), 
    radial-gradient(at 100% 0%, hsla(339,49%,30%,1) 0, transparent 50%);
  padding: 20px;
}

.login-box {
  display: flex;
  width: 900px;
  height: 550px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  overflow: hidden;
}

.login-left {
  flex: 1;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: white;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.login-welcome {
  position: relative;
  z-index: 1;
  text-align: center;
}

.login-welcome h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.login-welcome p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.login-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: white;
}

.login-form-wrapper {
  width: 100%;
  max-width: 320px;
}

.login-header {
  margin-bottom: 2rem;
  text-align: center;
}

.login-header h2 {
  font-size: 1.5rem;
  color: var(--text-main);
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.login-header p {
  color: var(--text-secondary);
}

.submit-btn {
  width: 100%;
  padding: 12px;
  font-weight: 600;
  letter-spacing: 1px;
}

.login-footer {
  margin-top: 2rem;
  text-align: center;
  font-size: 0.875rem;
}

.login-footer p {
  color: var(--text-secondary);
  margin-bottom: 0.5rem;
}

.register-links {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
}

.register-links a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.register-links a:hover {
  color: var(--primary-hover);
  text-decoration: underline;
}

.divider {
  color: var(--border-color);
}

@media (max-width: 768px) {
  .login-box {
    flex-direction: column;
    height: auto;
    width: 100%;
  }
  .login-left {
    padding: 30px 20px;
  }
  .login-right {
    padding: 40px 20px;
  }
}
</style>
