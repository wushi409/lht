<template>
  <div class="page-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <div>
            <h3 style="margin: 0">申请处理</h3>
            <p class="tip">后端未提供企业端申请列表，请输入申请ID进行状态更新。</p>
          </div>
        </div>
      </template>

      <el-form :model="form" label-width="120px" class="form-body">
        <el-form-item label="申请ID">
          <el-input v-model="form.id" placeholder="输入申请记录ID" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" placeholder="选择状态" style="width: 220px">
            <el-option
              v-for="item in statusOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.notes" type="textarea" :rows="3" placeholder="可填写面试安排、反馈等" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSubmit">更新状态</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { companyApi } from '@/api/company'
import { ApplicationStatus } from '@/stores/dict'

const saving = ref(false)
const form = reactive({
  id: '',
  status: 'INTERVIEW',
  notes: ''
})

const statusOptions = computed(() => Object.values(ApplicationStatus))

const handleSubmit = async () => {
  if (!form.id) {
    ElMessage.warning('请填写申请ID')
    return
  }
  saving.value = true
  try {
    await companyApi.updateApplicationStatus(form.id, {
      status: form.status,
      notes: form.notes
    })
    ElMessage.success('状态已更新')
  } catch (error) {
    console.error(error)
  } finally {
    saving.value = false
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
.tip {
  margin: 6px 0 0;
  color: #909399;
  font-size: 13px;
}
.form-body {
  max-width: 600px;
}
</style>
