<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1>🎓 校园双选会</h1>
        <p class="hero-subtitle">连接高校人才与优质企业，开启您的职业生涯</p>
        <div class="hero-actions">
          <router-link to="/student/jobs">
            <el-button type="primary" size="large" round>浏览职位</el-button>
          </router-link>
          <router-link to="/register/student">
            <el-button size="large" round plain>学生注册</el-button>
          </router-link>
          <router-link to="/register/company">
            <el-button size="large" round plain>企业入驻</el-button>
          </router-link>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features">
      <div class="container">
        <h2 class="section-title">平台优势</h2>
        <div class="feature-grid">
          <div class="feature-card">
            <div class="feature-icon" style="background: #e0e7ff;">
              <el-icon :size="32" color="#4f46e5"><User /></el-icon>
            </div>
            <h3>精准匹配</h3>
            <p>根据专业和技能，为您推荐最合适的职位和人才</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon" style="background: #d1fae5;">
              <el-icon :size="32" color="#059669"><OfficeBuilding /></el-icon>
            </div>
            <h3>名企入驻</h3>
            <p>汇聚各行业领军企业，提供海量优质就业机会</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon" style="background: #fef3c7;">
              <el-icon :size="32" color="#d97706"><DataLine /></el-icon>
            </div>
            <h3>高效流程</h3>
            <p>简历投递、面试安排、结果反馈，全流程在线化</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="stats">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">1000+</div>
            <div class="stat-label">注册学生</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">200+</div>
            <div class="stat-label">入驻企业</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">500+</div>
            <div class="stat-label">在招职位</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">95%</div>
            <div class="stat-label">满意度</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { User, OfficeBuilding, DataLine } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 100px 20px;
  text-align: center;
}

.hero-content h1 {
  font-size: 48px;
  margin: 0 0 20px 0;
  font-weight: 700;
}

.hero-subtitle {
  font-size: 20px;
  margin-bottom: 40px;
  opacity: 0.9;
}

.hero-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  flex-wrap: wrap;
}

.features {
  padding: 80px 20px;
  background: white;
}

.section-title {
  text-align: center;
  font-size: 32px;
  margin-bottom: 50px;
  color: var(--text-main);
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 30px;
  max-width: 1000px;
  margin: 0 auto;
}

.feature-card {
  text-align: center;
  padding: 40px 30px;
  border-radius: 12px;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.feature-card:hover {
  box-shadow: var(--shadow-lg);
  transform: translateY(-5px);
}

.feature-icon {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 10px;
  color: var(--text-main);
}

.feature-card p {
  color: var(--text-secondary);
  line-height: 1.6;
}

.stats {
  background: #f8fafc;
  padding: 60px 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 30px;
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: var(--primary-color);
}

.stat-label {
  color: var(--text-secondary);
  margin-top: 5px;
}
</style>
