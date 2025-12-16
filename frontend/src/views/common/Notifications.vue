<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>消息通知</span>
          <el-checkbox v-model="unreadOnly" @change="fetchNotifications">只看未读</el-checkbox>
        </div>
      </template>
      
      <div v-loading="loading">
        <div v-for="item in notifications" :key="item.id" class="notification-item" :class="{ unread: !item.read }" @click="markRead(item)">
          <div class="content">
            <h4>{{ item.title }}</h4>
            <p>{{ item.content }}</p>
          </div>
          <span class="time">{{ formatDate(item.createdAt) }}</span>
        </div>
        <el-empty v-if="!loading && notifications.length === 0" description="暂无消息" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const notifications = ref([])
const loading = ref(false)
const unreadOnly = ref(false)

const fetchNotifications = async () => {
  loading.value = true
  try {
    notifications.value = await request.get('/notifications', { params: { unread: unreadOnly.value } }) || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const markRead = async (item) => {
  if (item.read) return
  try {
    await request.post(`/notifications/${item.id}/read`)
    item.read = true
  } catch (e) {
    console.error(e)
  }
}

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

onMounted(fetchNotifications)
</script>

<style scoped>
.page-container { max-width: 800px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.notification-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px;
  border-bottom: 1px solid #e2e8f0;
  cursor: pointer;
  transition: background 0.2s;
}
.notification-item:hover { background: #f8fafc; }
.notification-item:last-child { border-bottom: none; }
.notification-item.unread { background: #eff6ff; }
.notification-item.unread:hover { background: #dbeafe; }
.content h4 { margin: 0 0 6px; font-size: 14px; color: #1f2937; }
.content p { margin: 0; font-size: 13px; color: #6b7280; }
.time { font-size: 12px; color: #9ca3af; white-space: nowrap; }
</style>
