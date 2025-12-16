<template>
  <div class="page-container">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filters">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="职位/公司名称" clearable @keyup.enter="fetchJobs" />
        </el-form-item>
        <el-form-item label="行业">
          <el-select v-model="filters.industry" placeholder="全部行业" clearable style="width: 160px">
            <el-option label="互联网" value="互联网" />
            <el-option label="金融" value="金融" />
            <el-option label="教育" value="教育" />
            <el-option label="制造业" value="制造业" />
            <el-option label="医疗健康" value="医疗健康" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="filters.location" placeholder="工作地点" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchJobs">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" v-loading="loading">
      <el-col :xs="24" :sm="12" :lg="8" v-for="job in jobs" :key="job.id">
        <el-card class="job-card" shadow="hover">
          <div class="job-header">
            <h3>{{ job.title }}</h3>
            <span class="salary">{{ job.salaryRange || '面议' }}</span>
          </div>
          <div class="company">{{ job.company?.name || '未知企业' }}</div>
          <div class="tags">
            <el-tag size="small" type="info">{{ job.location || '不限' }}</el-tag>
            <el-tag size="small" type="info">{{ job.jobType || '全职' }}</el-tag>
          </div>
          <p class="desc">{{ job.description || '暂无描述' }}</p>
          <div class="actions">
            <el-button size="small" @click="viewDetail(job)">详情</el-button>
            <el-button type="primary" size="small" @click="handleApply(job)">投递</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-empty v-if="!loading && jobs.length === 0" description="暂无职位" />

    <el-dialog v-model="dialogVisible" title="职位详情" width="600px">
      <div v-if="currentJob">
        <h2 style="margin:0 0 8px">{{ currentJob.title }}</h2>
        <p style="color:#f56c6c;font-size:18px;margin:0 0 16px">{{ currentJob.salaryRange || '面议' }}</p>
        <p><strong>公司：</strong>{{ currentJob.company?.name }}</p>
        <p><strong>地点：</strong>{{ currentJob.location || '不限' }}</p>
        <p><strong>类型：</strong>{{ currentJob.jobType || '全职' }}</p>
        <p><strong>招聘人数：</strong>{{ currentJob.headcount || '若干' }}</p>
        <el-divider />
        <p><strong>职位描述：</strong></p>
        <p style="white-space:pre-wrap">{{ currentJob.description || '暂无' }}</p>
        <p v-if="currentJob.skills"><strong>技能要求：</strong>{{ currentJob.skills }}</p>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="openApplyDialog(currentJob)">立即投递</el-button>
      </template>
    </el-dialog>

    <!-- 投递简历弹窗 -->
    <el-dialog v-model="applyDialogVisible" title="投递简历" width="450px">
      <div v-if="applyJobData">
        <p style="margin:0 0 16px">
          投递职位：<strong>{{ applyJobData.title }}</strong> - {{ applyJobData.company?.name }}
        </p>
        <el-form label-width="80px">
          <el-form-item label="选择简历" required>
            <el-select v-model="selectedResumeId" placeholder="请选择简历" style="width: 100%" v-loading="loadingResumes">
              <el-option 
                v-for="resume in resumes" 
                :key="resume.id" 
                :label="resume.title + (resume.isDefault ? ' (默认)' : '')" 
                :value="resume.id" 
              />
            </el-select>
          </el-form-item>
        </el-form>
        <el-empty v-if="!loadingResumes && resumes.length === 0" description="暂无简历，请先创建简历" :image-size="60">
          <el-button type="primary" size="small" @click="$router.push('/student/resumes')">去创建简历</el-button>
        </el-empty>
      </div>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="applying" :disabled="!selectedResumeId" @click="submitApply">确认投递</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getJobs } from '@/api/job'
import { applyJob, listResumes } from '@/api/student'
import { ElMessage } from 'element-plus'

const jobs = ref([])
const loading = ref(false)
const applying = ref(false)
const dialogVisible = ref(false)
const currentJob = ref(null)
const filters = reactive({ keyword: '', industry: '', location: '' })

// 投递相关
const applyDialogVisible = ref(false)
const applyJobData = ref(null)
const resumes = ref([])
const selectedResumeId = ref(null)
const loadingResumes = ref(false)

const fetchJobs = async () => {
  loading.value = true
  try {
    // 过滤掉空值参数
    const params = {}
    if (filters.keyword) params.keyword = filters.keyword
    if (filters.industry) params.industry = filters.industry
    if (filters.location) params.location = filters.location
    jobs.value = await getJobs(params) || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const viewDetail = (job) => {
  currentJob.value = job
  dialogVisible.value = true
}

// 打开投递弹窗
const openApplyDialog = async (job) => {
  applyJobData.value = job
  selectedResumeId.value = null
  applyDialogVisible.value = true
  dialogVisible.value = false
  
  // 加载简历列表
  loadingResumes.value = true
  try {
    resumes.value = await listResumes() || []
    // 自动选择默认简历
    const defaultResume = resumes.value.find(r => r.isDefault)
    if (defaultResume) {
      selectedResumeId.value = defaultResume.id
    }
  } catch (e) {
    console.error(e)
  } finally {
    loadingResumes.value = false
  }
}

// 提交投递
const submitApply = async () => {
  if (!selectedResumeId.value) {
    ElMessage.warning('请选择简历')
    return
  }
  applying.value = true
  try {
    await applyJob({ jobId: applyJobData.value.id, resumeId: selectedResumeId.value })
    ElMessage.success('投递成功')
    applyDialogVisible.value = false
  } catch (e) {
    console.error(e)
  } finally {
    applying.value = false
  }
}

// 兼容卡片上的投递按钮
const handleApply = (job) => {
  openApplyDialog(job)
}

onMounted(fetchJobs)
</script>

<style scoped>
.filter-card { margin-bottom: 16px; }
.job-card { margin-bottom: 16px; }
.job-header { display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 8px; }
.job-header h3 { margin: 0; font-size: 16px; }
.salary { color: #f56c6c; font-weight: 600; }
.company { color: #6b7280; font-size: 14px; margin-bottom: 8px; }
.tags { display: flex; gap: 8px; margin-bottom: 8px; }
.desc { color: #6b7280; font-size: 13px; line-height: 1.5; margin: 0 0 12px; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.actions { display: flex; justify-content: flex-end; gap: 8px; }
</style>
