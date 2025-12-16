<template>
  <div class="register-page">
    <div class="register-card">
      <div class="brand-text">
        <h2 class="title">学生注册</h2>
        <p class="subtitle">Student Registration</p>
      </div>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" />
        </el-form-item>
        <el-form-item label="学院" prop="college">
          <el-input v-model="form.college" placeholder="请输入学院" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item class="mt-4">
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width: 100%">注册</el-button>
        </el-form-item>
      </el-form>
      
      <div class="links">
        <span>已有账号？</span>
        <router-link to="/login">立即登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerStudent } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '', password: '', name: '', studentNo: '', college: '', phone: '', email: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await registerStudent(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
  background-image: url('https://static.vecteezy.com/system/resources/previews/002/099/717/original/mountain-beautiful-landscape-background-design-illustration-free-vector.jpg');
  background-size: cover;
  position: relative;
  padding: 20px;
}
.register-page::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(30, 64, 175, 0.8); /* Formal Blue Overlay */
}

.register-card {
  position: relative;
  width: 420px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.brand-text { text-align: center; margin-bottom: 30px; }
.title { margin: 0; color: #1e40af; font-size: 24px; font-weight: 700; }
.subtitle { margin: 8px 0 0; color: #666; font-size: 12px; text-transform: uppercase; }

.mt-4 { margin-top: 16px; }

.links {
  text-align: center;
  margin-top: 16px;
  color: #6b7280;
  font-size: 14px;
  padding-top: 16px;
  border-top: 1px solid #f0f0f0;
}
.links a { color: #1e40af; text-decoration: none; margin-left: 4px; font-weight: 600; }
.links a:hover { text-decoration: underline; }
</style>
