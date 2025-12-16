<template>
  <div class="jobs-page">
    <div class="container">
      <h2>职位大厅</h2>
      
      <!-- 搜索筛选 -->
      <el-card class="filter-card" shadow="never">
        <el-form :inline="true" :model="filters">
          <el-form-item label="关键词">
            <el-input v-model="filters.keyword" placeholder="职位名称/公司" clearable style="width: 200px" />
          </el-form-item>
          <el-form-item label="行业">
            <el-select v-model="filters.industry" placeholder="选择行业" clearable style="width: 150px">
              <el-option label="互联网" value="互联网" />
              <el-option label="金融" value="金融" />
              <el-option label="教育" value="教育" />
              <el-option label="制造业" value="制造业" />
            </el-select>
          </el-form-item>
          <el-form-item label="地点">
            <el-input v-model="filters.location" placeholder="工作地点" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="fetchJobs">搜索</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 职位列表 -->
      <div class="job-list" v-loading="loading">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :lg="8" v-for="job in jobs" :key="job.id">
            <el-card class="job-card" shadow="hover" @click="viewJobDetail(job)">
              <div class="job-header">
                <h3 class="job-title">{{ job.title }}</h3>
                <span class="job-salary">{{ job.salaryRange || '面议' }}</span>
              </div>
              <div class="job-company">{{ job.company?.name || job.companyName || '未知企业' }}</div>
              <div class="job-tags">
                <el-tag size="small" type="info">{{ job.location || '不限' }}</el-tag>
                <el-tag size="small" type="info">{{ job.jobType || '全职' }}</el-tag>
              </div>
              <div class="job-footer">
                <span class="job-date">{{ formatDate(job.publishAt) }}</span>
                <el-button type="primary" size="small" @click.stop="handleApply(job)">投递</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-empty v-if="!loading && jobs.length === 0" description="暂无相关职位" />
      </div>

      <!-- 职位详情对话框 -->
      <el-dialog v-model="dialogVisible" title="职位详情" width="600px">
        <div v-if="currentJob" class="job-detail">
          <h2>{{ currentJob.title }}</h2>
          <div class="detail-meta">
            <span>{{ currentJob.company?.name || currentJob.companyName }}</span>
            <span>{{ currentJob.location }}</span>
            <span class="salary">{{ currentJob.salaryRange || '面议' }}</span>
          </div>
          
          <el-divider />
          
          <div class="detail-section" v-if="currentJob.description">
            <h4>职位描述</h4>
            <p>{{ currentJob.description }}</p>
          </div>
          
          <div class="detail-section" v-if="currentJob.skills">
            <h4>技能要求</h4>
            <p>{{ currentJob.skills }}</p>
          </div>
        </div>
        <template #footer>
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="handleApply(currentJob)" :loading="applying">立即投递</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getJobs } from '@/api/job'
import { applyJob } from '@/api/student'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'

const userStore = useUserStore()
const router = useRouter()
const jobs = ref([])
const loading = ref(false)
const applying = ref(false)
const dialogVisible = ref(false)
const currentJob = ref(null)

const filters = reactive({
  keyword: '',
  industry: '',
  location: ''
})

const fetchJobs = async () => {
  loading.value = true
  try {
    const data = await getJobs(filters)
    jobs.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const viewJobDetail = (job) => {
  currentJob.value = job
  dialogVisible.value = true
}

const handleApply = async (job) => {
  if (!userStore.token) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  
  if (userStore.role !== 'STUDENT') {
    ElMessage.warning('只有学生可以投递简历')
    return
  }
  
  applying.value = true
  try {
    await applyJob({ jobId: job.id })
    ElMessage.success('投递成功')
    dialogVisible.value = false
  } catch (error) {
    console.error(error)
  } finally {
    applying.value = false
  }
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleDateString('zh-CN')
}

onMounted(() => {
  fetchJobs()
})
</script>

<style scoped>
.jobs-page {
  padding: 30px 20px;
  min-height: calc(100vh - 64px - 200px);
}

.jobs-page h2 {
  margin: 0 0 20px 0;
  font-size: 28px;
  color: var(--text-main);
}

.filter-card {
  margin-bottom: 24px;
}

.job-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.job-card:hover {
  transform: translateY(-3px);
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 8px;
}

.job-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-main);
  margin: 0;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.job-salary {
  color: #f56c6c;
  font-weight: 600;
  font-size: 14px;
  margin-left: 10px;
}

.job-company {
  color: var(--text-secondary);
  font-size: 14px;
  margin-bottom: 10px;
}

.job-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.job-date {
  color: var(--text-placeholder);
  font-size: 12px;
}

.job-detail h2 {
  margin: 0 0 10px 0;
  font-size: 22px;
}

.detail-meta {
  display: flex;
  gap: 15px;
  color: var(--text-secondary);
  font-size: 14px;
}

.detail-meta .salary {
  color: #f56c6c;
  font-weight: 600;
}

.detail-section {
  margin-bottom: 20px;
}

.detail-section h4 {
  margin: 0 0 10px 0;
  color: var(--text-main);
}

.detail-section p {
  color: var(--text-regular);
  line-height: 1.8;
  white-space: pre-wrap;
}
</style>
