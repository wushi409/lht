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
             <span>已报名企业 ({{ companies.length }})</span>
           </div>
         </template>
         <div class="companies-list" v-if="companies.length > 0">
            <div v-for="company in companies" :key="company.id" class="company-item">
               <div class="co-logo">{{ (company.name || 'Co').substring(0,2) }}</div>
               <div class="co-name">{{ company.name }}</div>
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
const companies = ref([])
const loading = ref(false)

const fetchData = async () => {
  loading.value = true
  try {
    const id = route.params.id
    // Mocking check, normally fetch by ID
    // fair.value = await request.get(`/job-fairs/${id}`)
    
    // Fallback Mock for demo if ID not valid or API missing
    const allFairs = await request.get('/job-fairs') || []
    fair.value = allFairs.find(f => f.id == id) || allFairs[0] || {
        name: '2025春季大型双选会',
        startTime: '2025-03-20',
        endTime: '2025-03-25',
        location: '体育馆',
        description: '这是本次春季双选会的详细介绍信息...'
    }

    // Mock companies
    companies.value = Array(8).fill(null).map((_, i) => ({ id: i, name: `参会企业示例${i+1}` }))

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
