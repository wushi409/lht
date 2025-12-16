<template>
  <div class="home-page-content">
    <!-- Banner Carousel -->
    <div class="banner-section">
      <el-carousel trigger="click" height="420px" :interval="5000">
        <el-carousel-item v-for="item in 3" :key="item">
           <div class="carousel-slide" :class="`slide-${item}`">
              <div class="container slide-content">
                <div class="slide-text">
                  <h2>2025届毕业生 <br>春季大型双选会</h2>
                  <p>汇聚知名企业，共创美好未来</p>
                  <el-button type="primary" size="large" class="slide-btn" @click="$router.push('/login')">立即报名参与</el-button>
                </div>
              </div>
           </div>
        </el-carousel-item>
      </el-carousel>
      
      <!-- Quick Stats Bar OVERLAY -->
      <div class="container stats-overlay-container">
        <div class="stats-bar">
           <div class="stat-box">
             <div class="stat-num">{{ stats.companyCount || 800 }}+</div>
             <div class="stat-name">合作单位</div>
           </div>
           <el-divider direction="vertical" class="stat-divider" />
           <div class="stat-box">
             <div class="stat-num">{{ stats.jobCount || 15000 }}+</div>
             <div class="stat-name">提供岗位</div>
           </div>
           <el-divider direction="vertical" class="stat-divider" />
           <div class="stat-box">
             <div class="stat-num">{{ stats.applicationCount || 5000 }}+</div>
             <div class="stat-name">达成就业意向</div>
           </div>
        </div>
      </div>
    </div>

    <!-- Main Content Grid -->
    <div class="container main-content">
      <div class="content-wrapper">
        <!-- Left Column -->
        <div class="left-col">
          <!-- Notification Section -->
          <div class="panel">
            <div class="panel-header">
              <h3 class="panel-title">通知公告</h3>
              <el-button link type="primary" @click="$router.push('/public/announcements')">更多 >></el-button>
            </div>
            <div class="panel-body">
              <div class="news-list" v-loading="loadingAnnouncements">
                <div v-for="item in announcements" :key="item.id" class="news-row">
                  <span class="news-dot"></span>
                  <a class="news-link">{{ item.title }}</a>
                  <span class="news-date">{{ formatDate(item.createdAt) }}</span>
                </div>
                <div v-if="!loadingAnnouncements && announcements.length === 0" class="empty-text">暂无公告</div>
              </div>
            </div>
          </div>

          <!-- Hot Jobs Section -->
          <div class="panel mt-4">
             <div class="panel-header">
              <h3 class="panel-title">最新职位</h3>
              <el-tabs v-model="activeJobTab" class="header-tabs" @tab-click="handleTabClick">
                <el-tab-pane label="名企推荐" name="hot"></el-tab-pane>
                <el-tab-pane label="最新发布" name="new"></el-tab-pane>
                <el-tab-pane label="实习生" name="intern"></el-tab-pane>
              </el-tabs>
            </div>
            <div class="job-list-detailed" v-loading="loadingJobs">
               <div v-for="job in hotJobs" :key="job.id" class="job-row" @click="showJobDetail(job)">
                  <div class="job-main-info">
                    <div class="job-row-title">{{ job.title }}</div>
                    <div class="job-row-tags">
                      <span class="job-tag">{{ job.location || '北京' }}</span>
                      <el-divider direction="vertical" />
                      <span class="job-tag">{{ job.degree || '本科' }}</span>
                      <el-divider direction="vertical" />
                      <span class="job-tag">{{ job.jobType || '全职' }}</span>
                    </div>
                  </div>
                  <div class="job-company-info">{{ job.company?.name }}</div>
                  <div class="job-salary-info">{{ job.salaryRange || '面议' }}</div>
                  <div class="job-date-info">{{ formatDate(job.createdAt) }}</div>
               </div>
            </div>
          </div>
        </div>

        <!-- Right Column -->
        <div class="right-col">
          <!-- Login Box -->
          <div class="login-card">
            <h3 class="login-title">用户登录</h3>
            <div class="login-actions">
              <el-button type="primary" class="login-btn" @click="$router.push('/login')">学生登录</el-button>
              <el-button class="login-btn enterprise-btn" @click="$router.push('/login')">企业登录</el-button>
            </div>
            <div class="register-links">
              还没有账号？<span class="text-primary cursor-pointer" @click="$router.push('/register/student')">立即注册</span>
            </div>
          </div>

           <!-- Company Wall -->
          <div class="side-panel mt-4">
             <div class="side-header">
               <span>知名企业</span>
               <el-button link size="small" @click="$router.push('/public/companies')">更多</el-button>
             </div>
             <div class="company-wall" v-loading="loadingCompanies">
                <div v-for="company in companies" :key="company.id" class="company-logo-box" @click="showCompanyDetail(company)">
                   {{ (company.name || '企').substring(0,2) }}
                </div>
             </div>
          </div>

        </div>
      </div>
    </div>
    
    <!-- 职位详情弹窗 -->
    <el-dialog v-model="jobDialogVisible" title="职位详情" width="600px">
      <div v-if="currentJob">
        <div class="dialog-header">
          <h2>{{ currentJob.title }}</h2>
          <span class="dialog-salary">{{ currentJob.salaryRange || '面议' }}</span>
        </div>
        <p class="dialog-company">{{ currentJob.company?.name }} | {{ currentJob.location || '不限' }}</p>
        <div class="dialog-tags">
          <el-tag>{{ currentJob.location || '不限' }}</el-tag>
          <el-tag type="info">{{ currentJob.jobType || '全职' }}</el-tag>
          <el-tag type="info" v-if="currentJob.headcount">招{{ currentJob.headcount }}人</el-tag>
        </div>
        <el-divider />
        <div class="dialog-section">
          <h4>职位描述</h4>
          <p>{{ currentJob.description || '暂无描述' }}</p>
        </div>
        <div class="dialog-section" v-if="currentJob.skills">
          <h4>技能要求</h4>
          <p>{{ currentJob.skills }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="jobDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="$router.push('/login')">登录投递</el-button>
      </template>
    </el-dialog>

    <!-- 企业详情弹窗 -->
    <el-dialog v-model="companyDialogVisible" title="企业详情" width="550px">
      <div v-if="currentCompany">
        <div class="dialog-header">
          <div class="dialog-logo">{{ (currentCompany.name || '企').substring(0,2) }}</div>
          <div>
            <h2>{{ currentCompany.name }}</h2>
            <div class="dialog-tags">
              <el-tag>{{ currentCompany.industry || '未分类' }}</el-tag>
              <el-tag type="info">{{ currentCompany.scale || '规模未知' }}</el-tag>
            </div>
          </div>
        </div>
        <el-divider />
        <div class="dialog-section">
          <h4>企业简介</h4>
          <p>{{ currentCompany.description || '暂无简介' }}</p>
        </div>
        <div class="dialog-section" v-if="currentCompany.contactName || currentCompany.contactPhone">
          <h4>联系方式</h4>
          <p v-if="currentCompany.contactName">联系人：{{ currentCompany.contactName }}</p>
          <p v-if="currentCompany.contactPhone">电话：{{ currentCompany.contactPhone }}</p>
          <p v-if="currentCompany.contactEmail">邮箱：{{ currentCompany.contactEmail }}</p>
        </div>
      </div>
      <template #footer>
        <el-button @click="companyDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="$router.push('/login')">登录查看职位</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'


const activeJobTab = ref('hot')
const stats = ref({})
const hotJobs = ref([])
const companies = ref([])
const announcements = ref([])

const loadingJobs = ref(false)
const loadingAnnouncements = ref(false)
const loadingCompanies = ref(false)

const jobDialogVisible = ref(false)
const companyDialogVisible = ref(false)
const currentJob = ref(null)
const currentCompany = ref(null)

const handleTabClick = () => {
  // Mock tab switch behavior
}

const showJobDetail = (job) => {
  currentJob.value = job
  jobDialogVisible.value = true
}

const showCompanyDetail = (company) => {
  currentCompany.value = company
  companyDialogVisible.value = true
}

const formatDateTime = (date) => {
  if (!date) return ''
  return new Date(date).toISOString().split('T')[0]
}
const formatDate = formatDateTime

const fetchData = async () => {
  try {
    stats.value = await request.get('/public/stats') || {}
  } catch (e) { console.error(e) }

  loadingJobs.value = true
  try {
    const jobs = await request.get('/jobs') || []
    hotJobs.value = jobs.slice(0, 8)
  } catch (e) {
    hotJobs.value = []
  } finally {
    loadingJobs.value = false
  }

  loadingAnnouncements.value = true
  try {
    const list = await request.get('/announcements') || []
    announcements.value = list.slice(0, 6)
  } catch (e) {
    announcements.value = []
  } finally {
    loadingAnnouncements.value = false
  }

  loadingCompanies.value = true
  try {
    const list = await request.get('/companies') || []
    companies.value = list.slice(0, 12)
  } catch (e) {
    companies.value = []
  } finally {
    loadingCompanies.value = false
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
/* Only content specific styles */
.container {
  width: 1200px;
  margin: 0 auto;
}
.w-full { width: 100%; }
.mb-2 { margin-bottom: 8px; }
.mt-4 { margin-top: 16px; }
.text-primary { color: #1e40af; }
.text-gray { color: #666; }
.cursor-pointer { cursor: pointer; }

/* Banner */
.banner-section {
  position: relative;
  background: #fff;
}
.carousel-slide {
  height: 100%;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
}
.slide-1 { background-image: linear-gradient(120deg, #a1c4fd 0%, #c2e9fb 100%); }
.slide-2 { background-image: linear-gradient(120deg, #d4fc79 0%, #96e6a1 100%); }
.slide-3 { background-image: linear-gradient(120deg, #84fab0 0%, #8fd3f4 100%); }

.slide-content { z-index: 2; position: relative; }
.slide-text h2 {
  font-size: 48px;
  color: #1e3a8a;
  margin-bottom: 20px;
  font-weight: 800;
  text-shadow: 0 2px 10px rgba(0,0,0,0.05);
}
.slide-text p {
  font-size: 20px;
  color: #333;
  margin-bottom: 30px;
}
.slide-btn { padding: 12px 36px; font-weight: 600; }

.stats-overlay-container {
  position: relative;
  height: 0;
  z-index: 10;
}
.stats-bar {
  position: absolute;
  top: -60px;
  right: 0;
  background: white;
  padding: 20px 40px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.08);
  border-radius: 4px;
  display: flex;
  align-items: center;
}
.stat-box {
  text-align: center;
  min-width: 120px;
}
.stat-num {
  font-size: 28px;
  font-weight: 700;
  color: #f56c6c;
}
.stat-name {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}
.stat-divider {
  height: 40px;
  background-color: #eee;
}

/* Base Layout */
.main-content {
  padding: 40px 0;
}
.content-wrapper {
  display: flex;
  gap: 20px;
}
.left-col { flex: 1; min-width: 0; }
.right-col { width: 300px; flex-shrink: 0; }

/* Panel Styles */
.panel {
  background: white;
  border-top: 3px solid #1e40af; /* Formal Blue Top Border */
  padding: 20px 24px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.05);
}
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 12px;
  margin-bottom: 16px;
}
.panel-title {
  margin: 0;
  font-size: 18px;
  color: #333;
  border-left: 4px solid #1e40af;
  padding-left: 10px;
  line-height: 1;
}

/* News List */
.news-row {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px dashed #eee;
}
.news-row:hover .news-link { color: #1e40af; }
.news-dot {
  width: 4px;
  height: 4px;
  background: #ccc;
  border-radius: 50%;
  margin-right: 12px;
}
.news-link {
  flex: 1;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-right: 12px;
}
.news-date {
  font-size: 12px;
  color: #999;
  font-family: monospace;
}
.empty-text {
  padding: 20px;
  text-align: center;
  color: #999;
}

/* Job List */
.job-list-detailed {
  display: block;
}
.job-row {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}
.job-row:hover {
  background: #f9fafc;
  padding-left: 10px;
  padding-right: 10px;
  margin: 0 -10px;
}
.job-main-info { flex: 1; }
.job-row-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}
.job-row-title:hover { color: #1e40af; }
.job-row-tags {
  font-size: 12px;
  color: #888;
}
.job-company-info {
  width: 200px;
  font-size: 14px;
  color: #666;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.job-salary-info {
  width: 120px;
  font-size: 16px;
  font-weight: 700;
  color: #f56c6c;
  text-align: right;
  padding-right: 20px;
}
.job-date-info {
  width: 100px;
  text-align: right;
  color: #999;
  font-size: 12px;
}

/* Login Card */
.login-card {
  background: white;
  padding: 24px;
  border-top: 3px solid #1e40af;
  box-shadow: 0 2px 6px rgba(0,0,0,0.05);
}
.login-title {
  font-size: 18px;
  margin: 0 0 20px;
  text-align: center;
  font-weight: 600;
  color: #1e40af;
}
.login-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
:deep(.login-btn) {
  width: 100%;
  height: 44px;
  font-size: 15px;
  border-radius: 4px;
  padding: 12px 20px;
}
:deep(.login-btn.el-button--primary) {
  background-color: #1e40af;
  border-color: #1e40af;
}
:deep(.enterprise-btn) {
  background-color: white;
  border: 1px solid #1e40af;
  color: #1e40af;
}
:deep(.enterprise-btn:hover) {
  background-color: #f0f7ff;
  border-color: #1e40af;
  color: #1e40af;
}
.register-links {
  margin-top: 16px;
  text-align: center;
  font-size: 13px;
  color: #666;
}

/* Side Panels */
.side-panel {
  background: white;
  padding: 16px;
}
.side-header {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  border-left: 3px solid #1e40af;
  padding-left: 10px;
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* Company Wall */
.company-wall {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.company-logo-box {
  width: calc(33.33% - 7px);
  aspect-ratio: 1;
  background: #f4f6f8;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-weight: 700;
  font-size: 14px;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.2s;
}
.company-logo-box:hover {
  background: #e8f4ff;
  color: #1e40af;
  border-color: #1e40af;
}

/* Dialog Styles */
.dialog-header { display: flex; gap: 16px; align-items: flex-start; margin-bottom: 8px; }
.dialog-header h2 { margin: 0 0 8px; font-size: 20px; color: #1f2937; }
.dialog-salary { color: #f56c6c; font-size: 18px; font-weight: 600; }
.dialog-company { color: #6b7280; margin: 0 0 12px; }
.dialog-tags { display: flex; gap: 8px; flex-wrap: wrap; }
.dialog-logo { width: 60px; height: 60px; background: #f0f7ff; color: #1e40af; font-size: 20px; font-weight: 700; display: flex; align-items: center; justify-content: center; border-radius: 12px; flex-shrink: 0; }
.dialog-section { margin-bottom: 16px; }
.dialog-section h4 { margin: 0 0 8px; font-size: 14px; color: #374151; }
.dialog-section p { margin: 0 0 4px; color: #6b7280; line-height: 1.6; white-space: pre-wrap; }
</style>
