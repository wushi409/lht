<template>
  <div class="register-page">
    <div class="register-card">
      <div class="brand-text">
        <h2 class="title">企业入驻</h2>
        <p class="subtitle">Company Registration</p>
        <p class="tip">提交后需等待管理员审核</p>
      </div>
      
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="统一社会信用代码" prop="creditCode">
          <el-input v-model="form.creditCode" placeholder="18位统一社会信用代码（用于登录）" maxlength="18" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" placeholder="登录密码" show-password />
        </el-form-item>
        <el-form-item label="企业名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入企业全称" />
        </el-form-item>
        <el-form-item label="行业" prop="industry">
          <el-select v-model="form.industry" placeholder="请选择行业" style="width: 100%">
            <el-option label="互联网" value="互联网" />
            <el-option label="金融" value="金融" />
            <el-option label="教育" value="教育" />
            <el-option label="制造业" value="制造业" />
            <el-option label="医疗健康" value="医疗健康" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="企业规模" prop="scale">
          <el-select v-model="form.scale" placeholder="请选择规模" style="width: 100%">
            <el-option label="1-50人" value="1-50人" />
            <el-option label="50-200人" value="50-200人" />
            <el-option label="200-500人" value="200-500人" />
            <el-option label="500-1000人" value="500-1000人" />
            <el-option label="1000人以上" value="1000人以上" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="form.contactName" placeholder="HR联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="联系电话" />
        </el-form-item>
        <el-form-item label="联系邮箱" prop="contactEmail">
          <el-input v-model="form.contactEmail" placeholder="联系邮箱" />
        </el-form-item>
        <el-form-item label="企业简介">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="简要介绍企业" />
        </el-form-item>
        <el-form-item class="mt-4">
          <el-button type="primary" size="large" :loading="loading" @click="handleRegister" style="width: 100%">提交申请</el-button>
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
import { registerCompany } from '@/api/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  creditCode: '', password: '', name: '', industry: '', scale: '',
  contactName: '', contactPhone: '', contactEmail: '', description: ''
})

const rules = {
  creditCode: [
    { required: true, message: '请输入统一社会信用代码', trigger: 'blur' },
    { len: 18, message: '统一社会信用代码为18位', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, message: '密码至少6位', trigger: 'blur' }],
  name: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
  industry: [{ required: true, message: '请选择行业', trigger: 'change' }],
  contactName: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await registerCompany(form)
    ElMessage.success('申请已提交，请等待审核')
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
  width: 460px;
  max-height: 90vh;
  overflow-y: auto;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.brand-text { text-align: center; margin-bottom: 20px; }
.title { margin: 0; color: #1e40af; font-size: 24px; font-weight: 700; }
.subtitle { margin: 8px 0 0; color: #666; font-size: 12px; text-transform: uppercase; }
.tip { color: #f59e0b; font-size: 13px; margin: 8px 0 0; }

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
