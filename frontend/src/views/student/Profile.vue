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
          <el-input v-model="form.studentId" disabled></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="form.gender">
            <el-option label="男" value="MALE" />
            <el-option label="女" value="FEMALE" />
          </el-select>
        </el-form-item>
        <el-form-item label="学院">
          <el-input v-model="form.college"></el-input>
        </el-form-item>
        <el-form-item label="专业">
          <el-input v-model="form.major"></el-input>
        </el-form-item>
        <el-form-item label="入学年份">
           <el-input v-model="form.enrollmentYear"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email"></el-input>
        </el-form-item>
        <el-form-item label="求职意向">
          <el-input v-model="form.intention" type="textarea" :rows="3"></el-input>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="form.introduction" type="textarea" :rows="4"></el-input>
        </el-form-item>
        <el-form-item label="简历链接">
          <el-input v-model="form.resumeUrl" placeholder="请输入在线简历链接或上传文件后自动生成"></el-input>
          <!-- Upload placeholder -->
        </el-form-item>
      </el-form>
      <el-skeleton v-else :rows="10" animated />
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
    await updateProfile(form.value)
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
