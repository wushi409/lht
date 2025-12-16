<template>
  <div class="jobs-container p-6">
    <div class="filter-section mb-6 bg-white p-4 rounded shadow-sm">
      <el-form :inline="true" :model="filters" class="demo-form-inline">
        <el-form-item label="关键词">
          <el-input v-model="filters.keyword" placeholder="职位名称/公司" clearable />
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="filters.industry" placeholder="例如：互联网" clearable />
        </el-form-item>
        <el-form-item label="地点">
          <el-input v-model="filters.location" placeholder="例如：北京" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchJobs">搜索</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="job-list grid gap-4 grid-cols-1 md:grid-cols-2 lg:grid-cols-3">
      <el-card v-for="job in jobs" :key="job.id" class="job-card hover:shadow-md transition-shadow">
        <template #header>
          <div class="flex justify-between items-center">
            <h3 class="text-lg font-bold truncate">{{ job.title }}</h3>
            <span class="text-orange-500 font-bold">{{ job.salaryRange || '面议' }}</span>
          </div>
        </template>
        <div class="text-gray-600 mb-2">
          <p><span class="font-semibold">公司：</span>{{ job.companyName }}</p>
          <p><span class="font-semibold">地点：</span>{{ job.location }}</p>
          <p><span class="font-semibold">学历：</span>{{ job.degreeRequirement || '不限' }}</p>
        </div>
        <div class="flex justify-end mt-4">
          <el-button type="primary" size="small" @click="viewJobDetail(job)">查看详情</el-button>
        </div>
      </el-card>
    </div>

    <el-empty v-if="jobs.length === 0" description="暂无相关职位" />

    <!-- Job Detail Dialog -->
    <el-dialog v-model="dialogVisible" title="职位详情" width="50%">
      <div v-if="currentJob">
        <h2 class="text-xl font-bold mb-2">{{ currentJob.title }}</h2>
        <p class="text-gray-500 mb-4">{{ currentJob.companyName }} | {{ currentJob.location }} | {{ currentJob.salaryRange }}</p>
        
        <el-divider content-position="left">职位描述</el-divider>
        <p class="whitespace-pre-wrap mb-4">{{ currentJob.description }}</p>
        
        <el-divider content-position="left">任职要求</el-divider>
        <p class="whitespace-pre-wrap mb-4">{{ currentJob.requirements }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleApply" :loading="applying">立即投递</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getJobs } from '@/api/job'
import { applyJob } from '@/api/student'
import { ElMessage } from 'element-plus'

const jobs = ref([])
const filters = reactive({
  keyword: '',
  industry: '',
  location: ''
})

const dialogVisible = ref(false)
const currentJob = ref(null)
const applying = ref(false)

const fetchJobs = async () => {
  try {
    const data = await getJobs(filters)
    jobs.value = data || []
  } catch (error) {
    console.error(error)
  }
}

const viewJobDetail = (job) => {
  currentJob.value = job
  dialogVisible.value = true
}

const handleApply = async () => {
  if (!currentJob.value) return
  
  applying.value = true
  try {
    // Assuming applyJob takes an object with jobId
    await applyJob({ jobId: currentJob.value.id })
    ElMessage.success('投递成功')
    dialogVisible.value = false
  } catch (error) {
    console.error(error)
  } finally {
    applying.value = false
  }
}

onMounted(() => {
  fetchJobs()
})
</script>

<style scoped>
.job-card {
  border-radius: 8px;
}
</style>
