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
          <el-select v-model="selectedEventId" placeholder="选择活动" @change="handleEventChange" style="width: 250px">
            <el-option label="全部活动" :value="null" />
            <el-option v-for="event in events" :key="event.id" :label="event.name" :value="event.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input v-model="searchKeyword" placeholder="搜索学生姓名/学号/活动名称" clearable style="width: 250px" @clear="handleSearch" @keyup.enter="handleSearch">
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </el-form-item>
      </el-form>
      
      <el-table :data="registrations" v-loading="loading" stripe>
        <el-table-column label="活动名称" min-width="150" v-if="selectedEventId === null">
          <template #default="{ row }">{{ row.event?.name || '-' }}</template>
        </el-table-column>
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
      
      <el-empty v-if="!loading && registrations.length === 0" description="暂无报名记录" />
      
      <el-pagination
        v-if="totalElements > 0"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="totalElements"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px; justify-content: center"
        @current-change="fetchRegistrations"
        @size-change="fetchRegistrations"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'

const events = ref([])
const registrations = ref([])
const selectedEventId = ref(null)
const searchKeyword = ref('')
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(5)
const totalElements = ref(0)

// 直接使用后端返回的数据，无需前端过滤
const paginatedRegistrations = computed(() => registrations.value)

const fetchEvents = async () => {
  try {
    events.value = await request.get('/events') || []
    // 默认选择"全部活动"
    selectedEventId.value = null
    fetchRegistrations()
  } catch (e) {
    console.error(e)
  }
}

const fetchRegistrations = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value - 1, // 后端从0开始
      size: pageSize.value
    }
    if (selectedEventId.value !== null) {
      params.eventId = selectedEventId.value
    }
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value
    }
    
    const response = await request.get('/admin/event-registrations/page', { params })
    registrations.value = response.content || []
    totalElements.value = response.totalElements || 0
  } catch (e) {
    console.error(e)
    registrations.value = []
    totalElements.value = 0
  } finally {
    loading.value = false
  }
}

const handleEventChange = () => {
  currentPage.value = 1
  fetchRegistrations()
}

const handleSearch = () => {
  currentPage.value = 1 // 搜索时重置到第一页
  fetchRegistrations()
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
