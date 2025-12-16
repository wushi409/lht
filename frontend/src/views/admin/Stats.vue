<template>
  <div class="page-container">
    <div class="header-row">
      <h2>数据统计</h2>
      <el-button type="primary" @click="fetchData" :loading="loading">刷新</el-button>
    </div>
    <el-row :gutter="16">
      <el-col :span="8" v-for="item in cards" :key="item.key" style="margin-bottom: 16px">
        <el-card shadow="hover">
          <div class="card-title">{{ item.label }}</div>
          <div class="card-value">{{ summary[item.key] ?? '—' }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-empty v-if="!loading && cards.length === 0" description="暂无数据" />
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { adminApi } from '@/api/admin'

const summary = reactive({})
const loading = ref(false)

const cards = [
  { key: 'approvedCompanies', label: '已通过企业数' },
  { key: 'pendingCompanies', label: '待审核企业数' },
  { key: 'publishedJobs', label: '已发布岗位数' },
  { key: 'applications', label: '投递总数' },
  { key: 'hired', label: '已录用人数' },
  { key: 'interviews', label: '面试中人数' }
]

const fetchData = async () => {
  loading.value = true
  try {
    const data = await adminApi.getStatsSummary()
    Object.assign(summary, data || {})
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container {
  max-width: 1100px;
  margin: 0 auto;
}
.header-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}
.card-title {
  color: #909399;
}
.card-value {
  font-size: 28px;
  font-weight: 700;
  margin-top: 8px;
}
</style>
