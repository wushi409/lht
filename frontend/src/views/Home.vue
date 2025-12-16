<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-overlay"></div>
      <div class="hero-content container">
        <h1 class="hero-title">连接高校人才与优质企业<br>开启您的职业生涯</h1>
        <p class="hero-subtitle">专业的校园招聘服务平台，为毕业生提供海量就业机会，为企业输送优秀人才。</p>
        <div class="hero-actions">
          <router-link to="/student/jobs">
            <el-button type="primary" size="large" class="hero-btn">我是学生，找工作</el-button>
          </router-link>
          <router-link to="/register/company">
            <el-button size="large" class="hero-btn-outline">我是企业，招人才</el-button>
          </router-link>
        </div>
        
        <div class="hero-stats">
          <div class="hero-stat-item">
            <div class="num">1000+</div>
            <div class="label">入驻企业</div>
          </div>
          <div class="divider"></div>
          <div class="hero-stat-item">
            <div class="num">5000+</div>
            <div class="label">在招职位</div>
          </div>
          <div class="divider"></div>
          <div class="hero-stat-item">
            <div class="num">98%</div>
            <div class="label">匹配成功率</div>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="section features">
      <div class="container">
        <div class="section-header">
          <h2>为什么选择我们</h2>
          <p>打造最专业的校园双选平台，提供全方位的就业服务</p>
        </div>
        
        <div class="feature-grid">
          <div class="feature-card">
            <div class="icon-box blue">
              <el-icon><User /></el-icon>
            </div>
            <h3>精准匹配</h3>
            <p>基于大数据分析，根据专业、技能和兴趣为您推荐最匹配的职位和人才。</p>
          </div>
          <div class="feature-card">
            <div class="icon-box purple">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <h3>名企云集</h3>
            <p>严选优质企业入驻，覆盖互联网、金融、教育等多个热门行业。</p>
          </div>
          <div class="feature-card">
            <div class="icon-box green">
              <el-icon><DataLine /></el-icon>
            </div>
            <h3>高效流程</h3>
            <p>在线投递、视频面试、实时反馈，让求职招聘流程更加高效便捷。</p>
          </div>
          <div class="feature-card">
            <div class="icon-box orange">
              <el-icon><Monitor /></el-icon>
            </div>
            <h3>双选会专场</h3>
            <p>定期举办线上线下双选会，打破地域限制，实现零距离沟通。</p>
          </div>
        </div>
      </div>
    </section>
    
    <!-- CTA Section -->
    <section class="section cta">
      <div class="container cta-content">
        <h2>准备好开始了吗？</h2>
        <p>立即注册，探索更多机会</p>
        <router-link to="/register/student">
          <el-button type="primary" size="large" class="cta-btn">免费注册</el-button>
        </router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { User, OfficeBuilding, DataLine, Monitor } from '@element-plus/icons-vue'
</script>

<style scoped>
.home-page {
  min-height: 100vh;
  background-color: var(--bg-body);
}

/* Hero Section */
.hero {
  position: relative;
  height: 600px;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  color: white;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 50%, rgba(59, 130, 246, 0.15) 0%, transparent 50%),
              radial-gradient(circle at 80% 50%, rgba(139, 92, 246, 0.15) 0%, transparent 50%);
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  max-width: 800px !important;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  background: linear-gradient(to right, #60a5fa, #a78bfa);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.hero-subtitle {
  font-size: 1.25rem;
  color: #94a3b8;
  margin-bottom: 2.5rem;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 1.5rem;
  justify-content: center;
  margin-bottom: 4rem;
}

.hero-btn {
  padding: 12px 36px;
  font-size: 1.1rem;
  height: auto;
  border-radius: 99px;
  font-weight: 600;
}

.hero-btn-outline {
  padding: 12px 36px;
  font-size: 1.1rem;
  height: auto;
  border-radius: 99px;
  background: transparent;
  border: 1px solid rgba(255,255,255,0.2);
  color: white;
  font-weight: 600;
}

.hero-btn-outline:hover {
  background: rgba(255,255,255,0.1);
  border-color: white;
  color: white;
}

.hero-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 3rem;
  padding: 2rem;
  background: rgba(255,255,255,0.05);
  border-radius: 1rem;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255,255,255,0.1);
}

.hero-stat-item {
  text-align: center;
}

.hero-stat-item .num {
  font-size: 2rem;
  font-weight: 700;
  color: white;
  margin-bottom: 0.25rem;
}

.hero-stat-item .label {
  color: #94a3b8;
  font-size: 0.875rem;
}

.divider {
  width: 1px;
  height: 40px;
  background: rgba(255,255,255,0.1);
}

/* Section Common */
.section {
  padding: 5rem 0;
}

.section-header {
  text-align: center;
  margin-bottom: 4rem;
}

.section-header h2 {
  font-size: 2.25rem;
  font-weight: 700;
  color: var(--text-main);
  margin-bottom: 1rem;
}

.section-header p {
  font-size: 1.125rem;
  color: var(--text-secondary);
}

/* Features */
.features {
  background-color: var(--bg-white);
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.feature-card {
  padding: 2.5rem 2rem;
  background: white;
  border-radius: 1rem;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-xl);
  border-color: var(--primary-light);
}

.icon-box {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  margin-bottom: 1.5rem;
  transition: transform 0.3s ease;
}

.feature-card:hover .icon-box {
  transform: scale(1.1) rotate(5deg);
}

.icon-box.blue { background: #eff6ff; color: #3b82f6; }
.icon-box.purple { background: #f3e8ff; color: #9333ea; }
.icon-box.green { background: #ecfdf5; color: #10b981; }
.icon-box.orange { background: #fff7ed; color: #f97316; }

.feature-card h3 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: var(--text-main);
}

.feature-card p {
  color: var(--text-regular);
  line-height: 1.6;
}

/* CTA */
.cta {
  background: var(--bg-body);
  padding: 6rem 0;
}

.cta-content {
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  border-radius: 2rem;
  padding: 4rem 2rem;
  text-align: center;
  color: white;
  position: relative;
  overflow: hidden;
}

.cta-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.1'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
}

.cta-content h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  position: relative;
}

.cta-content p {
  font-size: 1.25rem;
  margin-bottom: 2.5rem;
  opacity: 0.9;
  position: relative;
}

.cta-btn {
  background: white;
  color: var(--primary-color);
  border: none;
  padding: 12px 48px;
  font-weight: 600;
  font-size: 1.1rem;
  height: auto;
  border-radius: 99px;
  position: relative;
}

.cta-btn:hover {
  background: #f8fafc;
  transform: scale(1.05);
}

@media (max-width: 768px) {
  .hero-title { font-size: 2.5rem; }
  .hero-stats { flex-direction: column; gap: 1.5rem; }
  .divider { display: none; }
}
</style>
