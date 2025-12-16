<template>
  <div class="page-container">
    <el-card>
      <template #header>系统公告</template>
      
      <div v-loading="loading">
        <div v-for="item in announcements" :key="item.id" class="announcement-item" @click="viewDetail(item)">
          <h4>{{ item.title }}</h4>
          <p class="meta">{{ formatDate(item.createdAt) }}</p>
        </div>
        <el-empty v-if="!loading && announcements.length === 0" description="暂无公告" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="current?.title" width="600px">
      <div v-if="current">
        <p class="meta">发布时间：{{ formatDate(current.createdAt) }}</p>
        <el-divider />
        <div style="white-space: pre-wrap; line-height: 1.8;">{{ current.content }}</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

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

const formatDate = (date) => date ? new Date(date).toLocaleDateString('zh-CN') : ''

onMounted(fetchAnnouncements)
</script>

<style scoped>
.page-container { max-width: 800px; }
.announcement-item {
  padding: 16px;
  border-bottom: 1px solid #e2e8f0;
  cursor: pointer;
  transition: background 0.2s;
}
.announcement-item:hover { background: #f8fafc; }
.announcement-item:last-child { border-bottom: none; }
.announcement-item h4 { margin: 0 0 8px; font-size: 15px; color: #1f2937; }
.meta { margin: 0; font-size: 13px; color: #9ca3af; }
</style>
