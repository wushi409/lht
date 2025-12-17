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
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="280" fixed="right">
              <template #default="{ row }">
                <template v-if="row.status === 'REGISTERED'">
                  <el-button type="success" link size="small" @click="showSelfCheckin(row)">
                    <el-icon><CircleCheck /></el-icon> 自助签到
                  </el-button>
                  <el-divider direction="vertical" />
                  <el-button type="primary" link size="small" @click="showQRCode(row)">
                    <el-icon><qrcode /></el-icon> 查看签到码
                  </el-button>
                  <el-divider direction="vertical" />
                  <el-button type="danger" link size="small" @click="handleCancel(row)">
                    <el-icon><close /></el-icon> 取消报名
                  </el-button>
                </template>
                <span v-else style="color: #9ca3af;">-</span>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-if="!loadingMy && myRegistrations.length === 0" description="暂无报名记录" />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 签到码弹窗 -->
    <el-dialog v-model="qrDialogVisible" title="签到信息" width="500px" center>
      <div style="text-align: center;">
        <!-- 个人签到码 -->
        <div style="background: #f5f7fa; padding: 30px; border-radius: 12px; margin-bottom: 25px;">
          <el-tag type="info" size="small" style="margin-bottom: 10px;">管理员手动签到用</el-tag>
          <div style="font-size: 14px; color: #999; margin-bottom: 10px;">我的签到码</div>
          <div style="font-size: 42px; font-weight: bold; color: #1e40af; letter-spacing: 6px; font-family: 'Courier New', monospace;">
            {{ checkinCode }}
          </div>
          <div style="font-size: 13px; color: #999; margin-top: 10px;">
            向管理员出示此码完成签到
          </div>
        </div>

        <!-- 活动签到码二维码 -->
        <div style="background: #fff3cd; padding: 20px; border-radius: 12px; margin-bottom: 20px;">
          <el-tag type="warning" size="small" style="margin-bottom: 10px;">自助签到用</el-tag>
          <div style="font-size: 14px; color: #856404; margin-bottom: 15px;">
            扫码获取活动签到码
          </div>
          <div style="background: white; padding: 20px; border-radius: 8px; display: inline-block; box-shadow: 0 2px 8px rgba(0,0,0,0.1);">
            <QrcodeVue v-if="qrCodeData" :value="qrCodeData" :size="200" level="H" />
          </div>
          <div style="font-size: 13px; color: #856404; margin-top: 15px;">
            📱 扫码后在"自助签到"中输入看到的数字
          </div>
        </div>
        
        <p style="font-size: 15px; color: #666;">
          活动：{{ currentQRRegistration?.event?.name }}
        </p>
      </div>
    </el-dialog>

    <!-- 自助签到弹窗 -->
    <el-dialog v-model="selfCheckinDialogVisible" title="自助签到" width="450px" center>
      <div style="text-align: center;">
        <el-alert 
          title="请输入管理员在现场公布的活动签到码" 
          type="warning" 
          :closable="false"
          style="margin-bottom: 20px;"
        />
        
        <div style="background: #f5f7fa; padding: 30px; border-radius: 12px;">
          <div style="font-size: 14px; color: #666; margin-bottom: 15px;">
            活动：{{ currentCheckinRegistration?.event?.name }}
          </div>
          
          <div style="background: #fff3cd; padding: 15px; border-radius: 8px; margin-bottom: 15px;">
            <div style="font-size: 13px; color: #856404;">
              ⚠️ 注意：这里输入的是<strong>活动签到码</strong>
            </div>
            <div style="font-size: 12px; color: #856404; margin-top: 5px;">
              不是您个人的签到码（00000101）
            </div>
          </div>
          
          <el-input 
            v-model="selfCheckinCode" 
            placeholder="请输入活动签到码" 
            size="large"
            maxlength="8"
            style="width: 100%; font-size: 24px; text-align: center;"
            @keyup.enter="handleSelfCheckin"
          >
            <template #prepend>
              <el-icon><CircleCheck /></el-icon>
            </template>
          </el-input>
          
          <el-button 
            type="primary" 
            size="large"
            :loading="checkingIn"
            @click="handleSelfCheckin"
            style="width: 100%; margin-top: 20px;"
          >
            确认签到
          </el-button>
        </div>

        <p style="margin-top: 20px; font-size: 13px; color: #999;">
          活动签到码由管理员在活动现场公布
        </p>
        <p style="margin-top: 5px; font-size: 13px; color: #999;">
          所有参加该活动的学生使用同一个签到码
        </p>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import request from '@/api/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { CircleCheck } from '@element-plus/icons-vue'
import QrcodeVue from 'qrcode.vue'

const activeTab = ref('list')
const events = ref([])
const myRegistrations = ref([])
const loading = ref(false)
const loadingMy = ref(false)

// 签到码相关
const qrDialogVisible = ref(false)
const qrCodeData = ref('')
const checkinCode = ref('')
const currentQRRegistration = ref(null)

// 自助签到相关
const selfCheckinDialogVisible = ref(false)
const selfCheckinCode = ref('')
const checkingIn = ref(false)
const currentCheckinRegistration = ref(null)

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

// 检查是否已报名某活动（排除已取消的报名）
const isRegistered = (eventId) => {
  return myRegistrations.value.some(r => 
    (r.event?.id === eventId || r.eventId === eventId) && r.status !== 'CANCELLED'
  )
}

// 生成签到码（基于报名ID）
const generateCheckinCode = (registrationId) => {
  // 使用报名ID生成8位签到码
  const id = registrationId.toString().padStart(6, '0')
  const checksum = (registrationId % 100).toString().padStart(2, '0')
  return `${id}${checksum}`
}

// 显示签到码
const showQRCode = (registration) => {
  currentQRRegistration.value = registration
  const eventId = registration.event?.id || registration.eventId
  
  // 生成个人签到码（用于管理员手动签到）
  checkinCode.value = generateCheckinCode(registration.id)
  
  // 二维码内容：活动签到码（用于学生自助签到）
  // 扫码后显示活动签到码，学生可以在"自助签到"中输入
  const eventCheckinCode = registration.event?.checkinCode || '未生成'
  qrCodeData.value = eventCheckinCode
  
  qrDialogVisible.value = true
}

// 显示自助签到弹窗
const showSelfCheckin = (registration) => {
  currentCheckinRegistration.value = registration
  selfCheckinCode.value = ''
  selfCheckinDialogVisible.value = true
}

// 自助签到
const handleSelfCheckin = async () => {
  if (!selfCheckinCode.value || selfCheckinCode.value.length !== 8) {
    ElMessage.warning('请输入8位签到码')
    return
  }
  
  checkingIn.value = true
  try {
    // 调用签到接口，传递签到码
    await request.post(`/students/me/registrations/${currentCheckinRegistration.value.id}/checkin`, {
      checkinCode: selfCheckinCode.value
    })
    
    ElMessage.success('签到成功！')
    selfCheckinDialogVisible.value = false
    
    // 刷新报名列表
    fetchMyRegistrations()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '签到失败，请检查签到码是否正确')
  } finally {
    checkingIn.value = false
  }
}

// 获取状态文本
const getStatusText = (status) => {
  const map = {
    'REGISTERED': '未签到',
    'CHECKED_IN': '已签到',
    'CANCELLED': '已取消'
  }
  return map[status] || status
}

// 获取状态类型
const getStatusType = (status) => {
  const map = {
    'REGISTERED': 'info',
    'CHECKED_IN': 'success',
    'CANCELLED': 'info'
  }
  return map[status] || ''
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
