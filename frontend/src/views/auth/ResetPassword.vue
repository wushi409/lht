<template>
  <div class="reset-page">
    <div class="reset-card">
      <div class="brand-text">
        <h2 class="title">找回密码</h2>
        <p class="subtitle">Reset Password</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="账号/手机号" prop="username">
          <el-input v-model="form.username" placeholder="请输入学号 / 企业账号 / 管理员账号或学生手机号" />
        </el-form-item>
        <el-form-item label="验证码" prop="code">
          <el-row :gutter="8" style="width: 100%">
            <el-col :span="14">
              <el-input v-model="form.code" placeholder="请输入验证码" />
            </el-col>
            <el-col :span="10">
              <el-button type="primary" :loading="sendingCode" @click="handleSendCode" style="width: 100%">
                发送验证码
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item class="mt-4">
          <el-button type="primary" size="large" :loading="loading" @click="handleReset" style="width: 100%">
            重置密码
          </el-button>
        </el-form-item>
      </el-form>

      <div class="links">
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { sendCode, resetPassword } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const sendingCode = ref(false)

const form = reactive({ username: '', code: '', newPassword: '' })

const rules = {
  username: [{ required: true, message: '请输入账号或手机号', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ]
}

const handleSendCode = async () => {
  if (!form.username) {
    return ElMessage.warning('请先输入账号或手机号')
  }
  sendingCode.value = true
  try {
    const code = await sendCode({ username: form.username, scene: 'RESET_PASSWORD' })
    // 演示环境直接展示验证码
    ElMessage.success(`验证码已发送：${code}`)
  } catch (e) {
    console.error(e)
  } finally {
    sendingCode.value = false
  }
}

const handleReset = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await resetPassword({ username: form.username, code: form.code, newPassword: form.newPassword })
    ElMessage.success('密码重置成功，请使用新密码登录')
    router.push('/login')
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.reset-page {
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
.reset-page::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(30, 64, 175, 0.8);
}

.reset-card {
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
