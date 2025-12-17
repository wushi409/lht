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

      <div class="login-mode">
        <el-radio-group v-model="loginMethod" size="small">
          <el-radio-button label="password">密码登录</el-radio-button>
          <el-radio-button label="code">验证码登录</el-radio-button>
        </el-radio-group>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin" class="login-form">
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            :placeholder="activeTab === 'student' ? '学号/手机号' : (activeTab === 'company' ? '企业账号' : '管理员账号')" 
            size="large" 
            prefix-icon="User" 
          />
        </el-form-item>
        <el-form-item v-if="loginMethod === 'password'" prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="密码" 
            size="large" 
            prefix-icon="Lock" 
            show-password 
          />
        </el-form-item>
        <el-form-item v-else prop="code">
          <el-row :gutter="8" style="width: 100%">
            <el-col :span="16">
              <el-input
                v-model="form.code"
                placeholder="请输入验证码"
                size="large"
              />
            </el-col>
            <el-col :span="8">
              <el-button type="primary" size="large" :loading="sendingCode" @click="handleSendCode" style="width: 100%">
                发送验证码
              </el-button>
            </el-col>
          </el-row>
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
        <router-link to="/reset-password" v-if="loginMethod === 'code'">忘记密码？</router-link>
        <el-button link type="info" size="small" @click="$router.push('/')">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login, sendCode, codeLogin } from '@/api/auth'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)
const activeTab = ref('student')
const loginMethod = ref('password')

const sendingCode = ref(false)

const form = reactive({ username: '', password: '', code: '' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ 
    validator: (rule, value, callback) => {
      if (loginMethod.value === 'password' && !value) {
        callback(new Error('请输入密码'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }],
  code: [{
    validator: (rule, value, callback) => {
      if (loginMethod.value === 'code' && !value) {
        callback(new Error('请输入验证码'))
      } else {
        callback()
      }
    },
    trigger: 'blur'
  }]
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
    let data
    if (loginMethod.value === 'password') {
      data = await login({ username: form.username, password: form.password })
    } else {
      data = await codeLogin({ username: form.username, code: form.code })
    }
    
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

const handleSendCode = async () => {
  if (!form.username) {
    return ElMessage.warning('请先输入账号或手机号')
  }
  sendingCode.value = true
  try {
    const code = await sendCode({ username: form.username, scene: 'LOGIN' })
    // 演示环境直接展示验证码，正式环境可去掉 code 显示
    ElMessage.success(`验证码已发送：${code}`)
  } catch (e) {
    console.error(e)
  } finally {
    sendingCode.value = false
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

.login-mode {
  display: flex;
  justify-content: center;
  margin-bottom: 12px;
}

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
