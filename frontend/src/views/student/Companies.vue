<template>
  <div class="companies-page">
    <div class="container">
      <h2>参会企业</h2>
      
      <div class="company-list" v-loading="loading">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :lg="8" v-for="company in companies" :key="company.id">
            <el-card class="company-card" shadow="hover" @click="viewCompany(company)">
              <div class="company-header">
                <div class="company-avatar">
                  {{ (company.name || '企').charAt(0) }}
                </div>
                <div class="company-info">
                  <h3>{{ company.name }}</h3>
                  <span class="company-industry">{{ company.industry || '未分类' }}</span>
                </div>
              </div>
              <div class="company-meta">
                <el-tag size="small" type="info">{{ company.scale || '规模未知' }}</el-tag>
              </div>
              <p class="company-desc">{{ company.description || '暂无简介' }}</p>
            </el-card>
          </el-col>
        </el-row>
        
        <el-empty v-if="!loading && companies.length === 0" description="暂无参会企业" />
      </div>

      <!-- 企业详情对话框 -->
      <el-dialog v-model="dialogVisible" title="企业详情" width="600px">
        <div v-if="currentCompany" class="company-detail">
          <div class="detail-header">
            <div class="detail-avatar">
              {{ (currentCompany.name || '企').charAt(0) }}
            </div>
            <div class="detail-info">
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
          
          <div class="detail-section" v-if="currentCompany.contactName">
            <h4>联系方式</h4>
            <p>联系人：{{ currentCompany.contactName }}</p>
            <p v-if="currentCompany.contactPhone">电话：{{ currentCompany.contactPhone }}</p>
            <p v-if="currentCompany.contactEmail">邮箱：{{ currentCompany.contactEmail }}</p>
          </div>
        </div>
        <template #footer>
          <el-button @click="dialogVisible = false">关闭</el-button>
          <el-button type="primary" @click="viewJobs">查看职位</el-button>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { listCompanies } from '@/api/student'

const router = useRouter()
const companies = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const currentCompany = ref(null)

const fetchCompanies = async () => {
  loading.value = true
  try {
    const data = await listCompanies()
    companies.value = data || []
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const viewCompany = (company) => {
  currentCompany.value = company
  dialogVisible.value = true
}

const viewJobs = () => {
  dialogVisible.value = false
  router.push({ path: '/student/jobs', query: { keyword: currentCompany.value?.name } })
}

onMounted(() => {
  fetchCompanies()
})
</script>

<style scoped>
.companies-page {
  padding: 30px 20px;
  min-height: calc(100vh - 64px - 200px);
}

.companies-page h2 {
  margin: 0 0 20px 0;
  font-size: 28px;
  color: var(--text-main);
}

.company-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.company-card:hover {
  transform: translateY(-3px);
}

.company-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 12px;
}

.company-avatar {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  flex-shrink: 0;
}

.company-info h3 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: var(--text-main);
}

.company-industry {
  color: var(--text-secondary);
  font-size: 13px;
}

.company-meta {
  margin-bottom: 10px;
}

.company-desc {
  color: var(--text-secondary);
  font-size: 14px;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  margin: 0;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 20px;
}

.detail-avatar {
  width: 70px;
  height: 70px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
}

.detail-info h2 {
  margin: 0 0 8px 0;
  font-size: 22px;
}

.detail-tags {
  display: flex;
  gap: 8px;
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
  margin: 5px 0;
}
</style>
