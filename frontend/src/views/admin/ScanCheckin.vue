<template>
  <div class="page-container">
    <el-card>
      <template #header>签到管理</template>
      
      <el-tabs v-model="activeTab">
        <!-- 手动签到 -->
        <el-tab-pane label="手动签到" name="manual">
          <div class="manual-container">
            <el-form :inline="true">
              <el-form-item label="选择活动">
                <el-select v-model="manualEventId" placeholder="请选择活动" style="width: 300px;">
                  <el-option v-for="event in events" :key="event.id" :label="event.name" :value="event.id" />
                </el-select>
              </el-form-item>
            </el-form>

            <div v-if="manualEventId" style="margin-top: 30px;">
              <el-alert 
                title="请学生出示签到码，输入后点击签到" 
                type="info" 
                :closable="false"
                style="margin-bottom: 20px;"
              />
              
              <div class="checkin-input-box">
                <el-input 
                  v-model="checkinCodeInput" 
                  placeholder="请输入8位签到码" 
                  size="large"
                  maxlength="8"
                  style="width: 300px; font-size: 24px;"
                  @keyup.enter="handleManualCheckin"
                >
                  <template #append>
                    <el-button 
                      type="primary" 
                      :loading="checkingIn"
                      @click="handleManualCheckin"
                    >
                      签到
                    </el-button>
                  </template>
                </el-input>
              </div>

              <!-- 最近签到记录 -->
              <div v-if="recentCheckins.length > 0" style="margin-top: 30px;">
                <h3 style="margin-bottom: 15px; color: #666;">最近签到记录</h3>
                <el-table :data="recentCheckins" stripe max-height="400">
                  <el-table-column label="时间" width="160">
                    <template #default="{ row }">{{ formatDateTime(row.checkinTime) }}</template>
                  </el-table-column>
                  <el-table-column label="学号" prop="student.studentNo" width="120" />
                  <el-table-column label="姓名" prop="student.name" width="100" />
                  <el-table-column label="学院" prop="student.college" min-width="150" />
                  <el-table-column label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag type="success">已签到</el-tag>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </el-tab-pane>

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

            <div v-if="currentEvent" class="qr-display">
              <el-alert 
                title="学生扫码后可看到签到码，然后在自助签到中输入" 
                type="success" 
                :closable="false"
                style="margin-bottom: 20px;"
              />
              
              <!-- 活动签到码显示 -->
              <div style="background: #f0f9ff; padding: 40px; border-radius: 12px; margin-bottom: 30px;">
                <div style="font-size: 16px; color: #666; margin-bottom: 15px;">活动签到码</div>
                <div style="font-size: 56px; font-weight: bold; color: #1e40af; letter-spacing: 8px; font-family: 'Courier New', monospace;">
                  {{ currentEvent.checkinCode || '未生成' }}
                </div>
                <div style="font-size: 14px; color: #999; margin-top: 15px;">
                  可以口头告知或展示二维码
                </div>
              </div>
              
              <!-- 二维码 -->
              <div style="background: white; padding: 30px; border-radius: 12px; display: inline-block; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
                <QrcodeVue v-if="qrCodeData" :value="qrCodeData" :size="300" level="H" />
              </div>
              
              <p style="margin-top: 20px; font-size: 16px; color: #666;">
                活动：{{ currentEvent?.name }}
              </p>
              <p style="margin-top: 10px; color: #666; font-size: 14px;">
                📱 学生使用微信扫码可直接看到签到码数字
              </p>
              <p style="color: #999; font-size: 13px; margin-top: 5px;">
                然后在"自助签到"中输入此签到码即可完成签到
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

          <el-table v-if="statsEventId" :data="paginatedCheckinList" v-loading="loadingStats" stripe style="margin-top: 20px;">
            <el-table-column label="学号" prop="student.studentNo" width="120" />
            <el-table-column label="姓名" prop="student.name" width="100" />
            <el-table-column label="学院" prop="student.college" min-width="150" />
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
          
          <el-pagination
            v-if="statsEventId && checkinList.length > 0"
            v-model:current-page="statsCurrentPage"
            v-model:page-size="statsPageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="checkinList.length"
            layout="total, sizes, prev, pager, next, jumper"
            style="margin-top: 20px; justify-content: center"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import QrcodeVue from 'qrcode.vue'

const activeTab = ref('manual')
const events = ref([])

// 手动签到相关
const manualEventId = ref(null)
const checkinCodeInput = ref('')
const checkingIn = ref(false)
const recentCheckins = ref([])

// 二维码相关
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
const statsCurrentPage = ref(1)
const statsPageSize = ref(20)

const fetchEvents = async () => {
  try {
    events.value = await request.get('/events') || []
  } catch (e) {
    console.error(e)
  }
}

// 手动签到
const handleManualCheckin = async () => {
  if (!manualEventId.value) {
    ElMessage.warning('请先选择活动')
    return
  }
  
  if (!checkinCodeInput.value || checkinCodeInput.value.length !== 8) {
    ElMessage.warning('请输入8位签到码')
    return
  }
  
  checkingIn.value = true
  try {
    // 从签到码解析报名ID
    const registrationId = parseInt(checkinCodeInput.value.substring(0, 6))
    
    // 调用签到接口
    const data = await request.post(`/admin/checkin`, {
      eventId: manualEventId.value,
      registrationId: registrationId
    })
    
    ElMessage.success(`签到成功！学生：${data.studentName}`)
    
    // 添加到最近签到记录
    recentCheckins.value.unshift(data)
    if (recentCheckins.value.length > 10) {
      recentCheckins.value.pop()
    }
    
    // 清空输入
    checkinCodeInput.value = ''
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '签到失败，请检查签到码是否正确')
  } finally {
    checkingIn.value = false
  }
}

const generateQRCode = () => {
  if (!selectedEventId.value) {
    qrCodeData.value = ''
    return
  }
  
  currentEvent.value = events.value.find(e => e.id === selectedEventId.value)
  // 二维码内容：直接就是活动签到码，扫码后直接显示
  qrCodeData.value = currentEvent.value?.checkinCode || '未生成'
}

const paginatedCheckinList = computed(() => {
  const start = (statsCurrentPage.value - 1) * statsPageSize.value
  const end = start + statsPageSize.value
  return checkinList.value.slice(start, end)
})

const loadStats = async () => {
  if (!statsEventId.value) return
  
  loadingStats.value = true
  statsCurrentPage.value = 1 // 重置到第一页
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
.manual-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.checkin-input-box {
  text-align: center;
  padding: 40px;
  background: #f5f7fa;
  border-radius: 12px;
}

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
