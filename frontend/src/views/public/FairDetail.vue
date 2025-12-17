<template>
  <div class="container main-content" v-loading="loading">
    <div class="page-header" v-if="fair">
      <div class="breadcrumb">当前位置：首页 > 双选会 > 详情</div>
      <h2 class="detail-title">{{ fair.name }}</h2>
      <div class="detail-meta">
        <span><el-icon><Clock /></el-icon> {{ formatDate(fair.startTime) }} ~ {{ formatDate(fair.endTime) }}</span>
        <el-divider direction="vertical" />
        <span><el-icon><Location /></el-icon> {{ fair.location || '学校体育馆' }}</span>
      </div>
    </div>
    
    <div class="content-body" v-if="fair">
       <el-card class="box-card mb-4">
         <template #header>
           <div class="card-header">
             <span>活动介绍</span>
           </div>
         </template>
         <div class="text-content">
           {{ fair.description || '暂无详细介绍，请联系就业中心咨询。' }}
         </div>
         <div class="action-buttons mt-4">
             <el-button type="primary" size="large" @click="handleAction('company')">我是企业，我要预订展位</el-button>
             <el-button type="success" size="large" @click="handleAction('student')">我是学生，查看参会企业</el-button>
         </div>
       </el-card>

       <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span>参会企业及展位 ({{ booths.length }})</span>
          </div>
        </template>
        <div class="companies-list" v-if="booths.length > 0">
           <div v-for="item in booths" :key="item.id" class="company-item">
              <div class="co-logo">{{ (item.companyName || 'Co').substring(0,2) }}</div>
              <div class="co-name">{{ item.companyName }}</div>
              <div class="co-booth">展位号：{{ item.boothNo }}</div>
              <div class="co-location" v-if="item.location">位置：{{ item.location }}</div>
           </div>
        </div>
        <el-empty v-else description="暂无企业报名" />
      </el-card>
    </div>
    <el-empty v-else-if="!loading" description="未找到该双选会信息" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import request from '@/api/request'
import { Clock, Location } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const fair = ref(null)
const booths = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const id = route.params.id

    // 获取双选会基本信息
    const allFairs = await request.get('/job-fairs') || []
    fair.value = allFairs.find(f => f.id == id) || allFairs[0] || null

    // 获取展位及参会企业信息
    if (fair.value) {
      const boothList = await request.get(`/job-fairs/${fair.value.id}/booths`) || []
      booths.value = boothList.map(b => ({
        id: b.id,
        companyName: b.company?.name || '企业',
        boothNo: b.boothNo,
        location: b.location
      }))
    } else {
      booths.value = []
    }

  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const handleAction = (type) => {
    if (type === 'company') {
        router.push('/login?role=company')
    } else {
        router.push('/login?role=student')
    }
}

const formatDate = (date) => {
    if(!date) return ''
    return new Date(date).toLocaleDateString()
}

onMounted(fetchData)
</script>

<style scoped>
.container { width: 1200px; margin: 0 auto; padding: 20px 0; }
.breadcrumb { font-size: 12px; color: #666; margin-bottom: 20px; }
.detail-title { color: #1e40af; margin: 0 0 16px; font-size: 28px; }
.detail-meta { color: #666; font-size: 14px; display: flex; align-items: center; gap: 8px; margin-bottom: 30px; }

.text-content { line-height: 1.6; color: #333; font-size: 15px; }
.mb-4 { margin-bottom: 24px; }
.mt-4 { margin-top: 24px; }

.companies-list { display: grid; grid-template-columns: repeat(6, 1fr); gap: 16px; }
.company-item { border: 1px solid #eee; padding: 12px; text-align: center; border-radius: 4px; }
.co-logo { width: 40px; height: 40px; background: #eef5ff; color: #1e40af; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 8px; font-weight: bold; }
.co-name { font-size: 13px; color: #333; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
