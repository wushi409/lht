<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <h2>高校毕业生双选会信息管理系统</h2>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="0px">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="用户名/学号/手机号">
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="密码" show-password>
                <template #prefix><el-icon><Lock /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%" @click="handleLogin" :loading="loading">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="学生注册" name="register_student">
          <!-- Student Register Form Placeholder -->
          <div style="text-align: center; padding: 20px;">
            请联系管理员或前往注册页面
            <el-button type="text" @click="$router.push('/register/student')">去注册</el-button>
          </div>
        </el-tab-pane>
        <el-tab-pane label="企业入驻" name="register_company">
           <!-- Company Register Form Placeholder -->
           <div style="text-align: center; padding: 20px;">
            <el-button type="text" @click="$router.push('/register/company')">企业入驻申请</el-button>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
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
const activeTab = ref('login')

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
        const result = await userStore.loginAction(loginForm)
        console.log('登录返回数据:', result)
        console.log('Token:', userStore.token)
        console.log('Role:', userStore.role)
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
        console.error('登录错误:', error)
        ElMessage.error(error.message || '登录失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.login-card {
  width: 400px;
}
.card-header {
  text-align: center;
}
</style>
