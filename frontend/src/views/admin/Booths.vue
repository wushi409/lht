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
            <el-option label="全部" :value="null" />
            <el-option v-for="fair in fairs" :key="fair.id" :label="fair.name" :value="fair.id" />
          </el-select>
        </el-form-item>
      </el-form>
      
      <el-table :data="booths" v-loading="loading" stripe>
        <el-table-column label="双选会" prop="jobFair.name" min-width="150" v-if="!selectedFairId" />
        <el-table-column label="展位号" prop="boothNo" width="100" />
        <el-table-column label="企业" min-width="150">
          <template #default="{ row }">{{ row.company?.name || '-' }}</template>
        </el-table-column>
        <el-table-column label="位置" prop="location" min-width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">
              <el-icon><edit /></el-icon> 编辑
            </el-button>
            <el-divider direction="vertical" />
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              <el-icon><delete /></el-icon> 删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && booths.length === 0" description="暂无展位数据" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑展位' : '添加展位'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="双选会" required>
          <el-select v-model="form.jobFairId" style="width:100%" :disabled="isEdit">
            <el-option v-for="fair in fairs" :key="fair.id" :label="fair.name" :value="fair.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="展位号" required>
          <el-input v-model="form.boothNo" placeholder="如：A01" />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" placeholder="展位位置描述" />
        </el-form-item>
        <el-form-item label="企业">
          <el-select v-model="form.companyId" placeholder="选择企业" style="width:100%" clearable filterable>
            <el-option v-for="company in companies" :key="company.id" :label="company.name" :value="company.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, Delete } from '@element-plus/icons-vue'

const fairs = ref([])
const booths = ref([])
const companies = ref([])
const selectedFairId = ref(null)
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = reactive({ jobFairId: null, boothNo: '', location: '', companyId: null })

const fetchFairs = async () => {
  try {
    fairs.value = await request.get('/job-fairs') || []
    // 默认选择"全部"
    selectedFairId.value = null
    fetchBooths()
  } catch (e) {
    console.error(e)
  }
}

const fetchBooths = async () => {
  loading.value = true
  try {
    if (selectedFairId.value) {
      // 选择了特定双选会
      booths.value = await request.get(`/job-fairs/${selectedFairId.value}/booths`) || []
    } else {
      // 选择"全部"
      booths.value = await request.get('/admin/booths') || []
    }
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchCompanies = async () => {
  try {
    companies.value = await request.get('/companies') || []
  } catch (e) {
    console.error(e)
  }
}

const openCreate = () => {
  isEdit.value = false
  editId.value = null
  form.jobFairId = selectedFairId.value
  form.boothNo = ''
  form.location = ''
  form.companyId = null
  dialogVisible.value = true
}

const openEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.jobFairId = row.jobFair?.id || selectedFairId.value
  form.boothNo = row.boothNo
  form.location = row.location
  form.companyId = row.company?.id || null
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.jobFairId || !form.boothNo) return ElMessage.warning('请填写必填项')
  saving.value = true
  try {
    if (isEdit.value) {
      await request.put(`/booths/${editId.value}`, form)
      ElMessage.success('修改成功')
    } else {
      await request.post('/booths', form)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchBooths()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该展位吗？', '提示', { type: 'warning' })
    await request.delete(`/booths/${row.id}`)
    ElMessage.success('删除成功')
    fetchBooths()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

onMounted(() => {
  fetchFairs()
  fetchCompanies()
})
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
