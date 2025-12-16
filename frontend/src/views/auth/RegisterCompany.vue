<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>🏢 企业入驻申请</h2>
        <p>加入校园双选会，招募优秀人才</p>
      </div>
      
      <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-position="top" size="large">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="信用代码" prop="username">
              <el-input v-model="registerForm.username" placeholder="统一社会信用代码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="企业名称" prop="companyName">
              <el-input v-model="registerForm.companyName" placeholder="企业全称" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="设置登录密码" show-password />
        </el-form-item>
        
        <el-form-item label="所属行业" prop="industry">
          <el-select v-model="registerForm.industry" placeholder="请选择行业" style="width: 100%">
            <el-option label="互联网/IT" value="互联网" />
            <el-option label="金融/银行" value="金融" />
            <el-option label="教育/培训" value="教育" />
            <el-option label="制造业" value="制造业" />
            <el-option label="医疗健康" value="医疗健康" />
            <el-option label="房地产/建筑" value="房地产" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="registerForm.contactPerson" placeholder="姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="registerForm.contactPhone" placeholder="手机号" />
            </el-form-item>
          </el-col>
           <el-col :span="8">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="registerForm.contactEmail" placeholder="邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="企业简介" prop="description">
          <el-input v-model="registerForm.description" type="textarea" :rows="3" placeholder="简要介绍企业发展情况、主营业务等" />
        </el-form-item>
        
        <el-form-item class="submit-item">
          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister">提交入驻申请</el-button>
        </el-form-item>
        
        <div class="login-link">
          <p>已有账号？ <router-link to="/login">立即登录</router-link></p>
        </div>
      </el-form>
    </div>
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
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择行业', trigger: 'change' }]
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
  background-color: var(--bg-body);
  background-image: 
    radial-gradient(at 0% 0%, hsla(253,16%,7%,1) 0, transparent 50%), 
    radial-gradient(at 50% 0%, hsla(225,39%,30%,1) 0, transparent 50%), 
    radial-gradient(at 100% 0%, hsla(339,49%,30%,1) 0, transparent 50%);
  padding: 40px 20px;
}

.register-box {
  width: 100%;
  max-width: 600px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 2rem;
}

.register-header h2 {
  font-size: 1.8rem;
  color: var(--text-main);
  margin-bottom: 0.5rem;
  font-weight: 700;
}

.register-header p {
  color: var(--text-secondary);
}

.submit-btn {
  width: 100%;
  padding: 12px;
  font-weight: 600;
  margin-top: 1rem;
}

.login-link {
  text-align: center;
  margin-top: 1.5rem;
  font-size: 0.9rem;
  color: var(--text-regular);
}

.login-link a {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
}

.login-link a:hover {
  text-decoration: underline;
}

/* Override Element Plus inputs */
:deep(.el-input__wrapper) {
  box-shadow: 0 0 0 1px var(--border-color) inset;
  padding: 8px 15px;
}

:deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px var(--text-secondary) inset;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--primary-color) inset !important;
}

:deep(.el-select .el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px var(--primary-color) inset !important;
}
</style>
