<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <div>
            <el-button v-if="!editing" type="primary" @click="editing = true">编辑</el-button>
            <template v-else>
              <el-button @click="cancelEdit">取消</el-button>
              <el-button type="primary" :loading="saving" @click="saveProfile">保存</el-button>
            </template>
          </div>
        </div>
      </template>
      
      <el-form :model="form" label-width="80px" :disabled="!editing" v-loading="loading">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号">
              <el-input v-model="form.studentNo" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名">
              <el-input v-model="form.name" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院">
              <el-input v-model="form.college" placeholder="请输入学院" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getProfile, updateProfile } from '@/api/student'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const editing = ref(false)
const form = reactive({ studentNo: '', name: '', college: '', major: '', phone: '', email: '' })
const original = ref({})

const fetchProfile = async () => {
  loading.value = true
  try {
    const data = await getProfile()
    Object.assign(form, data)
    original.value = { ...data }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const cancelEdit = () => {
  Object.assign(form, original.value)
  editing.value = false
}

const saveProfile = async () => {
  saving.value = true
  try {
    const data = await updateProfile(form)
    Object.assign(form, data)
    original.value = { ...data }
    editing.value = false
    ElMessage.success('保存成功')
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

onMounted(fetchProfile)
</script>

<style scoped>
.page-container { max-width: 800px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
