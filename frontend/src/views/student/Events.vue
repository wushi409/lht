<template>
  <div class="page-container">
    <el-card>
      <template #header>双选会活动</template>
      
      <el-tabs v-model="activeTab">
        <el-tab-pane label="活动列表" name="list">
          <el-table :data="events" v-loading="loading" stripe>
            <el-table-column label="活动名称" prop="name" min-width="180" />
            <el-table-column label="时间" min-width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.startTime) }} - {{ formatTime(row.endTime) }}
              </template>
            </el-table-column>
            <el-table-column label="地点" prop="location" min-width="120" />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button 
                  v-if="!isRegistered(row.id)" 
                  type="primary" 
                  size="small" 
                  @click="handleRegister(row)"
                >报名</el-button>
                <el-tag v-else type="success" size="small">已报名</el-tag>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loading && events.length === 0" description="暂无活动" />
        </el-tab-pane>
        
        <el-tab-pane label="我的报名" name="my">
          <el-table :data="myRegistrations" v-loading="loadingMy" stripe>
            <el-table-column label="活动名称" prop="event.name" min-width="180" />
            <el-table-column label="报名时间" min-width="150">
              <template #default="{ row }">{{ formatDateTime(row.createdAt || row.registeredAt) }}</template>
            </el-table-column>
            <el-table-column label="签到状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 'CHECKED_IN' ? 'success' : 'info'">
                  {{ row.status === 'CHECKED_IN' ? '已签到' : '未签到' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="{ row }">
                <template v-if="row.status === 'REGISTERED'">
                  <el-button type="primary" link size="small" @click="showQRCode(row)">
                    查看签到码
                  </el-button>
                  <el-divider direction="vertical" />
                  <el-button type="danger" link size="small" @click="handleCancel(row)">
                    取消报名
                  </el-button>
                </template>
                <span v-else style="color:#16a34a;font-size:12px;">已签到</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loadingMy && myRegistrations.length === 0" description="暂无报名记录" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 签到二维码弹窗 -->
    <el-dialog v-model="qrDialogVisible" title="签到二维码" width="400px" center>
      <div style="text-align: center;">
        <p style="margin-bottom: 16px; color: #666;">请扫描此二维码完成签到</p>
        <QrcodeVue v-if="qrCodeData" :value="qrCodeData" :size="250" level="H" />
        <p style="margin-top: 16px; font-size: 14px; color: #999;">
          活动：{{ currentQRRegistration?.event?.name }}
        </p>
        <p style="margin-top: 8px; font-size: 12px; color: #999;">
          您也可以在活动现场扫描管理员展示的签到二维码
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import QrcodeVue from 'qrcode.vue'

const activeTab = ref('list')
const events = ref([])
const myRegistrations = ref([])
const loading = ref(false)
const loadingMy = ref(false)

// 二维码相关
const qrDialogVisible = ref(false)
const qrCodeData = ref('')
const currentQRRegistration = ref(null)

const fetchEvents = async () => {
  loading.value = true
  try {
    events.value = await request.get('/events') || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const fetchMyRegistrations = async () => {
  loadingMy.value = true
  try {
    myRegistrations.value = await request.get('/students/me/registrations') || []
  } catch (e) {
    console.error(e)
  } finally {
    loadingMy.value = false
  }
}

const handleRegister = async (event) => {
  try {
    await ElMessageBox.confirm(`确定报名「${event.name}」吗？`, '提示')
    await request.post('/students/me/registrations', { eventId: event.id })
    ElMessage.success('报名成功')
    fetchMyRegistrations()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const handleCancel = async (row) => {
  try {
    await ElMessageBox.confirm('确定取消报名吗？', '提示', { type: 'warning' })
    await request.delete(`/students/me/registrations/${row.id}`)
    ElMessage.success('取消成功')
    fetchMyRegistrations()
  } catch (e) {
    if (e !== 'cancel') console.error(e)
  }
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

const formatTime = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

// 检查是否已报名某活动
const isRegistered = (eventId) => {
  return myRegistrations.value.some(r => r.event?.id === eventId || r.eventId === eventId)
}

// 显示签到二维码
const showQRCode = (registration) => {
  currentQRRegistration.value = registration
  // 生成签到链接，使用活动ID
  const baseUrl = window.location.origin
  const eventId = registration.event?.id || registration.eventId
  qrCodeData.value = `${baseUrl}/student-checkin?eventId=${eventId}`
  qrDialogVisible.value = true
}

watch(activeTab, (val) => {
  if (val === 'my') fetchMyRegistrations()
})

onMounted(() => {
  fetchEvents()
  fetchMyRegistrations()
})
</script>

<style scoped>
.page-container { max-width: 1000px; }
</style>
