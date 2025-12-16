<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="register-header">
          <h2>🏢 企业入驻申请</h2>
          <p>加入校园双选会，招募优秀人才</p>
        </div>
      </template>
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-width="100px">
        <el-form-item label="信用代码" prop="username">
          <el-input v-model="registerForm.username" placeholder="统一社会信用代码" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="企业名称" prop="companyName">
          <el-input v-model="registerForm.companyName" placeholder="请输入企业全称" />
        </el-form-item>
        <el-form-item label="行业" prop="industry">
          <el-select v-model="registerForm.industry" placeholder="请选择行业" style="width: 100%">
            <el-option label="互联网" value="互联网" />
            <el-option label="金融" value="金融" />
            <el-option label="教育" value="教育" />
            <el-option label="制造业" value="制造业" />
            <el-option label="医疗健康" value="医疗健康" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="registerForm.contactPerson" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="registerForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="registerForm.contactEmail" placeholder="请输入联系邮箱" />
        </el-form-item>
        <el-form-item label="企业简介">
          <el-input v-model="registerForm.description" type="textarea" :rows="3" placeholder="简要介绍企业情况" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="handleRegister">提交申请</el-button>
        </el-form-item>
        <div class="login-link">
          <router-link to="/login">已有账号？去登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { registerCompany } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: '',
  companyName: '',
  industry: '',
  contactPerson: '',
  contactPhone: '',
  contactEmail: '',
  description: ''
})

const rules = {
  username: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  companyName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const handleRegister = () => {
  registerFormRef.value.validate(async valid => {
    if (valid) {
      loading.value = true
      try {
        await registerCompany(registerForm)
        ElMessage.success('申请提交成功，请等待管理员审核')
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
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 40px 20px;
}

.register-card {
  width: 550px;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.register-header {
  text-align: center;
}

.register-header h2 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
}

.register-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.login-link {
  text-align: center;
  font-size: 14px;
}

.login-link a {
  color: var(--primary-color);
  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
