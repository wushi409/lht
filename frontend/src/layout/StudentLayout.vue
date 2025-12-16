<template>
  <div class="student-layout">
    <nav class="navbar" :class="{ 'scrolled': isScrolled || !isHomePage }">
      <div class="container navbar-content">
        <div class="logo">
          <router-link to="/">
            <div class="logo-box">
              <el-icon class="logo-icon"><Promotion /></el-icon>
            </div>
            <span class="logo-text">双选会系统</span>
          </router-link>
        </div>

        <div class="nav-links">
          <router-link to="/" class="nav-item">首页</router-link>
          <router-link to="/student/jobs" class="nav-item">职位大厅</router-link>
          <router-link to="/student/events" class="nav-item">双选会日程</router-link>
          <router-link to="/student/companies" class="nav-item">参会企业</router-link>
        </div>

        <div class="auth-actions" v-if="!token">
          <router-link to="/login" class="login-btn">登录</router-link>
          <router-link to="/register/student" class="register-btn">立即注册</router-link>
        </div>

        <div class="user-actions" v-else>
          <el-dropdown trigger="click">
            <span class="user-avatar">
              <el-avatar :size="36" :src="userInfo?.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
              <span class="username">{{ userInfo?.name || userInfo?.username || '同学' }}</span>
              <el-icon class="el-icon--right"><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu class="custom-dropdown">
                <el-dropdown-item @click="$router.push('/student/dashboard')">
                  <el-icon><Odometer /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/student/profile')">
                  <el-icon><User /></el-icon>我的简历
                </el-dropdown-item>
                <el-dropdown-item @click="$router.push('/student/applications')">
                  <el-icon><Tickets /></el-icon>投递记录
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </nav>

    <main class="page-content">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-col brand-col">
            <div class="footer-logo">
              <el-icon><Promotion /></el-icon> 校园双选会
            </div>
            <p>致力为高校毕业生和企业搭建高效、便捷的求职招聘平台。</p>
          </div>
          <div class="footer-col">
            <h3>关于我们</h3>
            <p><router-link to="/">平台简介</router-link></p>
            <p><router-link to="/">联系我们</router-link></p>
          </div>
          <div class="footer-col">
            <h3>求职服务</h3>
            <p><router-link to="/student/jobs">浏览职位</router-link></p>
            <p><router-link to="/student/companies">企业名录</router-link></p>
          </div>
          <div class="footer-col">
            <h3>企业服务</h3>
            <p><router-link to="/register/company">企业入驻</router-link></p>
            <p><router-link to="/login">企业登录</router-link></p>
          </div>
        </div>
        <div class="copyright">
          © 2025 高校毕业生双选会信息管理系统 All Rights Reserved.
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter, useRoute } from 'vue-router'
import { CaretBottom, Promotion, Odometer, User, Tickets, SwitchButton } from '@element-plus/icons-vue'

const userStore = useUserStore()
const router = useRouter()
const route = useRoute()
const token = computed(() => userStore.token)
const userInfo = computed(() => userStore.userInfo)

const isScrolled = ref(false)
const isHomePage = computed(() => route.path === '/')

const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

const handleLogout = () => {
  userStore.logout()
  router.push('/login')
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.student-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 72px;
  background: transparent;
  z-index: 1000;
  transition: all 0.3s ease;
}

.navbar.scrolled {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-color);
  box-shadow: var(--shadow-sm);
}

.navbar-content {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo a {
  display: flex;
  align-items: center;
  text-decoration: none;
  gap: 12px;
}

.logo-box {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
}

.logo-text {
  font-size: 1.25rem;
  font-weight: 700;
  color: white; /* Default for transparent on Home */
  transition: color 0.3s;
}

.navbar.scrolled .logo-text {
  color: var(--text-main);
}

/* Nav Links */
.nav-links {
  display: flex;
  gap: 2.5rem;
  background: rgba(0,0,0,0.2);
  padding: 8px 24px;
  border-radius: 99px;
  backdrop-filter: blur(4px);
  transition: all 0.3s;
}

.navbar.scrolled .nav-links {
  background: transparent;
  padding: 0;
  backdrop-filter: none;
}

.nav-item {
  text-decoration: none;
  color: rgba(255,255,255,0.9);
  font-weight: 500;
  font-size: 0.95rem;
  transition: color 0.2s;
  position: relative;
}

.nav-item::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 2px;
  background: white;
  transition: width 0.3s ease;
  border-radius: 2px;
}

.nav-item:hover::after, .nav-item.router-link-active::after {
  width: 20px;
}

.navbar.scrolled .nav-item {
  color: var(--text-regular);
}

.navbar.scrolled .nav-item:hover, .navbar.scrolled .nav-item.router-link-active {
  color: var(--primary-color);
}

.navbar.scrolled .nav-item::after {
  background: var(--primary-color);
}

/* Auth Actions */
.auth-actions {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.login-btn {
  text-decoration: none;
  color: white;
  font-weight: 600;
  transition: color 0.2s;
}

.navbar.scrolled .login-btn {
  color: var(--text-regular);
}

.navbar.scrolled .login-btn:hover {
  color: var(--primary-color);
}

.register-btn {
  text-decoration: none;
  background: white;
  color: var(--primary-color);
  padding: 8px 20px;
  border-radius: 99px;
  font-weight: 600;
  transition: all 0.2s;
}

.register-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.navbar.scrolled .register-btn {
  background: var(--primary-color);
  color: white;
}

.navbar.scrolled .register-btn:hover {
  background: var(--primary-hover);
}

/* User Actions */
.user-avatar {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 0.75rem;
  color: white;
  padding: 4px 8px;
  border-radius: 99px;
  transition: background 0.2s;
}

.user-avatar:hover {
  background: rgba(255,255,255,0.1);
}

.navbar.scrolled .user-avatar {
  color: var(--text-main);
}

.navbar.scrolled .user-avatar:hover {
  background: var(--bg-body);
}

.username {
  font-weight: 500;
  font-size: 0.95rem;
}

/* Page Content */
.page-content {
  padding-top: 0; /* Let hero section go under navbar */
  flex: 1;
}

/* Adjust page content if not homepage to avoid overlap if needed, 
   but currently we want transparent nav on top. 
   However, for non-home pages we probably want padding. */
.student-layout:not(:has(.home-page)) .page-content {
  padding-top: 72px;
}

/* Footer */
.footer {
  background: #0f172a;
  color: #94a3b8;
  padding: 4rem 0 2rem;
  margin-top: auto;
}

.footer-content {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr;
  gap: 4rem;
  margin-bottom: 3rem;
}

.brand-col .footer-logo {
  font-size: 1.5rem;
  font-weight: 700;
  color: white;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.footer-col h3 {
  color: white;
  margin-bottom: 1.5rem;
  font-size: 1.1rem;
  font-weight: 600;
}

.footer-col p {
  margin-bottom: 0.75rem;
}

.footer-col a {
  color: #94a3b8;
  text-decoration: none;
  transition: color 0.2s;
}

.footer-col a:hover {
  color: white;
  text-decoration: underline;
}

.copyright {
  border-top: 1px solid #1e293b;
  padding-top: 2rem;
  text-align: center;
  font-size: 0.875rem;
  color: #64748b;
}

@media (max-width: 768px) {
  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  .nav-links {
    display: none; /* Mobile menu implementation needed if mobile support required */
  }
}
</style>
