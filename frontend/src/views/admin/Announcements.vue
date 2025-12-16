<template>
  <div class="admin-announcements p-6">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <h2 class="text-xl font-bold">公告管理</h2>
          <el-button type="primary" @click="showCreateDialog">发布公告</el-button>
        </div>
      </template>

      <el-table :data="announcements" v-loading="loading">
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="发布时间">
          <template #default="scope">
            {{ new Date(scope.row.createdAt).toLocaleDateString() }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-popconfirm title="确定删除吗？" @confirm="handleDelete(scope.row.id)">
              <template #reference>
                <el-button size="small" type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布公告" width="40%">
      <el-form :model="form">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">发布</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const announcements = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({
  title: '',
  content: ''
})

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const data = await request.get('/announcements')
    announcements.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const showCreateDialog = () => {
  form.title = ''
  form.content = ''
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!form.title || !form.content) {
    ElMessage.warning('请填写完整')
    return
  }
  try {
    await request.post('/announcements', form)
    ElMessage.success('发布成功')
    dialogVisible.value = false
    fetchAnnouncements()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = async (id) => {
  try {
    await request.delete(`/announcements/${id}`)
    ElMessage.success('删除成功')
    fetchAnnouncements()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  fetchAnnouncements()
})
</script>
