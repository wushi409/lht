<template>
  <div class="login-page">
    <div class="login-container">
      <!-- Left Side - Image/Brand -->
      <div class="login-banner">
        <div class="banner-content">
          <h1>开启职业新篇章</h1>
          <p>连接高校人才与优质企业，搭建高效沟通桥梁。</p>
        </div>
        <div class="banner-bg"></div>
      </div>

      <!-- Right Side - Form -->
      <div class="login-form-wrapper">
        <div class="form-header">
          <h2>欢迎回来</h2>
          <p>请登录您的账号</p>
        </div>

        <el-tabs v-model="activeTab" class="login-tabs">
          <el-tab-pane label="账号登录" name="password">
            <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
              <el-form-item prop="username">
                <el-input 
                  v-model="loginForm.username" 
                  placeholder="请输入用户名/学号" 
                  size="large"
                >
                  <template #prefix>
                    <el-icon><User /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input 
                  v-model="loginForm.password" 
                  type="password" 
                  placeholder="请输入密码" 
                  size="large"
                  show-password
                >
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>

              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                <a href="#" class="forgot-password">忘记密码？</a>
              </div>

              <el-button type="primary" :loading="loading" class="submit-btn" size="large" @click="handleLogin">
                登录
              </el-button>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="手机验证码" name="sms" disabled>
             <div class="placeholder-box">
               <p>验证码登录功能即将上线...</p>
             </div>
          </el-tab-pane>
        </el-tabs>

        <div class="form-footer">
          <p>还没有账号？ <router-link to="/register/student">立即注册</router-link></p>
          <div class="role-switch">
             <router-link to="/register/company" class="company-link">我是企业用户</router-link>
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
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const loginFormRef = ref(null)
const loading = ref(false)
const activeTab = ref('password')
const rememberMe = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.login(loginForm)
        ElMessage.success('登录成功')
        
        // Redirect based on role
        const role = userStore.role
        if (role === 'STUDENT') {
          router.push('/student/dashboard')
        } else if (role === 'COMPANY') {
          router.push('/company/dashboard')
        } else if (role === 'ADMIN') {
          router.push('/admin/dashboard')
        } else {
          router.push('/')
        }
      } catch (error) {
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f3f4f6;
  padding: 2rem;
}

.login-container {
  display: flex;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.1);
  overflow: hidden;
  width: 1000px;
  max-width: 100%;
  min-height: 600px;
}

.login-banner {
  flex: 1;
  background: linear-gradient(135deg, #4f46e5 0%, #4338ca 100%);
  padding: 3rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: white;
  position: relative;
}

.banner-content {
  z-index: 2;
}

.banner-content h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  font-weight: 700;
}

.banner-content p {
  font-size: 1.1rem;
  opacity: 0.9;
}

.login-form-wrapper {
  flex: 1;
  padding: 3rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.form-header {
  margin-bottom: 2rem;
  text-align: center;
}

.form-header h2 {
  font-size: 1.75rem;
  color: #111827;
  margin-bottom: 0.5rem;
}

.form-header p {
  color: #6b7280;
}

.login-tabs :deep(.el-tabs__nav-wrap::after) {
  height: 1px;
}

.login-form {
  margin-top: 1.5rem;
}

.submit-btn {
  width: 100%;
  margin-top: 1.5rem;
  font-weight: 600;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 0.5rem;
}

.forgot-password {
  color: #4f46e5;
  text-decoration: none;
  font-size: 0.875rem;
}

.form-footer {
  margin-top: 2rem;
  text-align: center;
  font-size: 0.875rem;
  color: #6b7280;
}

.form-footer a {
  color: #4f46e5;
  text-decoration: none;
  font-weight: 600;
}

.role-switch {
  margin-top: 1rem;
  padding-top: 1rem;
  border-top: 1px solid #e5e7eb;
}

.company-link {
  color: #6b7280;
  font-size: 0.875rem;
}

.company-link:hover {
  color: #4f46e5;
}

.placeholder-box {
  padding: 2rem;
  text-align: center;
  color: #9ca3af;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    min-height: auto;
  }
  
  .login-banner {
    padding: 2rem;
    display: none; /* Hide banner on mobile */
  }
  
  .login-form-wrapper {
    padding: 2rem;
  }
}
</style>
