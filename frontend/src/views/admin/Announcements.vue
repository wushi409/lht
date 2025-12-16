<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="openCreate">发布公告</el-button>
        </div>
      </template>
      
      <el-table :data="announcements" v-loading="loading" stripe>
        <el-table-column label="标题" prop="title" min-width="200" />
        <el-table-column label="发布时间" min-width="120">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <span class="action-link" @click="viewDetail(row)">查看</span>
            <el-divider direction="vertical" />
            <span class="action-link primary" @click="openEdit(row)">编辑</span>
            <el-divider direction="vertical" />
            <span class="action-link danger" @click="handleDelete(row)">删除</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && announcements.length === 0" description="暂无公告" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form v-if="isEdit" :model="form" label-width="60px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="公告标题" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="公告内容" />
        </el-form-item>
      </el-form>
      <div v-else>
        <h3 style="margin:0 0 8px">{{ current?.title }}</h3>
        <p style="color:#9ca3af;font-size:13px;margin:0 0 16px">{{ formatDate(current?.createdAt) }}</p>
        <el-divider />
        <div style="white-space:pre-wrap;line-height:1.8">{{ current?.content }}</div>
      </div>
      <template #footer v-if="isEdit">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">{{ editId ? '保存' : '发布' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'

const announcements = ref([])
const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const current = ref(null)
const form = reactive({ title: '', content: '' })

const dialogTitle = computed(() => {
  if (!isEdit.value) return '公告详情'
  return editId.value ? '编辑公告' : '发布公告'
})

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    announcements.value = await request.get('/announcements') || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  form.title = ''
  form.content = ''
  editId.value = null
  isEdit.value = true
  dialogVisible.value = true
}

const openEdit = (row) => {
  form.title = row.title
  form.content = row.content
  editId.value = row.id
  isEdit.value = true
  dialogVisible.value = true
}

const viewDetail = (row) => {
  current.value = row
  isEdit.value = false
  dialogVisible.value = true
}

const handleSave = async () => {
  if (!form.title || !form.content) return ElMessage.warning('请填写完整')
  saving.value = true
  try {
    if (editId.value) {
      await request.post(`/announcements/${editId.value}`, form)
      ElMessage.success('保存成功')
    } else {
      await request.post('/announcements', form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    fetchAnnouncements()
  } catch (e) {
    console.error(e)
  } finally {
    saving.value = false
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定删除该公告吗？', '提示', { type: 'warning' })
    await request.post(`/announcements/${row.id}/delete`)
    ElMessage.success('删除成功')
    fetchAnnouncements()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

onMounted(fetchAnnouncements)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
