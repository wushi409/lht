<template>
  <div class="admin-checkin p-6">
    <el-card header="活动签到管理">
      <div class="flex gap-4 mb-6">
        <el-input v-model="registrationId" placeholder="请输入报名ID进行签到" class="w-64" />
        <el-button type="primary" @click="handleCheckin">签到</el-button>
      </div>
      
      <el-divider />
      
      <h3 class="text-lg font-bold mb-4">今日活动</h3>
      <el-table :data="events" v-loading="loading">
        <el-table-column prop="title" label="活动名称" />
        <el-table-column prop="startTime" label="开始时间">
           <template #default="scope">
             {{ new Date(scope.row.startTime).toLocaleString() }}
           </template>
        </el-table-column>
        <el-table-column prop="location" label="地点" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const registrationId = ref('')
const events = ref([])
const loading = ref(false)

const handleCheckin = async () => {
  if (!registrationId.value) {
    ElMessage.warning('请输入ID')
    return
  }
  try {
    await request.post(`/events/${registrationId.value}/checkin`)
    ElMessage.success('签到成功')
    registrationId.value = ''
  } catch (error) {
    console.error(error)
  }
}

const fetchEvents = async () => {
  loading.value = true
  try {
    const data = await request.get('/events')
    events.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchEvents()
})
</script>
