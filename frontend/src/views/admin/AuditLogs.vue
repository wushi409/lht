<template>
  <div class="page-container">
    <el-card>
      <template #header>审计日志</template>
      
      <el-table :data="logs" v-loading="loading" stripe>
        <el-table-column label="操作类型" prop="action" width="120" />
        <el-table-column label="操作人" prop="operator" width="120" />
        <el-table-column label="目标" prop="target" min-width="150" />
        <el-table-column label="详情" prop="detail" min-width="200" show-overflow-tooltip />
        <el-table-column label="IP地址" prop="ipAddress" width="130" />
        <el-table-column label="时间" min-width="160">
          <template #default="{ row }">{{ formatDateTime(row.createdAt) }}</template>
        </el-table-column>
      </el-table>
      
      <el-empty v-if="!loading && logs.length === 0" description="暂无审计日志" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const logs = ref([])
const loading = ref(false)

const fetchLogs = async () => {
  loading.value = true
  try {
    logs.value = await request.get('/admin/audit-logs') || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const formatDateTime = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.toLocaleDateString('zh-CN')} ${d.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit', second: '2-digit' })}`
}

onMounted(fetchLogs)
</script>
