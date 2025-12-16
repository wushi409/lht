<template>
  <div class="dashboard-page">
    <h2>欢迎回来，{{ userStore.userInfo?.username || '用户' }}</h2>
    
    <!-- Company Dashboard -->
    <div v-if="role === 'COMPANY'" class="dashboard-content">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #e0e7ff;">
              <el-icon :size="28" color="#4f46e5"><Briefcase /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.jobCount || 0 }}</div>
              <div class="stat-label">发布职位</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #d1fae5;">
              <el-icon :size="28" color="#059669"><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.applicationCount || 0 }}</div>
              <div class="stat-label">收到简历</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #fef3c7;">
              <el-icon :size="28" color="#d97706"><VideoCamera /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.interviewCount || 0 }}</div>
              <div class="stat-label">待处理面试</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-card class="quick-actions">
        <template #header>快捷操作</template>
        <el-button type="primary" @click="$router.push('/company/jobs')">
          <el-icon><Plus /></el-icon> 发布新职位
        </el-button>
        <el-button @click="$router.push('/company/applications')">
          <el-icon><Document /></el-icon> 处理简历
        </el-button>
        <el-button @click="$router.push('/company/interviews')">
          <el-icon><VideoCamera /></el-icon> 面试管理
        </el-button>
      </el-card>
    </div>

    <!-- Admin Dashboard -->
    <div v-else-if="role === 'ADMIN'" class="dashboard-content">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #fee2e2;">
              <el-icon :size="28" color="#dc2626"><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.pendingCompanyCount || 0 }}</div>
              <div class="stat-label">待审核企业</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #e0e7ff;">
              <el-icon :size="28" color="#4f46e5"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.studentCount || 0 }}</div>
              <div class="stat-label">注册学生</div>
            </div>
          </el-card>
        </el-col>
        <el-col :xs="24" :sm="8">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-icon" style="background: #f3e8ff;">
              <el-icon :size="28" color="#9333ea"><Briefcase /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.jobCount || 0 }}</div>
              <div class="stat-label">总职位数</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
      
      <el-card class="quick-actions">
        <template #header>系统管理</template>
        <el-button type="danger" @click="$router.push('/admin/companies')">
          <el-icon><Checked /></el-icon> 审核企业
        </el-button>
        <el-button type="primary" @click="$router.push('/admin/stats')">
          <el-icon><DataLine /></el-icon> 查看统计
        </el-button>
        <el-button @click="$router.push('/admin/announcements')">
          <el-icon><Notification /></el-icon> 发布公告
        </el-button>
      </el-card>
    </div>

    <!-- Student redirect -->
    <div v-else class="dashboard-content">
      <el-card>
        <p>正在跳转到个人中心...</p>
      </el-card>
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
  // TODO: 从后端获取统计数据
})
</script>

<style scoped>
.dashboard-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard-page h2 {
  margin: 0 0 24px 0;
  font-size: 24px;
  color: var(--text-main);
}

.stat-card {
  margin-bottom: 20px;
}

.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 24px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
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
  font-weight: 700;
  color: var(--text-main);
}

.stat-label {
  color: var(--text-secondary);
  font-size: 14px;
  margin-top: 4px;
}

.quick-actions {
  margin-top: 20px;
}

.quick-actions :deep(.el-card__body) {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}
</style>
