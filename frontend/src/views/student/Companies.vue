<template>
  <div class="page-container">
    <el-card class="filter-card">
      <el-form :inline="true" :model="filters">
        <el-form-item label="企业名称">
          <el-input v-model="filters.keyword" placeholder="搜索企业" clearable @keyup.enter="search" />
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
        <el-form-item>
          <el-button type="primary" @click="search">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" v-loading="loading">
      <el-col :xs="24" :sm="12" :lg="8" v-for="company in filteredList" :key="company.id">
        <el-card class="company-card" shadow="hover" @click="viewDetail(company)">
          <div class="company-header">
            <div class="avatar">{{ (company.name || '企').charAt(0) }}</div>
            <div class="info">
              <h3>{{ company.name }}</h3>
              <div class="tags">
                <el-tag size="small">{{ company.industry || '未分类' }}</el-tag>
                <el-tag size="small" type="info">{{ company.scale || '规模未知' }}</el-tag>
              </div>
            </div>
          </div>
          <p class="desc">{{ company.description || '暂无简介' }}</p>
        </el-card>
      </el-col>
    </el-row>
    
    <el-empty v-if="!loading && filteredList.length === 0" description="暂无企业" />

    <el-dialog v-model="dialogVisible" title="企业详情" width="550px">
      <div v-if="current">
        <h2 style="margin:0 0 16px">{{ current.name }}</h2>
        <p><strong>行业：</strong>{{ current.industry || '未分类' }}</p>
        <p><strong>规模：</strong>{{ current.scale || '未知' }}</p>
        <el-divider />
        <p><strong>企业简介：</strong></p>
        <p>{{ current.description || '暂无' }}</p>
        <template v-if="current.contactName || current.contactPhone || current.contactEmail">
          <el-divider />
          <p><strong>联系方式：</strong></p>
          <p v-if="current.contactName">联系人：{{ current.contactName }}</p>
          <p v-if="current.contactPhone">电话：{{ current.contactPhone }}</p>
          <p v-if="current.contactEmail">邮箱：{{ current.contactEmail }}</p>
        </template>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { listCompanies } from '@/api/student'

const companies = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const current = ref(null)
const filters = reactive({ keyword: '', industry: '' })

const filteredList = computed(() => {
  let list = companies.value
  if (filters.keyword) {
    const kw = filters.keyword.toLowerCase()
    list = list.filter(c => c.name?.toLowerCase().includes(kw))
  }
  if (filters.industry) {
    list = list.filter(c => c.industry === filters.industry)
  }
  return list
})

const fetchCompanies = async () => {
  loading.value = true
  try {
    companies.value = await listCompanies() || []
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

const search = () => {} // 前端过滤，无需请求

const viewDetail = (company) => {
  current.value = company
  dialogVisible.value = true
}

onMounted(fetchCompanies)
</script>

<style scoped>
.filter-card { margin-bottom: 16px; }
.company-card { margin-bottom: 16px; cursor: pointer; }
.company-header { display: flex; gap: 12px; margin-bottom: 12px; }
.avatar {
  width: 48px; height: 48px; border-radius: 8px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white; display: flex; align-items: center; justify-content: center;
  font-size: 20px; font-weight: 600;
}
.info h3 { margin: 0 0 6px; font-size: 15px; }
.tags { display: flex; gap: 6px; }
.desc { color: #6b7280; font-size: 13px; margin: 0; line-height: 1.5; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
</style>
