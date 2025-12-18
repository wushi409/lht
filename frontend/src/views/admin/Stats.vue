<template>
  <div class="page-container">
    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.studentCount || 0 }}</div>
          <div class="stat-label">学生数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.companyCount || 0 }}</div>
          <div class="stat-label">企业数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.jobCount || 0 }}</div>
          <div class="stat-label">职位数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.applicationCount || 0 }}</div>
          <div class="stat-label">投递数</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :span="12">
        <el-card>
          <template #header>行业分布</template>
          <div ref="industryChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>职位类型</template>
          <div ref="jobTypeChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card>
          <template #header>近6个月投递趋势</template>
          <div ref="trendChart" style="height: 320px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card>
          <template #header>投递状态分布</template>
          <div ref="statusChart" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>学生求职方向</template>
          <div ref="intentChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-card style="margin-top: 16px">
      <template #header>热门职位 TOP 10</template>
      <el-table :data="topJobs" stripe>
        <el-table-column label="排名" type="index" width="60" />
        <el-table-column label="职位" prop="title" />
        <el-table-column label="公司" prop="companyName" />
        <el-table-column label="投递数" prop="applicationCount" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { adminApi } from '@/api/admin'
import request from '@/api/request'

const stats = ref({})
const topJobs = ref([])
const industryChart = ref(null)
const jobTypeChart = ref(null)
const trendChart = ref(null)
const statusChart = ref(null)
const intentChart = ref(null)

const STATUS_LABEL_MAP = {
  SUBMITTED: '已投递',
  VIEWED: '查看',
  INTERVIEW: '已面试',
  HIRED: '已录用',
  REJECTED: '已拒绝',
  WITHDRAWN: '已撤回'
}

let industryChartInstance = null
let jobTypeChartInstance = null
let trendChartInstance = null
let statusChartInstance = null
let intentChartInstance = null

const fetchStats = async () => {
  try {
    stats.value = await adminApi.getStatsOverview() || {}
  } catch (e) {
    console.error(e)
  }
}

const fetchTrendStats = async () => {
  try {
    const data = await request.get('/admin/stats/trend') || []
    await nextTick()
    if (trendChart.value) {
      trendChartInstance = echarts.init(trendChart.value)
      trendChartInstance.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: data.map(d => d.month) },
        yAxis: { type: 'value', name: '投递数' },
        series: [{
          type: 'line',
          smooth: true,
          data: data.map(d => d.count)
        }]
      })
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchIndustryStats = async () => {
  try {
    const data = await request.get('/admin/stats/industry') || []
    await nextTick()
    if (industryChart.value) {
      industryChartInstance = echarts.init(industryChart.value)
      industryChartInstance.setOption({
        tooltip: { trigger: 'item' },
        series: [{
          type: 'pie',
          radius: '60%',
          data: data.map(d => ({ name: d.industry || '其他', value: d.count }))
        }]
      })
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchJobTypeStats = async () => {
  try {
    const data = await request.get('/admin/stats/job-types') || []
    await nextTick()
    if (jobTypeChart.value) {
      jobTypeChartInstance = echarts.init(jobTypeChart.value)
      jobTypeChartInstance.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: data.map(d => d.jobType || '其他') },
        yAxis: { type: 'value' },
        series: [{ type: 'bar', data: data.map(d => d.count) }]
      })
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchTopJobs = async () => {
  try {
    topJobs.value = await request.get('/admin/stats/top-jobs') || []
  } catch (e) {
    console.error(e)
  }
}

const fetchApplicationStatusStats = async () => {
  try {
    const data = await request.get('/admin/stats/applications') || []
    await nextTick()
    if (statusChart.value) {
      statusChartInstance = echarts.init(statusChart.value)
      statusChartInstance.setOption({
        tooltip: { trigger: 'item' },
        legend: { bottom: 0 },
        series: [{
          type: 'pie',
          radius: '60%',
          data: data.map(d => ({
            name: STATUS_LABEL_MAP[d.status] || d.status || '未知',
            value: d.count
          }))
        }]
      })
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchJobIntentStats = async () => {
  try {
    const data = await request.get('/admin/stats/job-intents') || []
    const top = data.slice(0, 10)
    await nextTick()
    if (intentChart.value) {
      intentChartInstance = echarts.init(intentChart.value)
      intentChartInstance.setOption({
        tooltip: { trigger: 'axis' },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', data: top.map(d => d.intent), axisLabel: { interval: 0, rotate: 30 } },
        yAxis: { type: 'value' },
        series: [{ type: 'bar', data: top.map(d => d.count) }]
      })
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchStats()
  fetchIndustryStats()
  fetchJobTypeStats()
  fetchTopJobs()
  fetchTrendStats()
   fetchApplicationStatusStats()
   fetchJobIntentStats()
})
</script>

<style scoped>
.stat-cards { margin-bottom: 16px; }
.stat-card { text-align: center; padding: 20px 0; }
.stat-value { font-size: 32px; font-weight: 700; color: #3b82f6; }
.stat-label { font-size: 14px; color: #6b7280; margin-top: 8px; }
</style>
