<template>
  <div class="page-container">
    <el-card class="profile-card">
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" @click="handleSave" :loading="loading">保存修改</el-button>
        </div>
      </template>
      <el-form :model="form" label-width="100px" v-if="form">
        <el-form-item label="学号">
          <el-input v-model="form.studentNo" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="form.college" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="默认简历ID">
          <el-input v-model="form.defaultResumeId" placeholder="可留空" />
        </el-form-item>
      </el-form>
      <el-skeleton v-else :rows="6" animated />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getProfile, updateProfile } from '@/api/student'
import { ElMessage } from 'element-plus'

const form = ref(null)
const loading = ref(false)

onMounted(async () => {
  await fetchProfile()
})

const fetchProfile = async () => {
  try {
    const data = await getProfile()
    form.value = data
  } catch (error) {
    console.error(error)
  }
}

const handleSave = async () => {
  loading.value = true
  try {
    await updateProfile({
      name: form.value.name,
      college: form.value.college,
      phone: form.value.phone,
      email: form.value.email,
      defaultResumeId: form.value.defaultResumeId
    })
    ElMessage.success('保存成功')
    await fetchProfile()
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.page-container {
  max-width: 800px;
  margin: 0 auto;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
