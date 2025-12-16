<template>
  <div class="page-container">
    <div class="dashboard-header">
      <h2>工作台</h2>
      <p>欢迎回来，{{ userStore.userInfo?.username }}</p>
    </div>

    <!-- Company Dashboard -->
    <template v-if="role === 'COMPANY'">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <div class="stat-card blue">
            <div class="stat-icon"><el-icon><Briefcase /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.jobCount || 0 }}</div>
              <div class="stat-label">在招职位</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-card green">
            <div class="stat-icon"><el-icon><Document /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.applicationCount || 0 }}</div>
              <div class="stat-label">收到简历</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-card orange">
            <div class="stat-icon"><el-icon><VideoCamera /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.interviewCount || 0 }}</div>
              <div class="stat-label">面试安排</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <div class="section-card mt-6">
        <div class="section-header"><h3>快捷操作</h3></div>
        <div class="action-grid">
          <div class="action-item" @click="$router.push('/company/jobs')">
            <div class="action-icon blue"><el-icon><Plus /></el-icon></div>
            <span>发布职位</span>
          </div>
          <div class="action-item" @click="$router.push('/company/applications')">
            <div class="action-icon purple"><el-icon><Document /></el-icon></div>
            <span>筛选简历</span>
          </div>
          <div class="action-item" @click="$router.push('/company/interviews')">
            <div class="action-icon orange"><el-icon><VideoCamera /></el-icon></div>
            <span>面试管理</span>
          </div>
        </div>
      </div>
    </template>

    <!-- Admin Dashboard -->
    <template v-else-if="role === 'ADMIN'">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <div class="stat-card red">
            <div class="stat-icon"><el-icon><OfficeBuilding /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingCompanyCount || 0 }}</div>
              <div class="stat-label">待审核企业</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-card blue">
            <div class="stat-icon"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.studentCount || 0 }}</div>
              <div class="stat-label">注册学生</div>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="stat-card purple">
            <div class="stat-icon"><el-icon><Briefcase /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.jobCount || 0 }}</div>
              <div class="stat-label">平台职位</div>
            </div>
          </div>
        </el-col>
      </el-row>

      <div class="section-card mt-6">
        <div class="section-header"><h3>系统管理</h3></div>
        <div class="action-grid">
          <div class="action-item" @click="$router.push('/admin/companies')">
            <div class="action-icon red"><el-icon><Checked /></el-icon></div>
            <span>审核企业</span>
          </div>
          <div class="action-item" @click="$router.push('/admin/stats')">
            <div class="action-icon blue"><el-icon><DataLine /></el-icon></div>
            <span>数据统计</span>
          </div>
          <div class="action-item" @click="$router.push('/admin/announcements')">
            <div class="action-icon green"><el-icon><Notification /></el-icon></div>
            <span>发布公告</span>
          </div>
        </div>
      </div>
    </template>

    <div v-else>
      <el-empty description="正在跳转..." />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { Briefcase, Document, VideoCamera, Plus, OfficeBuilding, User, Checked, DataLine, Notification } from '@element-plus/icons-vue'

const userStore = useUserStore()
const role = computed(() => userStore.role)
const stats = ref({})

onMounted(() => {
  // Mock data for now
  if (role.value === 'COMPANY') {
    stats.value = { jobCount: 5, applicationCount: 12, interviewCount: 3 }
  } else if (role.value === 'ADMIN') {
    stats.value = { pendingCompanyCount: 2, studentCount: 156, jobCount: 342 }
  }
})
</script>

<style scoped>
.page-container {
  padding: 24px;
}

.dashboard-header {
  margin-bottom: 32px;
}

.dashboard-header h2 {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--text-main);
  margin: 0 0 8px 0;
}

.dashboard-header p {
  color: var(--text-secondary);
  margin: 0;
}

/* Stat Cards */
.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  height: 100%;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-card.blue .stat-icon { background: #eff6ff; color: #3b82f6; }
.stat-card.green .stat-icon { background: #ecfdf5; color: #10b981; }
.stat-card.orange .stat-icon { background: #fff7ed; color: #f97316; }
.stat-card.red .stat-icon { background: #fef2f2; color: #ef4444; }
.stat-card.purple .stat-icon { background: #f3e8ff; color: #9333ea; }

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-main);
  line-height: 1;
  margin-bottom: 4px;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 14px;
}

/* Section Card */
.section-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
}

.section-header {
  margin-bottom: 24px;
  border-bottom: 1px solid var(--border-color);
  padding-bottom: 16px;
}

.section-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: var(--text-main);
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 24px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  padding: 16px;
  border-radius: 12px;
  transition: all 0.2s;
}

.action-item:hover {
  background: #f8fafc;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 12px;
}

.action-icon.blue { background: #eff6ff; color: #3b82f6; }
.action-icon.purple { background: #f3e8ff; color: #9333ea; }
.action-icon.orange { background: #fff7ed; color: #f97316; }
.action-icon.red { background: #fef2f2; color: #ef4444; }
.action-icon.green { background: #ecfdf5; color: #10b981; }

.action-item span {
  font-size: 0.9rem;
  color: var(--text-main);
  font-weight: 500;
}

.mt-6 { margin-top: 1.5rem; }
</style>
