<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>展位管理</span>
          <el-button type="primary" @click="openCreate">添加展位</el-button>
        </div>
      </template>

      <el-form :inline="true" style="margin-bottom: 16px">
        <el-form-item label="双选会">
          <el-select v-model="selectedFairId" placeholder="选择双选会" @change="fetchBooths">
            <el-option v-for="fair in fairs" :key="fair.id" :label="fair.name" :value="fair.id" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <el-table :data="booths" v-loading="loading" stripe>
        <el-table-column label="展位号" prop="boothNo" width="100" />
        <el-table-column label="企业" min-width="150">
          <template #default="{ row }">{{ row.company?.name || '-' }}</template>
        </el-table-column>
        <el-table-column label="位置" prop="location" min-width="120" />
        <el-table-column label="签到状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.checkedIn ? 'success' : 'info'">{{ row.checkedIn ? '已签到' : '未签到' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <span v-if="!row.checkedIn" class="action-link primary" @click="handleCheckin(row)">签到</span>
            <span v-else style="color:#9ca3af">-</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && booths.length === 0" description="暂无展位数据" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="添加展位" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="双选会" required>
          <el-select v-model="form.jobFairId" style="width:100%">
            <el-option v-for="fair in fairs" :key="fair.id" :label="fair.name" :value="fair.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="展位号" required>
          <el-input v-model="form.boothNo" placeholder="如：A01" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" placeholder="展位位置描述" />
        </el-form-item>
        <el-form-item label="企业ID">
          <el-input-number v-model="form.companyId" :min="1" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const fairs = ref([])
const booths = ref([])
const selectedFairId = ref(null)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const form = reactive({ jobFairId: null, boothNo: '', location: '', companyId: null })

const fetchFairs = async () => {
  try {
    fairs.value = await request.get('/job-fairs') || []
    if (fairs.value.length > 0) {
      selectedFairId.value = fairs.value[0].id
      fetchBooths()
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchBooths = async () => {
  if (!selectedFairId.value) return
  loading.value = true
  try {
    booths.value = await request.get(`/job-fairs/${selectedFairId.value}/booths`) || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  form.jobFairId = selectedFairId.value
  form.boothNo = ''
  form.location = ''
  form.companyId = null
  dialogVisible.value = true
}

const handleCreate = async () => {
  if (!form.jobFairId || !form.boothNo) return ElMessage.warning('请填写必填项')
  saving.value = true
  try {
    await request.post('/booths', form)
    ElMessage.success('添加成功')
    dialogVisible.value = false
    fetchBooths()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

const handleCheckin = async (row) => {
  try {
    await request.post(`/booths/${row.id}/checkin`)
    ElMessage.success('签到成功')
    fetchBooths()
  } catch (e) {
    console.error(e)
  }
}

onMounted(fetchFairs)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
