<template>
  <div class="container main-content">
    <div class="page-header">
      <h2 class="page-title">宣讲会列表</h2>
      <div class="breadcrumb">当前位置：首页 > 宣讲会</div>
    </div>
    
    <div class="seminar-list" v-loading="loading">
       <div v-for="event in events" :key="event.id" class="seminar-card">
         <div class="date-box">
           <div class="month">{{ new Date(event.startTime).getMonth() + 1 }}月</div>
           <div class="day">{{ new Date(event.startTime).getDate() }}</div>
         </div>
         <div class="info-box">
           <div class="title">{{ event.name }}</div>
           <div class="meta">
             <span><el-icon><Clock /></el-icon> {{ formatTime(event.startTime) }} - {{ formatTime(event.endTime) }}</span>
             <el-divider direction="vertical" />
             <span><el-icon><Location /></el-icon> {{ event.location || '线上' }}</span>
           </div>
         </div>
         <div class="action-box">
           <el-button type="primary" plain round size="small" @click="showDetail(event)">详情/报名</el-button>
         </div>
       </div>
       <el-empty v-if="!loading && events.length === 0" description="暂无宣讲会" />
    </div>

    <!-- 宣讲会详情弹窗 -->
    <el-dialog v-model="dialogVisible" title="宣讲会详情" width="550px">
      <div v-if="currentEvent">
        <div class="detail-header">
          <div class="detail-date">
            <div class="detail-month">{{ new Date(currentEvent.startTime).getMonth() + 1 }}月</div>
            <div class="detail-day">{{ new Date(currentEvent.startTime).getDate() }}</div>
          </div>
          <div class="detail-info">
            <h2>{{ currentEvent.name }}</h2>
            <div class="detail-meta">
              <p><el-icon><Clock /></el-icon> {{ formatDateTime(currentEvent.startTime) }} - {{ formatTime(currentEvent.endTime) }}</p>
              <p><el-icon><Location /></el-icon> {{ currentEvent.location || '线上' }}</p>
              <p v-if="currentEvent.capacity"><el-icon><User /></el-icon> 容纳人数：{{ currentEvent.capacity }}人</p>
            </div>
          </div>
        </div>
        <el-divider />
        <div class="detail-section">
          <h4>活动介绍</h4>
          <p>{{ currentEvent.description || '暂无详细介绍' }}</p>
        </div>
        <div class="detail-section" v-if="currentEvent.type">
          <h4>活动类型</h4>
          <el-tag>{{ eventTypeText(currentEvent.type) }}</el-tag>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleAction">{{ actionButtonText }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'
import { Clock, Location, User } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const events = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const currentEvent = ref(null)

const fetchEvents = async () => {
  loading.value = true
  try {
    const allEvents = await request.get('/events') || []
    // Filter for seminar type if applicable, or show all for now
    events.value = allEvents
  } catch (e) {
    events.value = []
  } finally {
    loading.value = false
  }
}

const formatTime = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })}`
}

const showDetail = (event) => {
  currentEvent.value = event
  dialogVisible.value = true
}

const eventTypeText = (type) => {
  const map = {
    'PRESENTATION': '企业宣讲',
    'INTERVIEW_DAY': '面试专场',
    'CAREER_FAIR': '招聘会',
    'WORKSHOP': '工作坊'
  }
  return map[type] || type
}

const actionButtonText = computed(() => {
  return userStore.token ? '查看详情' : '登录报名'
})

const handleAction = () => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  
  // 根据角色跳转
  if (userStore.role === 'STUDENT') {
    router.push('/student/events')
  } else if (userStore.role === 'COMPANY') {
    router.push('/company/profile')
  } else if (userStore.role === 'ADMIN') {
    router.push('/admin/stats')
  } else {
    router.push('/login')
  }
}

onMounted(fetchEvents)
</script>

<style scoped>
.container { width: 1200px; margin: 0 auto; padding: 20px 0; }
.page-header { border-bottom: 2px solid #1e40af; margin-bottom: 20px; padding-bottom: 10px; display: flex; justify-content: space-between; align-items: flex-end; }
.page-title { margin: 0; color: #1e40af; }
.breadcrumb { font-size: 12px; color: #666; }

.seminar-list { background: white; padding: 20px; border-radius: 4px; }
.seminar-card { display: flex; align-items: center; padding: 20px; border-bottom: 1px solid #eee; transition: all 0.2s; }
.seminar-card:hover { background: #fcfcfc; box-shadow: 0 2px 12px rgba(0,0,0,0.05); transform: translateY(-2px); }

.date-box { width: 70px; height: 70px; background: #f0f7ff; color: #1e40af; display: flex; flex-direction: column; align-items: center; justify-content: center; border-radius: 8px; margin-right: 20px; flex-shrink: 0; }
.month { font-size: 12px; }
.day { font-size: 24px; font-weight: 700; line-height: 1; margin-top: 4px; }

.info-box { flex: 1; }
.title { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 8px; }
.meta { font-size: 13px; color: #666; display: flex; align-items: center; gap: 8px; }

.action-box { width: 100px; text-align: right; }

/* Detail Dialog */
.detail-header { display: flex; gap: 20px; }
.detail-date { width: 70px; height: 70px; background: #f0f7ff; color: #1e40af; display: flex; flex-direction: column; align-items: center; justify-content: center; border-radius: 8px; flex-shrink: 0; }
.detail-month { font-size: 12px; }
.detail-day { font-size: 24px; font-weight: 700; line-height: 1; margin-top: 4px; }
.detail-info h2 { margin: 0 0 12px; font-size: 18px; color: #1f2937; }
.detail-meta { font-size: 14px; color: #6b7280; }
.detail-meta p { margin: 0 0 6px; display: flex; align-items: center; gap: 6px; }
.detail-section { margin-bottom: 16px; }
.detail-section h4 { margin: 0 0 8px; font-size: 14px; color: #374151; }
.detail-section p { margin: 0; color: #6b7280; line-height: 1.6; white-space: pre-wrap; }
</style>
