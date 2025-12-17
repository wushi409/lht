<template>
  <div class="checkin-page">
    <el-card class="checkin-card">
      <div v-if="loading" class="loading-box">
        <el-icon class="is-loading" style="font-size: 48px;"><Loading /></el-icon>
        <p style="margin-top: 16px;">正在签到...</p>
      </div>

      <div v-else-if="result" class="result-box" :class="result.success ? 'success' : 'error'">
        <el-icon v-if="result.success" style="font-size: 64px; color: #67c23a;"><SuccessFilled /></el-icon>
        <el-icon v-else style="font-size: 64px; color: #f56c6c;"><CircleCloseFilled /></el-icon>
        
        <h2 style="margin-top: 20px;">{{ result.message }}</h2>
        
        <div v-if="result.success && result.data" class="info-box">
          <p><strong>活动：</strong>{{ result.data.eventName }}</p>
          <p><strong>签到时间：</strong>{{ formatDateTime(result.data.checkinTime) }}</p>
        </div>

        <el-button type="primary" size="large" @click="goToEvents" style="margin-top: 30px;">
          查看我的报名
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/request'
import { SuccessFilled, CircleCloseFilled, Loading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const loading = ref(true)
const result = ref(null)

const checkin = async () => {
  const eventId = route.query.eventId
  
  if (!eventId) {
    result.value = {
      success: false,
      message: '无效的签到链接'
    }
    loading.value = false
    return
  }

  try {
    const data = await request.post(`/students/me/checkin/${eventId}`)
    result.value = {
      success: true,
      message: '签到成功！',
      data
    }
  } catch (error) {
    result.value = {
      success: false,
      message: error.response?.data?.message || '签到失败，请检查是否已报名该活动'
    }
  } finally {
    loading.value = false
  }
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN')}`
}

const goToEvents = () => {
  router.push('/student/events')
}

onMounted(() => {
  checkin()
})
</script>

<style scoped>
.checkin-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.checkin-card {
  max-width: 500px;
  width: 100%;
}

.loading-box, .result-box {
  text-align: center;
  padding: 60px 40px;
}

.result-box.success {
  background: #f0f9ff;
}

.result-box.error {
  background: #fef0f0;
}

.info-box {
  margin-top: 30px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  text-align: left;
}

.info-box p {
  margin: 10px 0;
  font-size: 16px;
  color: #666;
}
</style>
