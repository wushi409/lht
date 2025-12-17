<template>
  <div class="container main-content">
    <div class="page-header">
      <h2 class="page-title">招聘信息</h2>
      <div class="breadcrumb">当前位置：首页 > 招聘信息</div>
    </div>
    
    <div class="content-wrapper">
      <!-- Search/Filter Sidebar -->
      <div class="left-col sidebar">
        <div class="filter-group">
          <h4 class="filter-title">职位类型</h4>
          <div class="filter-list">
             <div 
               v-for="type in jobTypes" 
               :key="type.value" 
               class="filter-item" 
               :class="{ active: filters.jobType === type.value }"
               @click="filters.jobType = type.value"
             >
               {{ type.label }}
             </div>
          </div>
        </div>
        <div class="filter-group">
          <h4 class="filter-title">学历要求</h4>
          <div class="filter-list">
             <div 
               v-for="deg in degrees" 
               :key="deg.value" 
               class="filter-item" 
               :class="{ active: filters.degree === deg.value }"
               @click="filters.degree = deg.value"
             >
               {{ deg.label }}
             </div>
          </div>
        </div>
      </div>

      <!-- Job List -->
      <div class="right-col main-list">
         <div class="list-header-bar">
           <span>共找到 <strong class="text-primary">{{ filteredJobs.length }}</strong> 个职位</span>
           <el-radio-group v-model="sortType" size="small">
             <el-radio-button label="default">默认</el-radio-button>
             <el-radio-button label="new">最新</el-radio-button>
           </el-radio-group>
         </div>

         <div class="job-list-container" v-loading="loading">
           <div v-for="job in paginatedJobs" :key="job.id" class="job-card-row" @click="showDetail(job)">
             <div class="row-left">
               <div class="job-name">
                 {{ job.title }}
                 <el-tag v-if="job.jobType" size="small" effect="plain" class="ml-2">{{ job.jobType }}</el-tag>
               </div>
               <div class="job-attrs">
                 <span><el-icon><Location /></el-icon> {{ job.location || '不限' }}</span>
                 <el-divider direction="vertical" />
                 <span><el-icon><Reading /></el-icon> {{ job.degree || '学历不限' }}</span>
                 <el-divider direction="vertical" />
                 <span><el-icon><User /></el-icon> 招{{ job.headcount || '若干' }}人</span>
               </div>
             </div>
             <div class="row-center">
               <div class="company-name">{{ job.company?.name }}</div>
               <div class="company-industry">{{ job.company?.industry || '行业未知' }}</div>
             </div>
             <div class="row-right">
               <div class="salary">{{ job.salaryRange || '面议' }}</div>
               <div class="date">{{ formatDate(job.createdAt) }}</div>
             </div>
           </div>
           <el-empty v-if="!loading && filteredJobs.length === 0" description="暂无符合条件的职位" />
         </div>
         
         <div class="pagination-bar" v-if="filteredJobs.length > 0">
           <el-pagination 
             background 
             layout="prev, pager, next" 
             :total="filteredJobs.length"
             :page-size="pageSize"
             v-model:current-page="currentPage"
           />
         </div>
      </div>
    </div>

    <!-- 职位详情弹窗 -->
    <el-dialog v-model="dialogVisible" title="职位详情" width="600px">
      <div v-if="currentJob">
        <div class="detail-header">
          <h2>{{ currentJob.title }}</h2>
          <span class="detail-salary">{{ currentJob.salaryRange || '面议' }}</span>
        </div>
        <div class="detail-company">{{ currentJob.company?.name || '企业' }}</div>
        <div class="detail-tags">
          <el-tag>{{ currentJob.location || '不限' }}</el-tag>
          <el-tag type="info">{{ currentJob.jobType || '全职' }}</el-tag>
          <el-tag type="info" v-if="currentJob.headcount">招{{ currentJob.headcount }}人</el-tag>
          <el-tag type="info" v-if="currentJob.degree">{{ currentJob.degree }}</el-tag>
        </div>
        <el-divider />
        <div class="detail-section">
          <h4>职位描述</h4>
          <p>{{ currentJob.description || '暂无描述' }}</p>
        </div>
        <div class="detail-section" v-if="currentJob.skills">
          <h4>技能要求</h4>
          <p>{{ currentJob.skills }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleApply">{{ applyButtonText }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import request from '@/api/request'
import { Location, Reading, User } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 筛选选项
const jobTypes = [
  { label: '不限', value: '' },
  { label: '全职', value: '全职' },
  { label: '实习', value: '实习' },
  { label: '兼职', value: '兼职' }
]
const degrees = [
  { label: '不限', value: '' },
  { label: '大专', value: '大专' },
  { label: '本科', value: '本科' },
  { label: '硕士', value: '硕士' },
  { label: '博士', value: '博士' }
]

const allJobs = ref([])
const loading = ref(false)
const sortType = ref('default')
const dialogVisible = ref(false)
const currentJob = ref(null)

// 筛选条件
const filters = reactive({
  jobType: '',
  degree: '',
  keyword: ''
})

// 分页
const currentPage = ref(1)
const pageSize = 5

// 筛选后的职位
const filteredJobs = computed(() => {
  let result = [...allJobs.value]
  
  // 关键词搜索
  if (filters.keyword) {
    const kw = filters.keyword.toLowerCase()
    result = result.filter(job => 
      job.title?.toLowerCase().includes(kw) || 
      job.company?.name?.toLowerCase().includes(kw) ||
      job.description?.toLowerCase().includes(kw)
    )
  }
  
  if (filters.jobType) {
    result = result.filter(job => job.jobType === filters.jobType)
  }
  if (filters.degree) {
    result = result.filter(job => job.degree === filters.degree)
  }
  
  // 排序
  if (sortType.value === 'new') {
    result.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  }
  
  return result
})

// 当前页的职位
const paginatedJobs = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredJobs.value.slice(start, start + pageSize)
})

// 筛选条件变化时重置页码
watch(filters, () => {
  currentPage.value = 1
})
watch(sortType, () => {
  currentPage.value = 1
})

const fetchJobs = async () => {
  loading.value = true
  try {
    allJobs.value = await request.get('/jobs') || []
  } catch (e) {
    allJobs.value = []
  } finally {
    loading.value = false
  }
}

const showDetail = (job) => {
  currentJob.value = job
  dialogVisible.value = true
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toISOString().split('T')[0]
}

const applyButtonText = computed(() => {
  return userStore.token ? '查看详情' : '登录投递'
})

const handleApply = () => {
  if (!userStore.token) {
    router.push('/login')
    return
  }
  
  // 根据角色跳转
  if (userStore.role === 'STUDENT') {
    router.push('/student/jobs')
  } else if (userStore.role === 'COMPANY') {
    router.push('/company/jobs')
  } else if (userStore.role === 'ADMIN') {
    router.push('/admin/stats')
  } else {
    router.push('/login')
  }
}

onMounted(() => {
  // 从URL获取搜索关键词
  if (route.query.keyword) {
    filters.keyword = route.query.keyword
  }
  fetchJobs()
})

// 监听路由变化
watch(() => route.query.keyword, (newKeyword) => {
  filters.keyword = newKeyword || ''
  currentPage.value = 1
})
</script>

<style scoped>
.container { width: 1200px; margin: 0 auto; padding: 20px 0; }
.page-header { border-bottom: 2px solid #1e40af; margin-bottom: 20px; padding-bottom: 10px; display: flex; justify-content: space-between; align-items: flex-end; }
.page-title { margin: 0; color: #1e40af; }
.breadcrumb { font-size: 12px; color: #666; }

.content-wrapper { display: flex; gap: 20px; }
.left-col { width: 240px; background: white; padding: 16px; align-self: flex-start; }
.right-col { flex: 1; min-width: 0; background: white; padding: 20px; }

.filter-group { margin-bottom: 24px; }
.filter-title { margin: 0 0 10px; font-size: 14px; color: #333; font-weight: 700; border-left: 3px solid #1e40af; padding-left: 8px; }
.filter-item { font-size: 13px; color: #666; padding: 8px 12px; cursor: pointer; transition: all 0.2s; border-radius: 4px; margin-bottom: 4px; }
.filter-item:hover { color: #1e40af; background: #f0f7ff; }
.filter-item.active { color: white; background: #1e40af; }

.list-header-bar { display: flex; justify-content: space-between; align-items: center; background: #f8f9fa; padding: 10px 16px; margin-bottom: 16px; border: 1px solid #eee; }
.text-primary { color: #1e40af; }

.job-card-row { display: flex; padding: 20px; border-bottom: 1px dashed #eee; cursor: pointer; transition: background 0.2s; }
.job-card-row:hover { background: #f9fafc; }
.row-left { flex: 1; }
.job-name { font-size: 16px; font-weight: 600; color: #333; margin-bottom: 8px; }
.job-attrs { font-size: 12px; color: #888; display: flex; align-items: center; gap: 6px; }
.row-center { width: 200px; padding: 0 20px; border-left: 1px solid #f0f0f0; border-right: 1px solid #f0f0f0; display: flex; flex-direction: column; justify-content: center; }
.company-name { font-size: 14px; color: #333; font-weight: 500; margin-bottom: 4px; }
.company-industry { font-size: 12px; color: #999; }
.row-right { width: 140px; text-align: right; display: flex; flex-direction: column; justify-content: center; }
.salary { font-size: 16px; font-weight: 700; color: #f56c6c; margin-bottom: 4px; }
.date { font-size: 12px; color: #999; }

.pagination-bar { margin-top: 20px; display: flex; justify-content: center; }
.ml-2 { margin-left: 8px; }

/* Detail Dialog */
.detail-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px; }
.detail-header h2 { margin: 0; font-size: 20px; color: #1f2937; }
.detail-salary { color: #f56c6c; font-size: 18px; font-weight: 600; }
.detail-company { color: #6b7280; margin: 0 0 12px; }
.detail-tags { display: flex; gap: 8px; flex-wrap: wrap; }
.detail-section { margin-bottom: 16px; }
.detail-section h4 { margin: 0 0 8px; font-size: 14px; color: #374151; }
.detail-section p { margin: 0; color: #6b7280; line-height: 1.6; white-space: pre-wrap; }
</style>
