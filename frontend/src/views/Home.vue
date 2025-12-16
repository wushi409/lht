<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="container hero-content">
        <div class="hero-text">
          <h1 class="hero-title">连接未来，<span class="highlight">职</span>等你来</h1>
          <p class="hero-subtitle">
            汇聚优质名企，助力高校毕业生高质量就业。这里有数千个职位等你挑选，一站式双选服务平台。
          </p>
          <div class="hero-actions">
            <router-link to="/student/jobs" class="btn btn-lg btn-primary">立即求职</router-link>
            <router-link to="/register/company" class="btn btn-lg btn-outline">企业入驻</router-link>
          </div>
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-num">500+</span>
              <span class="stat-label">合作企业</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">2000+</span>
              <span class="stat-label">在招职位</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">10k+</span>
              <span class="stat-label">投递简历</span>
            </div>
          </div>
        </div>
        <div class="hero-image">
          <div class="hero-blob"></div>
          <img src="https://img.freepik.com/free-vector/job-hunt-concept-illustration_114360-446.jpg?w=740&t=st=1700000000~exp=1700003600~hmac=xxxxx" alt="Job Fair" class="hero-img-main" />
        </div>
      </div>
    </section>

    <!-- Search Section -->
    <section class="search-section">
      <div class="container">
        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索职位名称、公司或关键词..."
            class="search-input"
            size="large"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
            <template #append>
              <el-button type="primary" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
          <div class="hot-tags">
            <span>热门搜索：</span>
            <a href="#" @click.prevent="quickSearch('Java')">Java开发</a>
            <a href="#" @click.prevent="quickSearch('前端')">前端工程师</a>
            <a href="#" @click.prevent="quickSearch('运营')">产品运营</a>
            <a href="#" @click.prevent="quickSearch('管培生')">管培生</a>
          </div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <div class="section-header">
          <h2>平台特色服务</h2>
          <p>为学生和企业提供全流程、智能化的双选服务体验</p>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon icon-1"><el-icon><Monitor /></el-icon></div>
            <h3>在线投递</h3>
            <p>海量职位一键投递，实时追踪面试进度，求职更高效。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon icon-2"><el-icon><VideoCamera /></el-icon></div>
            <h3>视频面试</h3>
            <p>支持远程视频面试，打破地域限制，随时随地连接机会。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon icon-3"><el-icon><DataAnalysis /></el-icon></div>
            <h3>智能推荐</h3>
            <p>基于大数据分析，精准匹配人岗需求，提升求职成功率。</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon icon-4"><el-icon><School /></el-icon></div>
            <h3>双选活动</h3>
            <p>线上线下双选会无缝融合，现场签到、活动报名一站搞定。</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Latest Jobs -->
    <section class="jobs-section">
      <div class="container">
        <div class="section-header-flex">
          <h2>最新优选职位</h2>
          <router-link to="/student/jobs" class="view-more">查看更多 <el-icon><ArrowRight /></el-icon></router-link>
        </div>
        
        <div class="jobs-grid">
          <!-- Mock Data for now, later fetch from API -->
          <div v-for="i in 6" :key="i" class="job-card" @click="$router.push('/student/jobs')">
            <div class="job-header">
              <h3 class="job-title">{{ ['Java开发工程师', '产品经理', 'UI设计师', '新媒体运营', '人力资源专员', '销售经理'][i-1] }}</h3>
              <span class="job-salary">{{ ['12-20k', '15-25k', '10-18k', '8-12k', '6-10k', '10-20k'][i-1] }}</span>
            </div>
            <div class="job-tags">
              <span class="tag">本科</span>
              <span class="tag">全职</span>
              <span class="tag">广州</span>
            </div>
            <div class="job-company">
              <el-avatar :size="32" src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png" />
              <span class="company-name">某知名互联网科技公司</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Call to Action -->
    <section class="cta-section">
      <div class="container cta-content">
        <h2>准备好开启你的职业生涯了吗？</h2>
        <p>立即注册加入，发现更多机会。</p>
        <router-link to="/register/student" class="btn btn-lg btn-white">免费注册</router-link>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Monitor, VideoCamera, DataAnalysis, School, ArrowRight } from '@element-plus/icons-vue'

const router = useRouter()
const searchKeyword = ref('')

const handleSearch = () => {
  if (searchKeyword.value) {
    router.push({ path: '/student/jobs', query: { keyword: searchKeyword.value } })
  }
}

const quickSearch = (keyword) => {
  searchKeyword.value = keyword
  handleSearch()
}
</script>

<style scoped>
.home-page {
  background-color: var(--bg-body);
}

.hero-section {
  background: linear-gradient(135deg, #fdfbfb 0%, #ebedee 100%);
  padding: 4rem 0;
  position: relative;
  overflow: hidden;
}

.hero-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 2rem;
  z-index: 1;
  position: relative;
}

.hero-text {
  flex: 1;
  max-width: 600px;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  line-height: 1.2;
  margin-bottom: 1.5rem;
  color: var(--text-main);
  letter-spacing: -0.02em;
}

.hero-title .highlight {
  color: var(--primary-color);
  position: relative;
}

.hero-title .highlight::after {
  content: '';
  position: absolute;
  bottom: 5px;
  left: 0;
  width: 100%;
  height: 12px;
  background-color: rgba(79, 70, 229, 0.2);
  z-index: -1;
  border-radius: 4px;
}

.hero-subtitle {
  font-size: 1.125rem;
  color: var(--text-secondary);
  margin-bottom: 2.5rem;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 1rem;
  margin-bottom: 3rem;
}

.btn-lg {
  padding: 0.75rem 2rem;
  font-size: 1.125rem;
  border-radius: var(--radius-md);
  text-decoration: none;
  display: inline-block;
  transition: all 0.3s;
}

.btn-outline {
  background: transparent;
  border: 2px solid var(--primary-color);
  color: var(--primary-color);
  font-weight: 600;
}

.btn-outline:hover {
  background: rgba(79, 70, 229, 0.05);
}

.hero-stats {
  display: flex;
  gap: 3rem;
  border-top: 1px solid rgba(0,0,0,0.1);
  padding-top: 2rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--text-main);
}

.stat-label {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.hero-image {
  flex: 1;
  display: flex;
  justify-content: center;
  position: relative;
}

.hero-img-main {
  max-width: 100%;
  height: auto;
  border-radius: 20px;
  box-shadow: var(--shadow-lg);
  position: relative;
  z-index: 2;
}

.search-section {
  background: white;
  padding: 2rem 0;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  margin-top: -3rem;
  position: relative;
  z-index: 10;
  border-radius: 0;
}

.search-box {
  max-width: 800px;
  margin: 0 auto;
  text-align: center;
}

.search-input :deep(.el-input__wrapper) {
  padding-left: 1rem;
  border-radius: var(--radius-md) 0 0 var(--radius-md);
  box-shadow: none;
  border: 1px solid var(--border-color);
}

.search-input :deep(.el-input-group__append) {
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 0 var(--radius-md) var(--radius-md) 0;
}

.hot-tags {
  margin-top: 1rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.hot-tags a {
  margin: 0 0.5rem;
  color: var(--text-regular);
  text-decoration: none;
  background: #f3f4f6;
  padding: 2px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.hot-tags a:hover {
  color: var(--primary-color);
  background: var(--primary-light);
}

.features-section {
  padding: 5rem 0;
  background-color: white;
}

.section-header {
  text-align: center;
  margin-bottom: 3rem;
}

.section-header h2 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  color: var(--text-main);
}

.section-header p {
  color: var(--text-secondary);
  font-size: 1.1rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  text-align: center;
  padding: 2rem;
  border-radius: var(--radius-lg);
  background: white;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-light);
}

.feature-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  margin: 0 auto 1.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
}

.icon-1 { background: #eff6ff; color: #3b82f6; }
.icon-2 { background: #fdf2f8; color: #ec4899; }
.icon-3 { background: #f5f3ff; color: #8b5cf6; }
.icon-4 { background: #ecfdf5; color: #10b981; }

.jobs-section {
  padding: 5rem 0;
  background-color: #f9fafb;
}

.section-header-flex {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.view-more {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 600;
  transition: gap 0.2s;
}

.view-more:hover {
  gap: 0.5rem;
}

.jobs-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.job-card {
  background: white;
  border-radius: var(--radius-md);
  padding: 1.5rem;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
}

.job-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
  border-color: var(--primary-color);
}

.job-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.job-title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-main);
  margin: 0;
}

.job-salary {
  color: var(--secondary-color);
  font-weight: 700;
}

.job-tags {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.tag {
  background: #f3f4f6;
  color: var(--text-secondary);
  font-size: 0.75rem;
  padding: 2px 8px;
  border-radius: 4px;
}

.job-company {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding-top: 1rem;
  border-top: 1px solid var(--border-color);
}

.company-name {
  font-size: 0.875rem;
  color: var(--text-regular);
}

.cta-section {
  padding: 5rem 0;
  background: var(--primary-color);
  text-align: center;
  color: white;
}

.cta-content h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
}

.cta-content p {
  font-size: 1.25rem;
  opacity: 0.9;
  margin-bottom: 2rem;
}

.btn-white {
  background: white;
  color: var(--primary-color);
  padding: 1rem 3rem;
  border-radius: 50px;
  font-weight: 700;
  text-decoration: none;
  box-shadow: var(--shadow-lg);
  transition: transform 0.2s;
}

.btn-white:hover {
  transform: scale(1.05);
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    text-align: center;
  }
  
  .hero-actions {
    justify-content: center;
  }
  
  .hero-stats {
    justify-content: center;
  }
  
  .search-section {
    margin-top: 0;
  }
}
</style>
