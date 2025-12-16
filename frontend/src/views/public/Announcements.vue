<template>
  <div class="container main-content">
    <div class="page-header">
      <h2 class="page-title">公告通知</h2>
      <div class="breadcrumb">当前位置：首页 > 公告通知</div>
    </div>
    
    <div class="content-wrapper">
      <div class="announcement-list" v-loading="loading">
        <div v-for="item in announcements" :key="item.id" class="announcement-card" @click="viewDetail(item)">
          <div class="card-date">
            <div class="date-month">{{ getMonth(item.createdAt) }}月</div>
            <div class="date-day">{{ getDay(item.createdAt) }}</div>
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-summary">{{ item.content?.substring(0, 100) }}{{ item.content?.length > 100 ? '...' : '' }}</p>
            <div class="card-meta">
              <span><el-icon><Clock /></el-icon> {{ formatDate(item.createdAt) }}</span>
              <el-tag v-if="item.pinned" type="danger" size="small">置顶</el-tag>
            </div>
          </div>
          <div class="card-arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
        <el-empty v-if="!loading && announcements.length === 0" description="暂无公告" />
      </div>
    </div>

    <!-- 公告详情弹窗 -->
    <el-dialog v-model="dialogVisible" :title="current?.title" width="650px">
      <div v-if="current" class="detail-content">
        <div class="detail-meta">
          <span><el-icon><Clock /></el-icon> 发布时间：{{ formatDate(current.createdAt) }}</span>
        </div>
        <el-divider />
        <div class="detail-body">{{ current.content }}</div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { Clock, ArrowRight } from '@element-plus/icons-vue'

const announcements = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const current = ref(null)

const fetchAnnouncements = async () => {
  loading.value = true
  try {
    announcements.value = await request.get('/announcements') || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const viewDetail = (item) => {
  current.value = item
  dialogVisible.value = true
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

const getMonth = (date) => {
  if (!date) return ''
  return new Date(date).getMonth() + 1
}

const getDay = (date) => {
  if (!date) return ''
  return new Date(date).getDate()
}

onMounted(fetchAnnouncements)
</script>

<style scoped>
.container { width: 1200px; margin: 0 auto; padding: 20px 0; }
.page-header { border-bottom: 2px solid #1e40af; margin-bottom: 20px; padding-bottom: 10px; display: flex; justify-content: space-between; align-items: flex-end; }
.page-title { margin: 0; color: #1e40af; }
.breadcrumb { font-size: 12px; color: #666; }

.announcement-list { background: white; border-radius: 4px; }

.announcement-card {
  display: flex;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s;
}
.announcement-card:hover {
  background: #f9fafc;
  transform: translateX(4px);
}
.announcement-card:last-child {
  border-bottom: none;
}

.card-date {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #1e40af, #3b82f6);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  margin-right: 20px;
  flex-shrink: 0;
}
.date-month { font-size: 12px; opacity: 0.9; }
.date-day { font-size: 26px; font-weight: 700; line-height: 1; margin-top: 2px; }

.card-content { flex: 1; min-width: 0; }
.card-title {
  margin: 0 0 8px;
  font-size: 17px;
  color: #1f2937;
  font-weight: 600;
}
.card-title:hover { color: #1e40af; }
.card-summary {
  margin: 0 0 10px;
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.card-meta {
  font-size: 13px;
  color: #9ca3af;
  display: flex;
  align-items: center;
  gap: 12px;
}
.card-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.card-arrow {
  color: #d1d5db;
  font-size: 20px;
  margin-left: 16px;
}
.announcement-card:hover .card-arrow {
  color: #1e40af;
}

/* Detail Dialog */
.detail-meta {
  font-size: 14px;
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 4px;
}
.detail-body {
  font-size: 15px;
  color: #374151;
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
