<template>
  <div class="stats-container">
    <h2>数据统计分析</h2>

    <!-- 概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :xs="12" :sm="6" v-for="card in overviewCards" :key="card.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: card.color }">
              <el-icon :size="30" color="#fff">
                <component :is="card.icon" />
              </el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 企业行业分布 -->
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <span>企业行业分布</span>
          </template>
          <div ref="industryChart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 投递状态统计 -->
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <span>投递状态统计</span>
          </template>
          <div ref="applicationChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 职位类型分布 -->
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <span>职位类型分布</span>
          </template>
          <div ref="jobTypeChart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 月度投递趋势 -->
      <el-col :xs="24" :sm="12">
        <el-card>
          <template #header>
            <span>月度投递趋势</span>
          </template>
          <div ref="trendChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 热门职位排行 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card>
          <template #header>
            <span>热门职位 TOP 10</span>
          </template>
          <el-table :data="topJobs" style="width: 100%">
            <el-table-column type="index" label="排名" width="80" />
            <el-table-column prop="title" label="职位名称" min-width="200" />
            <el-table-column prop="companyName" label="公司名称" min-width="180" />
            <el-table-column prop="applicationCount" label="投递人数" width="120" align="center" />
            <el-table-column prop="salaryRange" label="薪资范围" width="150" />
            <el-table-column prop="location" label="工作地点" width="120" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import request from '@/api/request'
import { User, OfficeBuilding, Briefcase, Document } from '@element-plus/icons-vue'

const industryChart = ref(null)
const applicationChart = ref(null)
const jobTypeChart = ref(null)
const trendChart = ref(null)
const topJobs = ref([])

const overviewCards = ref([
  { title: '注册学生', value: 0, icon: 'User', color: '#409EFF' },
  { title: '入驻企业', value: 0, icon: 'OfficeBuilding', color: '#67C23A' },
  { title: '发布职位', value: 0, icon: 'Briefcase', color: '#E6A23C' },
  { title: '投递简历', value: 0, icon: 'Document', color: '#F56C6C' }
])

const fetchStats = async () => {
  try {
    const data = await request({ url: '/admin/stats/overview', method: 'get' })
    if (data) {
      overviewCards.value[0].value = data.studentCount || 0
      overviewCards.value[1].value = data.companyCount || 0
      overviewCards.value[2].value = data.jobCount || 0
      overviewCards.value[3].value = data.applicationCount || 0
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchIndustryData = async () => {
  try {
    const data = await request({ url: '/admin/stats/industry', method: 'get' })
    if (data && industryChart.value) {
      const chart = echarts.init(industryChart.value)
      chart.setOption({
        tooltip: {
          trigger: 'item',
          formatter: '{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            type: 'pie',
            radius: '60%',
            data: data.map(item => ({
              name: item.industry || '未分类',
              value: item.count
            })),
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchApplicationStats = async () => {
  try {
    const data = await request({ url: '/admin/stats/applications', method: 'get' })
    if (data && applicationChart.value) {
      const chart = echarts.init(applicationChart.value)
      const statusMap = {
        SUBMITTED: '已投递',
        INTERVIEW: '面试中',
        HIRED: '已录用',
        REJECTED: '已拒绝',
        WITHDRAWN: '已撤回'
      }
      chart.setOption({
        tooltip: {
          trigger: 'item'
        },
        legend: {
          bottom: '5%',
          left: 'center'
        },
        series: [
          {
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: 20,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: data.map(item => ({
              name: statusMap[item.status] || item.status,
              value: item.count
            }))
          }
        ]
      })
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchJobTypeData = async () => {
  try {
    const data = await request({ url: '/admin/stats/job-types', method: 'get' })
    if (data && jobTypeChart.value) {
      const chart = echarts.init(jobTypeChart.value)
      chart.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.jobType || '未分类')
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            type: 'bar',
            data: data.map(item => item.count),
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#83bff6' },
                { offset: 0.5, color: '#188df0' },
                { offset: 1, color: '#188df0' }
              ])
            },
            emphasis: {
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#2378f7' },
                  { offset: 0.7, color: '#2378f7' },
                  { offset: 1, color: '#83bff6' }
                ])
              }
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchTrendData = async () => {
  try {
    const data = await request({ url: '/admin/stats/trend', method: 'get' })
    if (data && trendChart.value) {
      const chart = echarts.init(trendChart.value)
      chart.setOption({
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: data.map(item => item.month)
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            type: 'line',
            data: data.map(item => item.count),
            smooth: true,
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
              ])
            },
            itemStyle: {
              color: '#409EFF'
            }
          }
        ]
      })
    }
  } catch (error) {
    console.error(error)
  }
}

const fetchTopJobs = async () => {
  try {
    const data = await request({ url: '/admin/stats/top-jobs', method: 'get' })
    topJobs.value = data || []
  } catch (error) {
    console.error(error)
  }
}

onMounted(async () => {
  await fetchStats()
  await nextTick()
  fetchIndustryData()
  fetchApplicationStats()
  fetchJobTypeData()
  fetchTrendData()
  fetchTopJobs()

  // 响应式调整
  window.addEventListener('resize', () => {
    if (industryChart.value) echarts.init(industryChart.value).resize()
    if (applicationChart.value) echarts.init(applicationChart.value).resize()
    if (jobTypeChart.value) echarts.init(jobTypeChart.value).resize()
    if (trendChart.value) echarts.init(trendChart.value).resize()
  })
})
</script>

<style scoped>
.stats-container {
  max-width: 1400px;
  margin: 0 auto;
}

.overview-cards {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-title {
  font-size: 14px;
  color: #909399;
}
</style>
