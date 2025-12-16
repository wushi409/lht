<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>活动签到</span>
        </div>
      </template>

      <el-form :inline="true" style="margin-bottom: 16px">
        <el-form-item label="选择活动">
          <el-select v-model="selectedEventId" placeholder="选择活动" @change="fetchRegistrations" style="width: 250px">
            <el-option v-for="event in events" :key="event.id" :label="event.name" :value="event.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchKeyword" placeholder="搜索学生姓名/学号" clearable style="width: 200px" />
        </el-form-item>
      </el-form>
      
      <el-table :data="filteredRegistrations" v-loading="loading" stripe>
        <el-table-column label="学生姓名" min-width="100">
          <template #default="{ row }">{{ row.student?.name || '-' }}</template>
        </el-table-column>
        <el-table-column label="学号" min-width="120">
          <template #default="{ row }">{{ row.student?.studentNo || '-' }}</template>
        </el-table-column>
        <el-table-column label="学院" min-width="120">
          <template #default="{ row }">{{ row.student?.college || '-' }}</template>
        </el-table-column>
        <el-table-column label="报名时间" min-width="150">
          <template #default="{ row }">{{ formatDateTime(row.registeredAt) }}</template>
        </el-table-column>
        <el-table-column label="签到状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'CHECKED_IN' ? 'success' : 'info'">{{ row.status === 'CHECKED_IN' ? '已签到' : '未签到' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="签到时间" min-width="150">
          <template #default="{ row }">{{ row.checkinTime ? formatDateTime(row.checkinTime) : '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status !== 'CHECKED_IN'" type="primary" size="small" @click="handleCheckin(row)">签到</el-button>
            <span v-else style="color: #67c23a">✓</span>
          </template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && filteredRegistrations.length === 0" description="暂无报名记录" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'

const events = ref([])
const registrations = ref([])
const selectedEventId = ref(null)
const searchKeyword = ref('')
const loading = ref(false)

const filteredRegistrations = computed(() => {
  if (!searchKeyword.value) return registrations.value
  const kw = searchKeyword.value.toLowerCase()
  return registrations.value.filter(r => 
    r.student?.name?.toLowerCase().includes(kw) || 
    r.student?.studentNo?.toLowerCase().includes(kw)
  )
})

const fetchEvents = async () => {
  try {
    events.value = await request.get('/events') || []
    if (events.value.length > 0) {
      selectedEventId.value = events.value[0].id
      fetchRegistrations()
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchRegistrations = async () => {
  if (!selectedEventId.value) return
  loading.value = true
  try {
    // 获取该活动的所有报名记录
    const allRegs = await request.get('/admin/event-registrations', { params: { eventId: selectedEventId.value } }) || []
    registrations.value = allRegs
  } catch (e) {
    console.error(e)
    registrations.value = []
  } finally {
    loading.value = false
  }
}

const handleCheckin = async (row) => {
  try {
    await request.post(`/events/${row.id}/checkin`)
    ElMessage.success('签到成功')
    row.status = 'CHECKED_IN'
    row.checkinTime = new Date().toISOString()
  } catch (e) {
    console.error(e)
  }
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

onMounted(fetchEvents)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
