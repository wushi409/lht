<template>
  <div class="container main-content">
    <div class="page-header">
      <h2 class="page-title">双选会场次</h2>
      <div class="breadcrumb">当前位置：首页 > 双选会</div>
    </div>
    
    <div class="fairs-list" v-loading="loading">
       <div v-for="fair in fairs" :key="fair.id" class="fair-card">
         <div class="fair-cover">
           <!-- Placeholder for cover image -->
           <div class="cover-placeholder">双选会</div>
         </div>
         <div class="fair-content">
            <h3 class="fair-title">{{ fair.name }}</h3>
            <div class="fair-meta">
              <p><el-icon><Clock /></el-icon> 报名时间：{{ formatDate(fair.startTime) }} ~ {{ formatDate(fair.endTime) }}</p>
              <p><el-icon><Location /></el-icon> 举办地点：{{ fair.location || '学校体育馆' }}</p>
            </div>
            <div class="fair-tags">
               <el-tag size="small">大型招聘会</el-tag>
               <el-tag size="small" type="success">多专业</el-tag>
            </div>
         </div>
         <div class="fair-action">
           <el-button type="primary" @click="$router.push(`/public/fairs/${fair.id}`)">企业预订展位</el-button>
           <el-button plain @click="$router.push(`/public/fairs/${fair.id}`)">学生查看详情</el-button>
         </div>
       </div>
       <el-empty v-if="!loading && fairs.length === 0" description="暂无双选会" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { Clock, Location } from '@element-plus/icons-vue'

const fairs = ref([])
const loading = ref(false)

const fetchFairs = async () => {
  loading.value = true
  try {
    // Assuming /job-fairs endpoint exists or reusing /events
    fairs.value = await request.get('/job-fairs') || []
  } catch (e) {
    fairs.value = []
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

onMounted(fetchFairs)
</script>

<style scoped>
.container { width: 1200px; margin: 0 auto; padding: 20px 0; }
.page-header { border-bottom: 2px solid #1e40af; margin-bottom: 20px; padding-bottom: 10px; display: flex; justify-content: space-between; align-items: flex-end; }
.page-title { margin: 0; color: #1e40af; }
.breadcrumb { font-size: 12px; color: #666; }

.fair-card { display: flex; background: white; padding: 20px; margin-bottom: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.fair-cover { width: 240px; height: 135px; background: #e0eaff; border-radius: 4px; overflow: hidden; margin-right: 24px; flex-shrink: 0; }
.cover-placeholder { width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; font-size: 24px; color: #1e40af; opacity: 0.3; font-weight: 700; background: linear-gradient(45deg, #1e40af, #60a5fa); color: white; opacity: 1; }

.fair-content { flex: 1; }
.fair-title { margin: 0 0 12px; font-size: 20px; color: #333; }
.fair-meta p { margin: 0 0 8px; color: #666; font-size: 14px; display: flex; align-items: center; gap: 6px; }
.fair-tags { margin-top: 12px; display: flex; gap: 8px; }

.fair-action { width: 150px; display: flex; flex-direction: column; justify-content: center; gap: 12px; border-left: 1px dashed #eee; padding-left: 20px; }
.fair-action button { margin: 0; width: 100%; }
</style>
