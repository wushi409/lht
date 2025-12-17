<template>
  <div class="page-container">
    <el-card>
      <template #header>签到管理</template>
      
      <el-tabs v-model="activeTab">
        <!-- 活动签到二维码 -->
        <el-tab-pane label="签到二维码" name="qrcode">
          <div class="qr-container">
            <el-form :inline="true">
              <el-form-item label="选择活动">
                <el-select v-model="selectedEventId" placeholder="请选择活动" style="width: 300px;" @change="generateQRCode">
                  <el-option v-for="event in events" :key="event.id" :label="event.name" :value="event.id" />
                </el-select>
              </el-form-item>
            </el-form>

            <div v-if="qrCodeData" class="qr-display">
              <el-alert 
                title="请将此二维码展示给学生扫描签到" 
                type="success" 
                :closable="false"
                style="margin-bottom: 20px;"
              />
              <QrcodeVue :value="qrCodeData" :size="300" level="H" />
              <p style="margin-top: 16px; font-size: 16px; color: #666;">
                活动：{{ currentEvent?.name }}
              </p>
              <p style="color: #999; font-size: 14px;">
                学生使用微信或其他扫码工具扫描此二维码即可签到
              </p>
            </div>
          </div>
        </el-tab-pane>

        <!-- 签到统计 -->
        <el-tab-pane label="签到统计" name="stats">
          <el-form :inline="true">
            <el-form-item label="选择活动">
              <el-select v-model="statsEventId" placeholder="请选择活动" style="width: 300px;" @change="loadStats">
                <el-option v-for="event in events" :key="event.id" :label="event.name" :value="event.id" />
              </el-select>
            </el-form-item>
          </el-form>

          <div v-if="statsEventId" class="stats-box">
            <div class="stat-item">
              <div class="stat-num">{{ stats.totalRegistrations }}</div>
              <div class="stat-label">报名人数</div>
            </div>
            <div class="stat-item">
              <div class="stat-num">{{ stats.checkedInCount }}</div>
              <div class="stat-label">已签到</div>
            </div>
            <div class="stat-item">
              <div class="stat-num">{{ stats.notCheckedInCount }}</div>
              <div class="stat-label">未签到</div>
            </div>
          </div>

          <el-table v-if="statsEventId" :data="checkinList" v-loading="loadingStats" stripe style="margin-top: 20px;">
            <el-table-column label="学号" prop="student.studentId" width="120" />
            <el-table-column label="姓名" prop="student.name" width="100" />
            <el-table-column label="专业" prop="student.major" min-width="150" />
            <el-table-column label="报名时间" width="160">
              <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="签到时间" width="160">
              <template #default="{ row }">{{ row.checkinTime ? formatDateTime(row.checkinTime) : '-' }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'CHECKED_IN' ? 'success' : 'info'">
                  {{ row.status === 'CHECKED_IN' ? '已签到' : '未签到' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import QrcodeVue from 'qrcode.vue'

const activeTab = ref('qrcode')
const events = ref([])
const selectedEventId = ref(null)
const qrCodeData = ref('')
const currentEvent = ref(null)

// 统计相关
const statsEventId = ref(null)
const stats = ref({
  totalRegistrations: 0,
  checkedInCount: 0,
  notCheckedInCount: 0
})
const checkinList = ref([])
const loadingStats = ref(false)

const fetchEvents = async () => {
  try {
    events.value = await request.get('/events') || []
  } catch (e) {
    console.error(e)
  }
}

const generateQRCode = () => {
  if (!selectedEventId.value) {
    qrCodeData.value = ''
    return
  }
  
  currentEvent.value = events.value.find(e => e.id === selectedEventId.value)
  const baseUrl = window.location.origin
  qrCodeData.value = `${baseUrl}/student-checkin?eventId=${selectedEventId.value}`
}

const loadStats = async () => {
  if (!statsEventId.value) return
  
  loadingStats.value = true
  try {
    const data = await request.get(`/admin/events/${statsEventId.value}/registrations`)
    checkinList.value = data || []
    
    stats.value.totalRegistrations = checkinList.value.length
    stats.value.checkedInCount = checkinList.value.filter(r => r.status === 'CHECKED_IN').length
    stats.value.notCheckedInCount = stats.value.totalRegistrations - stats.value.checkedInCount
  } catch (e) {
    console.error(e)
  } finally {
    loadingStats.value = false
  }
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

onMounted(() => {
  fetchEvents()
})
</script>

<style scoped>
.qr-container {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.qr-display {
  margin-top: 30px;
  text-align: center;
  padding: 40px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stats-box {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px 40px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-num {
  font-size: 36px;
  font-weight: 700;
  color: #1e40af;
}

.stat-label {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}
</style>
