<template>
  <div class="login-page">
    <div class="login-card">
      <div class="brand-text">
        <h2 class="title">大学就业服务网</h2>
        <p class="subtitle">University Employment Service Platform</p>
      </div>
      
      <el-tabs v-model="activeTab" class="login-tabs" stretch>
        <el-tab-pane label="学生登录" name="student"></el-tab-pane>
        <el-tab-pane label="企业登录" name="company"></el-tab-pane>
        <el-tab-pane label="管理员" name="admin"></el-tab-pane>
      </el-tabs>

      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            :placeholder="activeTab === 'company' ? '企业账号' : '学号/用户名'" 
            size="large" 
            prefix-icon="User" 
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码" 
            size="large" 
            prefix-icon="Lock" 
            show-password 
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" native-type="submit" style="width: 100%">
            登录
          </el-button>
        </el-form-item>
      </el-form>
      
      <div class="links">
        <router-link to="/register/student" v-if="activeTab === 'student'">注册学生账号</router-link>
        <router-link to="/register/company" v-if="activeTab === 'company'">企业入驻申请</router-link>
        <span v-if="activeTab === 'admin'"></span>
        <el-button link type="info" size="small" @click="$router.push('/')">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const activeTab = ref('student')

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

onMounted(() => {
  const role = route.query.role
  if (role) {
    if (role === 'company') activeTab.value = 'company'
    else if (role === 'admin') activeTab.value = 'admin'
    else activeTab.value = 'student'
  }
})

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    // Assuming backend login is unified or we might need to pass role hint if backend requires it
    // For now keeping unified login call
    const data = await login(form)
    
    // Check if role matches expected tab (optional validation)
    if (activeTab.value === 'student' && data.role !== 'STUDENT') {
       ElMessage.warning('非学生账号，已自动切换角色')
    }
    
    userStore.setToken(data.token)
    userStore.setRole(data.role)
    userStore.setUserInfo({ username: form.username, role: data.role })
    ElMessage.success('登录成功')
    
    const role = data.role
    if (role === 'ADMIN') router.push('/admin/stats')
    else if (role === 'COMPANY') router.push('/company/profile')
    else router.push('/student/jobs')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  background-image: url('https://static.vecteezy.com/system/resources/previews/002/099/717/original/mountain-beautiful-landscape-background-design-illustration-free-vector.jpg'); /* Optional: Replace with local or better formal bg */
  background-size: cover;
  position: relative;
}
.login-page::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(30, 64, 175, 0.8); /* Formal Blue Overlay */
}

.login-card {
  position: relative;
  width: 420px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.brand-text { text-align: center; margin-bottom: 30px; }
.title { margin: 0; color: #1e40af; font-size: 24px; font-weight: 700; }
.subtitle { margin: 8px 0 0; color: #666; font-size: 12px; text-transform: uppercase; }

.login-tabs { margin-bottom: 20px; }
:deep(.el-tabs__nav-wrap::after) { height: 1px; }

.links {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}
.links a { font-size: 14px; color: #1e40af; text-decoration: none; }
.links a:hover { text-decoration: underline; }
</style>
