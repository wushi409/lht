<template>
  <div class="public-layout">
    <!-- Top Bar (University Context) -->
    <div class="top-bar">
      <div class="container top-bar-inner">
        <span>欢迎访问大学生就业创业指导服务平台</span>
        <div class="top-actions">
           <el-button link class="top-link" @click="$router.push('/login')">用户登录</el-button>
        </div>
      </div>
    </div>

    <!-- Header -->
    <header class="header">
      <div class="container header-content">
        <div class="brand" @click="$router.push('/')" style="cursor: pointer">
          <div class="logo-circle">
            <span class="icon">🎓</span>
          </div>
          <div class="brand-text">
            <h1 class="brand-name">大学就业服务网</h1>
            <p class="brand-en">University Employment Service Platform</p>
          </div>
        </div>
        <div class="header-search">
          <el-input 
            v-model="searchText" 
            placeholder="搜索职位 / 公司" 
            class="main-search-input"
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button :icon="Search" @click="handleSearch">搜索</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </header>

    <!-- Navigation -->
    <nav class="main-nav">
      <div class="container nav-list">
        <router-link to="/" class="nav-item" exact-active-class="active">首页</router-link>
        <router-link to="/public/jobs" class="nav-item" active-class="active">招聘信息</router-link>
        <router-link to="/public/seminars" class="nav-item" active-class="active">宣讲会</router-link>
        <router-link to="/public/fairs" class="nav-item" active-class="active">双选会</router-link>
        <router-link to="/public/companies" class="nav-item" active-class="active">知名企业</router-link>
        <router-link to="/public/announcements" class="nav-item" active-class="active">公告通知</router-link>
      </div>
    </nav>

    <!-- Main Content -->
    <div class="layout-main">
        <router-view />
    </div>

    <!-- Footer -->
    <footer class="main-footer">
      <div class="container footer-inner">
         <div class="footer-info">
           <p>主办单位：XX大学就业指导中心 &nbsp;&nbsp; 技术支持：校园双选系统团队</p>
           <p>地址：xx省xx市xx区xx路xx号 &nbsp;&nbsp; 邮编：100000</p>
           <p>联系电话：010-88888888 &nbsp;&nbsp; 邮箱：job@university.edu.cn</p>
         </div>
         <div class="qr-code">
           <div class="qr-placeholder">微信公众号</div>
         </div>
      </div>
      <div class="footer-copyright">
        Copyright © 2025 All Rights Reserved. 
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'

const router = useRouter()
const searchText = ref('')

const handleSearch = () => {
  if (searchText.value.trim()) {
    router.push({ path: '/public/jobs', query: { keyword: searchText.value.trim() } })
  }
}
</script>

<style scoped>
/* Reset & Base */
.public-layout {
  background-color: #f4f6f8;
  min-height: 100vh;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
  color: #333;
}
.container {
  width: 1200px;
  margin: 0 auto;
}

/* Top Bar */
.top-bar {
  background: #f8f9fa;
  border-bottom: 1px solid #eef0f2;
  font-size: 12px;
  color: #666;
  height: 36px;
  line-height: 36px;
}
.top-bar-inner {
  display: flex;
  justify-content: space-between;
}
.top-actions { display: flex; align-items: center; }
.top-link { padding: 0 5px; font-size: 12px; color: #666; }

/* Header */
.header {
  background: white;
  padding: 24px 0;
}
.header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.brand {
  display: flex;
  align-items: center;
  gap: 16px;
}
.logo-circle {
  width: 56px;
  height: 56px;
  background: #1e40af; /* Formal Blue */
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 32px;
}
.brand-name {
  margin: 0;
  font-size: 28px;
  color: #1e40af;
  font-weight: 700;
  letter-spacing: 1px;
}
.brand-en {
  margin: 4px 0 0;
  font-size: 12px;
  color: #888;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.header-search {
  width: 360px;
}
:deep(.main-search-input) {
  height: 40px;
}
:deep(.main-search-input .el-input__wrapper) {
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  border-radius: 4px 0 0 4px;
  height: 40px;
}
:deep(.main-search-input .el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #1e40af inset;
}
:deep(.main-search-input .el-input-group__append) {
  background-color: #1e40af;
  color: white;
  border-color: #1e40af;
  padding: 0 20px;
  border-radius: 0 4px 4px 0;
}
:deep(.main-search-input .el-input-group__append:hover) {
  background-color: #1e3a8a;
}

/* Nav */
.main-nav {
  background: #1e40af;
  color: white;
  height: 50px;
  line-height: 50px;
}
.nav-list {
  display: flex;
  gap: 0;
}
.nav-item {
  display: block;
  padding: 0 24px;
  color: white;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
  text-decoration: none;
}
.nav-item:hover, .nav-item.active {
  background: rgba(255, 255, 255, 0.15);
}

/* Footer */
.main-footer {
  background: #1e3a8a; /* Dark Blue */
  color: white;
  margin-top: 40px;
  padding-top: 40px;
}
.footer-inner {
  display: flex;
  justify-content: space-between;
  padding-bottom: 40px;
  border-bottom: 1px solid rgba(255,255,255,0.1);
}
.footer-info p {
  margin: 0 0 10px;
  color: rgba(255,255,255,0.8);
  font-size: 14px;
}
.qr-placeholder {
  width: 100px;
  height: 100px;
  background: white;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}
.footer-copyright {
  text-align: center;
  padding: 20px 0;
  color: rgba(255,255,255,0.6);
  font-size: 13px;
}
</style>
