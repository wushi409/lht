<template>
  <div class="page-container">
    <div class="welcome-banner">
      <div class="welcome-content">
        <h1>早安，{{ userInfo.name || userInfo.username || '同学' }} 👋</h1>
        <p>祝你今天求职顺利！这里是你的求职控制台。</p>
      </div>
      <div class="welcome-image">
        <img src="https://illustrations.popsy.co/amber/student-going-to-school.svg" alt="Student">
      </div>
    </div>

    <el-row :gutter="24">
      <el-col :md="16">
        <!-- Stats Row -->
        <el-row :gutter="20" class="mb-6">
          <el-col :span="8">
            <div class="stat-card blue">
              <div class="stat-icon">
                <el-icon><Tickets /></el-icon>
              </div>
              <div class="stat-value">12</div>
              <div class="stat-label">已投递</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-card green">
              <div class="stat-icon">
                <el-icon><VideoCamera /></el-icon>
              </div>
              <div class="stat-value">3</div>
              <div class="stat-label">待面试</div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="stat-card orange">
              <div class="stat-icon">
                <el-icon><Star /></el-icon>
              </div>
              <div class="stat-value">5</div>
              <div class="stat-label">收藏职位</div>
            </div>
          </el-col>
        </el-row>

        <!-- Recommended Jobs -->
        <div class="section-card">
          <div class="section-header">
            <h3>推荐职位</h3>
            <el-button link type="primary" @click="$router.push('/student/jobs')">查看更多</el-button>
          </div>
          <div class="job-list">
            <div v-for="i in 3" :key="i" class="job-item">
              <div class="job-logo">
                <el-avatar shape="square" :size="48" src="https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png" />
              </div>
              <div class="job-info">
                <h4>前端开发工程师</h4>
                <p>字节跳动 · 北京 · 15k-25k</p>
              </div>
              <el-button size="small" round>投递</el-button>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :md="8">
        <!-- Profile Card -->
        <div class="profile-card mb-6">
          <div class="profile-header">
            <el-avatar :size="64" :src="userInfo.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
            <h3>{{ userInfo.name || userInfo.username }}</h3>
            <p>{{ userInfo.college || '计算机学院' }}</p>
          </div>
          <div class="resume-status">
            <span>简历完善度</span>
            <el-progress :percentage="80" :color="customColors" />
          </div>
          <el-button type="primary" plain block class="w-full mt-4" @click="$router.push('/student/profile')">
            编辑简历
          </el-button>
        </div>

        <!-- Upcoming Events -->
        <div class="section-card">
          <div class="section-header">
            <h3>近期双选会</h3>
          </div>
          <div class="event-list">
            <div class="event-item">
              <div class="event-date">
                <span class="month">12月</span>
                <span class="day">20</span>
              </div>
              <div class="event-info">
                <h4>秋季大型校园双选会</h4>
                <p>体育馆主馆</p>
              </div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { Tickets, VideoCamera, Star } from '@element-plus/icons-vue'

const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo || {})

const customColors = [
  { color: '#f56c6c', percentage: 20 },
  { color: '#e6a23c', percentage: 40 },
  { color: '#5cb87a', percentage: 60 },
  { color: '#1989fa', percentage: 80 },
  { color: '#6f7ad3', percentage: 100 },
]
</script>

<style scoped>
.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.welcome-banner {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  border-radius: 16px;
  padding: 32px 40px;
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 25px -5px rgba(59, 130, 246, 0.4);
}

.welcome-banner::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background-image: radial-gradient(circle at 20% 120%, rgba(255,255,255,0.2) 0%, transparent 50%);
}

.welcome-content {
  position: relative;
  z-index: 1;
}

.welcome-content h1 {
  margin: 0 0 8px 0;
  font-size: 2rem;
  font-weight: 700;
}

.welcome-content p {
  margin: 0;
  opacity: 0.9;
  font-size: 1.1rem;
}

.welcome-image img {
  height: 120px;
  width: auto;
  display: block;
}

/* Stats */
.stat-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
  transition: all 0.3s ease;
  border: 1px solid var(--border-color);
  height: 140px;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  margin-bottom: 12px;
}

.stat-card.blue .stat-icon { background: #eff6ff; color: #3b82f6; }
.stat-card.green .stat-icon { background: #ecfdf5; color: #10b981; }
.stat-card.orange .stat-icon { background: #fff7ed; color: #f97316; }

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-main);
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
  margin-bottom: 24px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h3 {
  margin: 0;
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-main);
}

.job-item {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid var(--border-color);
}

.job-item:last-child {
  border-bottom: none;
}

.job-logo {
  margin-right: 16px;
}

.job-info {
  flex: 1;
}

.job-info h4 {
  margin: 0 0 4px 0;
  font-size: 1rem;
  color: var(--text-main);
}

.job-info p {
  margin: 0;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

/* Profile Card */
.profile-card {
  background: white;
  border-radius: 16px;
  padding: 32px 24px;
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  text-align: center;
}

.profile-header h3 {
  margin: 16px 0 4px 0;
  font-size: 1.25rem;
  color: var(--text-main);
}

.profile-header p {
  margin: 0 0 24px 0;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.resume-status {
  text-align: left;
}

.resume-status span {
  display: block;
  font-size: 0.875rem;
  color: var(--text-regular);
  margin-bottom: 8px;
}

/* Event List */
.event-item {
  display: flex;
  align-items: center;
  padding: 12px;
  background: #f8fafc;
  border-radius: 12px;
  margin-bottom: 12px;
}

.event-date {
  background: white;
  padding: 8px 12px;
  border-radius: 8px;
  text-align: center;
  margin-right: 16px;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

.event-date .month {
  display: block;
  font-size: 12px;
  color: var(--text-secondary);
  text-transform: uppercase;
}

.event-date .day {
  display: block;
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color);
}

.event-info h4 {
  margin: 0 0 4px 0;
  font-size: 0.95rem;
  color: var(--text-main);
}

.event-info p {
  margin: 0;
  font-size: 0.8rem;
  color: var(--text-secondary);
}

.mb-6 { margin-bottom: 1.5rem; }
.w-full { width: 100%; }
</style>