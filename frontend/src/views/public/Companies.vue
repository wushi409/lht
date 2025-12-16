<template>
  <div class="container main-content">
    <div class="page-header">
      <h2 class="page-title">知名企业</h2>
      <div class="breadcrumb">当前位置：首页 > 知名企业</div>
    </div>
    
    <div class="company-grid" v-loading="loading">
       <div v-for="company in companies" :key="company.id" class="company-card" @click="showDetail(company)">
          <div class="logo-box">{{ (company.name || '企').substring(0,2) }}</div>
          <div class="info">
            <h4 class="name">{{ company.name }}</h4>
            <div class="industry">{{ company.industry || '行业未知' }}</div>
            <div class="tags">
              <el-tag size="small" type="info">{{ company.scale || '规模未知' }}</el-tag>
            </div>
          </div>
       </div>
    </div>
    <el-empty v-if="!loading && companies.length === 0" description="暂无企业" />

    <!-- 企业详情弹窗 -->
    <el-dialog v-model="dialogVisible" title="企业详情" width="550px">
      <div v-if="currentCompany">
        <div class="detail-header">
          <div class="detail-logo">{{ (currentCompany.name || '企').substring(0,2) }}</div>
          <div>
            <h2>{{ currentCompany.name }}</h2>
            <div class="detail-tags">
              <el-tag>{{ currentCompany.industry || '未分类' }}</el-tag>
              <el-tag type="info">{{ currentCompany.scale || '规模未知' }}</el-tag>
            </div>
          </div>
        </div>
        <el-divider />
        <div class="detail-section">
          <h4>企业简介</h4>
          <p>{{ currentCompany.description || '暂无简介' }}</p>
        </div>
        <div class="detail-section" v-if="currentCompany.contactName || currentCompany.contactPhone">
          <h4>联系方式</h4>
          <p v-if="currentCompany.contactName">联系人：{{ currentCompany.contactName }}</p>
          <p v-if="currentCompany.contactPhone">电话：{{ currentCompany.contactPhone }}</p>
          <p v-if="currentCompany.contactEmail">邮箱：{{ currentCompany.contactEmail }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="$router.push('/login')">登录查看职位</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const companies = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const currentCompany = ref(null)

const fetchCompanies = async () => {
  loading.value = true
  try {
    companies.value = await request.get('/companies') || []
  } catch (e) {
    companies.value = []
  } finally {
    loading.value = false
  }
}

const showDetail = (company) => {
  currentCompany.value = company
  dialogVisible.value = true
}

onMounted(fetchCompanies)
</script>

<style scoped>
.container { width: 1200px; margin: 0 auto; padding: 20px 0; }
.page-header { border-bottom: 2px solid #1e40af; margin-bottom: 20px; padding-bottom: 10px; display: flex; justify-content: space-between; align-items: flex-end; }
.page-title { margin: 0; color: #1e40af; }
.breadcrumb { font-size: 12px; color: #666; }

.company-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 20px; }
.company-card { background: white; border: 1px solid #eee; padding: 20px; text-align: center; cursor: pointer; transition: all 0.2s; border-radius: 4px; }
.company-card:hover { border-color: #1e40af; transform: translateY(-3px); box-shadow: 0 4px 12px rgba(0,0,0,0.05); }

.logo-box { width: 60px; height: 60px; background: #f0f7ff; color: #1e40af; font-size: 20px; font-weight: 700; display: flex; align-items: center; justify-content: center; border-radius: 12px; margin: 0 auto 16px; }
.name { font-size: 16px; color: #333; margin: 0 0 8px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.industry { font-size: 12px; color: #999; margin-bottom: 12px; }

/* Detail Dialog */
.detail-header { display: flex; gap: 16px; align-items: center; }
.detail-header h2 { margin: 0 0 8px; font-size: 20px; }
.detail-logo { width: 60px; height: 60px; background: #f0f7ff; color: #1e40af; font-size: 20px; font-weight: 700; display: flex; align-items: center; justify-content: center; border-radius: 12px; flex-shrink: 0; }
.detail-tags { display: flex; gap: 8px; flex-wrap: wrap; }
.detail-section { margin-bottom: 16px; }
.detail-section h4 { margin: 0 0 8px; font-size: 14px; color: #374151; }
.detail-section p { margin: 0 0 4px; color: #6b7280; line-height: 1.6; }
</style>
