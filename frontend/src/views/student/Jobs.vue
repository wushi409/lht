<template>
  <div class="page-container">
    <div class="filter-bar">
      <el-form :inline="true" :model="filters">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="职位名称/公司" clearable @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="filters.industry" placeholder="行业" clearable />
        </el-form-item>
        <el-form-item label="工作地点">
          <el-input v-model="filters.location" placeholder="城市" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="job-list" v-loading="loading">
      <el-empty v-if="jobs.length === 0 && !loading" description="暂无相关职位" />
      <el-row :gutter="20" v-else>
        <el-col :span="24" v-for="job in jobs" :key="job.id" style="margin-bottom: 20px;">
          <el-card shadow="hover">
            <div class="job-card-content">
              <div class="job-info">
                <div class="job-title-row">
                  <h3 class="job-title" @click="viewDetail(job)">{{ job.title }}</h3>
                  <span class="salary">{{ job.salary }}</span>
                </div>
                <div class="company-info">
                  <el-icon><OfficeBuilding /></el-icon> {{ job.companyName }} 
                  <el-divider direction="vertical" />
                  <span>{{ job.location }}</span>
                  <el-divider direction="vertical" />
                  <span>{{ job.education }}</span>
                  <el-divider direction="vertical" />
                   <span>{{ job.workExperience }}</span>
                </div>
                <div class="tags" style="margin-top: 10px">
                  <el-tag size="small" style="margin-right: 5px">{{ job.jobType }}</el-tag>
                  <el-tag size="small" type="info" style="margin-right: 5px" v-if="job.industry">{{ job.industry }}</el-tag>
                </div>
              </div>
              <div class="actions">
                <el-button type="primary" size="small" @click="apply(job)">投递简历</el-button>
                <el-button size="small" @click="viewDetail(job)">查看详情</el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Job Detail Dialog -->
    <el-dialog v-model="detailVisible" title="职位详情" width="60%">
      <div v-if="currentJob">
        <h3>{{ currentJob.title }}</h3>
        <p class="salary">{{ currentJob.salary }}</p>
        <div class="meta-info">
          <span><el-icon><Location /></el-icon> {{ currentJob.location }}</span>
          <span style="margin-left: 20px"><el-icon><School /></el-icon> {{ currentJob.education }}</span>
          <span style="margin-left: 20px"><el-icon><Timer /></el-icon> {{ currentJob.workExperience }}</span>
        </div>
        <el-divider />
        <h4>职位描述</h4>
        <div style="white-space: pre-wrap;">{{ currentJob.description }}</div>
        <el-divider />
        <h4>技能要求</h4>
        <div style="white-space: pre-wrap;">{{ currentJob.requirement }}</div>
        <el-divider />
        <h4>公司信息</h4>
        <p>{{ currentJob.companyName }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailVisible = false">关闭</el-button>
          <el-button type="primary" @click="apply(currentJob)">立即投递</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getJobs } from '@/api/job'
import { applyJob } from '@/api/student'
import { ElMessage, ElMessageBox } from 'element-plus'
import { OfficeBuilding, Location, School, Timer } from '@element-plus/icons-vue'

const jobs = ref([])
const loading = ref(false)
const detailVisible = ref(false)
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

const handleSearch = () => {
  fetchJobs()
}

const resetFilters = () => {
  filters.keyword = ''
  filters.industry = ''
  filters.location = ''
  fetchJobs()
}

const viewDetail = (job) => {
  currentJob.value = job
  detailVisible.value = true
}

const apply = async (job) => {
  ElMessageBox.confirm(
    `确定要投递职位 "${job.title}" 吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info',
    }
  )
    .then(async () => {
      try {
        await applyJob({ jobId: job.id })
        ElMessage.success('投递成功')
        detailVisible.value = false
      } catch (error) {
        // Error handled in interceptor (e.g. 409 already applied)
      }
    })
    .catch(() => {})
}

onMounted(() => {
  fetchJobs()
})
</script>

<style scoped>
.page-container {
  max-width: 1000px;
  margin: 0 auto;
}
.filter-bar {
  background: white;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 4px;
}
.job-card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.job-title-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}
.job-title {
  margin: 0;
  margin-right: 20px;
  cursor: pointer;
  color: #303133;
}
.job-title:hover {
  color: #409EFF;
}
.salary {
  color: #f56c6c;
  font-weight: bold;
}
.company-info {
  color: #909399;
  font-size: 14px;
  display: flex;
  align-items: center;
}
.meta-info {
  color: #606266;
  margin-top: 5px;
}
</style>
