<template>
  <div class="admin-stats p-6">
    <h1 class="text-2xl font-bold mb-6">数据统计</h1>
    
    <!-- Summary Cards -->
    <el-row :gutter="20" class="mb-6">
      <el-col :span="6" v-for="(value, key) in summary" :key="key">
        <el-card shadow="hover">
          <template #header>{{ formatKey(key) }}</template>
          <div class="text-3xl font-bold text-blue-600">{{ value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- Charts -->
    <el-row :gutter="20">
      <el-col :span="12" class="mb-6">
        <el-card header="行业分布">
          <div ref="industryChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
      <el-col :span="12" class="mb-6">
        <el-card header="职位类型">
          <div ref="jobTypeChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card header="招聘趋势">
          <div ref="trendChart" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import request from '@/api/request'
import * as echarts from 'echarts'

const summary = ref({})
const industryChart = ref(null)
const jobTypeChart = ref(null)
const trendChart = ref(null)

const fetchStats = async () => {
  try {
    // Fetch summary
    const summaryData = await request.get('/admin/stats/summary')
    summary.value = summaryData || {}

    // Fetch Industry
    const industryData = await request.get('/admin/stats/industry')
    initPieChart(industryChart.value, industryData, '行业分布')

    // Fetch Job Types
    const jobTypeData = await request.get('/admin/stats/job-types')
    initPieChart(jobTypeChart.value, jobTypeData, '职位类型')

    // Fetch Trend
    const trendData = await request.get('/admin/stats/trend')
    initLineChart(trendChart.value, trendData, '招聘趋势')

  } catch (error) {
    console.error(error)
  }
}

const initPieChart = (dom, data, name) => {
  if (!dom) return
  const chart = echarts.init(dom)
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '5%', left: 'center' },
    series: [
      {
        name: name,
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false, position: 'center' },
        emphasis: { label: { show: true, fontSize: 20, fontWeight: 'bold' } },
        data: data.map(item => ({ value: item.count, name: item.name || item.industry || item.jobType }))
      }
    ]
  })
}

const initLineChart = (dom, data, name) => {
  if (!dom) return
  const chart = echarts.init(dom)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: data.map(item => item.date) },
    yAxis: { type: 'value' },
    series: [
      {
        name: name,
        data: data.map(item => item.count),
        type: 'line',
        smooth: true,
        areaStyle: {}
      }
    ]
  })
}

const formatKey = (key) => {
  const map = {
    totalStudents: '学生总数',
    totalCompanies: '企业总数',
    totalJobs: '职位总数',
    totalApplications: '投递总数',
    totalInterviews: '面试总数'
  }
  return map[key] || key
}

onMounted(() => {
  nextTick(() => {
    fetchStats()
  })
})
</script>
