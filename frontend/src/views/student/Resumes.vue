<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的简历</span>
          <el-button type="primary" @click="openCreate">新建简历</el-button>
        </div>
      </template>
      
      <el-table :data="resumes" v-loading="loading" stripe>
        <el-table-column label="简历名称" prop="title" min-width="150" />
        <el-table-column label="默认" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault" type="success" size="small">默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" min-width="120">
          <template #default="{ row }">{{ formatDate(row.updatedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <span class="action-link" @click="openEdit(row)">编辑</span>
            <template v-if="!row.isDefault">
              <el-divider direction="vertical" />
              <span class="action-link primary" @click="handleSetDefault(row)">设为默认</span>
            </template>
            <el-divider direction="vertical" />
            <span class="action-link danger" @click="handleDelete(row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && resumes.length === 0" description="暂无简历，点击上方按钮创建" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑简历' : '新建简历'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="简历名称" required>
          <el-input v-model="form.title" placeholder="如：前端开发简历" />
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="简要介绍自己" />
        </el-form-item>
        <el-form-item label="教育经历">
          <el-input v-model="form.education" type="textarea" :rows="3" placeholder="学校、专业、学历等" />
        </el-form-item>
        <el-form-item label="工作/实习">
          <el-input v-model="form.experience" type="textarea" :rows="3" placeholder="工作或实习经历" />
        </el-form-item>
        <el-form-item label="技能特长">
          <el-input v-model="form.skills" type="textarea" :rows="2" placeholder="掌握的技能" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { listResumes, createResume, updateResume, deleteResume, setDefaultResume } from '@/api/student'
import { ElMessage, ElMessageBox } from 'element-plus'

const resumes = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = reactive({ title: '', summary: '', education: '', experience: '', skills: '' })

const fetchResumes = async () => {
  loading.value = true
  try {
    resumes.value = await listResumes() || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  Object.assign(form, { title: '', summary: '', education: '', experience: '', skills: '' })
}

const openCreate = () => {
  resetForm()
  isEdit.value = false
  editId.value = null
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, row)
  isEdit.value = true
  editId.value = row.id
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.title) return ElMessage.warning('请输入简历名称')
  saving.value = true
  try {
    if (isEdit.value) {
      await updateResume(editId.value, form)
    } else {
      await createResume(form)
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchResumes()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

const handleSetDefault = async (row) => {
  try {
    await setDefaultResume(row.id)
    ElMessage.success('已设为默认')
    fetchResumes()
  } catch (e) {
    console.error(e)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该简历吗？', '提示', { type: 'warning' })
    await deleteResume(row.id)
    ElMessage.success('删除成功')
    fetchResumes()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

onMounted(fetchResumes)
</script>

<style scoped>
.page-container { max-width: 900px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
